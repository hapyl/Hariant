package me.hapyl.hariant.profile.setting;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.util.Icon;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;

public final class Settings {
    
    public static final Setting<Boolean> COOLDOWN_FEEDBACK;
    public static final Setting<Boolean> ATTACK_COOLDOWN_FEEDBACK;
    public static final Setting<Boolean> ELIMINATION_FEEDBACK;
    public static final Setting<Boolean> COMBAT_FEEDBACK;
    
    static {
        COOLDOWN_FEEDBACK = SettingImpl.ofBoolean(
                Key.ofString("cooldown_feedback"),
                Component.text("Cooldown Feedback"),
                Component.text("Whether to show cooldown messages and play cooldown fx."),
                Icon.ofMaterial(Material.CLOCK),
                true
        );
        
        ATTACK_COOLDOWN_FEEDBACK = SettingImpl.ofBoolean(
                Key.ofString("attack_cooldown_feedback"),
                Component.text("Attack Cooldown Feedback"),
                Component.text("Whether you will hear the \"pop\" sound when dealing damage while on cooldown."),
                Icon.ofMaterial(Material.WOODEN_SWORD),
                true
        );
        
        ELIMINATION_FEEDBACK = SettingImpl.ofBoolean(
                Key.ofString("elimination_feedback"),
                Component.text("Elimination/Assist Feedback"),
                Component.text("Whether you will see feedback for eliminations/assists."),
                Icon.ofMaterial(Material.SKELETON_SKULL),
                true
        );
        
        COMBAT_FEEDBACK = SettingImpl.ofBoolean(
                Key.ofString("combat_feedback"),
                Component.text("Combat Feedback"),
                Component.empty()
                         .append(Component.text("Whether you'll see combat feedback in chat, which includes:"))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text(" • ", NamedTextColor.DARK_GRAY))
                         .append(Component.text("Damage dealt/taken."))
                         .appendNewline()
                         .append(Component.text(" • ", NamedTextColor.DARK_GRAY))
                         .append(Component.text("Damage done/received.")),
                Icon.ofMaterial(Material.SWEET_BERRIES),
                false
        );
    }
    
    private Settings() {
    }
    
}
