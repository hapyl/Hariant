package me.hapyl.hariant.achievement;

import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.hero.Hero;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;

public class AchievementHeroImpl extends AchievementImpl {
    
    private static final Style LORE_STYLE = Style.style(Colors.DARK_GRAY).decoration(TextDecoration.ITALIC, false);
    
    private final Hero hero;
    
    AchievementHeroImpl(@NotNull Key key, double goal, @NotNull Component name, @NotNull Component description, @NotNull Hero hero) {
        super(key, goal, name, description);
        
        this.hero = hero;
        this.setCategory(AchievementCategory.HERO_PATH);
    }
    
    public @NotNull Hero getHero() {
        return hero;
    }
    
    @Override
    public @NotNull ItemBuilder createBuilder() {
        final ItemBuilder builder = super.createBuilder();
        
        builder.editLore(existingLore -> {
            // Add hero's name as the first lore line
            existingLore.addFirst(getHero().getName().style(LORE_STYLE));
        });
        
        return builder;
    }
    
}