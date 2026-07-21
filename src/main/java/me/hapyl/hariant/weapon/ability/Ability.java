package me.hapyl.hariant.weapon.ability;

import me.hapyl.eterna.module.component.Described;
import me.hapyl.eterna.module.component.Named;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.entity.cooldown.HariantCooldown;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.talent.Response;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public abstract class Ability implements Named, Described, HariantCooldown {
    
    private final Key key;
    private final Component name;
    
    private Component description;
    private int cooldown;
    
    public Ability(@NotNull Key key, @NotNull Component name) {
        this.key = key;
        this.name = name;
        this.description = Described.defaultValue();
        this.cooldown = 0;
    }
    
    @Override
    public @NotNull Key getCooldownKey() {
        return key;
    }
    
    @Override
    public int getCooldown() {
        return cooldown;
    }
    
    @Override
    public void setCooldown(int cooldown) {
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
