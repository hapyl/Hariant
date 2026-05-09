package me.hapyl.hariant.hero.troll;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.damage.DamageSource;
import me.hapyl.hariant.entity.damage.DamageSourceIdentity;
import me.hapyl.hariant.entity.damage.DeathMessage;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.event.HariantDamageEvent;
import me.hapyl.hariant.hero.HeroRegistry;
import me.hapyl.hariant.talent.TalentPassive;
import me.hapyl.hariant.talent.field.DisplayField;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public class TalentLastLaugh extends TalentPassive implements Listener {
    
    @DisplayField private final Decimal chance = Decimal.ofPercentage(1);
    
    private final DeathMessage deathMessage = DeathMessage.create("{player} was trolled to death [by {killer}]");
    
    public TalentLastLaugh(@NotNull Key key) {
        super(key, Component.text("Last Laugh"), Icon.ofMaterial(Material.BLAZE_POWDER));
        
        setDescription(
                Component.empty()
                         .append(Component.text("Your attacks have "))
                         .append(chance)
                         .append(Component.text(" chance to instantly kill the enemy."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("The chance is increased based on your ", NamedTextColor.DARK_GRAY))
                         .append(AttributeType.PHYSICAL_DAMAGE_BONUS)
                         .append(Component.text(".", NamedTextColor.DARK_GRAY))
        );
    }
    
    @EventHandler
    public void handleHariantDamageEvent(HariantDamageEvent ev) {
        final HariantEntity attacker = ev.getAttacker();
        
        if (!(attacker instanceof HariantPlayer player)) {
            return;
        }
        
        if (!player.getHero().equals(HeroRegistry.TROLL)) {
            return;
        }
        
        final double chance = calculateChance(player);
        
        if (!player.getRandom().chance(chance)) {
            return;
        }
        
        ev.getEntity().die(DamageSource.death(DamageSourceIdentity.create(this, deathMessage)).build());
    }
    
    public double calculateChance(@NotNull HariantPlayer player) {
        final double physicalDamageBonus = player.getAttributes().get(AttributeType.PHYSICAL_DAMAGE_BONUS);
        
        return chance.doubleValue() * (1 + physicalDamageBonus / 100);
    }
    
}
