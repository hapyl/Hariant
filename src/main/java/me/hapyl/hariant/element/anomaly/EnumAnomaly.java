package me.hapyl.hariant.element.anomaly;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.entity.HariantEntity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum EnumAnomaly implements ElementalAnomaly {
    
    BLEED(new ElementalAnomalyBleed()),
    BURN(new ElementalAnomalyBurn()),
    SOAKED(new ElementalAnomalySoaked()),
    FROZEN(new ElementalAnomalyFrozen()),
    
    ;
    
    private final ElementalAnomaly anomaly;
    
    EnumAnomaly(@NotNull ElementalAnomaly anomaly) {
        this.anomaly = anomaly;
    }
    
    @NotNull
    @Override
    public Key getKey() {
        return anomaly.getKey();
    }
    
    @NotNull
    @Override
    public Component getName() {
        return anomaly.getName();
    }
    
    @NotNull
    @Override
    public Component getDescription() {
        return anomaly.getDescription();
    }
    
    @NotNull
    @Override
    public Style getStyle() {
        return anomaly.getStyle();
    }
    
    @Override
    public void trigger(@NotNull HariantEntity entity, @Nullable HariantEntity source) {
        anomaly.trigger(entity, source);
    }
    
    @Override
    public void display(@NotNull Location location) {
        anomaly.display(location);
    }
    
    @NotNull
    @Override
    public Component asComponent() {
        return anomaly.asComponent();
    }
}
