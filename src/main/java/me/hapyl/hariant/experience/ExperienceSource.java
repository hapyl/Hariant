package me.hapyl.hariant.experience;

import me.hapyl.eterna.module.component.Named;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.jetbrains.annotations.NotNull;

public interface ExperienceSource extends Named, ComponentLike {
    
    @NotNull Component getName();
    
    long getExperience();
    
    @Override
    @NotNull Component asComponent();
    
    static @NotNull ExperienceSource create(@NotNull Component name, long experience) {
        return new ExperienceSourceImpl(name, experience);
    }
    
}
