package me.hapyl.hariant.hero.alchemist;

import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.text.RomanNumber;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.instance.Attributes;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.NormalAttack;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.hero.*;
import me.hapyl.hariant.talent.TalentRegistry;
import me.hapyl.hariant.talent.ultimate.TalentUltimate;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.weapon.Weapon;
import me.hapyl.hariant.weapon.WeaponMelee;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static org.bukkit.Sound.ITEM_ARMOR_EQUIP_ELYTRA;

public class HeroAlchemist extends Hero {
    
    private final int weaponSlotSelectingPotions = 7;
    
    private final Weapon weaponMissingStick = new WeaponAlchemistStickMissing();
    private final Weapon weaponAlchemicalMadness = new WeaponAlchemicalStickMadness();
    
    public HeroAlchemist(@NotNull Key key) {
        super(
                key,
                Component.text("Alchemist"),
                Attributes.base(1250, 80, 50)
                          .adjust(AttributeType.MOVEMENT_SPEED, 115),
                new WeaponAlchemistStick()
        );
        
        final HeroProfile profile = getProfile();
        profile.setElementType(ElementType.TOXIC);
        profile.setGender(Gender.FEMALE);
        profile.setArchetype(Archetype.STRATEGY);
        
        final HeroEquipment equipment = getEquipment();
        equipment.setHeadTexture("661691fb01825b9d9ec1b8f04199443146aa7d5627aa745962c0704b6a236027");
        equipment.setChestPlate(31, 5, 3, TrimPattern.SHAPER, TrimMaterial.COPPER);
        equipment.setLeggings(102, 55, 38, TrimPattern.SILENCE, TrimMaterial.COPPER);
        
        setDescription(Component.text("An alchemist who was cursed by the abyss."));
    }
    
    @NotNull
    @Override
    public TalentAbyssalBottle getFirstTalent() {
        return TalentRegistry.ABYSSAL_BOTTLE;
    }
    
    @NotNull
    @Override
    public TalentBundleOPotions getSecondTalent() {
        return TalentRegistry.BUNDLE_O_POTIONS;
    }
    
    @NotNull
    @Override
    public TalentAlchemicalCauldron getThirdTalent() {
        return TalentRegistry.ALCHEMICAL_CAULDRON;
    }
    
    @NotNull
    @Override
    public TalentAbyssalCorrosion getPassiveTalent() {
        return TalentRegistry.ABYSSAL_CORROSION;
    }
    
    @NotNull
    @Override
    public TalentUltimate getUltimateTalent() {
        return TalentRegistry.ABYSSAL_CURSE;
    }
    
    @NotNull
    @Override
    public Weapon getWeapon(@NotNull HariantPlayer player) {
        final HeroDataAlchemist heroData = player.getHeroData(HeroRegistry.ALCHEMIST, HeroDataAlchemist::new);
        final HariantEntityAlchemicalCauldron alchemicalCauldron = heroData.getAlchemicalCauldron();
        final int alchemicalMadness = heroData.getAlchemicalMadness();
        
        // If madness active, return infused weapon
        if (alchemicalMadness > 0) {
            return weaponAlchemicalMadness;
        }
        // Otherwise if the player is currently has cauldron, and it's brewing, return the missing stick weapon
        else if (alchemicalCauldron != null && alchemicalCauldron.isBrewing()) {
            return weaponMissingStick;
        }
        
        return super.getWeapon(player);
    }
    
    @Override
    public int getWeaponSlot(@NotNull HariantPlayer player) {
        final HeroDataAlchemist heroData = player.getHeroData(HeroRegistry.ALCHEMIST, HeroDataAlchemist::new);
        
        return heroData.getState() == State.SELECT_POTION
               ? weaponSlotSelectingPotions
               : super.getWeaponSlot(player);
    }
    
    @Override
    public void handleItemHeldEvent(@NotNull HariantPlayer player, @NotNull HeroInstance heroInstance, @NotNull PlayerItemHeldEvent ev) {
        final HeroDataAlchemist heroData = player.getHeroData(HeroRegistry.ALCHEMIST, HeroDataAlchemist::new);
        final State state = heroData.getState();
        
        final TalentAbyssalBottle abyssalBottle = this.getFirstTalent();
        final int slot = ev.getNewSlot();
        
        if (state == State.SELECT_POTION) {
            // Explicitly implementing return here to not call talent when we're leaving from selection
            if (slot == 0) {
                heroData.setState(State.NORMAL);
                player.playWorldSound(ITEM_ARMOR_EQUIP_ELYTRA, 0.5f);
                player.setCooldown(abyssalBottle, 5);
                return;
            }
            
            // If we're selecting a potion, call mapped talent
            final TalentAlchemistPotion selectedPotion = abyssalBottle.getAlchemistPotion(slot);
            
            if (selectedPotion == null) {
                return;
            }
            
            selectedPotion.drink0(player, heroData);
            
            // Start cooldown and reset state
            player.setCooldown(abyssalBottle);
            
            heroData.setState(State.NORMAL);
        }
        else {
            super.handleItemHeldEvent(player, heroInstance, ev);
        }
    }
    
    @NotNull
    @Override
    public List<Component> supplyActionbar(@NotNull HariantPlayer player) {
        final HeroDataAlchemist heroData = player.getHeroData(HeroRegistry.ALCHEMIST, HeroDataAlchemist::new);
        
        final AlchemistPotionInstance potionInstance = heroData.getPotionInstance();
        final HariantEntityAlchemicalCauldron alchemicalCauldron = heroData.getAlchemicalCauldron();
        final int alchemicalMadness = heroData.getAlchemicalMadness();
        
        final Style abyssalCorrosionStyle = Definition.ABYSSAL_CORROSION.getStyle();
        final int abyssalCorrosionLevel = heroData.getAbyssalCorrosionLevel();
        
        return List.of(
                // Append corrosion
                Component.empty()
                         .append(Definition.ABYSSAL_CORROSION.getPrefixStyled())
                         .appendSpace()
                         .append(Component.text("%.0f".formatted(heroData.getAbyssalCorrosion()), abyssalCorrosionStyle))
                         .appendSpace()
                         .append(
                                 abyssalCorrosionLevel == 0
                                 ? Component.text("✗", abyssalCorrosionStyle)
                                 : Component.text(RomanNumber.toRoman(abyssalCorrosionLevel), abyssalCorrosionStyle)
                         ),
                
                // Append current potion
                potionInstance != null
                ? Component.empty()
                           .append(Component.text("\uD83E\uDDEA ", NamedTextColor.DARK_PURPLE))
                           .append(Component.text(Tick.format(potionInstance.currentTick()), NamedTextColor.LIGHT_PURPLE))
                : Component.empty(),
                
                // Append cauldron
                alchemicalCauldron != null
                ? alchemicalCauldron.asComponent()
                : Component.empty(),
                
                // Append madness
                alchemicalMadness > 0
                ? heroData.getAlchemicalMadnessFormatted()
                : Component.empty()
        );
    }
    
    public int getCurseDuration(@NotNull HariantPlayer player, int baseDuration) {
        final HeroDataAlchemist heroData = player.getHeroData(this, HeroDataAlchemist::new);
        final double abyssalCorrosion = heroData.getAbyssalCorrosion();
        
        return (int) (baseDuration * (1 - abyssalCorrosion * this.getPassiveTalent().corrosionDecrementPerTick));
    }
    
    private static class WeaponAlchemistStick extends WeaponMelee {
        
        WeaponAlchemistStick() {
            super(
                    Key.ofString("alchemist_stick"),
                    Icon.ofMaterial(Material.STICK),
                    new NormalAttack(ElementType.PHYSICAL, AttributeType.ATTACK, 65, 10)
            );
            
            setName(Component.text("Brewing Stick"));
        }
    }
    
    private static class WeaponAlchemicalStickMadness extends WeaponMelee {
        WeaponAlchemicalStickMadness() {
            super(
                    Key.ofString("alchemist_stick_madness"),
                    Icon.ofMaterial(Material.STICK, ItemBuilder::glow),
                    new NormalAttack(ElementType.TOXIC, AttributeType.ATTACK, 125, 10)
            );
            
            setName(Component.text("Brewing Stick (Infused)"));
        }
    }
    
    private static class WeaponAlchemistStickMissing extends WeaponMelee {
        WeaponAlchemistStickMissing() {
            super(
                    Key.ofString("alchemist_stick_missing"),
                    Icon.ofMaterial(Material.SLIME_BALL),
                    new NormalAttack(ElementType.PHYSICAL, AttributeType.ATTACK, 1, 10)
            );
            
            setName(Component.text("Brewing Stick (Missing)"));
        }
    }
}
