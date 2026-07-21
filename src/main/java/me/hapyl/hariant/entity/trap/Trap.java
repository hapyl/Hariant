package me.hapyl.hariant.entity.trap;

import me.hapyl.eterna.module.annotate.EventLike;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.Ticking;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.VanillaAttributeModifier;
import me.hapyl.hariant.entity.damage.AssistSource;
import me.hapyl.hariant.util.ComponentProgress;
import me.hapyl.hariant.util.Prioritable;
import me.hapyl.hariant.util.Priority;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Input;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Trap implements Ticking, AssistSource, Prioritable {
    
    private static final TextColor[] GRADIENT = {
            TextColor.color(0x0A8BF5),
            TextColor.color(0x0B81F5),
            TextColor.color(0x0D77F5),
            TextColor.color(0x0E6DF6),
            TextColor.color(0x0F63F6),
            TextColor.color(0x115AF6),
            TextColor.color(0x1250F6),
            TextColor.color(0x1346F6),
            TextColor.color(0x143CF7),
            TextColor.color(0x1728F7),
            TextColor.color(0x1728F7),
            TextColor.color(0x143CF7),
            TextColor.color(0x1346F6),
            TextColor.color(0x1250F6),
            TextColor.color(0x115AF6),
            TextColor.color(0x0F63F6),
            TextColor.color(0x0E6DF6),
            TextColor.color(0x0D77F5),
            TextColor.color(0x0B81F5),
            TextColor.color(0x0A8BF5)
    };
    
    private static final Key MODIFIER_KEY = Key.ofString("trap");
    
    private static final List<? extends VanillaAttributeModifier> ATTRIBUTES = List.of(
            VanillaAttributeModifier.create(MODIFIER_KEY, Attribute.JUMP_STRENGTH, VanillaAttributeModifier.Operation.FLAT, -100),
            VanillaAttributeModifier.create(MODIFIER_KEY, Attribute.MOVEMENT_SPEED, VanillaAttributeModifier.Operation.FLAT, -100)
    );
    
    private static final long KEY_COOLDOWN = 100;
    private static final double SPEEDUP = 0.05;
    
    public final @NotNull HariantEntity entity;
    public final @NotNull HariantEntity source;
    public final @NotNull TrapName name;
    
    private final int duration;
    private final int speedup;
    private int tick;
    
    private @NotNull TrapKey trapKey;
    private long lastInput;
    
    public Trap(@NotNull HariantEntity entity, @NotNull HariantEntity source, @NotNull TrapName trapName, int duration) {
        this.entity = entity;
        this.source = source;
        this.trapKey = entity.random.nextBoolean() ? TrapKey.LEFT : TrapKey.RIGHT;
        this.name = trapName;
        this.duration = duration;
        this.speedup = (int) (duration * SPEEDUP);
        this.lastInput = System.currentTimeMillis() + 100;
    }
    
    public boolean input(@NotNull Input input) {
        // Check for internal cooldown
        final long currentTimeMillis = System.currentTimeMillis();
        
        if (currentTimeMillis - lastInput < KEY_COOLDOWN) {
            return false;
        }
        
        // Check for input key
        if (!trapKey.test(input)) {
            return false;
        }
        
        // Flip the key
        this.trapKey = trapKey.flipValue();
        this.lastInput = currentTimeMillis;
        
        // Increment tick
        this.tick += speedup;
        
        // Fx
        this.entity.playSound(Sound.UI_BUTTON_CLICK, 1.25f);
        
        // Return whether player has escaped
        return tick > duration;
    }
    
    @EventLike
    public void onTrap() {
    }
    
    @EventLike
    public void onEscape(@NotNull TrapEscape escape) {
    }
    
    public final void onTrap0() {
        ATTRIBUTES.forEach(entity::addVanillaAttributeModifier);
        this.onTrap();
    }
    
    public final void onEscape0(@NotNull TrapEscape escape) {
        ATTRIBUTES.forEach(entity::removeVanillaAttributeModifier);
        this.onEscape(escape);
    }
    
    @Override
    public void tick() {
        // Display the current key and trap name
        this.tickTitle();
        
        this.tick++;
    }
    
    public int currentTick() {
        return tick;
    }
    
    public void tickTitle() {
        final TextColor buttonColor = GRADIENT[tick % GRADIENT.length];
        
        final Component title = ComponentProgress.create(name.getName(), name.getStyle(), 1 - (double) tick / duration);
        final Component subTitle = Component.empty()
                                            .append(TrapKey.LEFT.asComponent().color(TrapKey.LEFT == trapKey ? buttonColor : Colors.DARK_GRAY))
                                            .append(Component.text("     "))
                                            .append(TrapKey.RIGHT.asComponent().color(TrapKey.RIGHT == trapKey ? buttonColor : Colors.DARK_GRAY));
        
        entity.showTitle(Title.title(title, subTitle, 0, 5, 0));
    }
    
    public boolean isOver() {
        return entity.isDead() || tick > duration;
    }
    
    @Override
    public @NotNull Priority getPriority() {
        return Priority.NORMAL;
    }
    
    public boolean blocksTalents() {
        return false;
    }
    
    public boolean blocksAttacks() {
        return false;
    }
    
    @Override
    public @NotNull HariantEntity source() {
        return source;
    }
    
    @Override
    public @NotNull Component getName() {
        return Component.text(name.getName());
    }
    
}