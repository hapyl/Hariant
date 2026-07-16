package me.hapyl.hariant;

import me.hapyl.eterna.module.annotate.UtilityClass;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

/**
 * Represents an all-in-one utility container class featuring exclusively {@link TextColor} constants.
 *
 * <p>
 * This class contains both vanilla named colors, as well as custom colors, therefore it is recomended to only use this class, instead of {@link NamedTextColor}.
 * </p>
 */
@UtilityClass
public final class Colors {
    
    // *-* Vanilla Colors *-* //
    
    public static final TextColor BLACK = NamedTextColor.BLACK;
    public static final TextColor DARK_BLUE = NamedTextColor.DARK_BLUE;
    public static final TextColor DARK_GREEN = NamedTextColor.DARK_GREEN;
    public static final TextColor DARK_AQUA = NamedTextColor.DARK_AQUA;
    public static final TextColor DARK_RED = NamedTextColor.DARK_RED;
    public static final TextColor DARK_PURPLE = NamedTextColor.DARK_PURPLE;
    public static final TextColor GOLD = NamedTextColor.GOLD;
    public static final TextColor GRAY = NamedTextColor.GRAY;
    public static final TextColor DARK_GRAY = NamedTextColor.DARK_GRAY;
    public static final TextColor BLUE = NamedTextColor.BLUE;
    public static final TextColor GREEN = NamedTextColor.GREEN;
    public static final TextColor AQUA = NamedTextColor.AQUA;
    public static final TextColor RED = NamedTextColor.RED;
    public static final TextColor LIGHT_PURPLE = NamedTextColor.LIGHT_PURPLE;
    public static final TextColor YELLOW = NamedTextColor.YELLOW;
    public static final TextColor WHITE = NamedTextColor.WHITE;
    
    // *-* Custom Colors *-* //
    
    public static final TextColor DEFAULT_COLOR = TextColor.color(0xABCDEF);
    public static final TextColor BRAND_COLOR = TextColor.color(0xEF4444);
    public static final TextColor LIGHT_GRAY = TextColor.color(0xBCB7BC);
    public static final TextColor ORANGE = TextColor.color(0xFFA60E);
    public static final TextColor STAFF = TextColor.color(0x1FEEFF);
    
    public static final TextColor ERROR = TextColor.color(0xC31A19);
    public static final TextColor SUCCESS = TextColor.color(0x1ECD23);
    public static final TextColor SEVERE = TextColor.color(0x910409);
    
    public static final TextColor ATTRIBUTE_MAX_HEALTH = TextColor.color(0xDA3E31);
    public static final TextColor ATTRIBUTE_ATTACK = TextColor.color(0xDA0C12);
    public static final TextColor ATTRIBUTE_DEFENSE = TextColor.color(0x2BA91C);
    public static final TextColor ATTRIBUTE_CRIT_CHANCE = TextColor.color(0x66BDFF);
    public static final TextColor ATTRIBUTE_CRIT_DAMAGE = TextColor.color(0x5FAAE7);
    public static final TextColor ATTRIBUTE_MOVEMENT_SPEED = TextColor.color(0xBED9E7);
    public static final TextColor ATTRIBUTE_EFFECT_RESISTANCE = TextColor.color(0x8F7AC7);
    public static final TextColor ATTRIBUTE_KNOCKBACK_RESISTANCE = TextColor.color(0x89A1C4);
    public static final TextColor ATTRIBUTE_ENERGY_RECHARGE = TextColor.color(0x44A4F2);
    public static final TextColor ATTRIBUTE_MENDING = TextColor.color(0x3DF251);
    public static final TextColor ATTRIBUTE_VITALITY = TextColor.color(0xA61712);
    public static final TextColor ATTRIBUTE_ELEMENTAL_MASTERY = TextColor.color(0xD785F2);
    public static final TextColor ATTRIBUTE_LUCK = TextColor.color(0x55D25C);
    public static final TextColor ATTRIBUTE_COOLDOWN_REDUCTION = TextColor.color(0xD2CECE);
    
    public static final TextColor ELEMENT_PHYSICAL = TextColor.color(0xDCDCDC);
    public static final TextColor ELEMENT_FIRE = TextColor.color(0xFF4B35);
    public static final TextColor ELEMENT_WATER = TextColor.color(0x4DBAFF);
    public static final TextColor ELEMENT_ICE = TextColor.color(0x67F3FF);
    public static final TextColor ELEMENT_TOXIC = TextColor.color(0x3FBC36);
    public static final TextColor ELEMENT_ELECTRIC = TextColor.color(0xF7B52F);
    public static final TextColor ELEMENT_AETHER = TextColor.color(0x8F56FF);
    
    public static final TextColor NUMBER = TextColor.color(0xE7FFF3);
    public static final TextColor TICK = TextColor.color(0xFF9C0A);
    
    public static final TextColor ULTIMATE_RESOURCE_ENERGY = TextColor.color(0x41B2F2);
    public static final TextColor ULTIMATE_OVERCHARGE = TextColor.color(0xCB2FCD);
    
    public static final TextColor RESOURCE_CAT_COINS = TextColor.color(0xF2AA24);
    public static final TextColor RESOURCE_RUBY = TextColor.color(0xE0283E);
    
    public static final TextColor ARCHETYPE_HEXBANE = TextColor.color(0x735136);
    public static final TextColor ARCHETYPE_DEFENSE = TextColor.color(0xE1C62A);
    
    public static final TextColor FLOWER_DEAD = TextColor.color(0x372E2B);
    public static final TextColor FLOWER_ROSE = TextColor.color(0xEC4346);
    public static final TextColor FLOWER_TULIP = TextColor.color(0xEC88C3);
    
    public static final TextColor EFFECT_BLEED = TextColor.color(0x980002);
    public static final TextColor EFFECT_HELLBURN = TextColor.color(0xCA0825);
    
    public static final TextColor ABYSS = TextColor.color(0x530D88);
    public static final TextColor ABYSSAL_CURSE = TextColor.color(0xA60228);
    
    public static final TextColor THE_KINGDOM = TextColor.color(0xB8860B);
    public static final TextColor THE_WITHERS = TextColor.color(0x444477);
    public static final TextColor THE_JUNGLE = TextColor.color(0x228B22);
    public static final TextColor MERCENARY = TextColor.color(0xDC143C);
    public static final TextColor BLOOD = TextColor.color(0x8B0000);
    public static final TextColor THE_SPACE = TextColor.color(0xA020F0);
    public static final TextColor HELL = TextColor.color(0x8B0000);
    
    public static final TextColor SOUL = TextColor.color(0x00B2BA);
    public static final TextColor BLOOD_PURPLE = TextColor.color(0x780839);
    public static final TextColor SKIN_COLOR_0 = TextColor.color(0xFFDBAC);
    public static final TextColor DECAY = TextColor.color(0x695E57);
    public static final TextColor DECAY_LIGHTER = TextColor.color(0x9B8F88);
    
    public static final TextColor RARITY_COMMON = TextColor.color(0xA0A0A0);
    public static final TextColor RARITY_UNCOMMON = TextColor.color(0x2ECC71);
    public static final TextColor RARITY_RARE = TextColor.color(0x3498DB);
    public static final TextColor RARITY_EPIC = TextColor.color(0x9B59B6);
    public static final TextColor RARITY_LEGENDARY = TextColor.color(0xF39C12);
    public static final TextColor RARITY_MYTHIC = TextColor.color(0xE74C3C);
    
    public static final TextColor DROP_CHANCE_GUARANTEED = TextColor.color(0x4CAF50);
    public static final TextColor DROP_CHANCE_COMMON = TextColor.color(0x8BC34A);
    public static final TextColor DROP_CHANCE_UNCOMMON = TextColor.color(0x2196F3);
    public static final TextColor DROP_CHANCE_RARE = TextColor.color(0x673AB7);
    public static final TextColor DROP_CHANCE_VERY_RARE = TextColor.color(0x9C27B0);
    public static final TextColor DROP_CHANCE_RNGESUS = TextColor.color(0xFF9800);
    public static final TextColor DROP_CHANCE_INSANE = TextColor.color(0xF44336);
    
    public static final TextColor RESTLESS_SOUL = TextColor.color(0x19B4BF);
    public static final TextColor CHAOS = TextColor.color(0x660499);
    public static final TextColor VOID = TextColor.color(0x5F3087);
    public static final TextColor SHARK = TextColor.color(0x96B7C0);
    
    private Colors() {
    }
    
}
