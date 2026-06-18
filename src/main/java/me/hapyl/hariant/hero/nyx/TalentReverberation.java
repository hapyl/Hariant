package me.hapyl.hariant.hero.nyx;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.HariantConstants;
import me.hapyl.hariant.attribute.AttributeScaling;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.modifier.AttributeModifier;
import me.hapyl.hariant.attribute.modifier.AttributeModifierType;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.effect.EffectType;
import me.hapyl.hariant.entity.heal.HealingSource;
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
    
    @DisplayField public final AttributeScaling roseDamage = AttributeScaling.of(AttributeType.ATTACK, 45);
    @DisplayField public final Decimal roseBloomDelay = Decimal.ofSeconds(0.8f);
    @DisplayField public final Decimal roseExplosionRadius = Decimal.ofValue(0.6);
    @DisplayField public final Decimal roseElementalApplication = Decimal.ofElementalApplication(ElementType.AETHER, 200);
    
    @DisplayField private final Decimal voidShieldCapacityOfNyxMaxHealth = Decimal.ofPercentage(10);
    @DisplayField private final Decimal voidShieldHealingOfNyxMaxHealth = Decimal.ofPercentage(10);
    @DisplayField private final Decimal voidShieldAetherStrength = Decimal.ofPercentage(250);
    
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
                         .append(Component.text("After "))
                         .append(Component.text("a short delay, the rose blooms to life once again and explodes, dealing "))
                         .append(ElementType.AETHER.asComponentAreaOfEffectDamage())
                         .append(Component.text(" and applies "))
                         .appendNewline() // fnl
                         .append(ElementType.AETHER)
                         .append(Component.text(" anomaly."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("Additionally, the "))
                         .append(Component.text("teammate", Colors.GREEN))
                         .append(Component.text(" who triggered the assist gains a "))
                         .append(Component.text("Void Shield", Colors.VOID))
                         .append(Component.text("."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("Void Shield", Colors.GOLD))
                         .appendNewline()
                         .append(Component.text("Whenever the shield is broken or refreshes, it "))
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
        final HariantEntity applier = ev.getApplier();
        
        if (!(applier instanceof HariantPlayer player) || ev.getEffectType() != EffectType.DEBUFF) {
            return;
        }
        
        final Location location = applier.getLocation();
        
        // Find Nyx in the team without cooldown
        final HariantPlayer nyx = player.getPlayerTeam().getPlayers()
                                        .stream()
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
        this.assist(ev.getEntity(), player, nyx);
    }
    
    public void assist(@NotNull HariantEntity entity, @NotNull HariantPlayer attacker, @NotNull HariantPlayer nyx) {
        // Assist
        this.createRose(nyx, entity.getLocation());
        
        // Create shield
        attacker.setShield(new VoidShield(attacker, nyx.getMaxHealth() * voidShieldCapacityOfNyxMaxHealth.doubleValue(), nyx));
    }
    
    public void createRose(@NotNull HariantPlayer player, @NotNull Location location) {
        player.delegate(new WiltedRose(player, location, this));
    }
    
    public class VoidShield extends Shield {
        
        private final HariantPlayer nyx;
        private final HealingSource healingSource;
        
        VoidShield(@NotNull HariantEntity entity, double maximumCapacity, @NotNull HariantPlayer nyx) {
            super(entity, shieldStrength, maximumCapacity, HariantConstants.INDEFINITE_DURATION);
            
            this.nyx = nyx;
            this.healingSource = HealingSource.create(nyx.getMaxHealth() * voidShieldHealingOfNyxMaxHealth.doubleValue(), nyx);
        }
        
        @Override
        public void onRemove(@NotNull Cause cause) {
            if (cause != Cause.BROKE && cause != Cause.REPLACED) {
                return;
            }
            
            // If the shield broke, heal the target by nyx
            entity.heal(healingSource);
            entity.getAttributes().addModifier(new AttributeModifierReverberation(nyx));
            
            // Fx
            entity.playWorldSound(Sound.ENTITY_ENDERMAN_TELEPORT, 0.75f);
            entity.playWorldSound(Sound.ENTITY_ELDER_GUARDIAN_CURSE, 1.25f);
        }
        
        @Override
        public @NotNull Component asComponent() {
            return super.asComponent()
                        .appendSpace()
                        .append(Component.text("[", Colors.GRAY))
                        .append(nyx.asHeadComponent())
                        .append(Component.text("]", Colors.GRAY));
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