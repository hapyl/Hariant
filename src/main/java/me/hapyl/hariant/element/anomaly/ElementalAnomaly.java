package me.hapyl.hariant.element.anomaly;

import me.hapyl.eterna.module.component.Described;
import me.hapyl.eterna.module.component.Named;
import me.hapyl.eterna.module.component.Styled;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.registry.Keyed;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.ui.ComponentDisplayable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.Style;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ElementalAnomaly extends Keyed, Named, Described, Styled, ComponentLike, ComponentDisplayable {
    
    @Override
    @NotNull
    Key getKey();
    
    @Override
    @NotNull
    Component getName();
    
    @NotNull
    @Override
    Component getDescription();
    
    @Override
    @NotNull
    Style getStyle();
    
    void trigger(@NotNull HariantEntity entity, @Nullable HariantEntity source);
    
    @Override
    void display(@NotNull Location location);
}
