package me.hapyl.hariant.util;

import me.hapyl.eterna.module.location.LocationHelper;
import me.hapyl.eterna.module.npc.Npc;
import me.hapyl.eterna.module.npc.NpcProperties;
import me.hapyl.eterna.module.npc.appearance.AppearanceBuilder;
import me.hapyl.eterna.module.reflect.Skin;
import me.hapyl.eterna.module.util.Removable;
import net.kyori.adventure.text.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

public class RiptideFx implements Removable {
    
    private static final float PITCH = 90f;
    private static final double Y_OFFSET = 2.0;
    
    private static final EntityDataAccessor<Byte> ACCESSOR_RIPTIDE = EntityDataSerializers.BYTE.createAccessor(8);
    
    private final double baseY;
    private final Npc npc;
    
    public RiptideFx(@NotNull Location location, @Range(from = 0, to = 10) double scale) {
        this.baseY = location.getY();
        this.npc = new Npc(scaleLocation(LocationHelper.copyOf(location), scale).setRotation(0, PITCH), Component.empty(), AppearanceBuilder.ofMannequin(Skin.empty()));
        
        final NpcProperties properties = npc.getProperties();
        properties.setCollidable(false);
        
        this.npc.setInvisible(true);
        
        // Make the entity riptide
        this.npc.editEntityData(data -> {
            data.set(ACCESSOR_RIPTIDE, (byte) 0x04);
        });
        
        this.npc.showAll();
    }
    
    public void scale(double newScale) {
        npc.setAttribute(Attributes.SCALE, newScale);
        npc.updateAttributes();
        
        // Update location
        npc.setLocation(this.scaleLocation(npc.getLocation(), newScale));
    }
    
    @Override
    public void remove() {
        npc.dispose();
    }
    
    private @NotNull Location scaleLocation(@NotNull Location location, double scale) {
        location.setY(baseY + Y_OFFSET * scale);
        return location;
    }
    
}
