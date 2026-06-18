package me.hapyl.hariant.talent.target;

import me.hapyl.hariant.Colors;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.game.GameInstance;
import me.hapyl.hariant.talent.TalentContext;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TalentTargetRequireGameInstance implements TalentTarget {
    
    @Override
    public @Nullable TalentContext createContext(@NotNull HariantPlayer player) {
        final GameInstance gameInstance = Hariant.getCurrentGameInstance().orElse(null);
        
        return gameInstance != null ? TalentContext.create(gameInstance) : null;
    }
    
    @Override
    public @NotNull Component errorMessage() {
        class Holder {
            private static final Component ERROR_MESSAGE = Component.text("Cannot be used outside the game!", Colors.RED);
        }
        
        return Holder.ERROR_MESSAGE;
    }
}
