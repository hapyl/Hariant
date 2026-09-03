package me.hapyl.hariant.hero.mage;

import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.hero.HeroData;
import me.hapyl.hariant.profile.ui.ActionbarSupplier;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class HeroDataMage extends HeroData<HeroMage> implements ActionbarSupplier {
    
    public HeroDataMage(@NotNull HeroMage hero, @NotNull HariantPlayer player) {
        super(hero, player);
    }
    
    @Override
    public void dispose() {
    }
    
    @Override
    public @NotNull List<Component> supplyActionbar(@NotNull HariantPlayer player) {
        return List.of();
    }
    
}