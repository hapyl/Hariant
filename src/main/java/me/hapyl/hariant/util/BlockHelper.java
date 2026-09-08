package me.hapyl.hariant.util;

import me.hapyl.hariant.Hariant;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public final class BlockHelper {
    
    private static final Set<Tag<Material>> PASSABLE_MATERIALS = Set.of(
            Tag.WOOL_CARPETS,
            Tag.PRESSURE_PLATES,
            createTag("passable", Set.of(
                    Material.MOSS_CARPET,
                    Material.PALE_MOSS_CARPET
            ))
    );
    
    private BlockHelper() {
    }
    
    public static boolean isPassable(@NotNull Block block) {
        // If bukkit passable check returns true, then it's passable
        if (block.isPassable()) {
            return true;
        }
        
        // Check for odd blocks
        final Material blockType = block.getType();
        
        for (Tag<Material> tag : PASSABLE_MATERIALS) {
            if (tag.isTagged(blockType)) {
                return true;
            }
        }
        
        return false;
    }
    
    public static boolean isSolid(@NotNull Block block) {
        if (!block.isSolid()) {
            return false;
        }
        
        // Some blocks are not considered as solid, such as barriers or command blocks
        final Material blockType = block.getType();
        
        return switch (blockType) {
            case BARRIER, COMMAND_BLOCK, REPEATING_COMMAND_BLOCK, CHAIN_COMMAND_BLOCK -> false;
            default -> true;
        };
    }
    
    private static <T extends Keyed> @NotNull Tag<T> createTag(@NotNull String key, @NotNull Set<T> values) {
        return new TagImpl<>(Hariant.createNamespacedKey(key), values);
    }
    
    private static class TagImpl<T extends Keyed> implements Tag<T> {
        
        private final NamespacedKey namespacedKey;
        private final Set<T> tagged;
        
        private TagImpl(@NotNull NamespacedKey namespacedKey, @NotNull Set<T> tagged) {
            this.namespacedKey = namespacedKey;
            this.tagged = tagged;
        }
        
        @Override
        public boolean isTagged(@NotNull T item) {
            return tagged.contains(item);
        }
        
        @Override
        public @NotNull Set<T> getValues() {
            return tagged;
        }
        
        @Override
        public @NotNull NamespacedKey getKey() {
            return namespacedKey;
        }
        
    }
    
}
