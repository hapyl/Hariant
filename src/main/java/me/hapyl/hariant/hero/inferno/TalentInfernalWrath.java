package me.hapyl.hariant.hero.inferno;

import com.google.common.collect.Lists;
import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.eterna.module.location.LocationHelper;
import me.hapyl.eterna.module.math.geometry.Geometry;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.attribute.AttributeScaling;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.element.anomaly.ElementalAnomalyType;
import me.hapyl.hariant.entity.EntityCollector;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.WarningType;
import me.hapyl.hariant.entity.damage.DamageSource;
import me.hapyl.hariant.entity.damage.DamageSourceIdentity;
import me.hapyl.hariant.entity.damage.DamageType;
import me.hapyl.hariant.entity.damage.DeathMessage;
import me.hapyl.hariant.entity.damage.component.DamageComponent;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.talent.TalentContext;
import me.hapyl.hariant.talent.TalentType;
import me.hapyl.hariant.talent.field.DisplayField;
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
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

public class TalentInfernalWrath extends TalentUltimate {
    
    private static final int NUMBER_OF_FX_ARMOR_STANDS = 10;
    private static final ItemStack MAGMA_TEXTURE = ItemBuilder.playerHead("721d0930bd61fea4cb9027b00e94e13d62029c524ea0b3260c747457ba1bcfa1").asItemStack();
    
    @DisplayField private final AttributeScaling damage = AttributeScaling.create(AttributeType.ATTACK, 66);
    
    @DisplayField private final Decimal radius = Decimal.ofValue(6);
    @DisplayField private final Decimal castingTime = Decimal.ofSeconds(2.5f);
    
    private final DamageSourceIdentity damageSourceIdentity = DamageSourceIdentity.create(
            this,
            DeathMessage.create("{player} suffered the wrath [of {killer}]")
    );
    
    public TalentInfernalWrath(@NotNull Key key) {
        super(key, Component.text("Infernal Wrath"), Icon.ofMaterial(Material.MAGMA_BLOCK), UltimateResourceType.ENERGY, 60);
        
        setTalentType(TalentType.IMPAIR);
        
        setDescription(
                Component.empty()
                         .append(Component.text("Unleash the infernal wrath by summoning a "))
                         .append(Component.text("ring of magma", Colors.RED))
                         .append(Component.text(" that orbits around you."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("After "))
                         .append(castingTime)
                         .append(Component.text(", the ring explodes violently, dealing "))
                         .append(ElementType.FIRE.asComponentDamage())
                         .append(Component.text(" to nearby "))
                         .append(Component.text("enemies", Colors.RED))
                         .append(Component.text(" and "))
                         .append(Component.text("forcefully", Style.style(TextDecoration.UNDERLINED)))
                         .append(Component.text(" triggers one instance of "))
                         .append(ElementalAnomalyType.BURN)
                         .append(Component.text("."))
        );
    }
    
    @Override
    public @NotNull Executable execute(@NotNull HariantPlayer player, @NotNull TalentContext context, double consumedResource) {
        return Executable.delegate(new InfernalWrath(player));
    }
    
    @Override
    public @NotNull TalentTarget target(@NotNull HariantPlayer player) {
        return TalentTarget.none();
    }
    
    public class InfernalWrath extends HariantTickingTask implements EntityCollector {
        
        private final HariantPlayer player;
        private final DamageSource damageSource;
        private final List<ArmorStand> armorStands;
        
        private final double radius = TalentInfernalWrath.this.radius.doubleValue();
        private final int castingTime = TalentInfernalWrath.this.castingTime.intValue();
        
        public InfernalWrath(@NotNull HariantPlayer player) {
            super(Scheduler.ofTimer());
            
            this.player = player;
            this.damageSource = DamageSource.builder(damageSourceIdentity, damage.getScaledValue(player))
                                            .source(player)
                                            .damageType(DamageType.ULTIMATE)
                                            .elementType(ElementType.FIRE)
                                            .components(DamageComponent.ofTrueDamage())
                                            .build();
            this.armorStands = Lists.newArrayList();
            
            // Create armor stands
            final Location location = getLocation();
            
            for (int i = 0; i < NUMBER_OF_FX_ARMOR_STANDS; i++) {
                this.armorStands.add(createFxArmorStand(location));
            }
        }
        
        @Override
        public void run(int tick) {
            final Location location = getLocation();
            
            // If casting is finished, unleash the wrath
            if (tick >= castingTime) {
                // Deal damage and apply burning
                collectNearbyEntities().forEach(entity -> {
                    entity.damage(damageSource);
                    entity.triggerAnomaly(ElementalAnomalyType.BURN, player);
                });
                
                // Fx
                iterateArmorStands((armorStand, index) -> {
                    final int linkIndex = index + 1 < armorStands.size() ? index + 1 : 0;
                    final ArmorStand linkArmorStand = armorStands.get(linkIndex);
                    
                    Geometry.drawLine(armorStand.getLocation(), linkArmorStand.getLocation(), 0.4, player.drawableOf(Particle.LAVA, 1, 0.1, 0.3, 0.1, 0.5f));
                });
                
                player.playWorldSound(location, Sound.ITEM_FIRECHARGE_USE, 0.5f);
                player.playWorldSound(location, Sound.ENTITY_WITHER_HURT, 0.5f);
                player.playWorldSound(location, Sound.ENTITY_WITHER_HURT, 0.0f);
                
                // Keep cancel last because it removes the armor stands
                this.cancel();
            }
            // Otherwise orbit around the player
            else {
                final double spread = Math.PI * 2 / Math.max(1, armorStands.size());
                final double radians = Math.toRadians(tick) * 18;
                
                final double progress = (double) tick / TalentInfernalWrath.this.castingTime.doubleValue();
                final double radius = TalentInfernalWrath.this.radius.doubleValue() * progress;
                
                iterateArmorStands((armorStand, index) -> {
                    final double radiansOffset = radians + index * spread;
                    
                    final double x = Math.sin(radiansOffset) * radius;
                    final double y = Math.sin(Math.PI * 4 * radians + index * spread) * 0.2 - progress * 1.5;
                    final double z = Math.cos(radiansOffset) * radius;
                    
                    LocationHelper.offset(location, x, y, z, () -> {
                        armorStand.teleport(location);
                        
                        // Particle fx
                        LocationHelper.offset(location, 0, 1, 0, () -> player.spawnWorldParticle(location, Particle.SMOKE, 1, 0.1, 0.1, 0.1, 0.05f));
                    });
                });
                
                if (modulo(2)) {
                    player.playWorldSound(location, Sound.BLOCK_LAVA_POP, (float) (1.0f + 1.0f * progress));
                    player.playWorldSound(location, Sound.ITEM_FIRECHARGE_USE, (float) (0.75f + 1.25f * progress));
                }
                // Warn entities in range
                else if (modulo(5)) {
                    collectNearbyEntities().forEach(entity -> entity.showWarning(WarningType.DANGER, 10));
                }
            }
        }
        
        @Override
        public void onCancel() {
            super.onCancel();
            
            armorStands.forEach(Entity::remove);
            armorStands.clear();
        }
        
        @NotNull
        @Override
        public Location getLocation() {
            return player.getEyeLocation().add(0, 0.25, 0);
        }
        
        @Override
        public @NotNull Color outlineColor() {
            return Color.RED;
        }
        
        @NotNull
        private Stream<HariantEntity> collectNearbyEntities() {
            return collectNearbyEntities(radius).filter(player::canAffect);
        }
        
        private void iterateArmorStands(@NotNull BiConsumer<ArmorStand, Integer> consumer) {
            int index = 0;
            
            for (final ArmorStand armorStand : armorStands) {
                consumer.accept(armorStand, index++);
            }
        }
        
        @NotNull
        public static ArmorStand createFxArmorStand(@NotNull Location location) {
            return location.getWorld().spawn(location, ArmorStand.class, self -> {
                self.getEquipment().setHelmet(MAGMA_TEXTURE);
                self.setSmall(true);
                self.setInvisible(true);
                self.setSilent(true);
                self.setGravity(false);
                self.setHeadPose(new EulerAngle(Math.toRadians(45d), 0d, 0d));
            });
        }
    }
    
}