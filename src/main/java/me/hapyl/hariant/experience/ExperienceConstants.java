package me.hapyl.hariant.experience;

import me.hapyl.hariant.game.Placement;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public final class ExperienceConstants {
    
    public static final Provider<Integer> MINUTE_PLAYED;
    public static final Provider<Double> ELIMINATION;
    public static final Provider<Double> ASSIST;
    public static final Provider<Placement> PLACEMENT;
    
    private static final long MINUTE_PLAYED_VALUE;
    private static final long ELIMINATION_VALUE;
    private static final long ASSIST_VALUE;
    private static final long GAME_ENDS_FIRST_PLACE_VALUE;
    private static final long GAME_ENDS_SECOND_PLACE_VALUE;
    private static final long GAME_ENDS_THIRD_PLACE_VALUE;
    private static final long GAME_ENDS_PARTICIPATION_VALUE;
    
    static {
        MINUTE_PLAYED_VALUE = 50;
        ELIMINATION_VALUE = 200;
        ASSIST_VALUE = 100;
        GAME_ENDS_FIRST_PLACE_VALUE = 1000;
        GAME_ENDS_SECOND_PLACE_VALUE = 750;
        GAME_ENDS_THIRD_PLACE_VALUE = 500;
        GAME_ENDS_PARTICIPATION_VALUE = 250;
        
        MINUTE_PLAYED = minutesPlayed -> ExperienceSource.create(Component.text("Minute Played (%s)".formatted(minutesPlayed)), MINUTE_PLAYED_VALUE * minutesPlayed);
        ELIMINATION = kills -> ExperienceSource.create(Component.text("Elimination (%.0f)".formatted(kills)), (long) (ELIMINATION_VALUE * kills));
        ASSIST = assists -> ExperienceSource.create(Component.text("Assists (%.0f)".formatted(assists)), (long) (ASSIST_VALUE * assists));
        PLACEMENT = placement -> ExperienceSource.create(
                placement.asComponent(),
                switch (placement) {
                    case FIRST_PLACE -> GAME_ENDS_FIRST_PLACE_VALUE;
                    case SECOND_PLACE -> GAME_ENDS_SECOND_PLACE_VALUE;
                    case THIRD_PLACE -> GAME_ENDS_THIRD_PLACE_VALUE;
                    case PARTICIPATION -> GAME_ENDS_PARTICIPATION_VALUE;
                }
        );
    }
    
    private ExperienceConstants() {
    }
    
    public interface Provider<T> {
        
        @NotNull ExperienceSource createSource(@NotNull T t);
        
    }
    
}
