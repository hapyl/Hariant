package me.hapyl.hariant.hero.archer;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.damage.DamageSource;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.event.HariantProjectileLaunchEvent;
import me.hapyl.hariant.handler.HariantProjectile;
import me.hapyl.hariant.hero.HeroRegistry;
import me.hapyl.hariant.talent.TalentPassive;
import me.hapyl.hariant.talent.field.DisplayField;
import me.hapyl.hariant.task.HariantTickingTask;
import me.hapyl.hariant.task.Scheduler;
import me.hapyl.hariant.util.BaseChance;
import me.hapyl.hariant.util.CommonComponents;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

public final class TalentHawkeye extends TalentPassive implements Listener {
    
    @NotNull
    public static final Component HAWKEYE_ARROW = Component.text("Hawkeye Arrow", TextColor.color(0xFFE600));
    
    @DisplayField private final BaseChance homingChance = BaseChance.baseChance(20);
    @DisplayField private final Decimal homingRadius = Decimal.ofValue(6);
    @DisplayField private final Decimal homingSmoothingFactor = Decimal.ofPercentage(70);
    
    public TalentHawkeye(@NotNull Key key) {
        super(key, Component.text("Hawkeye"), Icon.ofMaterial(Material.ENDER_EYE));
        
        this.setDescription(
                Component.empty()
                         .append(Component.text("Fully charged shots while "))
                         .append(Component.text("sneaking", Colors.WHITE, TextDecoration.UNDERLINED))
                         .append(Component.text(" have "))
                         .append(homingChance)
                         .append(Component.text(" chance to become a "))
                         .append(HAWKEYE_ARROW)
                         .append(Component.text(", that homes at nearby "))
                         .append(CommonComponents.ENEMY.textPlural())
                         .append(Component.text("."))
        
        );
    }
    
    @EventHandler
    public void handleHariantProjectileLaunchEvent(HariantProjectileLaunchEvent ev) {
        final HariantProjectile projectile = ev.getProjectile();
        final DamageSource damageSource = projectile.getDamageSource();
        final HariantEntity attacker = damageSource.getSource();
        
        // Don't pass if `DamageSourceArcherTalent` used because talents should not be homing
        if (!(attacker instanceof HariantPlayer player) || !player.compareHero(HeroRegistry.ARCHER) || damageSource instanceof DamageSourceArcherTalent) {
            return;
        }
        
        final Projectile handle = projectile.getHandle();
        
        if (!(handle instanceof Arrow arrow) || !arrow.isCritical() || !player.getHandle().isSneaking()) {
            return;
        }
        
        if (!homingChance.chance(player)) {
            return;
        }
        
        // Change the element type to ELECTRIC
        projectile.setDamageSource(projectile.getDamageSource().toBuilder().elementType(ElementType.ELECTRIC).build());
        
        player.delegate(
                new HariantTickingTask(Scheduler.ofTimer(1)) {
                    @Override
                    public void run(int tick) {
                        if (handle.isDead()) {
                            this.cancel();
                            return;
                        }
                        
                        // Home towards closest enemy
                        projectile.collectNearbyEntities(homingRadius)
                                  .filter(player::canAffect)
                                  .min(Comparator.comparingDouble(player::distanceToSquared))
                                  .ifPresent(entity -> {
                                      final Vector vector = entity.getMidpointLocation().toVector()
                                                                  .subtract(arrow.getLocation().toVector())
                                                                  .normalize()
                                                                  .multiply(homingSmoothingFactor.doubleValue());
                                      
                                      arrow.setVelocity(vector);
                                  });
                        
                        // Fx
                        if (this.modulo(2)) {
                            final Location location = arrow.getLocation();
                            
                            player.spawnWorldParticle(location, Particle.ENCHANTED_HIT, 5, 0, 0, 0, 0);
                            player.playWorldSound(location, Sound.ENTITY_ELDER_GUARDIAN_AMBIENT_LAND, 2.0f);
                        }
                    }
                }
        );
        
        // Fx
        player.playSound(Sound.ENCHANT_THORNS_HIT, 2.0f);
        player.playSound(Sound.ENTITY_ELDER_GUARDIAN_DEATH_LAND, 1.25f);
    }
    
}
