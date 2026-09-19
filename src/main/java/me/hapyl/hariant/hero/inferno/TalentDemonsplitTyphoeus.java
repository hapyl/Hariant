package me.hapyl.hariant.hero.inferno;

import com.google.common.collect.Sets;
import me.hapyl.eterna.module.location.LocationHelper;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.modifier.AttributeModifier;
import me.hapyl.hariant.attribute.modifier.AttributeModifierType;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.damage.*;
import me.hapyl.hariant.entity.damage.component.DamageComponents;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.event.HariantDamageEvent;
import me.hapyl.hariant.talent.TalentType;
import me.hapyl.hariant.util.field.DisplayField;
import me.hapyl.hariant.term.EnumTerminology;
import me.hapyl.hariant.util.GeometryExtras;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public final class TalentDemonsplitTyphoeus extends TalentDemonsplit implements Listener {
    
    public static final Key HELLFIRE_AURA_MODIFIER_KEY = Key.ofString("hellfire_aura");
    public static final Component HELLFIRE_AURA_NAME = Component.text("Hellfire Aura");
    
    private final @DisplayField Decimal fireResistanceReduction = Decimal.ofAttribute(AttributeType.FIRE_RESISTANCE, 25);
    private final @DisplayField Decimal fireResistanceReductionDuration = Decimal.ofSeconds(8);
    
    private final @DisplayField Decimal hellfireAuraRadius = Decimal.ofValue(3);
    
    private final @DisplayField Decimal repeatWindow = Decimal.ofSeconds(5);
    private final @DisplayField Decimal repeatMultiplier = Decimal.ofPercentage(50);
    private final @DisplayField Decimal repeatRadius = Decimal.ofValue(3);
    
    private final DamageSourceIdentity damageSourceIdentity = DamageSourceIdentity.create(
            Key.ofString("repeat"),
            Component.text("Repeat"),
            DeathMessage.create("{player} was killed by Typhoeus [({killer})]")
    );
    
    public TalentDemonsplitTyphoeus(@NotNull Key key) {
        super(key, InfernoDemonType.TYPHOEUS);
        
        setTalentType(TalentType.DAMAGE);
    }
    
    @Override
    public @NotNull InfernoDemonEntity newInstance(@NotNull HariantPlayer player, InfernoDemonType infernoDemonType) {
        return new InfernoDemonEntityTyphoeus(player);
    }
    
    @Override
    public @NotNull Component describeAbility() {
        return Component.empty()
                        .append(Component.text("Radiates a hellfire aura that reduces "))
                        .append(AttributeType.FIRE_RESISTANCE)
                        .append(Component.text(" of nearby "))
                        .append(Component.text("enemies", Colors.RED))
                        .append(Component.text(" by "))
                        .append(fireResistanceReduction)
                        .append(Component.text(" for "))
                        .append(fireResistanceReductionDuration)
                        .append(Component.text("."));
    }
    
    @Override
    public @NotNull Component describeReform() {
        return Component.empty()
                        .append(Component.text("Repeat the "))
                        .append(Component.text("DMG", Colors.DARK_RED))
                        .append(Component.text(" dealt in the last "))
                        .append(repeatWindow)
                        .append(Component.text(" multiplied by "))
                        .append(Component.text("x%.1f".formatted(repeatMultiplier.doubleValue()), Colors.RED))
                        .append(Component.text(" as "))
                        .append(EnumTerminology.TRUE_DAMAGE)
                        .append(Component.text("."));
    }
    
    @EventHandler(ignoreCancelled = true)
    public void handleHariantDamageEvent(HariantDamageEvent ev) {
        if (!(demonEntityFromDamageEventOrNull(ev) instanceof InfernoDemonEntityTyphoeus typhoeus)) {
            return;
        }
        
        typhoeus.damageDealt.add(new TyphoeusDamageData(ev.getDamage(), System.currentTimeMillis()));
    }
    
    public class InfernoDemonEntityTyphoeus extends InfernoDemonEntity {
        
        private final Set<TyphoeusDamageData> damageDealt;
        
        InfernoDemonEntityTyphoeus(@NotNull HariantPlayer player) {
            super(player, InfernoDemonType.TYPHOEUS, TalentDemonsplitTyphoeus.this);
            
            this.damageDealt = Sets.newHashSet();
        }
        
        @Override
        public void onForm(@NotNull HariantPlayer player, @NotNull HeroDataInferno data) {
            InfernoDemon.drawParticleBox(player, location -> player.spawnWorldParticle(location, Particle.FLAME, 1, 0), 2.0);
        }
        
        @Override
        public void onReform(@NotNull HariantPlayer player, @NotNull HeroDataInferno data) {
            super.onReform(player, data);
            
            // Calculate the damage to repeat
            final long currentTimeMillis = System.currentTimeMillis();
            final long repeatWindowInMillis = repeatWindow.longValue() * 50L;
            
            final double damage = damageDealt.stream()
                                             .filter(damageData -> currentTimeMillis - damageData.dealtAtMillis() < repeatWindowInMillis)
                                             .mapToDouble(TyphoeusDamageData::damage)
                                             .sum();
            
            if (damage > 0) {
                final DamageSource damageSource = new DamageSourceRepeat(player, damage * repeatMultiplier.doubleValue());
                final Location location = player.getLocationInFrontFromEyes(2.5);
                
                player.collectNearbyEntities(location, repeatRadius)
                      .filter(player::canAffect)
                      .forEach(entity -> entity.damage(damageSource));
                
                // Fx
                final double repeatRadius = TalentDemonsplitTyphoeus.this.repeatRadius.doubleValue();
                
                GeometryExtras.drawX(location, repeatRadius, 0.2, _location -> player.spawnWorldParticle(_location, Particle.LAVA, 1, 0));
                
                for (int i = 0; i < 20; i++) {
                    final double x = player.random.nextDouble(repeatRadius);
                    final double z = player.random.nextDouble(repeatRadius);
                    
                    final double offsetX = player.random.nextDouble(-0.2, 0.2);
                    final double offsetZ = player.random.nextDouble(-0.2, 0.2);
                    
                    LocationHelper.offset(location, x, 0, z, () -> {
                        player.spawnWorldParticle(location, Particle.FLAME, 0, offsetX, 0.85, offsetZ, 0.75f);
                    });
                }
                
                player.playWorldSound(location, Sound.ENTITY_ZOMBIFIED_PIGLIN_HURT, 0.75f);
                player.playWorldSound(location, Sound.ENTITY_WITHER_BREAK_BLOCK, 1.25f);
            }
        }
        
        @Override
        public boolean tick() {
            super.tick();
            
            // Hellfire aura
            if (localTicks() % 5 == 0) {
                collectNearbyEntities(hellfireAuraRadius)
                        .filter(player::canAffect)
                        .forEach(entity -> {
                            entity.getAttributes().addModifierIfAbsent(new HellfireAuraAttributeModifier(player));
                        });
            }
            
            return true;
        }
    }
    
    public record TyphoeusDamageData(double damage, long dealtAtMillis) {
    }
    
    public class HellfireAuraAttributeModifier extends AttributeModifier {
        HellfireAuraAttributeModifier(@NotNull HariantEntity applier) {
            super(HELLFIRE_AURA_MODIFIER_KEY, HELLFIRE_AURA_NAME, applier, fireResistanceReductionDuration.intValue());
            
            of(AttributeType.FIRE_RESISTANCE, AttributeModifierType.FLAT, -fireResistanceReduction.doubleValue());
        }
    }
    
    public class DamageSourceRepeat extends DamageSourceImpl {
        DamageSourceRepeat(@NotNull HariantPlayer source, double damage) {
            super(damageSourceIdentity, source, DamageType.TALENT, ElementType.FIRE, DamageComponents.ofTrueDamage(), Set.of(), damage, 0);
        }
    }
    
}