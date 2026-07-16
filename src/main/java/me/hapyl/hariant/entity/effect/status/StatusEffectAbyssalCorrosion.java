package me.hapyl.hariant.entity.effect.status;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.damage.DamageSource;
import me.hapyl.hariant.entity.damage.DamageSourceIdentity;
import me.hapyl.hariant.entity.damage.DeathMessage;
import me.hapyl.hariant.entity.effect.EffectType;
import net.kyori.adventure.text.Component;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class StatusEffectAbyssalCorrosion extends StatusEffectImpl {
    
    StatusEffectAbyssalCorrosion(int level) {
        super(Key.ofString("effect_abyssal_corrosion_level_" + level), Component.text("Abyssal Corrosion (Level %s)"), EffectType.DEBUFF);
    }
    
    public static class Level1 extends StatusEffectAbyssalCorrosion {
        Level1() {
            super(1);
        }
        
        @Override
        public void onTick(@NotNull HariantEntity entity, @NotNull HariantEntity applier, int tick) {
            // Add poison effect to make hearts go green
            entity.addVanillaEffect(PotionEffectType.POISON, 0, 2);
        }
    }
    
    public static class Level2 extends StatusEffectAbyssalCorrosion {
        
        private static final int DAMAGE_PERIOD = 20;
        private static final double DAMAGE = 4;
        
        private static final DamageSourceIdentity IDENTITY = DamageSourceIdentity.create(
                Key.ofString("abyssal_corrosion"),
                Component.text("Abyssal Corrosion"),
                DeathMessage.createWithDefaultKiller("{player} died from abyssal corrosion")
        );
        
        Level2() {
            super(2);
        }
        
        @Override
        public void onTick(@NotNull HariantEntity entity, @NotNull HariantEntity applier, int tick) {
            if (entity.localTicks() % DAMAGE_PERIOD != 0) {
                return;
            }
            
            entity.damage(
                    DamageSource.builder(IDENTITY, DAMAGE)
                                .source(applier)
                                .elementType(ElementType.TOXIC)
                                .build()
            );
        }
    }
    
    public static class Level3 extends StatusEffectAbyssalCorrosion {
        Level3() {
            super(3);
        }
        
        @Override
        public void onTick(@NotNull HariantEntity entity, @NotNull HariantEntity applier, int tick) {
            entity.addVanillaEffect(PotionEffectType.NAUSEA, 1, 5);
        }
    }
    
    
}
