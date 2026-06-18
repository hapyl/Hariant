package me.hapyl.hariant.game.battleground.clouds;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.entity.damage.DamageSource;
import me.hapyl.hariant.entity.damage.DamageSourceIdentity;
import me.hapyl.hariant.entity.damage.DeathMessage;
import me.hapyl.hariant.game.battleground.BattlegroundImpl;
import me.hapyl.hariant.game.booster.Booster;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.ImmutableLocation;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;

public final class BattlegroundClouds extends BattlegroundImpl {
    
    private final double deathZoneY = 20;
    private final DamageSource damageSource = DamageSource.death(DamageSourceIdentity.create(
            Key.ofString("fell_of_clouds"),
            Component.text("Fell out of Clouds"),
            DeathMessage.createWithDefaultKiller("{player} fell from the clouds")
    )).build();
    
    public BattlegroundClouds() {
        super(Component.text("Clouds"), Icon.ofMaterial(Material.WHITE_STAINED_GLASS));
        
        this.setSpawnLocations(
                ImmutableLocation.create(3500, 64, 0, -180f, 0f),
                ImmutableLocation.create(3521, 63.5, -17),
                ImmutableLocation.create(3489, 63, 24, -90f, 0f),
                ImmutableLocation.create(3547, 68, -11, 90f, 0f)
        );
        
        Booster.create(new CloudsBooster(ImmutableLocation.create(3499.0, 61.0, 38.0), ImmutableLocation.create(3499.0, 63.0, 59.0)));
        Booster.create(new CloudsBooster(ImmutableLocation.create(3507.0, 64.0, 59.0), ImmutableLocation.create(3506.0, 61.0, 33.0)));
        Booster.create(new CloudsBooster(ImmutableLocation.create(3517.0, 60.0, 28.0), ImmutableLocation.create(3529.0, 59.0, 55.0)));
        Booster.create(new CloudsBooster(ImmutableLocation.create(3535.0, 60.0, 55.0), ImmutableLocation.create(3513.0, 61.0, 30.0)));
        Booster.create(new CloudsBooster(ImmutableLocation.create(3552.0, 64.0, 21.0), ImmutableLocation.create(3545.0, 61.0, 52.0)));
        Booster.create(new CloudsBooster(ImmutableLocation.create(3539.0, 67.0, -5.0), ImmutableLocation.create(3521.0, 64.0, -17.0)));
        Booster.create(new CloudsBooster(ImmutableLocation.create(3518.0, 62.0, 5.0), ImmutableLocation.create(3539.0, 67.0, -8.0)));
        Booster.create(new CloudsBooster(ImmutableLocation.create(3538.0, 73.0, -54.0), ImmutableLocation.create(3553.0, 69.0, -28.0)));
        Booster.create(new CloudsBooster(ImmutableLocation.create(3528.0, 55.0, -40.0), ImmutableLocation.create(3521.0, 64.0, -17.0)));
        Booster.create(new CloudsBooster(ImmutableLocation.create(3496.0, 60.0, -38.0), ImmutableLocation.create(3506.0, 72.0, -57.0)));
        Booster.create(new CloudsBooster(ImmutableLocation.create(3499.0, 73.0, -52.0), ImmutableLocation.create(3499.0, 60.0, -34.0)));
        Booster.create(new CloudsBooster(ImmutableLocation.create(3479.0, 60.0, -25.0), ImmutableLocation.create(3464.0, 64.0, -35.0)));
        Booster.create(new CloudsBooster(ImmutableLocation.create(3460.0, 64.0, -39.0), ImmutableLocation.create(3448.0, 57.0, -17.0)));
        Booster.create(new CloudsBooster(ImmutableLocation.create(3436.0, 56.0, -32.0), ImmutableLocation.create(3444.0, 57.0, -16.0)));
        Booster.create(new CloudsBooster(ImmutableLocation.create(3453.0, 57.0, -14.0), ImmutableLocation.create(3473.0, 60.0, -13.0)));
        Booster.create(new CloudsBooster(ImmutableLocation.create(3472.0, 59.0, -1.0), ImmutableLocation.create(3454.0, 54.0, 22.0)));
        Booster.create(new CloudsBooster(ImmutableLocation.create(3458.0, 55.0, 26.0), ImmutableLocation.create(3489.0, 63.0, 24.0)));
    }
    
    @Override
    public void tick() {
        super.tick();
        
        Hariant.getPlayers().forEach(player -> {
            if (!player.isDead() && player.y() < deathZoneY) {
                player.die(damageSource);
                
                // TODO (xanyjl @ Saturday, May 30) -> Add achievement
            }
        });
    }
    
}