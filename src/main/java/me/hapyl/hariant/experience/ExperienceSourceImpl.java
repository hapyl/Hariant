package me.hapyl.hariant.experience;

import me.hapyl.hariant.Colors;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public class ExperienceSourceImpl implements ExperienceSource {
    
    private final Component name;
    private final long experience;
    private final Component component;
    
    ExperienceSourceImpl(@NotNull Component name, final long experience) {
        this.name = name;
        this.experience = experience;
        this.component = Component.empty()
                                  .append(Component.text("%,d".formatted(experience), Colors.EXPERIENCE))
                                  .append(Component.text(" from ", Colors.GRAY))
                                  .append(name.color(Colors.WHITE));
    }
    
    @Override
    public @NotNull Component getName() {
        return name;
    }
    
    @Override
    public long getExperience() {
        return experience;
    }
    
    @Override
    public @NotNull Component asComponent() {
        return component;
    }
    
}