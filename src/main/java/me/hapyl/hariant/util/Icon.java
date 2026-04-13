package me.hapyl.hariant.util;

import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.hariant.HariantConstants;
import me.hapyl.hariant.inventory.item.ItemCreator;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public interface Icon extends ItemCreator {
    
    @NotNull
    @Override
    ItemBuilder createBuilder();
    
    @NotNull
    static Icon ofMaterial(@NotNull Material material) {
        return () -> createBuilder(material);
    }
    
    @NotNull
    static Icon ofMaterial(@NotNull Material material, @NotNull Consumer<ItemBuilder> consumer) {
        return () -> {
            final ItemBuilder builder = createBuilder(material);
            consumer.accept(builder);
            
            return builder;
        };
    }
    
    @NotNull
    static Icon ofTexture(@NotNull String texture) {
        return () -> ItemBuilder.playerHead(texture).unsetComponents();
    }
    
    @NotNull
    static Icon ofTemporaryTexture() {
        class Holder {
            private static final String TEXTURE = "c807442b3643f5585cbd2229bc1a7788f6f25662bd765b9eab28b6eb6bdb82a4";
        }
        
        return ofTexture(Holder.TEXTURE);
    }
    
    @NotNull
    private static ItemBuilder createBuilder(@NotNull Material material) {
        return new ItemBuilder(HariantConstants.DUMMY_MATERIAL).setItemModel(material).unsetComponents();
    }
    
}
