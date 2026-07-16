package me.hapyl.hariant.hero.mage;

import com.google.common.collect.Lists;
import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.eterna.module.location.LocationHelper;
import me.hapyl.eterna.module.util.Disposable;
import me.hapyl.eterna.module.util.Ticking;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.hero.HeroRegistry;
import me.hapyl.hariant.task.executor.Promise;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public final class SoulStorm implements Ticking, Disposable {
    
    private static final ItemStack SOUL_HEAD = ItemBuilder.playerHead("fd4c142c382a6be00caeee9a02306f06544e6e46b953bcc8cac65e5c89d4790a").asIcon();
    
    private final HariantPlayer player;
    private final List<Entity> entities;
    private final Promise promise;
    
    private final int maxCharges;
    private int tick;
    
    SoulStorm(@NotNull HariantPlayer player, int charges, int maxCharges, @NotNull Promise promise) {
        this.player = player;
        this.maxCharges = maxCharges;
        this.promise = promise;
        this.entities = Lists.newArrayList();
        
        // Create souls
        final Location location = player.getLocation();
        
        for (int i = 0; i < charges; i++) {
            this.entities.add(createSoul(location));
        }
        
        // Give player another weapon
        HeroRegistry.MAGE.giveWeapon(player, true);
    }
    
    @Override
    public void dispose() {
        // Return normal weapon
        HeroRegistry.MAGE.giveWeapon(player, false);
        
        promise.fulfil();
        
        // Cleanup
        entities.forEach(Entity::remove);
        entities.clear();
    }
    
    @Override
    public void tick() {
        // Animate souls
        final Location location = player.getLocationInFront(-1);
        
        final float yaw = location.getYaw();
        final double yawRadians = Math.toRadians(yaw);
        
        final double rightX = Math.cos(yawRadians);
        final double rightZ = Math.sin(yawRadians);
        
        final int charges = entities.size();
        final double spread = Math.PI * 2 / Math.max(1, charges);
        
        for (int i = 0; i < charges; i++) {
            final Entity entity = entities.get(i);
            final double angle = Math.PI * 2 * Math.toRadians(tick) * 0.5 + spread * i;
            
            final double x = Math.cos(angle) * 0.6 * rightX;
            final double y = Math.sin(angle) * 0.6 + 0.5;
            final double z = Math.cos(angle) * 0.6 * rightZ;
            
            LocationHelper.offset(location, x, y, z, () -> {
                entity.teleport(location);
                
                // Fx
                if (tick % 5 == 0) {
                    LocationHelper.offset(location, 0, 1, 0, () -> player.spawnWorldParticle(location, Particle.SOUL_FIRE_FLAME, 1, 0));
                }
            });
        }
        
        tick++;
        
        // Show how many souls are left
        player.sendSubtitle(
                Component.empty()
                         .append(Component.text("🔥".repeat(charges), Colors.RESTLESS_SOUL))
                         .append(Component.text("🔥".repeat(maxCharges - charges), Colors.DARK_GRAY)),
                0, 5, 5
        );
    }
    
    public void decrementCharge() {
        if (entities.isEmpty()) {
            return;
        }
        
        final Entity entity = entities.removeLast();
        
        if (entity != null) {
            entity.remove();
        }
    }
    
    public boolean isEmpty() {
        return entities.isEmpty();
    }
    
    private static @NotNull Entity createSoul(@NotNull Location location) {
        return location.getWorld().spawn(location, ArmorStand.class, self -> {
            self.setMarker(true);
            self.setSilent(true);
            self.setInvisible(true);
            self.getEquipment().setHelmet(SOUL_HEAD);
            
            Objects.requireNonNull(self.getAttribute(Attribute.SCALE)).setBaseValue(0.5);
        });
    }
}
