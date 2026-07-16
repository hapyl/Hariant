package me.hapyl.hariant.hero.nyx;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.attribute.AttributeScaling;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.modifier.AttributeModifier;
import me.hapyl.hariant.attribute.modifier.AttributeModifierType;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.effect.EffectType;
import me.hapyl.hariant.entity.heal.HealingSource;
import me.hapyl.hariant.entity.player.DelegateType;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.entity.shield.Shield;
import me.hapyl.hariant.entity.shield.ShieldStrength;
import me.hapyl.hariant.event.HariantEffectEvent;
import me.hapyl.hariant.hero.HeroRegistry;
import me.hapyl.hariant.talent.TalentPassive;
import me.hapyl.hariant.talent.TalentType;
import me.hapyl.hariant.talent.field.DisplayField;
import me.hapyl.hariant.ui.ComponentDisplay;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public final class TalentReverberation extends TalentPassive implements Listener {
    
    @DisplayField public final AttributeScaling roseDamage = AttributeScaling.create(AttributeType.ATTACK, 50);
    
    @DisplayField public final Decimal roseBloomDelay = Decimal.ofSeconds(0.8f);
    @DisplayField public final Decimal roseExplosionRadius = Decimal.ofValue(1.2);
    @DisplayField public final Decimal roseElementalApplication = Decimal.ofElementalApplication(ElementType.AETHER, 250);
    
    @DisplayField private final Decimal voidShieldCapacityOfNyxMaxHealth = Decimal.ofPercentage(10);
    @DisplayField private final Decimal voidShieldHealingOfNyxMaxHealth = Decimal.ofPercentage(10);
    @DisplayField private final Decimal voidShieldAetherStrength = Decimal.ofPercentage(250);
    @DisplayField private final Decimal voidShieldDuration = Decimal.ofSeconds(6);
    
    @DisplayField private final Decimal effectResistanceIncrease = Decimal.ofAttribute(AttributeType.EFFECT_RESISTANCE, 25);
    @DisplayField private final Decimal effectResistanceDuration = Decimal.ofSeconds(6);
    
    @DisplayField private final Decimal teammateRadius = Decimal.ofValue(12);
    
    private final ShieldStrength shieldStrength = ShieldStrength.builder()
                                                                .ofElement(ElementType.AETHER, voidShieldAetherStrength.doubleValue())
                                                                .build();
    
    public TalentReverberation(@NotNull Key key) {
        super(key, Component.text("Reverberation"), Icon.ofMaterial(Material.SHULKER_SHELL));
        
        setTalentType(TalentType.SUPPORT);
        setCooldownSeconds(2.5f);
        
        setDescription(
                Component.empty()
                         .append(Component.text("Whenever "))
                         .append(Component.text("Nyx", Colors.GREEN))
                         .append(Component.text(" or a nearby "))
                         .append(Component.text("ally", Colors.GREEN))
                         .append(Component.text(" impairs an "))
                         .append(Component.text("enemy", Colors.ERROR))
                         .append(Component.text(", Nyx will summon a "))
                         .append(Component.text("Wilter Rose", Colors.ARCHETYPE_HEXBANE))
                         .append(Component.text(" at the enemy's location."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("Additionally, the "))
                         .append(Component.text("teammate", Colors.GREEN))
                         .append(Component.text(" who triggered the assist gains a "))
                         .append(Component.text("Void Shield", Colors.VOID))
                         .append(Component.text(" for "))
                         .append(voidShieldDuration)
                         .append(Component.text("."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("Void Shield", Colors.GOLD))
                         .appendNewline()
                         .append(Component.text("Whenever the shield is broken, expired or refreshed, it "))
                         .append(Component.text("heals", Colors.GREEN))
                         .append(Component.text(" its target and increases their "))
                         .append(AttributeType.EFFECT_RESISTANCE)
                         .append(Component.text(" by "))
                         .append(effectResistanceIncrease)
                         .append(Component.text(" for "))
                         .append(effectResistanceDuration)
                         .append(Component.text("."))
        );
    }
    
    @EventHandler
    public void handleHariantEffectEvent(HariantEffectEvent ev) {
        final HariantEntity entity = ev.getEntity();
        final HariantEntity applier = ev.getApplier();
        
        if (entity.equals(applier) || !(applier instanceof HariantPlayer player) || ev.getEffect().getEffectType() != EffectType.DEBUFF || ev.hasResisted()) {
            return;
        }
        
        final Location location = applier.getLocation();
        
        // Find Nyx in the team without cooldown
        final HariantPlayer nyx = player.getPlayerTeam().getPlayers()
                                        .filter(other -> {
                                            return other.getHero().equals(HeroRegistry.NYX)
                                                   && !other.hasCooldown(this)
                                                   && other.getLocation().distanceSquared(location) <= teammateRadius.doubleValueSquared();
                                        })
                                        .findAny()
                                        .orElse(null);
        
        if (nyx == null) {
            return;
        }
        
        // Start cooldown
        nyx.setCooldown(this);
        
        // Assist the player
        this.assist(entity, player, nyx);
    }
    
    public void assist(@NotNull HariantEntity entity, @NotNull HariantPlayer attacker, @NotNull HariantPlayer nyx) {
        // Assist
        this.createRose(nyx, entity.getLocation());
        
        // Create shield
        attacker.setShield(new VoidShield(attacker, nyx, nyx.getMaxHealth() * voidShieldCapacityOfNyxMaxHealth.doubleValue()));
    }
    
    public void createRose(@NotNull HariantPlayer player, @NotNull Location location) {
        player.delegate(new WiltedRose(player, location, this), DelegateType.INTERRUPTABLE);
    }
    
    public class VoidShield extends Shield {
        
        private final HealingSource healingSource;
        
        VoidShield(@NotNull HariantEntity entity, @NotNull HariantPlayer applier, double maximumCapacity) {
            super(entity, applier, shieldStrength, maximumCapacity, voidShieldDuration.intValue());
            
            this.healingSource = HealingSource.create(applier.getMaxHealth() * voidShieldHealingOfNyxMaxHealth.doubleValue(), TalentReverberation.this, applier);
        }
        
        @Override
        public void onRemove(@NotNull Cause cause) {
            // Don't heal if entity hsa died
            if (cause == Cause.ENTITY_DIED) {
                return;
            }
            
            // If the shield broke, heal the target by nyx
            entity.heal(healingSource);
            entity.getAttributes().addModifier(new AttributeModifierReverberation(applier));
            
            // Fx
            entity.playWorldSound(Sound.ENTITY_ENDERMAN_TELEPORT, 0.75f);
            entity.playWorldSound(Sound.ENTITY_ELDER_GUARDIAN_CURSE, 1.25f);
        }
        
        @Override
        public void display(double shielded, @NotNull Location location) {
            ComponentDisplay.ofAscend(Component.text("⚫ %.0f".formatted(shielded), Colors.VOID), location, 20, 1.5f);
        }
        
    }
    
    public class AttributeModifierReverberation extends AttributeModifier {
        AttributeModifierReverberation(@NotNull HariantEntity applier) {
            super(TalentReverberation.this, applier, effectResistanceDuration.intValue());
            
            of(AttributeType.EFFECT_RESISTANCE, AttributeModifierType.FLAT, effectResistanceIncrease);
        }
    }
    
}