package me.hapyl.hariant.weapon.ability;

import me.hapyl.eterna.module.component.Described;
import me.hapyl.eterna.module.component.Named;
import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.entity.cooldown.Cooldown;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.talent.Response;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public abstract class Ability implements Named, Described, Cooldown {
    
    private final Key key;
    private final Component name;
    
    private @NotNull Component description;
    private @NotNull Tick cooldown;
    
    public Ability(@NotNull Key key, @NotNull Component name) {
        this.key = key;
        this.name = name;
        this.description = Described.defaultValue();
        this.cooldown = Tick.zero();
    }
    
    @Override
    public @NotNull Key getCooldownKey() {
        return key;
    }
    
    @Override
    public @NotNull Tick getCooldown() {
        return cooldown;
    }
    
    @Override
    public void setCooldown(@NotNull Tick cooldown) {
        this.cooldown = cooldown;
    }
    
    @NotNull
    public abstract Response execute(@NotNull HariantPlayer player);
    
    @NotNull
    @Override
    public Component getName() {
        return name;
    }
    
    @NotNull
    @Override
    public Component getDescription() {
        return description;
    }
    
    @Override
    public void setDescription(@NotNull Component description) {
        this.description = description;
    }
    
}
