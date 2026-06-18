package me.hapyl.hariant.inventory.drop;

import me.hapyl.eterna.module.component.Named;
import me.hapyl.eterna.module.component.Styled;
import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.util.ComparableOrdinal;
import me.hapyl.hariant.util.Icon;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;

public enum DropTier implements Named, Styled, Icon, ComponentLike, ComparableOrdinal<DropTier> {
    
    GUARANTEED(Component.text("ɢᴜᴀʀᴀɴᴛᴇᴇᴅ"), Colors.DROP_CHANCE_GUARANTEED, "21865de1a77da86f8423af4ba44101397429d200be42045651212a615cf754f2", 1.0),
    COMMON(Component.text("ᴄᴏᴍᴍᴏɴ"), Colors.DROP_CHANCE_COMMON, "21865de1a77da86f8423af4ba44101397429d200be42045651212a615cf754f2", 0.9),
    UNCOMMON(Component.text("ᴜɴᴄᴏᴍᴍᴏɴ"), Colors.DROP_CHANCE_UNCOMMON, "d16d25953d4b8e6334926e206ac323e77733ed70401fd1a8eef7e2390e40db3d", 0.5),
    RARE(Component.text("ʀᴀʀᴇ"), Colors.DROP_CHANCE_RARE, "64095ac2f42b9b649d00de7d761b56396d4e4dac9382ac02ed15e6ddf901aa4b", 0.3),
    VERY_RARE(Component.text("ᴠᴇʀʏ ʀᴀʀᴇ"), Colors.DROP_CHANCE_VERY_RARE, "debf6be82e823e582ff8f45e01cf51751734d9a776cff514e88ea26d4973bbf0", 0.1),
    RNGESUS(Component.text("ʀɴɢᴇꜱᴜꜱ"), Colors.DROP_CHANCE_RNGESUS, "eca4130f68c71172be032b2fcb2d0ace7ffb143f375709a7f5a2bc84f6f9fbd3", 0.04),
    INSANE(Component.text("ɪɴꜱᴀɴᴇ"), Colors.DROP_CHANCE_INSANE, "d4675158c0767ee508c52d52426cef3a2c2b29b7e487c92953a3823f561d06af", 0.01);
    
    private final Component name;
    private final Style style;
    private final String texture;
    private final double threshold;
    
    DropTier(@NotNull Component name, @NotNull TextColor color, @NotNull String texture, double threshold) {
        this.name = name;
        this.style = Style.style(color);
        this.texture = texture;
        this.threshold = threshold;
    }
    
    @Override
    public @NotNull Component getName() {
        return name;
    }
    
    @Override
    public @NotNull Component asComponent() {
        return name.style(style);
    }
    
    public double threshold() {
        return threshold;
    }
    
    @Override
    public @NotNull ItemBuilder createBuilder() {
        final ItemBuilder builder = ItemBuilder.playerHead(texture);
        builder.setName(name);
        builder.addLore();
        builder.addLore(Component.text("Threshold: %.3f%%".formatted(threshold * 100)));
        
        return builder;
    }
    
    @Override
    public @NotNull Style getStyle() {
        return style;
    }
    
    public static @NotNull DropTier fromDropChance(double dropChance) {
        for (int i = values().length - 1; i >= 0; i--) {
            final DropTier tier = values()[i];
            
            if (dropChance <= tier.threshold) {
                return tier;
            }
        }
        
        return GUARANTEED;
    }
    
}