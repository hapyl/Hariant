package me.hapyl.hariant.hero.mage;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.cooldown.Cooldown;
import me.hapyl.hariant.entity.damage.DamageType;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.event.HariantDamageEvent;
import me.hapyl.hariant.hero.HeroRegistry;
import me.hapyl.hariant.talent.TalentPassive;
import me.hapyl.hariant.talent.field.DisplayField;
import me.hapyl.hariant.util.Definition;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public final class TalentSoulHarvest extends TalentPassive implements Listener {
    
    @DisplayField public final Decimal maximumSouls = Decimal.ofValue(20);
    @DisplayField public final Decimal startingSouls = Decimal.ofValue(5);
    
    @DisplayField private final Cooldown harvestCooldown = Cooldown.ofSeconds(Key.ofString("soul_harvest"), 0.25f);
    
    @DisplayField private final Decimal soulHarvest = Decimal.ofValue(1);
    @DisplayField private final Decimal soulHarvestElementalMasteryIncreasePerOnePoint = Decimal.ofPercentage(0.25);
    
    public TalentSoulHarvest(@NotNull Key key) {
        super(key, Component.text("Soul Harvest"), Icon.ofMaterial(Material.SKELETON_SPAWN_EGG));
        
        setDescription(
                Component.empty()
                         .append(Component.text("Dealing "))
                         .append(DamageType.MELEE)
                         .append(Component.text(" grants a "))
                         .append(Definition.SOUL_FRAGMENT)
                         .append(Component.text(", which is a resource used by your talents and weapon."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("Each point of "))
                         .append(AttributeType.ELEMENTAL_MASTERY)
                         .append(Component.text(" increases the soul yield by "))
                         .append(soulHarvestElementalMasteryIncreasePerOnePoint)
                         .append(Component.text("."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("You start with ", Colors.DARK_GRAY))
                         .append(Component.text(startingSouls.intValue(), Colors.DARK_GRAY))
                         .append(Component.text(" souls.", Colors.DARK_GRAY))
        );
    }
    
    public int calculateSoulHarvest(@NotNull HariantPlayer player) {
        final double elementalMastery = player.getAttributes().get(AttributeType.ELEMENTAL_MASTERY);
        
        return (int) (soulHarvest.intValue() * (1 + elementalMastery * soulHarvestElementalMasteryIncreasePerOnePoint.doubleValue()));
    }
    
    @EventHandler
    public void handleHariantDamageEvent(HariantDamageEvent ev) {
        final HariantEntity attacker = ev.getAttacker();
        
        if (!(attacker instanceof HariantPlayer player) || !player.getHero().equals(HeroRegistry.MAGE) || ev.getDamageType() != DamageType.MELEE) {
            return;
        }
        
        if (player.hasCooldown(harvestCooldown)) {
            return;
        }
        
        final HeroDataMage heroData = player.getHeroData(HeroRegistry.MAGE, HeroDataMage::new);
        final int soulHarvest = calculateSoulHarvest(player);
        
        heroData.incrementSouls(soulHarvest);
        player.setCooldown(harvestCooldown);
    }
    
}