package me.hapyl.hariant.hero.mage;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.damage.mutator.DamageMutator;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.event.HariantDamageComputeEvent;
import me.hapyl.hariant.hero.HeroRegistry;
import me.hapyl.hariant.talent.TalentPassive;
import me.hapyl.hariant.util.Icon;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public final class TalentSoulHarvest extends TalentPassive implements Listener {
    
    private static final String TALENT_NAME = "Aether Contamination";
    
    public TalentSoulHarvest(@NotNull Key key) {
        super(key, Component.text(TALENT_NAME), Icon.ofMaterial(Material.SCULK_VEIN));
        
        setDescription(
                Component.empty()
                         .append(Component.text("Dealing DMG to enemies affected by "))
                         .appendNewline()
                         .append(ElementType.AETHER)
                         .append(Component.text(" is increased based on the amount of "))
                         .append(Component.text("anomaly", Colors.ELEMENT_AETHER))
                         .append(Component.text(" and your "))
                         .append(AttributeType.ELEMENTAL_MASTERY)
                         .append(Component.text("."))
        );
    }
    
    public double calculateDamage(@NotNull HariantPlayer player, @NotNull HariantEntity entity) {
        final double aetherUnits = entity.getElementalUnit(ElementType.AETHER);
        final double elementalMastery = player.getAttributes().get(AttributeType.ELEMENTAL_MASTERY);
        
        return 1 + aetherUnits / 5000 + elementalMastery / 1000;
    }
    
    @EventHandler
    public void handleHariantDamageEvent(HariantDamageComputeEvent ev) {
        final HariantEntity attacker = ev.getAttacker();
        
        if (!(attacker instanceof HariantPlayer player)) {
            return;
        }
        
        if (!player.getHero().equals(HeroRegistry.MAGE)) {
            return;
        }
        
        ev.mutateDamage(() -> TALENT_NAME, DamageMutator.multiply(), this.calculateDamage(player, ev.getEntity()));
    }
    
}