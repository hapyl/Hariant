package me.hapyl.hariant.entity.vanilla;

import me.hapyl.hariant.attribute.instance.Attributes;
import me.hapyl.hariant.entity.HariantEntity;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class VanillaEntity<E extends LivingEntity> extends HariantEntity {
    
    private final Component name;
    private final Component headComponent;
    
    VanillaEntity(@NotNull E entity, @NotNull Component name, @NotNull Component headComponent, @NotNull Attributes attributes) {
        super(entity, attributes);
        
        this.name = name;
        this.headComponent = headComponent;
    }
    
    @SuppressWarnings("unchecked")
    public @NotNull E getEntity() {
        return (E) entity;
    }
    
    @Override
    public @NotNull Component getName() {
        return name;
    }
    
    @Override
    public @NotNull Component asHeadComponent() {
        return headComponent;
    }
    
}
