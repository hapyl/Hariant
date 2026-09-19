package me.hapyl.hariant.entity.type;

import com.google.common.collect.Sets;
import me.hapyl.eterna.module.component.ComponentList;
import me.hapyl.eterna.module.hologram.Hologram;
import me.hapyl.eterna.module.location.LocationHelper;
import me.hapyl.eterna.module.math.Tick;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.attribute.instance.Attributes;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.HariantDisplayEntity;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.TickSupplier;
import me.hapyl.hariant.entity.damage.DamageInstance;
import me.hapyl.hariant.task.HariantTickingTask;
import me.hapyl.hariant.task.Scheduler;
import me.hapyl.hariant.util.Models;
import me.hapyl.hariant.util.Resettable;
import me.hapyl.hariant.util.SoundFx;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class HariantEntityDummy extends HariantDisplayEntity {
    
    private static final double HOLOGRAM_OFFSET = 2.5;
    
    private static final Component HEAD_COMPONENT = createHeadComponent("5339d695bb37b5937a95963dab2bbc2a6e1cd8a2a25681e2991549c61c1d33dc");
    
    private final Hologram hologram;
    
    private final DamagePerSecond damagePerSecond;
    private final DamagePerRotation damagePerRotation;
    
    public HariantEntityDummy(@NotNull Location location) {
        super(Models.DUMMY, location, 3, Attributes.base(1_000_000, 100, 100));
        
        this.hologram = Hologram.ofTextDisplay(LocationHelper.getToTheRight(location, 2).add(0, 0.5, 0));
        this.hologram.showAll();
        
        this.damagePerSecond = new DamagePerSecond();
        this.damagePerRotation = new DamagePerRotation();
        
        this.setHurtSound(SoundFx.create(Sound.BLOCK_GRASS_BREAK, 0.0f));
    }
    
    @Override
    public void onDamageTaken(@NotNull DamageInstance damageInstance, @Nullable HariantEntity attacker) {
        // Ignore lethal damage
        if (damageInstance.isLethal()) {
            return;
        }
        
        this.damagePerSecond.increment(damageInstance);
        this.damagePerRotation.increment(damageInstance, this);
        
        // Fx
        new HariantTickingTask(Scheduler.ofTimer(1)) {
            private double theta = 0;
            
            @Override
            public void run(int tick) {
                if (theta >= Math.PI * 2) {
                    displayEntity.resetRotation();
                    this.cancel();
                    return;
                }
                
                displayEntity.editRotation(quaternion -> {
                    quaternion.x = (float) Math.cos(theta) * 0.2f;
                    quaternion.y = (float) Math.sin(theta) * 0.1f;
                    quaternion.z = 0;
                });
                
                theta += Math.PI * 0.3;
            }
        };
    }
    
    @Override
    public @NotNull Component asHeadComponent() {
        return HEAD_COMPONENT;
    }
    
    @Override
    public boolean tick() {
        if (!super.tick()) {
            return false;
        }
        
        if (damagePerRotation.lastDamageAt > 0 && localTicks() - damagePerRotation.lastDamageAt >= DamagePerRotation.RESET_THRESHOLD) {
            damagePerRotation.reset();
        }
        
        this.hologram.setLines(_ -> {
            final ComponentList components = ComponentList.empty();
            
            // Append elemental anomaly
            for (ElementType elementType : ElementType.values()) {
                components.append(elementData.getProgressBar(elementType));
            }
            
            // Append health
            components.appendEmpty();
            components.append(this.getHealthFormatted());
            
            // Append dps
            components.appendEmpty();
            components.append(damagePerSecond.asComponent());
            components.append(damagePerRotation.asComponent());
            
            return components;
        });
        
        // Sync display and hologram
        final double distanceToSquared = this.distanceToSquared(displayEntity);
        
        if (distanceToSquared >= 1) {
            final Location location = this.getLocation();
            
            displayEntity.teleport(location);
            hologram.teleport(location.add(0, HOLOGRAM_OFFSET, 0));
        }
        
        return true;
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        
        this.hologram.dispose();
    }
    
    @Override
    public @NotNull Component getName() {
        return Component.text("Dummy");
    }
    
    private static class DamagePerSecond implements ComponentLike {
        
        private final Set<Entry> entries;
        
        private DamagePerSecond() {
            this.entries = Sets.newHashSet();
        }
        
        public void increment(@NotNull DamageInstance damageInstance) {
            this.entries.add(new Entry(damageInstance.getDamage(), System.currentTimeMillis()));
        }
        
        @Override
        public @NotNull Component asComponent() {
            this.entries.removeIf(Entry::isExpired);
            
            final double dps = this.entries.stream().mapToDouble(Entry::damage).sum();
            
            return Component.text("ᴅᴘꜱ ", Colors.RED).append(Component.text("%,.0f".formatted(dps), Colors.WHITE, TextDecoration.BOLD));
        }
        
        private record Entry(double damage, long timestamp) {
            
            public boolean isExpired() {
                return System.currentTimeMillis() - timestamp > 1000L;
            }
        }
    }
    
    private static class DamagePerRotation implements ComponentLike, Resettable {
        
        private static final int RESET_THRESHOLD = Tick.fromSeconds(5);
        
        private double damage;
        private int lastDamageAt;
        
        DamagePerRotation() {
        }
        
        @Override
        public @NotNull Component asComponent() {
            return Component.text("ᴅᴘʀ ", Colors.GOLD).append(Component.text("%,.0f".formatted(damage), Colors.WHITE, TextDecoration.BOLD));
        }
        
        @Override
        public void reset() {
            damage = 0;
            lastDamageAt = 0;
        }
        
        public void increment(@NotNull DamageInstance damageInstance, @NotNull TickSupplier tickSupplier) {
            damage += damageInstance.getDamage();
            lastDamageAt = tickSupplier.localTicks();
        }
    }
    
}