package me.hapyl.hariant.config;

import me.hapyl.hariant.game.battleground.EnumBattleground;
import me.hapyl.hariant.game.type.EnumGameType;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public interface HariantConfig {
    
    @NotNull String databaseConnectionLink();
    
    @NotNull EnumBattleground getSelectedBattleground();
    
    void setSelectedBattleground(@NotNull EnumBattleground level);
    
    @NotNull EnumGameType getSelectedGameType();
    
    void setSelectedGameType(@NotNull EnumGameType gameType);
    
    @NotNull CompletableFuture<Void> saveToFile();
    
}
