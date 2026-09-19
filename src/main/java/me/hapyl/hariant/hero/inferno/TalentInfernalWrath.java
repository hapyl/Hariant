package me.hapyl.hariant.hero.inferno;

import com.google.common.collect.Lists;
import io.papermc.paper.math.Rotations;
import me.hapyl.eterna.module.entity.Entities;
import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.eterna.module.location.LocationHelper;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.Removable;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.attribute.AttributeScaling;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.element.ElementalAnomalySourceImpl;
import me.hapyl.hariant.element.anomaly.ElementalAnomalyType;
import me.hapyl.hariant.entity.EntityCollector;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.WarningType;
import me.hapyl.hariant.entity.damage.*;
import me.hapyl.hariant.entity.damage.component.DamageComponents;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.talent.TalentContext;
import me.hapyl.hariant.talent.TalentType;
import me.hapyl.hariant.util.field.DisplayField;
import me.hapyl.hariant.talent.target.TalentTarget;
import me.hapyl.hariant.talent.ultimate.TalentUltimate;
import me.hapyl.hariant.talent.ultimate.UltimateResourceType;
import me.hapyl.hariant.task.HariantTickingTask;
import me.hapyl.hariant.task.Scheduler;
import me.hapyl.hariant.task.executor.Executable;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Stream;

public class TalentInfernalWrath extends TalentUltimate {
    
    private static final ItemStack MAGMA_TEXTURE = ItemBuilder.playerHead("721d0930bd61fea4cb9027b00e94e13d62029c524ea0b3260c747457ba1bcfa1").asItemStack();
    
    private final @DisplayField AttributeScaling damage = AttributeScaling.create(AttributeType.ATTACK, 153.6);
    
    private final @DisplayField Decimal radius = Decimal.ofValue(16);
    private final @DisplayField Decimal castingTime = Decimal.ofSeconds(0.75f);
    private final @DisplayField Decimal coneAngle = Decimal.ofAngle(80);
    
    private final double halfAngleRadians = Math.toRadians(coneAngle.doubleValue() * 0.5);
    private final double halfAngleRadiansCos = Math.cos(halfAngleRadians);
    
    private final DamageSourceIdentity damageSourceIdentity = DamageSourceIdentity.createOfNamed(
            this,
            Key.ofString("infernal_wrath_damage_source"),
            DeathMessage.create("{player} suffered the wrath [of {killer}]")
    );
    
    public TalentInfernalWrath(@NotNull Key key) {
        super(key, Component.text("Infernal Wrath"), Icon.ofMaterial(Material.MAGMA_BLOCK), UltimateResourceType.ENERGY, 60);
        
        setDurationSeconds(2);
        setTalentType(TalentType.IMPAIR);
        
        setDescription(
                Component.empty()
                         .append(Component.text("Unleash the infernal wrath by summoning a "))
                         .append(Component.text("cone of magma", Colors.RED))
                         .append(Component.text(" in front of you."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("After a short casting time, the magma "))
                         .append(Component.text("explodes", Colors.RED))
                         .append(Component.text(", dealing "))
                         .append(ElementType.FIRE.asComponentDamage())
                         .append(Component.text(" to enemies within it and "))
                         .append(Component.text("forcefully", Style.style(TextDecoration.UNDERLINED)))
                         .append(Component.text(" triggering one instance of "))
                         .append(ElementalAnomalyType.BURN)
                         .append(Component.text("."))
        );
    }
    
    @Override
    public @NotNull Executable execute(@NotNull HariantPlayer player, @NotNull TalentContext context, double consumedResource) {
        return Executable.execute(new InfernalWrath(player));
    }
    
    @Override
    public @NotNull TalentTarget target(@NotNull HariantPlayer player) {
        return TalentTarget.none();
    }
    
    public class InfernalWrath extends HariantTickingTask implements EntityCollector {
        
        private static final double[] EDGE_OFFSET = { 1, -1 };
        private static final double VERY_CLOSE = 1.0E-6;
        
        private final HariantPlayer player;
        
        private final Location origin;
        private final Vector direction;
        private final DamageSource damageSource;
        
        private final List<FxEntity> fxEntities;
        private final int batchSize;
        
        InfernalWrath(@NotNull HariantPlayer player) {
            super(Scheduler.ofTimer());
            
            this.player = player;
            this.origin = player.getLocation();
            this.direction = player.getEyeLocation().getDirection().setY(0).normalize();
            this.damageSource = new InfernalWrathDamageSource(player);
            this.fxEntities = generateFxEntities();
            this.batchSize = fxEntities.size() / castingTime.intValue();
        }
        
        @Override
        public void run(int tick) {
            // Create entities during casting time
            if (tick <= castingTime.intValue()) {
                // If we hit casting time, create arch right away
                if (tick == castingTime.intValue()) {
                    this.createArch();
                }
                // Otherwise create edges in batches
                else {
                    final int from = tick * batchSize;
                    final int to = Math.min(from + batchSize, fxEntities.size());
                    
                    // Spawn the entities in batches
                    for (int i = from; i < to; i++) {
                        fxEntities.get(i).spawn();
                    }
                    
                    // Fx
                    final double progress = tick / castingTime.doubleValue();
                    
                    player.playWorldSound(origin, Sound.ITEM_FLINTANDSTEEL_USE, (float) (0.5f + progress));
                }
            }
            // Otherwise tick entities
            else if (tick <= getDuration()) {
                // Warn entities
                if (modulo(2)) {
                    this.collectEntities().forEach(entity -> entity.showWarning(WarningType.DANGER, 3));
                    
                    // Fx
                    fxEntities.forEach(entity -> player.spawnWorldParticle(entity.location, Particle.FLAME, 1, 0.05, 0.05, 0.05, 0.1f));
                    
                    // Sfx
                    final double progress = (double) tick / getDuration();
                    
                    player.playWorldSound(origin, Sound.ENTITY_BLAZE_HURT, (float) (0.5f + progress));
                }
                
            }
            // Explode
            else {
                this.collectEntities().forEach(entity -> {
                    // Deal damage, and if entity has survived, trigger BURN
                    if (entity.damage(damageSource) == DamageResult.OK) {
                        entity.triggerAnomaly(new InfernalWrathAnomalySource(player), true);
                    }
                });
                
                // Fx
                fxEntities.forEach(entity -> player.spawnWorldParticle(entity.location, Particle.LAVA, 2, 0.1, 0.1, 0.1, 0.15f));
                
                player.playWorldSound(origin, Sound.ENTITY_BLAZE_DEATH, 0.75f);
                player.playWorldSound(origin, Sound.ENTITY_GENERIC_EXPLODE, 0.75f);
                
                this.cancel();
            }
        }
        
        public @NotNull Stream<? extends HariantEntity> collectEntities() {
            return this.collectNearbyEntities(radius).filter(this::isInCone);
        }
        
        private boolean isInCone(@NotNull HariantEntity entity) {
            if (!player.canAffect(entity)) {
                return false;
            }
            
            final double dx = entity.x() - origin.x();
            final double dz = entity.z() - origin.z();
            final double distance = Math.sqrt(dx * dx + dz * dz);
            
            if (distance < VERY_CLOSE) {
                return true;
            }
            
            final double dot = (direction.getX() * dx + direction.getZ() * dz) / distance;
            return dot >= halfAngleRadiansCos;
        }
        
        @Override
        public void onCancel() {
            fxEntities.forEach(FxEntity::remove);
            fxEntities.clear();
        }
        
        @Override
        public @NotNull Location getLocation() {
            return origin;
        }
        
        private @NotNull List<FxEntity> generateFxEntities() {
            final List<FxEntity> entities = Lists.newArrayList();
            
            // Create edges
            for (double d = 0; d <= radius.doubleValue(); d += 0.5) {
                for (double edge : EDGE_OFFSET) {
                    final Vector edgeVector = direction.clone().rotateAroundY(halfAngleRadians * edge);
                    
                    final double x = edgeVector.getX() * d;
                    final double z = edgeVector.getZ() * d;
                    
                    entities.add(new FxEntity(anchorLocation(origin, x, z)));
                }
            }
            
            return entities;
        }
        
        private void createArch() {
            final int steps = (int) (coneAngle.doubleValue() / 2);
            
            for (int i = 0; i <= steps; i++) {
                final double angle = -halfAngleRadians + (2 * halfAngleRadians * i / steps);
                final Vector arc = direction.clone().rotateAroundY(angle);
                
                final double x = arc.getX() * radius.doubleValue();
                final double z = arc.getZ() * radius.doubleValue();
                
                final FxEntity fxEntity = new FxEntity(anchorLocation(origin, x, z));
                fxEntity.spawn();
                
                fxEntities.add(fxEntity);
            }
        }
        
        private static @NotNull Location anchorLocation(@NotNull Location origin, double x, double z) {
            return LocationHelper.anchor(LocationHelper.copyOf(origin).add(x, 0, z));
        }
        
    }
    
    public static class FxEntity implements Removable {
        
        private static final Random RANDOM = Hariant.getRandom();
        
        private final Location location;
        private @Nullable Entity entity;
        
        FxEntity(@NotNull Location location) {
            this.location = location;
        }
        
        public void spawn() {
            if (this.entity != null) {
                throw new IllegalStateException("Duplicate fx entity spawn");
            }
            
            this.entity = Entities.ARMOR_STAND.spawn(LocationHelper.copyOf(location).subtract(0, 0.5, 0), self -> {
                self.setMarker(true);
                self.setSilent(true);
                self.setSmall(true);
                self.setVisible(false);
                
                // Rotate the head randomly
                self.setHeadRotations(Rotations.ofDegrees(RANDOM.nextDouble() * 90, RANDOM.nextDouble() * 90, RANDOM.nextDouble() * 90));
                self.getEquipment().setHelmet(MAGMA_TEXTURE);
            });
        }
        
        @Override
        public void remove() {
            if (entity != null) {
                entity.remove();
            }
        }
        
    }
    
    public class InfernalWrathDamageSource extends DamageSourceImpl {
        InfernalWrathDamageSource(@NotNull HariantEntity source) {
            super(damageSourceIdentity, source, DamageType.ULTIMATE, ElementType.FIRE, DamageComponents.ofTrueDamage(), Set.of(), damage.getScaledValue(source), 0);
        }
    }
    
    public static class InfernalWrathAnomalySource extends ElementalAnomalySourceImpl {
        
        InfernalWrathAnomalySource(@Nullable HariantEntity source) {
            super(ElementalAnomalyType.BURN, source);
        }
        
    }
    
}