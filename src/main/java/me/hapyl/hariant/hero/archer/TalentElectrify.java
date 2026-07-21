package me.hapyl.hariant.hero.archer;

import me.hapyl.eterna.module.component.ProgressBar;
import me.hapyl.eterna.module.math.Vector3;
import me.hapyl.eterna.module.particle.ParticleBuilder;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.attribute.AttributeScaling;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.damage.DamageSource;
import me.hapyl.hariant.entity.damage.DamageSourceIdentity;
import me.hapyl.hariant.entity.damage.DamageType;
import me.hapyl.hariant.entity.damage.DeathMessage;
import me.hapyl.hariant.entity.damage.component.DamageComponent;
import me.hapyl.hariant.entity.player.DelegateType;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.event.HariantProjectileLaunchEvent;
import me.hapyl.hariant.handler.HariantProjectile;
import me.hapyl.hariant.hero.HeroRegistry;
import me.hapyl.hariant.math.ShapeProperties;
import me.hapyl.hariant.math.Shapes;
import me.hapyl.hariant.talent.TalentContext;
import me.hapyl.hariant.talent.field.DisplayField;
import me.hapyl.hariant.talent.target.TalentTarget;
import me.hapyl.hariant.talent.ultimate.TalentUltimate;
import me.hapyl.hariant.talent.ultimate.UltimateResourceType;
import me.hapyl.hariant.task.HariantTask;
import me.hapyl.hariant.task.HariantTickingTask;
import me.hapyl.hariant.task.Scheduler;
import me.hapyl.hariant.task.executor.Executable;
import me.hapyl.hariant.task.executor.While;
import me.hapyl.hariant.term.EnumTerminology;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public final class TalentElectrify extends TalentUltimate implements Listener {
    
    private final @DisplayField Decimal explosionRadius = Decimal.ofValue(10);
    private final @DisplayField Decimal explosionDuration = Decimal.ofSeconds(3);
    private final @DisplayField Decimal explosionDamagePeriod = Decimal.ofSeconds(0.5f);
    
    private final @DisplayField AttributeScaling explosionDamage = AttributeScaling.create(AttributeType.ATTACK, 236);
    private final @DisplayField Decimal elementalApplication = Decimal.ofElementalApplication(ElementType.ELECTRIC, 50);
    
    private final ParticleBuilder particleFx = ParticleBuilder.dustColorTransition(Color.fromRGB(247, 181, 47), Color.fromRGB(250, 224, 170), 1);
    private final ProgressBar progressBar = new ProgressBar("⚡", 20, Style.style(Colors.ELEMENT_ELECTRIC));
    
    private final DamageSourceIdentity damageSourceIdentity = DamageSourceIdentity.create(
            this,
            DeathMessage.create("{player} was electrified [by {killer}]")
    );
    
    public TalentElectrify(@NotNull Key key) {
        super(key, Component.text("Electrify"), Icon.ofMaterial(Material.BLAZE_POWDER), UltimateResourceType.ENERGY, 100);
        
        this.setDurationSeconds(10);
        
        this.setDescription(
                Component.empty()
                         .append(Component.text("Nock a special arrow, filled with unstable "))
                         .append(ElementType.ELECTRIC)
                         .append(Component.text(" energy on your bow, which "))
                         .append(Component.text("replaces your next bow shot").decorate(TextDecoration.UNDERLINED))
                         .append(Component.text(" for a maximum of "))
                         .append(this.getDurationFormatted())
                         .append(Component.text("."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("Upon impact, the arrow release the energy that deals "))
                         .append(ElementType.ELECTRIC.asComponentDamage())
                         .append(Component.text(" in large "))
                         .append(EnumTerminology.AREA_OF_EFFECT)
                         .append(Component.text(" over "))
                         .append(this.getDurationFormatted())
                         .append(Component.text("."))
        );
    }
    
    @Override
    @NotNull
    public TalentTarget target(@NotNull HariantPlayer player) {
        return TalentTarget.none();
    }
    
    @EventHandler
    public void handleHariantProjectileLaunchEvent(HariantProjectileLaunchEvent ev) {
        final HariantProjectile projectile = ev.getProjectile();
        final HariantEntity attacker = projectile.getDamageSource().getSource();
        
        if (!(attacker instanceof HariantPlayer player)) {
            return;
        }
        
        if (!player.hasHeroData(HeroRegistry.ARCHER)) {
            return;
        }
        
        final HeroDataArcher archerData = player.getHeroData(HeroRegistry.ARCHER, HeroDataArcher::new);
        
        if (!archerData.isInfused()) {
            return;
        }
        
        archerData.setInfused(false);
        
        final Projectile handle = projectile.getHandle();
        
        final ElectrifyArrowDamageSource damageSource = new ElectrifyArrowDamageSource(player);
        projectile.setDamageSource(damageSource);
        
        // Delegate the arrow to the player
        player.delegate(new HariantTask(Scheduler.ofTimer(1)) {
            @Override
            public void run() {
                // Kinda weird check but the arrow will die by either hitting an entity or hitting a block, so we only need one check
                if (!handle.isDead()) {
                    
                    // Fx
                    player.spawnWorldParticle(projectile.getLocation(), Particle.ELECTRIC_SPARK, 1, 0.1, 0.1, 0.1, 0.075f);
                    return;
                }
                
                createExplosion(player, projectile.getLocation(), damageSource);
                cancel();
            }
        }, DelegateType.PERSISTENT);
        
        // Fx
        player.playWorldSound(Sound.ENTITY_BREEZE_DEATH, 1.0f);
    }
    
    @NotNull
    @Override
    public Executable execute(@NotNull HariantPlayer player, @NotNull TalentContext context, double consumedResource) {
        final int duration = this.getDuration();
        
        final HeroDataArcher archerData = player.getHeroData(HeroRegistry.ARCHER, HeroDataArcher::new);
        archerData.setInfused(true);
        
        return Executable.whilst(While.duration(this, tick -> {
            // If shot the arrow, break early
            if (!archerData.isInfused()) {
                return true;
            }
            
            player.sendSubtitle(progressBar.build(duration - tick, duration), 0, 20, 5);
            return false;
        }));
    }
    
    private void createExplosion(@NotNull HariantPlayer player, @NotNull Location location, @NotNull TalentElectrify.ElectrifyArrowDamageSource damageSource) {
        final double initialRadius = explosionRadius.doubleValue();
        final int duration = explosionDuration.intValue();
        final int damagePeriod = explosionDamagePeriod.intValue();
        
        // Don't delegate to the player
        new HariantTickingTask(Scheduler.ofTimer(1)) {
            @Override
            public void run(int tick) {
                final double progress = (double) tick / duration;
                
                if (progress >= 1.0) {
                    this.cancel();
                    return;
                }
                
                final double speed = 20 * progress;
                final double radians = Math.toRadians(tick) * speed;
                
                final double x = Math.toRadians(Math.sin(radians)) * -45;
                final double z = Math.toRadians(Math.cos(radians)) * 45;
                
                final double radius = initialRadius * (1 - progress);
                
                // Actually damage the entities
                if (modulo(damagePeriod)) {
                    player.collectNearbyEntities(location, radius)
                          .filter(player::canAffect)
                          .forEach(entity -> entity.damage(damageSource.damageSource));
                    
                    player.playWorldSound(location, Sound.ENTITY_BEE_HURT, 2.0f);
                    player.playWorldSound(location, Sound.ENTITY_STRIDER_EAT, 1.0f);
                }
                
                if (modulo(2)) {
                    player.playWorldSound(location, Sound.ENTITY_BEE_HURT, (float) (0.75f + (1.25f * progress)));
                }
                
                // Fx
                Shapes.OCTAHEDRON.draw(location, particleFx::display, ShapeProperties.create(radius, 0.5, Vector3.of(x, 0, z)));
            }
        };
    }
    
    public class ElectrifyArrowDamageSource extends DamageSourceArcherTalent {
        
        private final DamageSource damageSource;
        
        ElectrifyArrowDamageSource(@NotNull HariantEntity attacker) {
            // The one defines the arrow damage, which shouldn't actually deal damage
            super(damageSourceIdentity, attacker, 1, 0);
            
            this.damageSource = DamageSource.builder(damageSourceIdentity, explosionDamage.getScaledValue(attacker))
                                            .source(attacker)
                                            .elementType(ElementType.ELECTRIC)
                                            .elementalUnits(elementalApplication.doubleValue())
                                            .damageType(DamageType.ULTIMATE)
                                            .components(DamageComponent.ofCommon())
                                            .build();
        }
        
    }
    
}
