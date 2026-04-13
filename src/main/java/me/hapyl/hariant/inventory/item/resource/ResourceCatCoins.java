package me.hapyl.hariant.inventory.item.resource;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.database.PlayerDatabase;
import me.hapyl.hariant.inventory.item.Resource;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.Prefixed;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public class ResourceCatCoins extends Resource implements Prefixed {
    
    private static final Component PREFIX = Component.text("⛂", Colors.RESOURCE_CAT_COINS);
    
    public ResourceCatCoins(@NotNull Key key) {
        super(key, Component.text("Catcoins"), Icon.ofTexture("4d7318c21c2a53693222cd60191518a8e2a885956dd0823b6142c9ce77d13811"));
        
        this.setDescription(Component.text("Can be used for various purchases in various stores."));
        this.setFlavorText(Component.text("A shiny golden coin with a face of a legendary cat engraved onto it."));
    }
    
    @Override
    public int maxStackSize() {
        return 1_000_000_000;
    }
    
    @NotNull
    @Override
    public Component format(@NotNull PlayerDatabase database) {
        return PREFIX.appendSpace().append(super.format(database));
    }
    
    @NotNull
    @Override
    public Component getPrefix() {
        return PREFIX;
    }
}
