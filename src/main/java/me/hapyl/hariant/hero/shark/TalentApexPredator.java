package me.hapyl.hariant.hero.shark;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.attribute.AttributeScaling;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.element.anomaly.ElementalAnomalyType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.damage.mutator.DamageMutator;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.event.HariantDamageEvent;
import me.hapyl.hariant.event.HariantElementalAnomalyEvent;
import me.hapyl.hariant.hero.HeroRegistry;
import me.hapyl.hariant.talent.TalentPassive;
import me.hapyl.hariant.talent.field.DisplayField;
import me.hapyl.hariant.util.Definition;
import me.hapyl.hariant.util.Icon;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public final class TalentApexPredator extends TalentPassive implements Listener {
    
    private final @DisplayField AttributeScaling damageIncrease = AttributeScaling.create(AttributeType.ELEMENTAL_MASTERY, 25, 10);
    
    public TalentApexPredator(@NotNull Key key) {
        super(key, Component.text("Apex Predator"), Icon.ofMaterial(Material.REDSTONE));
        
        // Match Shark's Bite cooldown
        setDurationSeconds(16);
        
        setDescription(
                Component.empty()
                         .append(Component.text("Triggering "))
                         .append(ElementalAnomalyType.BLEED)
                         .append(Component.text(" anomaly applies "))
                         .appendNewline()
                         .append(Definition.PREY)
                         .append(Component.text(" mark for "))
                         .append(this.getDurationFormatted())
                         .append(Component.text("."))
                         .appendNewline()
                         .appendNewline()
                         .append(Definition.PREY.getName().color(Colors.GOLD))
                         .appendNewline()
                         .append(Component.text("The enemy leaves a "))
                         .append(Component.text("blood trail", Colors.BLOOD))
                         .append(Component.text(" behind and takes an additional "))
                         .append(Component.text("DMG", Colors.RED))
                         .append(Component.text("."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("Only one mark may exist at the time.", Colors.DARK_GRAY))
        );
        
    }
    
    @EventHandler
    public void handleHariantElementalAnomalyEvent(HariantElementalAnomalyEvent ev) {
        final HariantEntity entity = ev.getEntity();
        
        if (ev.getElementalAnomaly() != ElementalAnomalyType.BLEED) {
            return;
        }
        
        if (!(ev.getSource() instanceof HariantPlayer player)) {
            return;
        }
        
        if (!player.getHero().equals(HeroRegistry.SHARK)) {
            return;
        }
        
        player.getHeroData(HeroRegistry.SHARK, HeroDataShark::new).createBloodScent(entity, getDuration());
    }
    
    @EventHandler
    public void handleHariantDamageEvent(HariantDamageEvent ev) {
        final HariantEntity entity = ev.getEntity();
        
        if (!(ev.getAttacker() instanceof HariantPlayer player)) {
            return;
        }
        
        if (!player.getHero().equals(HeroRegistry.SHARK)) {
            return;
        }
        
        final BloodScent bloodScent = player.touchHeroData(HeroRegistry.SHARK, HeroDataShark.class, HeroDataShark::getBloodScent).orElse(null);
        
        if (bloodScent == null || !bloodScent.getEntity().equals(entity)) {
            return;
        }
        
        ev.mutateDamage(() -> "Apex Predator", DamageMutator.multiply(), calculateDamageMultiplier(player));
    }
    
    private double calculateDamageMultiplier(@NotNull HariantPlayer player) {
        return 1 + damageIncrease.getScaledValue(player) / 100;
    }
    
}
