package me.hapyl.hariant.hero.shark;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.talent.TalentPassive;
import me.hapyl.hariant.util.Icon;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public final class TalentApexPredator extends TalentPassive {
    
    public TalentApexPredator(@NotNull Key key) {
        super(key, Component.text("Apex Predator"), Icon.ofMaterial(Material.REDSTONE));
    }
    
}
