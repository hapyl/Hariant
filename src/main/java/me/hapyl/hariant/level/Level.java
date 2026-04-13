package me.hapyl.hariant.level;

import com.google.common.collect.Lists;
import me.hapyl.eterna.module.component.Styled;
import me.hapyl.hariant.HariantConstants;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.List;

public final class Level implements Styled, ComponentLike {
    
    private static final Component PREFIX = Component.text("[", NamedTextColor.DARK_GRAY);
    private static final Component SUFFIX = Component.text("]", NamedTextColor.DARK_GRAY);
    
    private static final List<Level> LEVELS;
    
    static {
        // No point using TreeMap here, since we store a Level object, not experience
        LEVELS = Lists.newArrayList();
        
        for (int level = 0; level < HariantConstants.MAX_LEVEL; level++) {
            final long experience = (long) (HariantConstants.BASE_EXPERIENCE * level * Math.pow(level, HariantConstants.EXPERIENCE_EXPONENT));
            final long experienceRounded = Math.round(experience / 100.0) * 100;
            
            LEVELS.add(new Level(level + 1, experienceRounded));
        }
        
        // Setup rewards
    }
    
    private final int level;
    private final long experience;
    
    @NotNull
    private Style style;
    
    private Level(int level, long experience) {
        this.level = level;
        this.experience = experience;
        this.style = Style.style(NamedTextColor.GRAY);
    }
    
    public int getLevel() {
        return level;
    }
    
    public long getExperience() {
        return experience;
    }
    
    @NotNull
    @Override
    public Component asComponent() {
        return Component.empty()
                        .append(PREFIX)
                        .append(Component.text(level, style))
                        .append(SUFFIX);
    }
    
    @NotNull
    @Override
    public Style getStyle() {
        return style;
    }
    
    @Override
    public void setStyle(@NotNull Style style) {
        this.style = style;
    }
    
    @NotNull
    public static Level forExperience(long experience) {
        for (Level level : LEVELS) {
            if (experience >= level.experience) {
                return level;
            }
        }
        
        return LEVELS.getFirst();
    }
    
    @NotNull
    public static Level forLevel(@Range(from = 1, to = HariantConstants.MAX_LEVEL) int level) {
        return LEVELS.get(level - 1);
    }
    
}
