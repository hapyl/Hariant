package me.hapyl.hariant.hero.mage;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.hero.HeroRegistry;
import me.hapyl.hariant.talent.Response;
import me.hapyl.hariant.talent.Talent;
import me.hapyl.hariant.talent.TalentContext;
import me.hapyl.hariant.talent.field.DisplayField;
import me.hapyl.hariant.talent.target.TalentTarget;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public abstract class TalentMage extends Talent {
    
    @DisplayField private final Decimal soulCost;
    
    public TalentMage(@NotNull Key key, @NotNull Component name, @NotNull Icon icon, int soulCost) {
        super(key, name, icon);
        
        this.soulCost = Decimal.ofValue(soulCost);
    }
    
    @NotNull
    @Override
    public abstract TalentTarget target(@NotNull HariantPlayer player);
    
    @NotNull
    public abstract Response execute1(@NotNull HariantPlayer player, @NotNull TalentContext context);
    
    @NotNull
    @Override
    public Response execute(@NotNull HariantPlayer player, @NotNull TalentContext context) {
        final HeroDataMage heroData = player.getHeroData(HeroRegistry.MAGE, HeroDataMage::new);
        final int soulCost = this.soulCost.intValue();
        
        if (heroData.getSouls() < soulCost) {
            return Response.error("Not enough souls!");
        }
        
        final Response response = this.execute1(player, context);
        
        // Decrement souls if response isn't error
        if (!response.isError()) {
            heroData.decrementSouls(soulCost);
        }
        
        return response;
    }
    
    
}
