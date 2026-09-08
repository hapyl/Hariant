package me.hapyl.hariant.game.battleground;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.cooldown.HariantCooldown;
import me.hapyl.hariant.entity.damage.*;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.game.battleground.feature.BattlegroundFeatureImpl;
import me.hapyl.hariant.inventory.drop.DropTable;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.ImmutableLocation;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Set;

public class BattlegroundTheVault extends BattlegroundImpl {
    
    public BattlegroundTheVault() {
        super(
                Component.text("Dwarven Vault"),
                Component.text("A dwarven vault located somewhere hidden."),
                DropTable.empty(),
                Icon.ofMaterial(Material.RAW_GOLD)
        );
        
        setSpawnLocations(
                ImmutableLocation.create(6000.0, 64.0, 0.0, -180, 0),
                ImmutableLocation.create(6000.0, 63.0, -70.0),
                ImmutableLocation.create(5979.0, 75.0, -70.0, -90f, 0f),
                ImmutableLocation.create(6021.0, 75.0, -70.0, 90f, 0f),
                ImmutableLocation.create(6000.0, 75.0, -49.0, -180f, 0f),
                ImmutableLocation.create(6010.0, 64.0, -83.0, 90f, 0f)
        );
        
        setFeatures(new FeatureBouncyLava());
    }
    
    private static class FeatureBouncyLava extends BattlegroundFeatureImpl {
        
        private static final BoundingBox BOUNDING_BOX = new BoundingBox(5970.0, 22.0, -37.0, 6035.0, 32.0, 35.0);
        private static final HariantCooldown BOUNCY_LAVA_COOLDOWN = HariantCooldown.ofSeconds(Key.ofString("bouncy_lava_cooldown"), 1);
        
        private static final Decimal DAMAGE_OF_MAX_HEALTH = Decimal.ofPercentage(20);
        private static final Vector PUSH_VECTOR = new Vector(0.0d, 3.1d, 0.0d);
        
        private int tick;
        
        FeatureBouncyLava() {
            super(Component.text("Bouncy Lava"), Component.text("A hot and bouncy lava, go jump in it!"));
        }
        
        @Override
        public void tick() {
            if (tick++ % 2 != 0) {
                return;
            }
            
            Hariant.getPlayers().forEach(player -> {
                if (!BOUNDING_BOX.contains(player.getBoundingBox())) {
                    return;
                }
                
                if (player.hasCooldown(BOUNCY_LAVA_COOLDOWN)) {
                    return;
                }
                
                player.setCooldown(BOUNCY_LAVA_COOLDOWN);
                this.bounceUp(player);
            });
        }
        
        public void bounceUp(@Nonnull HariantPlayer player) {
            player.setVelocity(PUSH_VECTOR);
            player.damage(new DamageSourceBouncyLava(player.getMaxHealth() * DAMAGE_OF_MAX_HEALTH.doubleValue()));
            
            // Fx
            player.playWorldSound(Sound.BLOCK_LAVA_POP, 0.0f);
            player.playWorldSound(Sound.BLOCK_REDSTONE_TORCH_BURNOUT, 0.0f);
            player.playWorldSound(Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 0.75f);
        }
        
    }
    
    private static class DamageSourceBouncyLava extends DamageSourceImpl {
        
        private static final DamageSourceIdentity DAMAGE_SOURCE_IDENTITY = DamageSourceIdentity.create(
                Key.ofString("bouncy_lava"),
                Component.text("Bouncy Lava"),
                DeathMessage.create("{player} didn't bounce [while running from {killer}]")
        );
        
        DamageSourceBouncyLava(double damage) {
            super(DAMAGE_SOURCE_IDENTITY, null, DamageType.ENVIRONMENT, ElementType.FIRE, List.of(), Set.of(), damage, 0);
        }
        
    }
    
}