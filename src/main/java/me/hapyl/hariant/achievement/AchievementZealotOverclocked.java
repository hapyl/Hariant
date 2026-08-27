package me.hapyl.hariant.achievement;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.text.NumberToWord;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.entity.damage.DamageSource;
import me.hapyl.hariant.entity.damage.DamageType;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.event.HariantDamageEvent;
import me.hapyl.hariant.hero.HeroRegistry;
import me.hapyl.hariant.hero.zealot.HeroDataZealot;
import me.hapyl.hariant.hero.zealot.TalentPsionicOverload;
import me.hapyl.hariant.talent.TalentRegistry;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public final class AchievementZealotOverclocked extends AchievementHeroImpl implements Listener {
    
    private static final int NUMBER_OF_FEROCITY = 12;
    
    AchievementZealotOverclocked(@NotNull Key key) {
        super(
                key,
                1,
                Component.text("Overclocked"),
                Component.empty()
                         .append(Component.text("Trigger "))
                         .append(AttributeType.FEROCITY)
                         .append(Component.text(" DMG "))
                         .append(Component.text(NumberToWord.toWord(NUMBER_OF_FEROCITY).toLowerCase(), Colors.NUMBER))
                         .append(Component.text(" times while under the enhancements of "))
                         .append(TalentRegistry.PSIONIC_OVERLOAD)
                         .append(Component.text(".")),
                HeroRegistry.ZEALOT
        );
        
        setTier(AchievementTier.TIER_2);
    }
    
    @Override
    public int uniqueIdCounterValue() {
        return NUMBER_OF_FEROCITY;
    }
    
    @EventHandler
    public void handleHariantDamageEvent(HariantDamageEvent ev) {
        if (!(ev.getAttacker() instanceof HariantPlayer player)) {
            return;
        }
        
        final DamageSource damageSource = ev.getDamageSource();
        
        if (damageSource.getDamageType() != DamageType.FEROCITY) {
            return;
        }
        
        if (!player.getAttributes().hasModifier(TalentPsionicOverload.ModifierPsionicOverload.class)) {
            return;
        }
        
        player.getHeroData(HeroRegistry.ZEALOT, HeroDataZealot::new).numberOfFerocityTriggers.count(() -> 0);
    }
    
}
