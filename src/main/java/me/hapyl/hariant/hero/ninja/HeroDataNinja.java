package me.hapyl.hariant.hero.ninja;

import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.hero.HeroData;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public class HeroDataNinja extends HeroData<HeroNinja> {
    public HeroDataNinja(@NonNull HeroNinja hero, @NotNull HariantPlayer player) { super(hero, player); }

    @Override
    public void dispose() {

    }
}
