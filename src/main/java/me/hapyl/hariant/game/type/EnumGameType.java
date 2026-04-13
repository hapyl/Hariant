package me.hapyl.hariant.game.type;

import me.hapyl.eterna.module.component.ComponentList;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.game.GameInstance;
import me.hapyl.hariant.game.WinResult;
import me.hapyl.hariant.profile.PlayerProfile;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum EnumGameType implements GameType {
    
    DEATHMATCH(new GameTypeDeathmatch()),
    
    ;
    
    private final GameType gameType;
    
    EnumGameType(@NotNull GameTypeImpl gameType) {
        this.gameType = gameType;
    }
    
    @Nullable
    @Override
    public WinResult checkWinCondition(@NotNull GameInstance gameInstance) {
        return gameType.checkWinCondition(gameInstance);
    }
    
    @NotNull
    @Override
    public Component getName() {
        return gameType.getName();
    }
    
    @NotNull
    @Override
    public Component getDescription() {
        return gameType.getDescription();
    }
    
    @Override
    public int getMinimumTeamsRequired() {
        return gameType.getMinimumTeamsRequired();
    }
    
    @Override
    public int getTimeLimit() {
        return gameType.getTimeLimit();
    }
    
    @Override
    public boolean allowDuplicateHeroes() {
        return gameType.allowDuplicateHeroes();
    }
    
    @Override
    public void formatScoreboard(@NotNull PlayerProfile profile, @NotNull GameInstance gameInstance, @NotNull ComponentList components) {
        gameType.formatScoreboard(profile, gameInstance, components);
    }
    
    @Override
    public void onKill(@NotNull GameInstance gameInstance, @NotNull HariantPlayer player, @NotNull HariantPlayer victim) {
        gameType.onKill(gameInstance, player, victim);
    }
    
    @Override
    public void onDeath(@NotNull GameInstance gameInstance, @NotNull HariantPlayer player) {
        gameType.onDeath(gameInstance, player);
    }
}
