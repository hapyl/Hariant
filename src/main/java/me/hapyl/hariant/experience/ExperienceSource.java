package me.hapyl.hariant.experience;

import me.hapyl.eterna.module.component.Named;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public interface ExperienceSource extends Named {
    
    @Override
    @NotNull Component getName();
    
    long getExperience();
    
    static @NotNull ExperienceSource create(@NotNull Component name, long experience) {
        return new ExperienceSourceImpl(name, experience);
    }
    
}
