package me.hapyl.hariant.talent;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.talent.target.TalentTarget;
import me.hapyl.hariant.util.Icon;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public class TalentPassive extends Talent {
    
    public TalentPassive(@NotNull Key key, @NotNull Component name, @NotNull Icon icon) {
        super(key, name, icon);
        
        // Most passives are "enhance", so default to that
        this.setTalentType(TalentType.ENHANCE);
    }
    
    @Override
    @NotNull
    public final TalentTarget target(@NotNull HariantPlayer player) {
        return TalentTarget.none();
    }
    
    @NotNull
    @Override
    public final Response execute(@NotNull HariantPlayer player, @NotNull TalentContext context) {
        throw new IllegalStateException("Do not execute passive talents!");
    }
    
    @NotNull
    @Override
    public String getTalentClassName() {
        return "Passive Talent";
    }
}
