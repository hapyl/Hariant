package me.hapyl.hariant.hero.shaman;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.talent.TalentPassive;
import me.hapyl.hariant.util.Icon;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public class TalentOverheal extends TalentPassive {
    
    public TalentOverheal(@NotNull Key key) {
        super(key, Component.text("Overheal"), Icon.ofTemporaryTexture());
    }
    
}
