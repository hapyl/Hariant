package me.hapyl.hariant.talent.target;

import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.talent.TalentContext;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public class TalentTargetImpl implements TalentTarget {
    
    static final TalentTarget INSTANCE = new TalentTargetImpl();
    
    private final TalentContext emptyContext;
    
    private TalentTargetImpl() {
        this.emptyContext = TalentContext.create(new Object());
    }
    
    @Override
    public @NotNull TalentContext createContext(@NotNull HariantPlayer player) {
        return emptyContext;
    }
    
    @Override
    public @NotNull Component errorMessage() {
        return Component.empty();
    }
    
}