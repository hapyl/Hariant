package me.hapyl.hariant.game.battleground.japan;

import me.hapyl.eterna.module.location.LocationHelper;
import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.player.PlayerLib;
import me.hapyl.hariant.entity.EntityCollector;
import me.hapyl.hariant.entity.heal.HealingSource;
import me.hapyl.hariant.game.battleground.Battleground;
import me.hapyl.hariant.game.battleground.BattlegroundImpl;
import me.hapyl.hariant.game.battleground.feature.BattlegroundFeatureImpl;
import me.hapyl.hariant.game.booster.Booster;
import me.hapyl.hariant.inventory.drop.DropTable;
import me.hapyl.hariant.inventory.drop.Droppable;
import me.hapyl.hariant.inventory.item.ItemRegistry;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.ImmutableLocation;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.event.Listener;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class BattlegroundJapan extends BattlegroundImpl implements Listener {
    
    private final Booster[] boosters;
    
    public BattlegroundJapan() {
        super(Component.text("Japan"), Icon.ofMaterial(Material.CHERRY_SAPLING));
        
        this.setTimeBeforePlayersReveal(Tick.fromSeconds(8));
        
        this.setSpawnLocations(
                ImmutableLocation.create(1000, 64, 0, 180, 0),
                ImmutableLocation.create(1046, 80, -16, 180, 0),
                ImmutableLocation.create(1000, 85, -37, 180, 0),
                ImmutableLocation.create(937, 65, -11, -90, 0)
        );
        
        this.setFeatures(new BattlegroundFeatureJapanSakura());
        this.setDropTable(new DropTableJapan());
        
        // Store local boosters for particle fx
        this.boosters = new JapanBooster[] {
                Booster.create(new JapanBooster(ImmutableLocation.create(1000, 68, -26), 1.9, 1.0, 20)),
                Booster.create(new JapanBooster(ImmutableLocation.create(954, 80, -26), 1.45, 1.0, 15)),
                Booster.create(new JapanBooster(ImmutableLocation.create(1046, 80, -26), 1.45, 1.0, 15))
        };
    }
    
    @Override
    public void tick() {
        super.tick();
        
        // Tick boosters
        final double radians = Math.toRadians(Bukkit.getCurrentTick() * 10);
        
        for (Booster booster : boosters) {
            final Location location = booster.getLocation().getCenteredLocation();
            
            final double x = Math.sin(radians) * 0.9;
            final double z = Math.cos(radians) * 0.9;
            
            LocationHelper.offset(location, x, 0, z, () -> {
                PlayerLib.spawnParticle(location, Particle.FIREWORK, 1, 0, 0, 0, 0);
                PlayerLib.spawnParticle(location, Particle.FLAME, 1, 0, 0, 0, 0.025f);
            });
        }
    }
    
    private static class DropTableJapan extends DropTable {
        DropTableJapan() {
            super(
                    List.of(
                            Droppable.ofCatCoins(),
                            Droppable.ofItem(ItemRegistry.ARTIFACT_MAGIC_CODEX, 50),
                            Droppable.ofItem(ItemRegistry.ARTIFACT_SHATTERED_SOUL, 50),
                            Droppable.ofItem(ItemRegistry.ARTIFACT_WHOOPEE_CUSHION, 50),
                            Droppable.ofHeroRecruitVoucher()
                    ),
                    Battleground.DEFAULT_DROP_TABLE_AMOUNT
            );
        }
    }
    
    public class BattlegroundFeatureJapanSakura extends BattlegroundFeatureImpl implements EntityCollector {
        
        private final BoundingBox[] sakuraBoundingBoxes = {
                new BoundingBox(1021, 64, -19, 1035, 74, -5),
                new BoundingBox(965, 64, -19, 979, 74, -5)
        };
        
        private final Color outlineColor = Color.fromBGR(237, 57, 204);
        private final HealingSource healingSource = HealingSource.create(1);
        
        BattlegroundFeatureJapanSakura() {
            super(
                    Component.text("Healing Sakura"),
                    Component.text("Two grand Sakura trees whose petals are capable of healing.")
            );
        }
        
        @Override
        public void tick() {
            for (BoundingBox sakuraBoundingBox : sakuraBoundingBoxes) {
                this.collectNearbyEntities(sakuraBoundingBox).forEach(entity -> entity.heal(healingSource));
            }
        }
        
        @Override
        public @NotNull Location getLocation() {
            return getSpawnLocations().getFirst().getLocation();
        }
        
        @Override
        public @NotNull Color outlineColor() {
            return outlineColor;
        }
        
    }
    
}