package me.hapyl.hariant.hero.ninja;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.HariantConstants;
import me.hapyl.hariant.entity.effect.status.EnumStatusEffect;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.talent.Response;
import me.hapyl.hariant.talent.Talent;
import me.hapyl.hariant.talent.TalentContext;
import me.hapyl.hariant.talent.TalentType;
import me.hapyl.hariant.talent.target.TalentTarget;
import me.hapyl.hariant.task.HariantTickingTask;
import me.hapyl.hariant.task.Scheduler;
import me.hapyl.hariant.util.Icon;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

public class TalentKemuridama extends Talent {
    private static final long SMOKE_DURATION_TICKS = 60L;
    private static final long TICK_INTERVAL = 2L;
    private static final double SMOKE_RADIUS = 4.0;

    public TalentKemuridama(@NotNull Key key) {
        super(key,
                Component.text("Kemuri-dama"),
                Icon.ofMaterial(Material.GRAY_BUNDLE));

        setTalentType(TalentType.IMPAIR);
        setCooldownSeconds(20);

        setDescription(Component.empty().append(Component.text("A thick cloud of ash, granting absolute silence and shrouding the ninja in darkness.")));
    }

    @Override
    public @NotNull TalentTarget target(@NotNull HariantPlayer player) {
        return TalentTarget.none();
    }

    @Override
    public @NotNull Response execute(@NotNull HariantPlayer player, @NotNull TalentContext context) {
        Location center = player.getLocation();
        World world = center.getWorld();

        player.addEffect(EnumStatusEffect.INVISIBILITY, HariantConstants.INDEFINITE_DURATION, player);

        new HariantTickingTask(Scheduler.ofTimer()){
            @Override
            public void run(int tick) {
                if(tick >= SMOKE_DURATION_TICKS){
                    cancel();
                }
                spawnSmokeParticles(world, center);
            }
        };

        return Response.ok();
    }

    private void spawnSmokeParticles(World world, Location center){
        for (int i=0;i<20;i++){
            double offsetX = (Math.random() - 0.5) * SMOKE_RADIUS;
            double offsetY = Math.random() * 2;
            double offsetZ = (Math.random() - 0.5) * SMOKE_RADIUS;

            Location point = center.clone().add(offsetX, offsetY, offsetZ);
            world.spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, point, 1, 0, 0, 0, 0.01);
        }
    }
}
