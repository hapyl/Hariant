package me.hapyl.hariant.hero.bounty_hunter;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.damage.DamageType;
import me.hapyl.hariant.entity.damage.mutator.DamageMutator;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.event.HariantDamageComputeEvent;
import me.hapyl.hariant.hero.HeroRegistry;
import me.hapyl.hariant.talent.TalentPassive;
import me.hapyl.hariant.util.field.DisplayField;
import me.hapyl.hariant.util.ComponentFormatter;
import me.hapyl.hariant.util.Contains;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class TalentBackstabber extends TalentPassive implements Listener {
    
    private final @DisplayField Decimal damageMultiplier = Decimal.ofPercentage(25);
    private final @DisplayField Decimal backstabThreshold = Decimal.ofAngle(135);
    
    private final @DisplayField AllowedDamageTypes allowedDamageTypes = new AllowedDamageTypes(List.of(
            DamageType.MELEE,
            DamageType.TALENT,
            DamageType.ULTIMATE
    ));
    
    private final double backstabThresholdScalar = Math.cos(Math.toRadians(backstabThreshold.doubleValue()));
    
    public TalentBackstabber(@NotNull Key key) {
        super(key, Component.text("Backstabber"), Icon.ofMaterial(Material.IRON_NAUTILUS_ARMOR));
        
        setDescription(
                Component.empty()
                         .append(Component.text("Most "))
                         .append(Component.text("DMG", Colors.RED))
                         .append(Component.text(" dealt from behind an enemy is increased by "))
                         .append(damageMultiplier)
                         .append(Component.text("."))
        );
    }
    
    @EventHandler(ignoreCancelled = true)
    public void handleHariantDamageComputeEvent(HariantDamageComputeEvent ev) {
        if (!(ev.getAttacker() instanceof HariantPlayer player)) {
            return;
        }
        
        if (!player.getHero().equals(HeroRegistry.BOUNTY_HUNTER)) {
            return;
        }
        
        if (!allowedDamageTypes.contains(ev.getDamageType())) {
            return;
        }
        
        final HariantEntity entity = ev.getEntity();
        
        // Check whether the attack is roughly behind the entity
        final Location entityLocation = entity.getLocation();
        final Vector towardsPlayer = player.getLocation().toVector().subtract(entityLocation.toVector()).setY(0).normalize();
        
        final double dotProduct = towardsPlayer.dot(entityLocation.getDirection().setY(0).normalize());
        
        if (dotProduct > backstabThresholdScalar) {
            return;
        }
        
        ev.mutateDamage(this, DamageMutator.multiply(), 1 + damageMultiplier.doubleValue());
    }
    
    public static final class AllowedDamageTypes implements ComponentFormatter, Contains<DamageType> {
        
        private final List<? extends DamageType> allowedDamageTypes;
        private final Component component;
        
        AllowedDamageTypes(@NotNull List<? extends DamageType> allowedDamageTypes) {
            this.allowedDamageTypes = allowedDamageTypes;
            this.component = allowedDamageTypes.stream().map(DamageType::getName).collect(Component.toComponent(Component.text(", ")));
        }
        
        @Override
        public @NotNull Component format() {
            return component;
        }
        
        @Override
        public boolean contains(@NotNull DamageType damageType) {
            return allowedDamageTypes.contains(damageType);
        }
        
    }
    
}