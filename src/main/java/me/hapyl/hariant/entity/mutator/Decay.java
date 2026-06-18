package me.hapyl.hariant.entity.mutator;

import me.hapyl.hariant.Colors;
import me.hapyl.hariant.HariantConstants;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public final class Decay extends HealthMutatorImpl {
    
    private static final Component MUTATOR_NAME = Component.text("Decay");
    
    private static final Style HEALTH_STYLE = Style.style(Colors.DECAY_LIGHTER);
    private static final Style HEART_STYLE = Style.style(Colors.DECAY);
    
    private final double decrement;
    
    private double decay;
    private int tick;
    
    private Decay(double amount, int duration) {
        super(MUTATOR_NAME, HEALTH_STYLE, HEART_STYLE);
        
        this.decay = amount;
        this.decrement = amount / (duration + HariantConstants.DECAY_DELAY);
    }
    
    @Override
    public boolean isOver() {
        return decay <= 0.0;
    }
    
    @Override
    public void onApply(@NotNull HariantEntity entity) {
        // Fx
        entity.playWorldSound(Sound.ENTITY_WITHER_SKELETON_HURT, 0.0f);
        entity.playWorldSound(Sound.ENTITY_PLAYER_HURT_SWEET_BERRY_BUSH, 0.0f);
        entity.playWorldSound(Sound.ENTITY_VEX_DEATH, 0.75f);
        
        entity.spawnWorldParticle(entity.getMidpointLocation(), Particle.RAID_OMEN, 15, 0.3d, 0.6d, 0.3d, 0.02f);
    }
    
    @Override
    public void onRemove(@NotNull HariantEntity entity) {
        entity.playWorldSound(Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 1.25f);
    }
    
    @Override
    public double mutate(double health) {
        return Math.max(HariantConstants.DECAY_MINIMUM_HEALTH, health - decay);
    }
    
    @Override
    public void tick(@NotNull HariantEntity entity) {
        if (tick++ > HariantConstants.DECAY_DELAY) {
            decay = Math.max(0, decay - decrement);
        }
        
        entity.addVanillaEffect(PotionEffectType.WITHER, 0, 5);
    }
    
    @NotNull
    public static Decay create(double amount, int duration) {
        return new Decay(amount, duration);
    }
    
    @NotNull
    public static Decay create(double amount, @NotNull Decimal duration) {
        return create(amount, duration.intValue());
    }
    
}