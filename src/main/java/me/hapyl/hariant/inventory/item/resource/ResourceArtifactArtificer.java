package me.hapyl.hariant.inventory.item.resource;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.inventory.item.Rarity;
import me.hapyl.hariant.inventory.item.Resource;
import me.hapyl.hariant.util.Icon;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public final class ResourceArtifactArtificer extends Resource {
    
    public ResourceArtifactArtificer(@NotNull Key key) {
        super(key, Component.text("Artifact Artificer"), Icon.ofTexture("f26dad74b2bab105cb68c94eb3be32f5dbda42eab944b6ed9e803136f8f619bc"));
        
        setDescription(Component.text("Used for artifact artificing."));
        
        setRarity(Rarity.FIVE_STAR);
    }
    
    @Override
    public int maxStackSize() {
        return 100;
    }
    
}