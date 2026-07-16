package me.hapyl.hariant.hero.ninja;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.damage.DamageSource;
import me.hapyl.hariant.entity.damage.DamageSourceIdentity;
import me.hapyl.hariant.entity.damage.DeathMessage;
import me.hapyl.hariant.entity.effect.Effect;
import me.hapyl.hariant.entity.effect.EffectType;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.talent.Response;
import me.hapyl.hariant.talent.Talent;
import me.hapyl.hariant.talent.TalentContext;
import me.hapyl.hariant.talent.TalentType;
import me.hapyl.hariant.talent.target.TalentTarget;
import me.hapyl.hariant.util.Icon;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class TalentHappoShuriken extends Talent{

    private final double maxRange = 30.0;


    public TalentHappoShuriken(@NotNull Key key) {
        super(key, Component.text("Happo Shuriken"), Icon.ofMaterial(Material.NETHER_STAR));

        setTalentType(TalentType.DAMAGE);
        setCooldownSeconds(5);

        setDescription(
                Component.empty()
                        .append(Component.text("Let 'em see only a cold gleam of steel"))
        );
    }

    @Override
    public @NotNull TalentTarget target(@NotNull HariantPlayer player) {
        return TalentTarget.none();
    }

    @Override
    public @NotNull Response execute(@NotNull HariantPlayer player, @NotNull TalentContext context) {
        Location eyeLocation = player.getEyeLocation();
        Vector direction = eyeLocation.getDirection().normalize();
        var world = player.getWorld();

        final DamageSourceIdentity damageSourceIdentity = DamageSourceIdentity.create(
                this, DeathMessage.create("{player} saw too much gleams of [{killer}'s shurikens]")
        );

        var blockHit = world.rayTraceBlocks(eyeLocation,direction,maxRange);
        var effectiveDistance = (blockHit != null)
                ? eyeLocation.distance(blockHit.getHitPosition().toLocation(world))
                : maxRange;

        var entityHit = world.rayTraceEntities(
                eyeLocation,
                direction,
                maxRange,
                0.5,
                entity -> entity instanceof Player p
                && !player.compareEntity(entity)
                && p.getGameMode() != GameMode.SPECTATOR
        );

        var particleDistance = switch (entityHit) {
            case RayTraceResult r when r.getHitEntity() instanceof Player target -> {
                Hariant.getEntity(target).ifPresent(entity -> {
                    entity.damage(DamageSource.builder(damageSourceIdentity,120).build());
                });
                yield eyeLocation.distance(r.getHitPosition().toLocation(world));
            }
            case null, default -> effectiveDistance;
        };

        var step = 0.3;
        for (var d = 0.0; d <= particleDistance; d += step){
            var point = eyeLocation.clone().add(direction.clone().multiply(d));
            world.spawnParticle(Particle.CRIT, point, 1, 0, 0, 0, 0);
        }

        return Response.ok();
    }

}
