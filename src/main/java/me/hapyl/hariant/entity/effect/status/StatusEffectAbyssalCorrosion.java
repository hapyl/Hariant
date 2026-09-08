package me.hapyl.hariant.entity.effect.status;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.damage.DamageSourceIdentity;
import me.hapyl.hariant.entity.damage.DamageSourceImpl;
import me.hapyl.hariant.entity.damage.DamageType;
import me.hapyl.hariant.entity.damage.DeathMessage;
import me.hapyl.hariant.entity.damage.component.DamageComponents;
import me.hapyl.hariant.entity.effect.EffectType;
import net.kyori.adventure.text.Component;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class StatusEffectAbyssalCorrosion extends StatusEffectImpl {
    
    StatusEffectAbyssalCorrosion(int level) {
        super(Key.ofString("effect_abyssal_corrosion_level_" + level), Component.text("Abyssal Corrosion (Level %s)".formatted(level)), EffectType.DEBUFF);
    }
    
    public static class Level1 extends StatusEffectAbyssalCorrosion {
        
        Level1() {
            super(1);
        }
        
        @Override
        public void onTick(@NotNull HariantEntity entity, @NotNull HariantEntity applier, int tick, int duration) {
            // Add poison effect to make hearts go green
            entity.addVanillaEffect(PotionEffectType.POISON, 0, 2);
        }
    }
    
    public static class Level2 extends StatusEffectAbyssalCorrosion {
        
        private static final int DAMAGE_PERIOD = 20;
        private static final double DAMAGE = 4;
        
        Level2() {
            super(2);
        }
        
        @Override
        public void onTick(@NotNull HariantEntity entity, @NotNull HariantEntity applier, int tick, int duration) {
            if (entity.localTicks() % DAMAGE_PERIOD != 0) {
                return;
            }
            
            entity.damage(new AbyssalCorrosionDamageSource(applier, DAMAGE));
        }
        
    }
    
    public static class Level3 extends StatusEffectAbyssalCorrosion {
        
        Level3() {
            super(3);
        }
        
        @Override
        public void onTick(@NotNull HariantEntity entity, @NotNull HariantEntity applier, int tick, int duration) {
            entity.addVanillaEffect(PotionEffectType.NAUSEA, 1, 5);
        }
        
    }
    
    private static class AbyssalCorrosionDamageSource extends DamageSourceImpl {
        
        private static final DamageSourceIdentity IDENTITY = DamageSourceIdentity.create(
                Key.ofString("abyssal_corrosion"),
                Component.text("Abyssal Corrosion"),
                DeathMessage.createWithDefaultKiller("{player} died from abyssal corrosion")
        );
        
        AbyssalCorrosionDamageSource(@NotNull HariantEntity source, double damage) {
            super(IDENTITY, source, DamageType.TALENT, ElementType.TOXIC, DamageComponents.ofAnomaly(), Set.of(), damage, 0);
        }
        
    }
    
}
