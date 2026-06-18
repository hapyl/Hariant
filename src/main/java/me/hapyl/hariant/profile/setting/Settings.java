package me.hapyl.hariant.profile.setting;

import com.google.common.collect.Maps;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.Compute;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.dialog.DialogSpeed;
import me.hapyl.hariant.profile.AutoReady;
import me.hapyl.hariant.util.Icon;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public final class Settings {
    
    public static final Setting<Boolean> COOLDOWN_FEEDBACK;
    public static final Setting<Boolean> ATTACK_COOLDOWN_FEEDBACK;
    public static final Setting<Boolean> ELIMINATION_FEEDBACK;
    public static final Setting<Boolean> COMBAT_FEEDBACK;
    public static final Setting<Boolean> ALLOW_PINGS;
    
    public static final Setting<DialogSpeed> DIALOG_SPEED;
    public static final Setting<AutoReady> AUTO_READY;
    
    private static final Map<SettingCategory, List<Setting<?>>> SETTINGS;
    
    static {
        SETTINGS = Maps.newHashMap();
        
        COOLDOWN_FEEDBACK = ofBoolean(
                Key.ofString("cooldown_feedback"),
                Component.text("Cooldown Feedback"),
                Component.text("Whether to show cooldown messages and play cooldown fx."),
                Icon.ofMaterial(Material.CLOCK),
                SettingCategory.GAMEPLAY,
                true
        );
        
        ATTACK_COOLDOWN_FEEDBACK = ofBoolean(
                Key.ofString("attack_cooldown_feedback"),
                Component.text("Attack Cooldown Feedback"),
                Component.text("Whether you will hear the \"pop\" sound when dealing damage while on cooldown."),
                Icon.ofMaterial(Material.WOODEN_SWORD),
                SettingCategory.QUALITY_OF_LIFE,
                true
        );
        
        ELIMINATION_FEEDBACK = ofBoolean(
                Key.ofString("elimination_feedback"),
                Component.text("Elimination/Assist Feedback"),
                Component.text("Whether you will see feedback for eliminations/assists."),
                Icon.ofMaterial(Material.SKELETON_SKULL),
                SettingCategory.GAMEPLAY,
                true
        );
        
        COMBAT_FEEDBACK = ofBoolean(
                Key.ofString("combat_feedback"),
                Component.text("Combat Feedback"),
                Component.empty()
                         .append(Component.text("Whether you'll see combat feedback in chat, which includes:"))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text(" • ", Colors.DARK_GRAY))
                         .append(Component.text("Damage dealt/taken."))
                         .appendNewline()
                         .append(Component.text(" • ", Colors.DARK_GRAY))
                         .append(Component.text("Damage done/received.")),
                Icon.ofMaterial(Material.SWEET_BERRIES),
                SettingCategory.QUALITY_OF_LIFE,
                false
        );
        
        DIALOG_SPEED = ofEnum(
                Key.ofString("dialog_speed"),
                Component.text("Dialog Speed"),
                Component.text("Defines the speed of dialogs."),
                Icon.ofMaterial(Material.FILLED_MAP),
                SettingCategory.QUALITY_OF_LIFE,
                DialogSpeed.NORMAL
        );
        
        AUTO_READY = ofEnum(
                Key.ofString("auto_ready"),
                Component.text("Auto Ready"),
                Component.text("Defines whether you ready up automatically."),
                Icon.ofMaterial(Material.ENDER_PEARL),
                SettingCategory.QUALITY_OF_LIFE,
                AutoReady.NEVER
        );
        
        ALLOW_PINGS = ofBoolean(
                Key.ofString("allow_chat_pings"),
                Component.text("Allow Pings"),
                Component.text("Whether you can be pinged via @ in chat."),
                Icon.ofMaterial(Material.GOLD_INGOT),
                SettingCategory.CHAT,
                true
        );
    }
    
    private Settings() {
    }
    
    @NotNull
    public static List<Setting<?>> listCategory(@NotNull SettingCategory category) {
        final List<Setting<?>> categorySettings = SETTINGS.get(category);
        
        return categorySettings != null ? List.copyOf(categorySettings) : List.of();
    }
    
    @NotNull
    private static <T, S extends Setting<T>> S register(@NotNull S setting) {
        SETTINGS.compute(setting.getCategory(), Compute.listAdd(setting));
        return setting;
    }
    
    @NotNull
    private static Setting<Boolean> ofBoolean(@NotNull Key key, @NotNull Component name, @NotNull Component description, @NotNull Icon icon, @NotNull SettingCategory category, boolean defaultValue) {
        return register(new SettingImplPrimitiveBoolean(key, name, description, icon, category, defaultValue));
    }
    
    @NotNull
    private static Setting<Integer> ofInteger(
            @NotNull Key key,
            @NotNull Component name,
            @NotNull Component description,
            @NotNull Icon icon,
            @NotNull SettingCategory category,
            int defaultValue,
            int minValue,
            int maxValue
    ) {
        return register(new SettingImplPrimitiveInteger(key, name, description, icon, category, defaultValue, minValue, maxValue));
    }
    
    @NotNull
    private static <E extends Enum<E> & ComponentLike> Setting<E> ofEnum(
            @NotNull Key key,
            @NotNull Component name,
            @NotNull Component description,
            @NotNull Icon icon,
            @NotNull SettingCategory category,
            @NotNull E defaultValue
    ) {
        return register(new SettingImplEnum<>(key, name, description, icon, category, defaultValue));
    }
    
}