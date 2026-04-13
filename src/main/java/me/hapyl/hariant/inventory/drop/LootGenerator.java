package me.hapyl.hariant.inventory.drop;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface LootGenerator {
    
    @NotNull
    List<Droppable> generateLoot();
    
}
