package me.hapyl.hariant.achievement;

import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.hero.HeroRegistry;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;

public final class AchievementTrollLaughingOutLoud extends AchievementHeroImpl {
    
    AchievementTrollLaughingOutLoud(@NotNull Key key) {
        super(key, HeroRegistry.TROLL, 1);
        
        setName(Component.text("LOL!"));
        setDescription(Component.text("Perform a special trolling technique."));
        
        setHidden(true);
        setTier(AchievementTier.TIER_2);
    }
    
    @Override
    public @NotNull ItemBuilder createBuilder() {
        final ItemBuilder builder = super.createBuilder();
        
        builder.editLore(existingLore -> {
            existingLore.addFirst(getHero().getName().color(Colors.DARK_GRAY).decoration(TextDecoration.ITALIC, false));
        });
        
        return builder;
    }
}
