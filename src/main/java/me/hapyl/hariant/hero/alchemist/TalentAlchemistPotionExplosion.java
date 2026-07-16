package me.hapyl.hariant.hero.alchemist;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.attribute.AttributeScaling;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.EntityCollector;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.WarningType;
import me.hapyl.hariant.entity.damage.*;
import me.hapyl.hariant.entity.damage.component.DamageComponent;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.talent.TalentType;
import me.hapyl.hariant.talent.field.DisplayField;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.stream.Stream;

public final class TalentAlchemistPotionExplosion extends TalentAlchemistPotion {
    
    private final @DisplayField Decimal radius = Decimal.ofValue(3);
    private final @DisplayField Decimal knockbackStrength = Decimal.ofValue(0.85);
    private final @DisplayField Decimal verticalVelocity = Decimal.ofValue(0.6);
    
    private final @DisplayField AttributeScaling explosionDamage = AttributeScaling.create(AttributeType.ELEMENTAL_MASTERY, 250);
    private final @DisplayField AttributeScaling elementalApplication = AttributeScaling.create(AttributeType.ELEMENTAL_MASTERY, 500);
    
    private final DamageSourceIdentity damageSourceIdentity = DamageSourceIdentity.create(
            Key.ofString("damage_source_weird_concoction"),
            Component.text("Weird Concoction"),
            DeathMessage.create("{player} was toxically exploded [by {killer}]")
    );
    
    TalentAlchemistPotionExplosion(@NotNull TalentAbyssalBottle talent) {
        super(talent, "weird_concoction", Component.text("Potion of Weird Concoction"), Color.fromRGB(235, 207, 52), 50);
        
        setTalentType(TalentType.DAMAGE);
        setDurationSeconds(3.0f);
        
        setDescription(
                Component.empty()
                         .append(Component.text("A weird looking potions that "))
                         .append(Component.text("explodes", Colors.RED))
                         .append(Component.text(" after "))
                         .append(getDurationFormatted())
                         .append(Component.text(", dealing "))
                         .append(ElementType.TOXIC.asComponentAreaOfEffectDamage())
                         .append(Component.text(" applies "))
                         .append(ElementType.TOXIC)
                         .append(Component.text(" anomaly and knockbacks enemies."))
        );
    }
    
    @Override
    public @NotNull AlchemistPotionInstance drink(@NotNull HariantPlayer player, @NotNull HeroDataAlchemist heroData) {
        // Fx
        player.playWorldSound(Sound.ENTITY_CREEPER_PRIMED, 0.75f);
        
        return new ExplosionPotionInstance(player, this);
    }
    
    private class ExplosionPotionInstance extends AlchemistPotionInstance implements EntityCollector {
        
        private final DamageSource damageSource;
        
        ExplosionPotionInstance(@NotNull HariantPlayer player, @NotNull TalentAlchemistPotion alchemistPotion) {
            super(player, alchemistPotion);
            
            this.damageSource = new DamageSourceExplosion(player, explosionDamage.getScaledValue(player), elementalApplication.getScaledValue(player));
        }
        
        @Override
        public boolean tick() {
            super.tick();
            final Stream<HariantEntity> entities = this.collectNearbyEntities(radius).filter(player::canAffect);
            
            if (currentTick() == 0) {
                final KnockbackSource knockbackSource = KnockbackSource.create(player, knockbackStrength.doubleValue());
                
                entities.forEach(entity -> {
                    entity.damage(damageSource);
                    entity.knockback(knockbackSource);
                });
                
                // Push alchemist up
                player.setVelocity(new Vector(0, verticalVelocity.doubleValue(), 0));
                
                // Fx
                player.playWorldSound(Sound.ENTITY_WITCH_DEATH, 1.25f);
                player.playWorldSound(Sound.ENTITY_BREEZE_HURT, 0.75f);
                player.spawnWorldParticle(getLocation(), Particle.EXPLOSION_EMITTER, 1, 0.0f);
                
                return true;
            }
            
            // Fx
            player.spawnWorldParticle(player.getMidpointLocation(), Particle.SMOKE, 1, 0.05f);
            
            // Display warning
            entities.forEach(entity -> entity.showWarning(WarningType.DANGER, 5));
            
            return false;
        }
        
        @Override
        public @NotNull Location getLocation() {
            return player.getLocation();
        }
    }
    
    private class DamageSourceExplosion extends DamageSourceImpl {
        DamageSourceExplosion(@Nullable HariantEntity source, double damage, double elementUnits) {
            super(
                    damageSourceIdentity,
                    source,
                    DamageType.TALENT,
                    ElementType.TOXIC,
                    DamageComponent.ofCommon(),
                    Set.of(),
                    damage,
                    elementUnits
            );
        }
    }
}