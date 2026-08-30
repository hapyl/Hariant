package me.hapyl.hariant.daily;

import me.hapyl.hariant.Colors;
import me.hapyl.hariant.database.PlayerDatabase;
import me.hapyl.hariant.hero.Archetype;
import me.hapyl.hariant.profile.PlayerProfile;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public enum DailyType implements Daily {
    
    KILL_PLAYERS(new DailyImpl(
            Component.text("Swift Slayer"),
            goal -> Component.empty()
                             .append(Component.text("Defeat enemy players "))
                             .append(Component.text(goal, Colors.RED))
                             .append(Component.text(" times.")),
            DailyTier.TIER_1,
            5, 10
    )),
    
    PLAY_GAMES(new DailyImpl(
            Component.text("Ready Player One"),
            goal -> Component.empty()
                             .append(Component.text("Play a total of "))
                             .append(Component.text(goal, Colors.GREEN))
                             .append(Component.text(" games.")),
            DailyTier.TIER_1,
            2, 4
    )),
    
    WIN_GAMES(new DailyImpl(
            Component.text("Chicken Dinner"),
            goal -> Component.empty()
                             .append(Component.text("Win a total of "))
                             .append(Component.text(goal, Colors.GOLD))
                             .append(Component.text(" games.")),
            DailyTier.TIER_1,
            1, 2
    )),
    
    USE_TALENTS(new DailyImpl(
            Component.text("Talented"),
            goal -> Component.empty()
                             .append(Component.text("Use talents a total of "))
                             .append(Component.text(goal, Colors.AQUA))
                             .append(Component.text(" times.")),
            DailyTier.TIER_1,
            10, 20
    )),
    
    USE_ULTIMATES(new DailyImpl(
            Component.text("Ultimate Showdown"),
            goal -> Component.empty()
                             .append(Component.text("Use ultimates a total of "))
                             .append(Component.text(goal, Colors.AQUA))
                             .append(Component.text(" times.")),
            DailyTier.TIER_1,
            3, 6
    )),
    
    PLAY_HERO_DAMAGE(DailyArchetypeImpl.create(Archetype.DAMAGE, Component.text("Brute Force"))),
    PLAY_HERO_STRATEGY(DailyArchetypeImpl.create(Archetype.STRATEGY, Component.text("The Thinker"))),
    PLAY_HERO_SUPPORT(DailyArchetypeImpl.create(Archetype.SUPPORT, Component.text("Helping Hand"))),
    PLAY_HERO_HEXBANE(DailyArchetypeImpl.create(Archetype.HEXBANE, Component.text("Black Widow"))),
    PLAY_HERO_DEFENSE(DailyArchetypeImpl.create(Archetype.DEFENSE, Component.text("Protector"))),
    
    FIRST_BLOOD(new DailyImpl(
            Component.text("First Blood"),
            _ -> Component.empty()
                          .append(Component.text("Cause "))
                          .append(Component.text("first blood", Colors.RED))
                          .append(Component.text(" in a game.")),
            DailyTier.TIER_2,
            1, 1
    )),
    
    ;
    
    private final Daily daily;
    
    DailyType(@NotNull Daily daily) {
        this.daily = daily;
    }
    
    @Override
    public @NotNull Component getName() {
        return daily.getName();
    }
    
    @Override
    public @NotNull DailyDescription getDescription() {
        return daily.getDescription();
    }
    
    @Override
    public @NotNull DailyTier getTier() {
        return daily.getTier();
    }
    
    @Override
    public int getMinimumGoal() {
        return daily.getMinimumGoal();
    }
    
    @Override
    public int getMaximumGoal() {
        return daily.getMaximumGoal();
    }
    
    @Override
    public boolean canGenerate(@NotNull PlayerDatabase database) {
        return daily.canGenerate(database);
    }
    
    public @NotNull DailyInstance newInstance() {
        return new DailyInstance(this, this.getRandomGoal());
    }
    
    public void progress(@NotNull PlayerProfile profile) {
        profile.getDatabase().daily.progress(this);
    }
    
    public static void progressArchetypeDaily(PlayerProfile profile, @NotNull Archetype archetype) {
        (switch (archetype) {
            case DAMAGE -> PLAY_HERO_DAMAGE;
            case STRATEGY -> PLAY_HERO_STRATEGY;
            case SUPPORT -> PLAY_HERO_SUPPORT;
            case HEXBANE -> PLAY_HERO_HEXBANE;
            case DEFENSE -> PLAY_HERO_DEFENSE;
            default -> throw new IllegalArgumentException("Unknown archetype: " + archetype);
        }).progress(profile);
    }
    
}