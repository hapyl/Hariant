package me.hapyl.hariant.hero.orc;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.talent.TalentContext;
import me.hapyl.hariant.talent.target.TalentTarget;
import me.hapyl.hariant.talent.ultimate.TalentUltimate;
import me.hapyl.hariant.talent.ultimate.UltimateResourceType;
import me.hapyl.hariant.task.executor.Executable;
import me.hapyl.hariant.util.Icon;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public class TalentSpiritualCleansing extends TalentUltimate {
    
    public TalentSpiritualCleansing(@NotNull Key key) {
        super(key, Component.text("Spiritual Cleansing"), Icon.ofMaterial(Material.MILK_BUCKET), UltimateResourceType.ENERGY, 60);
    }
    
    @Override
    public @NotNull Executable execute(@NotNull HariantPlayer player, @NotNull TalentContext context, double consumedResource) {
        return null;
    }
    
    @Override
    public @NotNull TalentTarget target(@NotNull HariantPlayer player) {
        return null;
    }
}
