package me.hapyl.hariant.ui;

import me.hapyl.eterna.module.annotate.Mutates;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.entity.damage.DamageInstance;
import me.hapyl.hariant.task.HariantTickingTask;
import me.hapyl.hariant.task.Scheduler;
import me.hapyl.hariant.util.MathFont;
import me.hapyl.hariant.util.MatrixUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.ShadowColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.NotNull;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.Random;

public class ComponentDisplay {
    
    public static final ShadowColor NO_SHADOW = ShadowColor.shadowColor(0, 0, 0, 255);
    
    private final Component component;
    private final ComponentDisplayAnimation animation;
    private final int duration;
    private final float scale;
    
    public ComponentDisplay(@NotNull Component component, @NotNull ComponentDisplayAnimation animation, final int duration, final float scale) {
        this.component = component;
        this.animation = animation;
        this.duration = duration;
        this.scale = scale;
    }
    
    public void display(@NotNull @Mutates Location location) {
        // Randomize the location a little
        final Random random = Hariant.getRandom();
        location.add(random.nextDouble() * 0.75, random.nextDouble() * 0.35, random.nextDouble() * 0.75);
        
        final TextDisplay textDisplay = location.getWorld().spawn(location, TextDisplay.class, self -> {
            self.setBillboard(Display.Billboard.CENTER);
            self.setSeeThrough(true);
            self.setTeleportDuration(1);
            self.setInterpolationDuration(1);
            self.setTransformation(MatrixUtils.scale(scale));
            self.text(component);
            self.setBackgroundColor(Color.fromARGB(0, 0, 0, 0));
            self.setViewRange(16f);
        });
        
        final ComponentOrigin origin = new ComponentOrigin(textDisplay.getX(), textDisplay.getY(), textDisplay.getZ(), scale);
        
        new HariantTickingTask(Scheduler.ofTimer(1)) {
            @Override
            public void run(int tick) {
                if (tick > duration) {
                    this.cancel();
                    return;
                }
                
                animation.animate(textDisplay, origin, tick, duration);
            }
            
            @Override
            public void onCancel() {
                textDisplay.remove();
            }
        };
    }
    
    public static void ofAttributeBuff(@NotNull AttributeType attributeType, @NotNull Location location) {
        ofAttribute0(attributeType, ComponentDisplayAnimation.ofSineAscend(), location, Component.text("BUFF!", Colors.SUCCESS, TextDecoration.BOLD));
    }
    
    public static void ofAttributeDebuff(@NotNull AttributeType attributeType, @NotNull Location location) {
        ofAttribute0(attributeType, ComponentDisplayAnimation.ofSineDescend(), location, Component.text("DE-BUFF!", Colors.ERROR, TextDecoration.BOLD));
    }
    
    public static void ofDamage(@NotNull DamageInstance damageInstance, @NotNull Location location) {
        final Style style = damageInstance.getSource().getElementType().getStyle();
        
        final double damage = damageInstance.getDamage();
        final boolean critical = damageInstance.isCritical();
        
        new ComponentDisplay(
                Component.empty()
                         .append(Component.text(MathFont.format((int) damage), style).shadowColor(NO_SHADOW))
                         .append(critical ? Component.text("‼", style).shadowColor(NO_SHADOW) : Component.empty()),
                ComponentDisplayAnimation.ofFalloff(),
                20,
                1.75f
        ).display(location);
    }
    
    public static void ofAscend(@NotNull Component component, @NotNull Location location, int duration, float scale) {
        new ComponentDisplay(component, ComponentDisplayAnimation.ofSineAscend(), duration, scale).display(location);
    }
    
    public static void ofDescend(@NotNull Component component, @NotNull Location location, int duration, float scale) {
        new ComponentDisplay(component, ComponentDisplayAnimation.ofSineDescend(), duration, scale).display(location);
    }
    
    private static void ofAttribute0(@NotNull AttributeType attributeType, @NotNull ComponentDisplayAnimation animation, @NotNull Location location, @NotNull Component suffix) {
        new ComponentDisplay(
                Component.empty()
                         .append(attributeType.asComponent())
                         .appendSpace()
                         .append(suffix),
                animation,
                30, 1.0f
        ).display(location);
    }
    
    
}
