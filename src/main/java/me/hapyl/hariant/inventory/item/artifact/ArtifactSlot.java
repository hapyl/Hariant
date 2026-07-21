package me.hapyl.hariant.inventory.item.artifact;

import me.hapyl.eterna.module.component.Named;
import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.eterna.module.text.Capitalizable;
import me.hapyl.eterna.module.util.Enums;
import me.hapyl.hariant.inventory.item.ItemCreator;
import me.hapyl.hariant.inventory.item.artifact.affix.ArtifactAffix;
import me.hapyl.hariant.inventory.item.artifact.affix.ArtifactAffixDistribution;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.SlotBound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public enum ArtifactSlot implements SlotBound, ItemCreator, Named, ComponentLike {
    
    SLOT_1(
            19,
            Icon.ofMaterial(Material.HOST_ARMOR_TRIM_SMITHING_TEMPLATE),
            ArtifactAffixDistribution.create(List.of(
                    ArtifactAffix.MAX_HEATH,
                    ArtifactAffix.DEFENSE
            ))
    ),
    
    SLOT_2(
            30,
            Icon.ofMaterial(Material.RAISER_ARMOR_TRIM_SMITHING_TEMPLATE),
            ArtifactAffixDistribution.create(List.of(
                    ArtifactAffix.ATTACK,
                    ArtifactAffix.CRIT_CHANCE,
                    ArtifactAffix.CRIT_DAMAGE
            ))
    ),
    
    SLOT_3(
            32,
            Icon.ofMaterial(Material.WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE),
            ArtifactAffixDistribution.create(List.of(
                    ArtifactAffix.ENERGY_RECHARGE,
                    ArtifactAffix.EFFECT_RESISTANCE,
                    ArtifactAffix.ELEMENTAL_MASTERY,
                    ArtifactAffix.VITALITY,
                    ArtifactAffix.MENDING,
                    ArtifactAffix.LUCK,
                    ArtifactAffix.FEROCITY
            ))
    ),
    
    SLOT_4(
            25,
            Icon.ofMaterial(Material.SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE),
            ArtifactAffixDistribution.create(List.of(
                    ArtifactAffix.PHYSICAL_DAMAGE_BONUS,
                    ArtifactAffix.FIRE_DAMAGE_BONUS,
                    ArtifactAffix.WATER_DAMAGE_BONUS,
                    ArtifactAffix.ICE_DAMAGE_BONUS,
                    ArtifactAffix.TOXIC_DAMAGE_BONUS,
                    ArtifactAffix.ELECTRIC_DAMAGE_BONUS,
                    ArtifactAffix.AETHER_DAMAGE_BONUS,
                    ArtifactAffix.PHYSICAL_RESISTANCE,
                    ArtifactAffix.FIRE_RESISTANCE,
                    ArtifactAffix.WATER_RESISTANCE,
                    ArtifactAffix.ICE_RESISTANCE,
                    ArtifactAffix.TOXIC_RESISTANCE,
                    ArtifactAffix.ELECTRIC_RESISTANCE,
                    ArtifactAffix.AETHER_RESISTANCE
            ))
    );
    
    public static final int LENGTH = values().length;
    
    private final int inventorySlot;
    private final Icon icon;
    private final Component name;
    private final Component component;
    private final ArtifactAffixDistribution artifactAffixDistribution;
    
    ArtifactSlot(final int slot, @NotNull Icon icon, @NotNull ArtifactAffixDistribution artifactAffixDistribution) {
        this.inventorySlot = slot;
        this.icon = icon;
        this.name = Component.text(Capitalizable.capitalize(this));
        this.component = Component.text("[%s]".formatted(ordinal() + 1));
        this.artifactAffixDistribution = artifactAffixDistribution;
    }
    
    @Override
    public int getSlot() {
        return inventorySlot;
    }
    
    @Override
    public @NotNull ItemBuilder createBuilder() {
        return icon.createBuilder();
    }
    
    @Override
    public @NotNull Component getName() {
        return name;
    }
    
    @Override
    public @NotNull Component asComponent() {
        return component;
    }
    
    public @NotNull ArtifactAffixDistribution getArtifactAttributeDistribution() {
        return artifactAffixDistribution;
    }
    
    
    public static @NotNull ArtifactSlot ofRandom() {
        return Enums.getRandomValueOrFirst(ArtifactSlot.class);
    }
    
}