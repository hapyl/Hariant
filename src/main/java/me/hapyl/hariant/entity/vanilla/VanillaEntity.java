package me.hapyl.hariant.entity.vanilla;

import me.hapyl.eterna.module.component.ComponentList;
import me.hapyl.eterna.module.hologram.Hologram;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.attribute.instance.Attributes;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.HealthStyle;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class VanillaEntity<E extends LivingEntity> extends HariantEntity {
    
    public static final String HEALTH_BAR_CHAR = "|";
    public static final int HEALTH_BAR_LENGTH = 30;
    
    private final Component name;
    private final Component headComponent;
    
    private final Hologram aboveHead;
    
    VanillaEntity(@NotNull E entity, @NotNull Component name, @NotNull Component headComponent, @NotNull Attributes attributes) {
        super(entity, attributes);
        
        this.name = name;
        this.headComponent = headComponent;
        this.aboveHead = Hologram.ofArmorStand(this.getHealthBarLocation());
        this.aboveHead.showAll();
    }
    
    public @NotNull Location getHealthBarLocation() {
        return this.getLocation().add(0, this.getEyeHeight() + 0.5, 0);
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
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        
        // Destroy above head
        aboveHead.dispose();
    }
    
    public @NotNull ComponentList getAboveHead() {
        final double health = this.getFinalHealth();
        final double maxHealth = this.getMaxHealth();
        final int healthBarLength = (int) (health / maxHealth * HEALTH_BAR_LENGTH);
        
        final HealthStyle healthStyle = this.getHealthStyle();
        final ElementType elementType = this.lastAppliedElement();
        
        return ComponentList.of(
                Component.empty()
                         .append(Component.text(HEALTH_BAR_CHAR.repeat(healthBarLength), healthStyle.getHealthStyle()))
                         .append(Component.text(HEALTH_BAR_CHAR.repeat(HEALTH_BAR_LENGTH - healthBarLength), Colors.DARK_GRAY))
                         .appendSpace()
                         .append(this.getHealthFormatted()),
                this.getElementData().getProgressBar(elementType != null ? elementType : ElementType.PHYSICAL)
        );
    }
    
    @Override
    public boolean tick() {
        if (super.tick()) {
            
            // Update above head
            aboveHead.setLines(_ -> getAboveHead());
            aboveHead.teleport(this.getHealthBarLocation());
        }
        
        return false;
    }
    
}
