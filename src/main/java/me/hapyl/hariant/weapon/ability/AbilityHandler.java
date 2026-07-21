package me.hapyl.hariant.weapon.ability;

import me.hapyl.eterna.module.math.Tick;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.hero.Hero;
import me.hapyl.hariant.profile.setting.Settings;
import me.hapyl.hariant.talent.Response;
import me.hapyl.hariant.weapon.Weapon;
import net.kyori.adventure.text.Component;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;

public final class AbilityHandler implements Listener {
    
    @EventHandler
    public void handlePlayerInteractEvent(PlayerInteractEvent ev) {
        final HariantPlayer player = Hariant.getPlayer(ev.getPlayer()).orElse(null);
        final Action action = ev.getAction();
        
        if (ev.getHand() == EquipmentSlot.OFF_HAND || player == null || action == Action.PHYSICAL) {
            return;
        }
        
        processAbility(player, action.isLeftClick() ? AbilityType.LEFT_CLICK : AbilityType.RIGHT_CLICK);
    }
    
    @EventHandler
    public void handlePlayerToggleSneakEvent(PlayerToggleSneakEvent ev) {
        // Only trigger when the player sneaks, not un-sneaks
        if (ev.isSneaking()) {
            return;
        }
        
        final HariantPlayer player = Hariant.getPlayer(ev.getPlayer()).orElse(null);
        
        if (player == null) {
            return;
        }
        
        processAbility(player, AbilityType.SNEAK);
    }
    
    private static void processAbility(@NotNull HariantPlayer player, @NotNull AbilityType abilityType) {
        // Make sure that we clicked with a selected weapon slot
        final Hero hero = player.getHero();
        final Weapon weapon = hero.getWeapon(player);
        
        final int heldItemSlot = player.getInventory().getHeldItemSlot();
        
        if (heldItemSlot != hero.getWeaponSlot(player)) {
            return;
        }
        
        // Process ability
        final Ability ability = weapon.getAbility(abilityType);
        
        // Explicit `AbilityDescriptionOnly` check for faster performance
        if (ability == null || ability instanceof AbilityDescriptionOnly) {
            return;
        }
        
        // Cooldowns are a little weird for abilities, since they're stored on ability type, not the ability itself
        if (player.hasCooldown(ability)) {
            final int cooldownTimeLeft = player.getCooldownTimeLeft(ability);
            
            if (player.getSetting(Settings.COOLDOWN_FEEDBACK)) {
                player.messageError(
                        Component.empty()
                                 .append(Component.text("This ability is on cooldown for "))
                                 .append(Component.text(Tick.format(cooldownTimeLeft)))
                                 .append(Component.text("!"))
                );
                player.playSound(Sound.ENTITY_ENDERMAN_TELEPORT, 0.0f);
            }
            return;
        }
        
        final Response response = ability.execute(player);
        
        if (response.isError()) {
            player.messageError(Component.text(response.getReason()));
            return;
        }
        
        // Handle cooldown
        response.getStatus().setCooldown(player, ability);
    }
    
}
