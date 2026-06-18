package me.hapyl.hariant.npc;

import me.hapyl.eterna.module.npc.Npc;
import me.hapyl.eterna.module.npc.appearance.Appearance;
import me.hapyl.eterna.module.npc.appearance.AppearanceBuilder;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.registry.Keyed;
import me.hapyl.hariant.registry.Registrable;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class HariantNpc extends Npc implements Keyed, Registrable {
    
    private final Key key;
    
    public HariantNpc(
            @NotNull Key key,
            @NotNull Location location,
            @NotNull Component defaultName,
            @NotNull AppearanceBuilder<? extends Appearance> builder
    ) {
        super(location, defaultName, builder);
        
        this.key = key;
    }
    
    public boolean shouldCreate(@NotNull Player player) {
        return true;
    }
    
    @NotNull
    @Override
    public final Key getKey() {
        return key;
    }
    
    @Override
    public void onRegister() {
    }
    
    @Override
    public void onUnregister() {
    }
    
}