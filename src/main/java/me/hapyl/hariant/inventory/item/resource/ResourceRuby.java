package me.hapyl.hariant.inventory.item.resource;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.inventory.item.Rarity;
import me.hapyl.hariant.inventory.item.Resource;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.Prefixed;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public final class ResourceRuby extends Resource implements Prefixed, ResourceUnlocksHero {
    
    public static final Component PREFIX = Component.text("\uD83D\uDC8E", Colors.RESOURCE_RUBY);
    
    public ResourceRuby(@NotNull Key key) {
        super(key, Component.text("Ruby"), Icon.ofTexture("94ebf5606ac83a74c1cb9f7e5604be3ce297892a4ca1005ce510ca25eba2888"));
        
        this.setRarity(Rarity.FIVE_STAR);
        
        this.setDescription(Component.text("A resource that can be spent on unlocking unique rewards and heroes."));
        this.setFlavorText(Component.text("A flawless, shiny ruby gem. Whoever cut it sure knows how to do their job."));
    }
    
    @Override
    public int maxStackSize() {
        return 1_000_000_000;
    }
    
    @Override
    public @NotNull Component format(long amount) {
        return PREFIX.appendSpace().append(super.format(amount));
    }
    
    @NotNull
    @Override
    public Component getPrefix() {
        return PREFIX;
    }
    
    @Override
    public int unlockAmount() {
        return 100;
    }
    
}