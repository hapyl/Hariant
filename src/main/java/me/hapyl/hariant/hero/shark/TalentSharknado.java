package me.hapyl.hariant.hero.shark;

import me.hapyl.eterna.module.location.LocationHelper;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.text.NumberToWord;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.attribute.AttributeScaling;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.element.anomaly.ElementalAnomalyType;
import me.hapyl.hariant.entity.EntityCollector;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.PullSource;
import me.hapyl.hariant.entity.WarningType;
import me.hapyl.hariant.entity.damage.*;
import me.hapyl.hariant.entity.damage.component.DamageComponent;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.talent.TalentContext;
import me.hapyl.hariant.talent.field.DisplayField;
import me.hapyl.hariant.talent.target.TalentTarget;
import me.hapyl.hariant.talent.ultimate.TalentUltimate;
import me.hapyl.hariant.talent.ultimate.UltimateResourceType;
import me.hapyl.hariant.task.HariantTickingTask;
import me.hapyl.hariant.task.Scheduler;
import me.hapyl.hariant.task.executor.Executable;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.RiptideFx;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public final class TalentSharknado extends TalentUltimate {
    
    private final @DisplayField AttributeScaling damage = AttributeScaling.create(Map.of(
            AttributeType.ATTACK, 69.0,
            AttributeType.ELEMENTAL_MASTERY, 67.0
    ));
    
    private final @DisplayField Decimal damagePeriod = Decimal.ofSeconds(0.5f);
    private final @DisplayField Decimal eachNHitAppliesBleed = Decimal.ofValue(5);
    
    private final @DisplayField Decimal pullRadius = Decimal.ofValue(3);
    private final @DisplayField Decimal pullStrength = Decimal.ofValue(0.4);
    private final @DisplayField Decimal pullResistance = Decimal.ofValue(0.6);
    
    private final @DisplayField Decimal riptideScale = Decimal.ofValue(1.2);
    private final @DisplayField Decimal riptideRadius = Decimal.ofValue(1.5);
    private final @DisplayField Decimal castingTime = Decimal.ofSeconds(0.3f);
    
    private final DamageSourceIdentity damageSourceIdentity = DamageSourceIdentity.create(
            TalentSharknado.this,
            DeathMessage.create("{player} was consumed by [{killer}'s] Sharknado")
    );
    
    public TalentSharknado(@NotNull Key key) {
        super(key, Component.text("Sharknado"), Icon.ofMaterial(Material.HEART_OF_THE_SEA), UltimateResourceType.ENERGY, 60);
        
        setDurationSeconds(5);
        setCooldownSeconds(30);
        
        setDescription(
                Component.empty()
                         .append(Component.text("Summon a "))
                         .append(Component.text("Sharknado", Colors.SHARK))
                         .append(Component.text(" from the depth within in front you."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("The Sharknado constantly pulls nearby "))
                         .append(Component.text("enemies", Colors.RED))
                         .append(Component.text(" towards and deals "))
                         .append(ElementType.WATER.asComponentAreaOfEffectDamage())
                         .append(Component.text("."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("Additionally, every "))
                         .append(Component.text(NumberToWord.toWord(eachNHitAppliesBleed.intValue()).toLowerCase(), Colors.NUMBER))
                         .append(Component.text(" hits, it "))
                         .append(Component.text("forcefully", Style.style(TextDecoration.UNDERLINED)))
                         .append(Component.text(" triggers "))
                         .append(ElementalAnomalyType.BLEED)
                         .append(Component.text("."))
        );
    }
    
    @Override
    public @NotNull Executable execute(@NotNull HariantPlayer player, @NotNull TalentContext context, double consumedResource) {
        return Executable.execute(() -> {
            new Sharknado(player, LocationHelper.anchor(player.getLocationInFront(2.5)));
        });
    }
    
    @Override
    public @NotNull TalentTarget target(@NotNull HariantPlayer player) {
        return TalentTarget.none();
    }
    
    public class Sharknado extends HariantTickingTask implements EntityCollector {
        
        private static final double INITIAL_SCALE = 0.125;
        private static final Color FLASH_COLOR = Color.fromRGB(Colors.SHARK.value());
        
        private final HariantPlayer player;
        private final RiptideFx riptideFx;
        private final PullSource pullSource;
        private final Location location;
        private final DamageSource damageSource;
        
        private final int duration;
        private int totalHits;
        
        Sharknado(@NotNull HariantPlayer player, @NotNull Location location) {
            super(Scheduler.ofTimer());
            
            this.player = player;
            this.riptideFx = new RiptideFx(location, INITIAL_SCALE);
            this.pullSource = new SharknadoPullSource(player, location);
            this.location = location.add(0, riptideScale.doubleValue(), 0); // Offset location with the scale of riptide, which is used for entity collection
            this.damageSource = new SharknadoDamageSource(player, damage.getScaledValue(player));
            this.duration = getDuration() + castingTime.intValue();
            
            // Fx
            player.playWorldSound(location, Sound.ENTITY_BREEZE_INHALE, 6, 0.0f);
        }
        
        private @NotNull Stream<HariantEntity> stream() {
            return collectNearbyEntities(location, riptideRadius).filter(player::canAffect);
        }
        
        @Override
        public void run(int tick) {
            final double progress = (double) tick / castingTime.intValue();
            
            // Casting time
            if (progress <= 1.0) {
                this.riptideFx.scale(Math.min(INITIAL_SCALE + (riptideScale.doubleValue() - INITIAL_SCALE) * progress, riptideScale.doubleValue()));
                
                // Warn entities
                if (modulo(damagePeriod)) {
                    this.stream().forEach(entity -> entity.showWarning(WarningType.WARNING, 7));
                }
            }
            // Affect time
            else {
                if (tick > duration) {
                    this.cancel();
                    
                    // Fx
                    player.spawnWorldParticle(location, Particle.FLASH, 1, 0, 0, 0, 0, FLASH_COLOR);
                    return;
                }
                
                // Damage
                if (modulo(damagePeriod)) {
                    final boolean applyBleed = totalHits++ % eachNHitAppliesBleed.intValue() == 0;
                    
                    this.stream().forEach(entity -> {
                        entity.damage(damageSource);
                        
                        if (applyBleed) {
                            entity.triggerAnomaly(ElementalAnomalyType.BLEED, player);
                        }
                    });
                    
                    // Fx
                    player.spawnWorldParticle(location, Particle.SWEEP_ATTACK, 5, 0.5, 2, 0.5, 0.025f);
                    
                    player.playWorldSound(location, Sound.ENTITY_BREEZE_HURT, 0.75f);
                    
                    if (applyBleed) {
                        player.playWorldSound(location, Sound.ENTITY_BREEZE_DEATH, 1.25f);
                        player.playWorldSound(location, Sound.ENTITY_BREEZE_DEATH, 0.75f);
                    }
                }
            }
            
        }
        
        @Override
        public @NotNull Location getLocation() {
            return location;
        }
        
        @Override
        public void onCancel() {
            riptideFx.remove();
            pullSource.cancel();
        }
    }
    
    private class SharknadoPullSource extends PullSource {
        SharknadoPullSource(@NotNull HariantEntity source, @NotNull Location centre) {
            super(source, centre, TalentSharknado.this.getName(), TalentSharknado.this.getDuration() + castingTime.intValue(), pullRadius.doubleValue(), pullStrength.doubleValue(), pullResistance.doubleValue());
        }
    }
    
    private class SharknadoDamageSource extends DamageSourceImpl {
        SharknadoDamageSource(@Nullable HariantEntity source, double damage) {
            super(damageSourceIdentity, source, DamageType.ULTIMATE, ElementType.WATER, DamageComponent.ofCommon(), Set.of(), damage, 0);
        }
    }
    
}