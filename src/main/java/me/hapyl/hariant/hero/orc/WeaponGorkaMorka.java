package me.hapyl.hariant.hero.orc;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.attribute.AttributeScaling;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.NormalAttack;
import me.hapyl.hariant.entity.damage.*;
import me.hapyl.hariant.entity.damage.component.DamageComponents;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.hero.HeroRegistry;
import me.hapyl.hariant.talent.Response;
import me.hapyl.hariant.term.EnumTerminology;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.decimal.Decimal;
import me.hapyl.hariant.util.field.DisplayField;
import me.hapyl.hariant.weapon.WeaponMelee;
import me.hapyl.hariant.weapon.ability.Ability;
import me.hapyl.hariant.weapon.ability.AbilityType;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public final class WeaponGorkaMorka extends WeaponMelee {
    
    public static final Component WEAPON_NAME = Component.text("Gorka & Morka");
    
    public static final Component COMPONENT_ABILITY_COOLDOWN = Component.text("\uD83E\uDE93");
    public static final Component COMPONENT_ABILITY_COOLDOWN_MISSING = Component.text("\uD83E\uDE93", Colors.DARK_GRAY);
    
    public final @DisplayField Decimal throwSpeed = Decimal.ofBlocksPerSecond(20);
    public final @DisplayField Decimal recallSpeed = Decimal.ofBlocksPerSecond(30);
    
    public final @DisplayField Decimal maximumThrowDistance = Decimal.ofValue(50);
    public final @DisplayField Decimal collisionRadius = Decimal.ofValue(0.6);
    public final @DisplayField Decimal recallStrength = Decimal.ofValue(0.6);
    public final @DisplayField Decimal recallWait = Decimal.ofSeconds(0.5f);
    
    public final @DisplayField AttributeScaling throwDamage = AttributeScaling.create(AttributeType.ATTACK, 196);
    
    public final @DisplayField Decimal recallDamageOfDamage = Decimal.ofPercentage(50);
    public final @DisplayField Decimal damageIncreasePerBlockFlown = Decimal.ofPercentage(5);
    
    public final @DisplayField Decimal blizzardRadius = Decimal.ofValue(4);
    public final @DisplayField Decimal blizzardIceApplication = Decimal.ofElementalApplication(ElementType.ICE, 300);
    public final @DisplayField Decimal blizzardIceApplicationPeriod = Decimal.ofSeconds(0.5f);
    
    private final DamageSourceIdentity damageSourceIdentity = DamageSourceIdentity.create(
            Key.ofString("gorka_morka_range_damage_source"),
            WEAPON_NAME,
            DeathMessage.create("{player} was [{killer}'s] bullseye")
    );
    
    private final Ability abilityThrow;
    
    WeaponGorkaMorka() {
        super(
                Key.ofString("gorka_morka"),
                Icon.ofMaterial(Material.IRON_AXE),
                NormalAttack.melee(ElementType.PHYSICAL, AttributeType.ATTACK, 65, 10)
        );
        
        setName(WEAPON_NAME);
        
        setDescription(Component.text("An ancient poleaxe with icy runes engraved unto it, allowing the bearer to wield their power."));
        
        setAbility(AbilityType.RIGHT_CLICK, abilityThrow = new AbilityThrow());
    }
    
    public double calculateDamage(@NotNull HariantPlayer player, double distanceFlown) {
        return throwDamage.getScaledValue(player) * (1 + distanceFlown * damageIncreasePerBlockFlown.doubleValue());
    }
    
    public @NotNull DamageSource createDamageSource(@NotNull HariantPlayer player, double damage, double elementUnits) {
        return new GorkaMorkaDamageSource(player, damage, elementUnits);
    }
    
    public @NotNull DamageSource createRecallDamageSource(HariantPlayer player, double damage) {
        return new GorkaMorkaDamageSource(player, damage * recallDamageOfDamage.doubleValue(), 0);
    }
    
    public void startThrowCooldown(@NotNull HariantPlayer player) {
        player.setCooldown(abilityThrow);
    }
    
    public class AbilityThrow extends Ability {
        
        AbilityThrow() {
            super(Key.ofString("ability_throw"), Component.text("Throw"));
            
            setDescription(
                    Component.empty()
                             .append(Component.text("Throw the poleaxe forward."))
                             .appendNewline()
                             .appendNewline()
                             .append(Component.text("Upon colliding with an "))
                             .append(Component.text("enemy", Colors.RED))
                             .append(Component.text(" it deals "))
                             .append(ElementType.ICE.asComponentDamage())
                             .append(Component.text(" and applies "))
                             .append(ElementType.ICE)
                             .append(Component.text(" anomaly."))
                             .appendNewline()
                             .appendNewline()
                             .append(Component.text("The damage and elemental application is based on how long the poleaxe flew for.", Colors.DARK_GRAY))
                             .appendNewline()
                             .appendNewline()
                             .append(Component.text("Upon colliding with a "))
                             .append(Component.text("block", Colors.GREEN))
                             .append(Component.text(", it creates a "))
                             .append(Component.text("Blizzard", Colors.ELEMENT_ICE))
                             .append(Component.text(" zone that constantly applies "))
                             .append(ElementType.ICE)
                             .append(Component.text(" anomaly in small "))
                             .append(EnumTerminology.AREA_OF_EFFECT)
                             .append(Component.text("."))
                             .appendNewline()
                             .appendNewline()
                             .append(Component.text("Cooldown of this ability starts after the poleaxe is recalled and has returned to you.", Colors.DARK_GRAY))
            );
            
            setCooldownSeconds(8);
        }
        
        @Override
        public @NotNull Response execute(@NotNull HariantPlayer player) {
            final HeroDataOrc heroData = player.getHeroData(HeroRegistry.ORC, HeroDataOrc::new);
            
            // If entity already exists, return
            if (heroData.getWeaponEntity() != null) {
                return Response.error("Already thrown!");
            }
            
            heroData.setWeaponEntity(new WeaponGorkaMorkaEntity(player, WeaponGorkaMorka.this));
            
            // Start recall ability cooldown so you can't recall right away
            HeroRegistry.ORC.startRecallCooldown(player);
            
            // Fx
            player.playWorldSound(Sound.ITEM_TRIDENT_RETURN, 0.0f);
            
            return Response.ok();
        }
        
    }
    
    @Override
    public @NotNull Component getPrimaryAbilityCooldownComponent() {
        return COMPONENT_ABILITY_COOLDOWN;
    }
    
    public class GorkaMorkaDamageSource extends DamageSourceImpl {
        
        GorkaMorkaDamageSource(@NotNull HariantPlayer player, double damage, double elementUnits) {
            super(damageSourceIdentity, player, DamageType.MELEE, ElementType.ICE, DamageComponents.ofCommon(), Set.of(), damage, elementUnits);
        }
        
    }
    
    
}