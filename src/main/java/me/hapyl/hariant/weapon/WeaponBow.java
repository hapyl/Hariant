package me.hapyl.hariant.weapon;

import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.entity.NormalAttack;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.util.Icon;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class WeaponBow extends WeaponRange {
    
    private static final ItemStack ITEM_ARROW = new ItemBuilder(Material.ARROW)
            .setItemModel(Material.AIR)
            .setName(Component.text("Invisible Arrow"))
            .addLore()
            .addWrappedLore(
                    Component.empty()
                             .append(Component.text("Due to limitations of the greatest game in the world — Minecraft — it is impossible to shoot your bow without an arrow."))
                             .appendNewline()
                             .append(Component.text("I did my best to hide this arrow in your inventory, but it seems that you have found it, bravo!"))
            )
            .asIcon();
    
    public WeaponBow(@NotNull Key key, @NotNull NormalAttack normalAttackMelee, @NotNull NormalAttack normalAttackRanged) {
        super(key, Icon.ofMaterial(Material.BOW), normalAttackMelee, normalAttackRanged);
    }
    
    @Override
    public void onCreate(@NotNull HariantPlayer player) {
        // Since we use a vanilla bow, we must give an arrow to the player
        player.getInventory().setItem(9, ITEM_ARROW);
    }
    
    @Override
    public void startCooldown(@NotNull HariantPlayer player, int cooldown) {
        // Once again, bows are fucking annoying, so we have to set the cooldown to all bows,
        // to prevent vanilla cooldown behaviour... Furthermore, it HAS to be set via the vanilla
        // method, because sending a packet does not prevent the player from drawing their bow, for
        // some reason...
        player.getHandle().setCooldown(Material.BOW, cooldown);
    }
    
    @Override
    public final int getCooldown(@NotNull HariantPlayer player) {
        // Because of the reason above, call vanilla getCooldown on BOW
        return player.getHandle().getCooldown(Material.BOW);
    }
    
    @Override
    public final boolean hasCooldown(@NotNull HariantPlayer player) {
        // Because of the reason above, call vanilla hasCooldown on BOW
        return player.getHandle().hasCooldown(Material.BOW);
    }
    
    @NotNull
    @Override
    protected ItemBuilder createBuilder0() {
        // Because bows actually apply the cooldown when used (for some reason)
        // we cannot let the bow have a cooldown group, nor can we use `unsetComponents`
        // because that will remove the infinity enchantment, yeah, bows fucking suck...
        return new ItemBuilder(Material.BOW)
                .addEnchant(Enchantment.INFINITY, 1)
                .setUnbreakable()
                .hideComponents();
    }
}
