package me.hapyl.hariant.hero.shaman;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.talent.Response;
import me.hapyl.hariant.talent.Talent;
import me.hapyl.hariant.talent.TalentContext;
import me.hapyl.hariant.talent.target.TalentTarget;
import me.hapyl.hariant.util.Icon;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public class TalentTotemJump extends Talent {
    
    public TalentTotemJump(@NotNull Key key) {
        super(key, Component.empty(), Icon.ofTemporaryTexture());
    }
    
    @Override
    public @NotNull TalentTarget target(@NotNull HariantPlayer player) {
        return null;
    }
    
    @Override
    public @NotNull Response execute(@NotNull HariantPlayer player, @NotNull TalentContext context) {
        return null;
    }
}
