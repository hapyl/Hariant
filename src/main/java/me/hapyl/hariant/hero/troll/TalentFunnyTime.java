package me.hapyl.hariant.hero.troll;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.talent.TalentContext;
import me.hapyl.hariant.talent.TalentType;
import me.hapyl.hariant.talent.target.TalentTarget;
import me.hapyl.hariant.talent.ultimate.TalentUltimate;
import me.hapyl.hariant.talent.ultimate.UltimateResourceType;
import me.hapyl.hariant.task.HariantTickingTask;
import me.hapyl.hariant.task.Scheduler;
import me.hapyl.hariant.task.executor.Executable;
import me.hapyl.hariant.util.Icon;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;

public class TalentFunnyTime extends TalentUltimate {
    public TalentFunnyTime(@NotNull Key key) {
        super(key, Component.text("Funny Time"), Icon.ofMaterial(Material.CLOCK), UltimateResourceType.ENERGY, 50);

        setTalentType(TalentType.IMPAIR);

        setDescription(
                Component.empty()
                        .append(Component.text("Make them funny)"))
        );
    }

    @Override
    public @NotNull Executable execute(@NotNull HariantPlayer player, @NotNull TalentContext context, double consumedResource) {
        return Executable.execute(() ->{
            player.playWorldSound(Sound.ENTITY_WITCH_CELEBRATE, 1.0f);

            new HariantTickingTask(Scheduler.ofTimer(20)) {
                @Override
                public void run(int tick) {
                    int numOfRotations = 10;
                    Hariant.getPlayers().filter(player::canAffect).forEach((enemy) -> {
                        boolean turnRight = player.random.nextBoolean();
                        float yawChange = turnRight ? 90.0f : -90.0f;
                        Location enemyLocation = enemy.getLocation();
                        enemyLocation.setYaw(enemyLocation.getYaw()+yawChange);
                        enemy.setLocation(enemyLocation);
                    });

                    if(tick>=numOfRotations){
                        cancel();
                    }
                }
            };

        });
    }

    @Override
    public @NotNull TalentTarget target(@NotNull HariantPlayer player) {
        return TalentTarget.none();
    }

}
