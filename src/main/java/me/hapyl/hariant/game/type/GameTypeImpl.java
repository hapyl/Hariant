package me.hapyl.hariant.game.type;

import me.hapyl.eterna.module.component.ComponentList;
import me.hapyl.hariant.annotate.AutoRegisteredListener;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.game.GameInstance;
import me.hapyl.hariant.game.WinResult;
import me.hapyl.hariant.profile.PlayerProfile;
import me.hapyl.hariant.team.EnumTeam;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@AutoRegisteredListener
public class GameTypeImpl implements GameType {
    
    private final Component name;
    private final Component description;
    
    private final int timeLimit;
    private final boolean allowDuplicateHeroes;
    
    GameTypeImpl(@NotNull Component name, @NotNull Component description, int timeLimit, boolean allowDuplicateHeroes) {
        this.name = name;
        this.description = description;
        this.timeLimit = timeLimit;
        this.allowDuplicateHeroes = allowDuplicateHeroes;
        
        AutoRegisteredListener.Registry.register(this);
    }
    
    @Nullable
    @Override
    public WinResult checkWinCondition(@NotNull GameInstance gameInstance) {
        return WinResult.notWon();
    }
    
    @NotNull
    @Override
    public Component getName() {
        return name;
    }
    
    @NotNull
    @Override
    public Component getDescription() {
        return description;
    }
    
    @Override
    public int getMinimumTeamsRequired() {
        return 2;
    }
    
    @Override
    public int getTimeLimit() {
        return timeLimit;
    }
    
    @Override
    public boolean allowDuplicateHeroes() {
        return allowDuplicateHeroes;
    }
    
    @Override
    public void formatScoreboard(@NotNull PlayerProfile profile, @NotNull GameInstance gameInstance, @NotNull ComponentList components) {
    }
    
    @Override
    public void onKill(@NotNull GameInstance gameInstance, @NotNull HariantPlayer player, @NotNull HariantPlayer victim) {
    }
    
    @Override
    public void onDeath(@NotNull GameInstance gameInstance, @NotNull HariantPlayer player) {
    }
    
    @Override
    public @NotNull List<EnumTeam> getWiningTeamsWhenTimeLimit(@NotNull GameInstance gameInstance) {
        return List.of();
    }
    
}
