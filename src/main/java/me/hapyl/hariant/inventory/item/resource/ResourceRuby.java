package me.hapyl.hariant.inventory.item.resource;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.database.PlayerDatabase;
import me.hapyl.hariant.inventory.item.Resource;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.Prefixed;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;

public class ResourceRuby extends Resource implements Prefixed {
    
    private static final Component PREFIX = Component.text("\uD83D\uDC8E", Colors.RESOURCE_RUBY);
    
    public ResourceRuby(@NotNull Key key) {
        super(key, Component.text("Ruby"), Icon.ofTexture("4d7318c21c2a53693222cd60191518a8e2a885956dd0823b6142c9ce77d13811"));
        
        this.setDescription(Component.empty());
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
