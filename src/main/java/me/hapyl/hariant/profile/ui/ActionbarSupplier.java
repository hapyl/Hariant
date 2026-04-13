package me.hapyl.hariant.profile.ui;

import me.hapyl.hariant.entity.player.HariantPlayer;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ActionbarSupplier {
    
    @NotNull
    List<Component> supplyActionbar(@NotNull HariantPlayer player);
    
}
