package me.hapyl.hariant.hero.troll;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.entity.damage.AssistSource;
import me.hapyl.hariant.entity.player.DelegateType;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.talent.TalentContext;
import me.hapyl.hariant.talent.TalentType;
import me.hapyl.hariant.talent.field.DisplayField;
import me.hapyl.hariant.talent.target.TalentTarget;
import me.hapyl.hariant.talent.ultimate.TalentUltimate;
import me.hapyl.hariant.talent.ultimate.UltimateResourceType;
import me.hapyl.hariant.task.HariantTickingTask;
import me.hapyl.hariant.task.Scheduler;
import me.hapyl.hariant.task.executor.Executable;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;

public class TalentFunnyTime extends TalentUltimate {
    
    private final @DisplayField Decimal period = Decimal.ofSeconds(1);
    private final @DisplayField Decimal prankDelay = Decimal.ofSeconds(0.5f);
    private final @DisplayField Decimal rotationAngle = Decimal.ofValue(90, value -> Component.text("%.0f°".formatted(value)));
    
    public TalentFunnyTime(@NotNull Key key) {
        super(key, Component.text("Prank Time"), Icon.ofMaterial(Material.CLOCK), UltimateResourceType.ENERGY, 50);
        
        setTalentType(TalentType.IMPAIR);
        setDurationSeconds(10);
        
        setDescription(
                Component.empty()
                         .append(Component.text("Start a hilarious prank by constantly turning all "))
                         .append(Component.text("enemy players", Colors.RED))
                         .append(Component.text(" heads left or right for "))
                         .append(this.getDurationFormatted())
                         .append(Component.text("."))
        );
    }
    
    @Override
    public @NotNull Executable execute(@NotNull HariantPlayer player, @NotNull TalentContext context, double consumedResource) {
        final int numberOfRotations = this.getDuration() / period.intValue();
        
        return Executable.execute(() -> {
            player.delegate(
                    new HariantTickingTask(Scheduler.ofTimer(prankDelay.intValue(), period.intValue())) {
                        @Override
                        public void run(int tick) {
                            Hariant.getPlayers().filter(player::canAffect).forEach(enemy -> {
                                // Check for effect resistance
                                if (enemy.hasEffectResistance(AssistSource.create(player, TalentFunnyTime.this))) {
                                    return;
                                }
                                
                                final boolean turnRight = player.random.nextBoolean();
                                final float yawChange = turnRight ? rotationAngle.floatValue() : -rotationAngle.longValue();
                                final Location enemyLocation = enemy.getLocation();
                                
                                enemyLocation.setYaw(enemyLocation.getYaw() + yawChange);
                                enemy.setLocation(enemyLocation);
                                
                                // Fx
                                enemy.playSound(Sound.ENTITY_BLAZE_HURT, turnRight ? 1.25f : 0.75f);
                            });
                            
                            if (tick >= numberOfRotations) {
                                cancel();
                            }
                        }
                    },
                    DelegateType.PERSISTENT
            );
            
            // Fx
            player.playWorldSound(Sound.ENTITY_WITCH_CELEBRATE, 1.0f);
        });
    }
    
    @Override
    public @NotNull TalentTarget target(@NotNull HariantPlayer player) {
        return TalentTarget.none();
    }
    
}
