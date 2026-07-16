package me.hapyl.hariant.hero.blast_knight;

import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.attribute.*;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.*;
import me.hapyl.hariant.entity.damage.*;
import me.hapyl.hariant.entity.damage.component.DamageComponent;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.hero.HeroRegistry;
import me.hapyl.hariant.talent.Response;
import me.hapyl.hariant.talent.Talent;
import me.hapyl.hariant.talent.TalentContext;
import me.hapyl.hariant.talent.field.DisplayField;
import me.hapyl.hariant.talent.target.TalentTarget;
import me.hapyl.hariant.task.HariantTickingTask;
import me.hapyl.hariant.task.Scheduler;
import me.hapyl.hariant.util.Definition;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.stream.Stream;

public final class TalentQuantumDischarge extends Talent {
    
    private final @DisplayField AttributeScaling damagePerQuantumEnergyConsumed = AttributeScaling.create(AttributeType.ATTACK, 27);
    private final @DisplayField Decimal elementalApplicationPerQuantumEnergyConsumer = Decimal.ofElementalApplication(ElementType.AETHER, 40);
    private final @DisplayField Decimal delayPerQuantumEnergyConsumed = Decimal.ofSeconds(0.2f);
    
    private final @DisplayField Decimal novaExplosionKnockbackStrength = Decimal.ofValue(1.85);
    private final @DisplayField Decimal novaExplosionBoundingRadius = Decimal.ofValue(5);
    
    private final @DisplayField Decimal pullStrength = Decimal.ofValue(0.2);
    private final @DisplayField Decimal pullResistance = Decimal.ofValue(0.6);
    
    private final DamageSourceIdentity damageSourceIdentity = DamageSourceIdentity.create(
            Key.ofString("nova_explosion"),
            Component.text("Nova Explosion"),
            DeathMessage.create("{player} has been split into atoms [by {killer}]")
    );
    
    private final ItemStack itemStackFx = ItemBuilder.playerHead("d81fcffb53acbc7c00c53bc7121ca259371b5b76c001dc52139e1804c287e54").asItemStack();
    
    public TalentQuantumDischarge(@NotNull Key key) {
        super(key, Component.text("Quantum Discharge"), Icon.ofMaterial(Material.CHORUS_PLANT));
        
        setCooldownSeconds(20);
        
        setDescription(
                Component.empty()
                         .append(Component.text("Expend all "))
                         .append(Definition.QUANTUM_ENERGY)
                         .append(Component.text(" to power up a device that "))
                         .append(Component.text("pulls", Colors.AQUA))
                         .append(Component.text(" nearby "))
                         .append(Component.text("enemies", Colors.ERROR))
                         .append(Component.text(" towards it and charges."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("Once charged, the device creates a "))
                         .appendNewline()
                         .append(Component.text("Nova Explosion", Colors.LIGHT_PURPLE))
                         .append(Component.text(" that deals "))
                         .append(ElementType.AETHER.asComponentAreaOfEffectDamage())
                         .append(Component.text(", applies "))
                         .append(ElementType.AETHER)
                         .append(Component.text(" anomaly and does massive knockback."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("The damage, charging speed and elemental application is based on the amount of energy consumed.", Colors.DARK_GRAY))
        );
    }
    
    @Override
    public @NotNull TalentTarget target(@NotNull HariantPlayer player) {
        return TalentTarget.none();
    }
    
    @Override
    public @NotNull Response execute(@NotNull HariantPlayer player, @NotNull TalentContext context) {
        final HeroDataBlastKnight heroData = player.getHeroData(HeroRegistry.BLAST_KNIGHT, HeroDataBlastKnight::new);
        final int quantumEnergy = heroData.getQuantumEnergy();
        
        if (quantumEnergy < 1) {
            return Response.error("Not enough quantum energy!");
        }
        
        heroData.resetQuantumEnergy();
        
        final double damage = damagePerQuantumEnergyConsumed.getScaledValue(player) * quantumEnergy;
        final double elementalApplication = elementalApplicationPerQuantumEnergyConsumer.doubleValue() * quantumEnergy;
        final int delay = delayPerQuantumEnergyConsumed.intValue() * quantumEnergy;
        
        new QuantumDischarge(player, damage, elementalApplication, delay);
        return Response.ok();
    }
    
    private class QuantumDischarge extends HariantTickingTask implements KnockbackSource, EntityCollector {
        
        private static final Color OUTLINE_COLOR = Color.PURPLE;
        
        private final HariantPlayer player;
        private final Location location;
        private final DamageSource damageSource;
        private final PullSource pullSource;
        private final int delay;
        
        private final ArmorStand fxArmorStand;
        
        public QuantumDischarge(@NotNull HariantPlayer player, double damage, double elementalApplication, int delay) {
            super(Scheduler.ofTimer());
            
            this.player = player;
            this.location = player.getLocationInFront(1).add(0, 0.2, 0);
            this.damageSource = new NovaExplosionDamageSource(player, damage, elementalApplication);
            this.pullSource = new PullSourceNova(player, location, delay);
            this.delay = delay;
            this.fxArmorStand = createFxArmorStand(location);
        }
        
        @Override
        public void run(int tick) {
            final Stream<HariantEntity> entities = collectNearbyEntities(novaExplosionBoundingRadius).filter(player::canAffect);
            
            // Create explosion
            if (tick > delay) {
                entities.forEach(entity -> {
                    entity.damage(damageSource);
                    entity.knockback(this);
                });
                
                // Fx
                final Location fxLocation = fxArmorStand.getLocation().add(0, 1.5, 0);
                
                player.spawnWorldParticle(fxLocation, Particle.SMOKE, 50, 1.25, 0.5, 1.25, 0.5f);
                player.spawnWorldParticle(fxLocation, Particle.FIREWORK, 50, 1.25, 0.5, 1.25, 0.5f);
                player.spawnWorldParticle(fxLocation, Particle.WITCH, 50, 1.25, 0.5, 1.25, 1.0f);
                player.spawnWorldParticle(fxLocation, Particle.EXPLOSION, 1, 0.0, 0.5, 0.0, 0.0f);
                
                player.playWorldSound(fxLocation, Sound.ITEM_SHIELD_BREAK, 0.0f);
                player.playWorldSound(fxLocation, Sound.ENTITY_BLAZE_HURT, 0.0f);
                player.playWorldSound(fxLocation, Sound.ENTITY_WARDEN_DEATH, 0.0f);
                
                this.cancel();
                return;
            }
            
            // Pull enemies closer
            
            
            // Animation
            final Location location = fxArmorStand.getLocation();
            final double y = Math.sin(Math.toRadians(tick * 8)) * 0.1;
            
            location.add(0, y, 0);
            location.setYaw(location.getYaw() + 5);
            
            fxArmorStand.teleport(location);
            
            player.spawnParticle(location.add(0, 1.5, 0), Particle.WITCH, 5, 0.1, 0.1, 0.1, 0.015f);
            
            // Fx
            if (modulo(2)) {
                final HariantRandom random = player.random;
                
                fxArmorStand.setHeadPose(new EulerAngle(random.nextDouble() * 0.5, 0, random.nextDouble() * 0.5));
                player.playWorldSound(location, Sound.ENTITY_WITCH_HURT, 0.5f + (1.5f * tick / delay));
                
                // Show warning
                entities.forEach(entity -> entity.showWarning(WarningType.DANGER, 5));
            }
        }
        
        @Override
        public void onCancel() {
            fxArmorStand.remove();
            pullSource.cancel();
        }
        
        @Override
        public double x() {
            return location.x();
        }
        
        @Override
        public double z() {
            return location.z();
        }
        
        @Override
        public double strength() {
            return novaExplosionKnockbackStrength.doubleValue();
        }
        
        @Override
        public @NotNull Location getLocation() {
            return location;
        }
        
        @Override
        public @NotNull Color outlineColor() {
            return OUTLINE_COLOR;
        }
        
    }
    
    private class NovaExplosionDamageSource extends DamageSourceImpl {
        
        NovaExplosionDamageSource(@Nullable HariantEntity source, double damage, double elementalApplication) {
            super(damageSourceIdentity, source, DamageType.TALENT, ElementType.AETHER, DamageComponent.ofCommon(), Set.of(), damage, elementalApplication);
        }
        
    }
    
    private @NotNull ArmorStand createFxArmorStand(@NotNull Location location) {
        return location.getWorld().spawn(location, ArmorStand.class, self -> {
            self.setSilent(true);
            self.setMarker(true);
            self.setVisible(false);
            self.getEquipment().setHelmet(itemStackFx);
        });
    }
    
    private class PullSourceNova extends PullSource {
        public PullSourceNova(@NotNull HariantEntity source, @NotNull Location centre, int duration) {
            super(source, centre, Component.text("Nova Pull"), duration, novaExplosionBoundingRadius.doubleValue(), pullStrength.doubleValue(), pullResistance.doubleValue());
        }
    }
    
}