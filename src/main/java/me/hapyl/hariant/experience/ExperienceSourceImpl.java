package me.hapyl.hariant.experience;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public class ExperienceSourceImpl implements ExperienceSource {
    
    private final Component name;
    private final long experience;
    
    ExperienceSourceImpl(@NotNull Component name, long experience) {
        this.name = name;
        this.experience = experience;
    }
    
    @Override
    public @NotNull Component getName() {
        return name;
    }
    
    @Override
    public long getExperience() {
        return experience;
    }
    
}