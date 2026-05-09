package me.hapyl.hariant.entity.damage;

import me.hapyl.hariant.entity.HariantEntity;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public class AssistSourceImpl implements AssistSource {
    
    private final HariantEntity source;
    private final Component name;
    
    AssistSourceImpl(@NotNull HariantEntity source, @NotNull Component name) {
        this.source = source;
        this.name = name;
    }
    
    @Override
    @NotNull
    public HariantEntity source() {
        return source;
    }
    
    @Override
    @NotNull
    public Component getName() {
        return name;
    }
    
}
