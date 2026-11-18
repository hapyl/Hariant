package me.hapyl.hariant.hero.shaman;

import me.hapyl.eterna.module.component.ProgressBar;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.damage.DamageInstance;
import me.hapyl.hariant.entity.damage.DamageType;
import me.hapyl.hariant.entity.damage.mutator.DamageMutator;
import me.hapyl.hariant.entity.heal.HealingSource;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.event.HariantDamageComputeEvent;
import me.hapyl.hariant.event.HariantHealEvent;
import me.hapyl.hariant.hero.HeroRegistry;
import me.hapyl.hariant.talent.TalentPassive;
import me.hapyl.hariant.team.EnumTeam;
import me.hapyl.hariant.util.ComponentFormatter;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.decimal.Decimal;
import me.hapyl.hariant.util.field.DisplayField;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public final class TalentOverheal extends TalentPassive implements Listener {
    
    public static final int MAXIMUM_OVERHEAL = 10;
    
    public static final String STRING_OVERHEAL = "⚕";
    public static final Component COMPONENT_OVERHEAL = Component.text(STRING_OVERHEAL + " Overheal", Colors.OVERHEAL);
    
    public static final ProgressBar PROGRESS_BAR = new ProgressBar(STRING_OVERHEAL, MAXIMUM_OVERHEAL, Style.style(Colors.OVERHEAL));
    
    private final @DisplayField Decimal overhealConversion = Decimal.ofValue(50);
    private final @DisplayField Decimal maximumOverhealStacks = Decimal.ofValue(MAXIMUM_OVERHEAL);
    
    private final @DisplayField Decimal damageIncreaseMaxRadius = Decimal.ofValue(20);
    private final @DisplayField DamageIncrease damageIncrease = new DamageIncrease(500, 100, 4);
    
    public TalentOverheal(@NotNull Key key) {
        super(key, Component.text("Overheal"), Icon.ofMaterial(Material.GLISTERING_MELON_SLICE));
        
        setDescription(
                Component.empty()
                         .append(Component.text("Healing a teammate (excluding self), whole health is "))
                         .append(Component.text("full", Colors.GREEN))
                         .append(Component.text(" converts a portion of the overflow healing into  "))
                         .append(COMPONENT_OVERHEAL)
                         .append(Component.text("."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("When a nearby teammate deals "))
                         .append(DamageType.MELEE)
                         .append(Component.text(" or "))
                         .append(DamageType.RANGED)
                         .append(Component.text(", consume one stack of "))
                         .appendNewline()
                         .append(COMPONENT_OVERHEAL)
                         .append(Component.text(" to increase the "))
                         .append(Component.text("DMG"))
                         .append(Component.text(" dealt."))
        );
    }
    
    @EventHandler(ignoreCancelled = true)
    public void handleHariantDamageCalculationsEvent(HariantDamageComputeEvent ev) {
        final DamageInstance damageInstance = ev.getDamageInstance();
        final HariantEntity attacker = damageInstance.getAttacker();
        
        if (attacker == null) {
            return;
        }
        
        if (!ev.getDamageType().isDirectDamage()) {
            return;
        }
        
        final EnumTeam team = attacker.getTeam().orElse(null);
        
        if (team == null) {
            return;
        }
        
        final HariantPlayer shaman = team.getPlayers()
                                         .filter(player -> player.getHero().equals(HeroRegistry.SHAMAN))
                                         .findAny()
                                         .orElse(null);
        
        if (shaman == null) {
            return;
        }
        
        // Check whether shaman is close enough
        if (attacker.distanceToSquared(shaman) > damageIncreaseMaxRadius.doubleValueSquared()) {
            return;
        }
        
        final HeroDataShaman heroData = shaman.getHeroData(HeroRegistry.SHAMAN, HeroDataShaman::new);
        
        // `consumeOverheal` returns false if shaman doesn't have any Overheal stacks
        if (!heroData.consumeOverheal()) {
            return;
        }
        
        // Calculate damage increase
        damageInstance.mutateDamage(() -> "Overheal (%s)".formatted(shaman), DamageMutator.multiply(), damageIncrease.calculateDamageIncrease(shaman));
    }
    
    @EventHandler(ignoreCancelled = true)
    public void handleHariantHealEvent(HariantHealEvent ev) {
        final HealingSource healingSource = ev.getHealingSource();
        
        if (!(healingSource.getHealer() instanceof HariantPlayer player)) {
            return;
        }
        
        if (!player.getHero().equals(HeroRegistry.SHAMAN)) {
            return;
        }
        
        // Don't count self-healing
        if (ev.getEntity().equals(player)) {
            return;
        }
        
        // Convert overheal into stacks
        final double overheal = healingSource.getAmount() - ev.getActualHealing();
        final int overhealStacks = Math.floorDiv((int) overheal, overhealConversion.intValue());
        
        if (overhealStacks <= 0) {
            return;
        }
        
        player.getHeroData(HeroRegistry.SHAMAN, HeroDataShaman::new).increseOverheal(overheal, overhealStacks);
    }
    
    public static class DamageIncrease implements ComponentFormatter {
        
        private final double minimumHealth;
        private final double stackPerMaxHealth;
        private final double damageBonusPerStack;
        
        private final Component component;
        
        DamageIncrease(double minimumHealth, double stackPerMaxHealth, double damageBonus) {
            this.minimumHealth = minimumHealth;
            this.stackPerMaxHealth = stackPerMaxHealth;
            this.damageBonusPerStack = damageBonus;
            
            this.component = Component.empty()
                                      .append(Component.text("("))
                                      .append(AttributeType.MAX_HEALTH.getName())
                                      .append(Component.text(" - "))
                                      .append(Component.text(")"))
                                      .append(Component.text(minimumHealth))
                                      .append(Component.text(" // "))
                                      .append(Component.text(stackPerMaxHealth))
                                      .append(Component.text(" * "))
                                      .append(Component.text(damageBonusPerStack / 100));
        }
        
        @Override
        public @NotNull Component format() {
            return component;
        }
        
        public double calculateDamageIncrease(@NotNull HariantPlayer shaman) {
            final double maxHealth = shaman.getAttributes().get(AttributeType.MAX_HEALTH);
            
            return 1 + (int) (Math.max(0, maxHealth - minimumHealth) / stackPerMaxHealth) * damageBonusPerStack / 100;
        }
    }
    
}
