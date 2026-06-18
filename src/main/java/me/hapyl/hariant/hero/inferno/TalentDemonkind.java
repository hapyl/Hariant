package me.hapyl.hariant.hero.inferno;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.damage.DamageType;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.event.HariantDamageEvent;
import me.hapyl.hariant.hero.HeroRegistry;
import me.hapyl.hariant.talent.TalentPassive;
import me.hapyl.hariant.term.EnumTerminology;
import me.hapyl.hariant.util.Icon;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public final class TalentDemonkind extends TalentPassive implements Listener {
    
    public TalentDemonkind(@NotNull Key key) {
        super(key, Component.text("Demonkind"), Icon.ofMaterial(Material.ANCIENT_DEBRIS));
        
        setDescription(
                Component.empty()
                         .append(Component.text("As a kin of "))
                         .append(Component.text("demons", Colors.HELL))
                         .append(Component.text(", you have fully adapted to the harshest environments."))
                         .appendNewline()
                         .appendNewline()
                         .append(dot(
                                 Component.empty()
                                          .append(Component.text("Your attacks deal "))
                                          .append(EnumTerminology.TRUE_DAMAGE)
                                          .append(Component.text(" but cannot "))
                                          .append(Component.text("crit", Colors.ATTRIBUTE_CRIT_DAMAGE))
                                          .append(Component.text("."))
                         ))
                         .appendNewline()
                         .append(dot(
                                 Component.empty()
                                          .append(Component.text("You are immune to environment "))
                                          .append(ElementType.FIRE.asComponentDamage())
                                          .append(Component.text(", and your "))
                                          .append(AttributeType.FIRE_RESISTANCE)
                                          .append(Component.text(" is greatly increased."))
                         ))
                         .appendNewline()
                         .append(dot(Component.empty()
                                              .append(Component.text("Your "))
                                              .append(AttributeType.EFFECT_RESISTANCE)
                                              .append(Component.text(" and "))
                                              .appendNewline()
                                              .append(AttributeType.KNOCKBACK_RESISTANCE)
                                              .append(Component.text(" is greatly increased."))
                         ))
        );
    }
    
    @EventHandler
    public void handleHariantDamageEvent(HariantDamageEvent ev) {
        final HariantEntity entity = ev.getEntity();
        
        if (!(entity instanceof HariantPlayer player)) {
            return;
        }
        
        if (!player.getHero().equals(HeroRegistry.INFERNO)) {
            return;
        }
        
        // Cancel all FIRE damage
        if (ev.getElementType() == ElementType.FIRE && ev.getDamageType() == DamageType.ENVIRONMENT) {
            ev.setCancelled(true, true);
        }
    }
    
    @NotNull
    private static Component dot(@NotNull Component component) {
        return Component.empty()
                        .append(Component.text("● ", Colors.DARK_GRAY))
                        .append(component);
    }
    
}
