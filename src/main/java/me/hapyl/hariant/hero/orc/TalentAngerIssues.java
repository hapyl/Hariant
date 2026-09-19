package me.hapyl.hariant.hero.orc;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.event.HariantDamageEvent;
import me.hapyl.hariant.hero.HeroRegistry;
import me.hapyl.hariant.talent.TalentPassive;
import me.hapyl.hariant.talent.TalentRegistry;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.decimal.Decimal;
import me.hapyl.hariant.util.field.DisplayField;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public final class TalentAngerIssues extends TalentPassive implements Listener {
    
    private final @DisplayField Decimal angerIssuesDamageThresholdOfMaxHealth = Decimal.ofPercentage(50);
    private final @DisplayField Decimal angerIssuesResetThreshold = Decimal.ofSeconds(5);
    
    private final @DisplayField Decimal berserkDuration = Decimal.ofSeconds(5);
    
    public TalentAngerIssues(@NotNull Key key) {
        super(key, Component.text("Anger Issues"), Icon.ofMaterial(Material.OMINOUS_BOTTLE));
        
        setCooldownSeconds(10);
        
        setDescription(
                Component.empty()
                         .append(Component.text("Taking continuous "))
                         .append(Component.text("DMG", Colors.RED))
                         .append(Component.text(" angers you, making you briefly enter "))
                         .append(TalentBerserk.COMPONENT_BERSERK)
                         .append(Component.text("."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("This effect can only trigger once every ", Colors.DARK_GRAY))
                         .append(this.getCooldownFormatted().color(Colors.DARK_GRAY))
                         .append(Component.text(".", Colors.DARK_GRAY))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("Additionally, your hard skin greatly increases your "))
                         .append(AttributeType.EFFECT_RESISTANCE)
                         .append(Component.text(" and "))
                         .appendNewline()
                         .append(AttributeType.TOXIC_RESISTANCE)
                         .append(Component.text("."))
        );
    }
    
    @EventHandler
    public void handleHariantDamageEvent(HariantDamageEvent ev) {
        if (!(ev.getEntity() instanceof HariantPlayer player)) {
            return;
        }
        
        if (!player.getHero().equals(HeroRegistry.ORC)) {
            return;
        }
        
        // If on cooldown, return
        if (player.hasCooldown(this)) {
            return;
        }
        
        // If player already has berserk, don't trigger to not override existing berserk that is likely from ultimate
        if (TalentRegistry.BERSERK.hasBerserk(player)) {
            return;
        }
        
        final HeroDataOrc heroData = player.getHeroData(HeroRegistry.ORC, HeroDataOrc::new);
        
        // Enter berserk
        if (heroData.angerIssues.incrementDamageTaken(ev.getDamage())) {
            TalentRegistry.BERSERK.enterBerserk(player, berserkDuration.intValue());
            
            // Start cooldown
            player.setCooldown(this);
        }
    }
    
    public @NotNull AngerIssues createAngerIssues(@NotNull HariantPlayer player) {
        return new AngerIssues(player, player.getMaxHealth() * angerIssuesDamageThresholdOfMaxHealth.doubleValue());
    }
    
    public class AngerIssues {
        
        private final HariantPlayer player;
        private final double damageTarget;
        
        private double damageTaken;
        private int lastDamageAt;
        
        AngerIssues(@NotNull HariantPlayer player, double damageTarget) {
            this.player = player;
            this.damageTarget = damageTarget;
        }
        
        public boolean incrementDamageTaken(double damage) {
            final int localTicks = player.localTicks();
            
            // Reset damage if last taken damage was long ago
            if (this.lastDamageAt > 0 && localTicks - this.lastDamageAt > angerIssuesResetThreshold.intValue()) {
                this.damageTaken = 0;
            }
            
            // Increment damage taken
            this.damageTaken += damage;
            this.lastDamageAt = localTicks;
            
            // If damage taken is greater than threshold, enter berserk
            if (this.damageTaken >= this.damageTarget) {
                this.damageTaken = 0;
                return true;
            }
            
            return false;
        }
        
    }
    
}
