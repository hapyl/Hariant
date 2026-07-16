package me.hapyl.hariant.entity.damage.environment;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.HariantConstants;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.damage.DamageSourceIdentity;
import me.hapyl.hariant.entity.damage.DeathMessage;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Speleothem;
import org.jetbrains.annotations.NotNull;

public final class EnvironmentDamageSourceFall extends EnvironmentDamageSource {
    
    private static final double BASE_FALL_DAMAGE = 100;
    private static final double FALL_DAMAGE_EXPONENT = 0.25;
    
    private static final DamageSourceIdentity DAMAGE_SOURCE_IDENTITY = DamageSourceIdentity.create(
            Key.ofString("fall"),
            Component.text("Fall"),
            DeathMessage.createWithDefaultKiller("{player} fell to their death")
    );
    
    private EnvironmentDamageSourceFall(double damage) {
        super(DAMAGE_SOURCE_IDENTITY, ElementType.PHYSICAL, damage);
    }
    
    @NotNull
    public static EnvironmentDamageSupplier createSupplier() {
        return new EnvironmentDamageSupplier() {
            @NotNull
            @Override
            public EnvironmentDamageSource supply(@NotNull HariantEntity entity) {
                return new EnvironmentDamageSourceFall(this.calculateFallDamage(entity));
            }
            
            public double calculateFallDamage(@NotNull HariantEntity entity) {
                final float fallDistance = entity.getHandle().getFallDistance();
                
                // Check for block below the entity and soften the fall based on the block
                final Block block = entity.getLocation().getBlock().getRelative(BlockFace.DOWN);
                final Material blockType = block.getType();
                
                // If the block is fall damage resetting, deal 0 damage
                if (Tag.FALL_DAMAGE_RESETTING.isTagged(blockType)) {
                    return 0;
                }
                
                final double fallDamage = BASE_FALL_DAMAGE * Math.pow(Math.max(0, fallDistance - HariantConstants.FALL_DAMAGE_SAFE_FALL_DISTANCE), FALL_DAMAGE_EXPONENT);
                final double fallDamageMultiplier = switch (blockType) {
                    case HAY_BLOCK -> 0.5;
                    case BLACK_BED, BLUE_BED, BROWN_BED, CYAN_BED, GRAY_BED, GREEN_BED, LIME_BED, MAGENTA_BED, ORANGE_BED, PINK_BED, PURPLE_BED, RED_BED, WHITE_BED, YELLOW_BED -> 0.25;
                    case POINTED_DRIPSTONE -> {
                        if (!(block instanceof Speleothem dripstone) || dripstone.getVerticalDirection() != BlockFace.UP) {
                            yield 1;
                        }
                        
                        yield 2;
                    }
                    case ACACIA_LEAVES, AZALEA_LEAVES, BIRCH_LEAVES, CHERRY_LEAVES, JUNGLE_LEAVES, MANGROVE_LEAVES, OAK_LEAVES, SPRUCE_LEAVES -> 0.8;
                    default -> 1;
                };
                
                return fallDamage * fallDamageMultiplier;
            }
        };
    }
    
}