package me.hapyl.hariant.hero.inferno;

import me.hapyl.hariant.Colors;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.hero.HeroData;
import me.hapyl.hariant.hero.Race;
import me.hapyl.hariant.profile.ui.ActionbarSupplier;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HeroDataInferno extends HeroData<HeroInferno> implements ActionbarSupplier {
    
    @Nullable public InfernoDemon currentDemon;
    
    public HeroDataInferno(@NotNull HeroInferno hero, @NotNull HariantPlayer player) {
        super(hero, player);
    }
    
    @Override
    public void tick() {
        if (currentDemon != null) {
            // If time ran out, reform from the demon
            if (currentDemon.isOver()) {
                currentDemon.onReform(player, this);
                currentDemon.remove();
                currentDemon = null;
            }
        }
    }
    
    @Override
    public void dispose() {
        if (currentDemon != null) {
            currentDemon.remove();
            currentDemon = null;
        }
    }
    
    @Override
    public @NotNull List<Component> supplyActionbar(@NotNull HariantPlayer player) {
        return List.of(
                currentDemon != null
                ? Component.empty()
                           .append(Race.DEMON.getPrefix().color(Colors.DARK_RED))
                           .appendSpace()
                           .append(currentDemon.getDemonType().getDemonName().color(Colors.HELL))
                           .appendSpace()
                           .append(currentDemon.currentTickFormatted().color(Colors.NUMBER))
                           .append()
                : Component.empty()
        );
    }
}