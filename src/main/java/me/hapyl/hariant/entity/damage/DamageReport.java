package me.hapyl.hariant.entity.damage;

import com.google.common.collect.Lists;
import me.hapyl.eterna.module.text.Capitalizable;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.util.Hoverable;
import me.hapyl.hariant.util.Identified;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class DamageReport implements Hoverable {
    
    private final DamageInstance damageInstance;
    private final List<Step> stepList;
    
    DamageReport(@NotNull DamageInstance damageInstance) {
        this.damageInstance = damageInstance;
        this.stepList = Lists.newArrayList(new StepBaseImpl(damageInstance.getSource().getDamage()));
    }
    
    @NotNull
    @Override
    public HoverEvent<?> createHoverEvent() {
        final TextComponent.Builder builder = Component.text();
        
        final DamageSource damageSource = damageInstance.getSource();
        final HariantEntity entity = damageInstance.getEntity();
        final HariantEntity attacker = damageInstance.getAttacker();
        
        builder.append(Component.text("DAMAGE REPORT", NamedTextColor.GOLD, TextDecoration.BOLD));
        builder.appendNewline();
        
        // Add source data
        builder.append(
                Component.empty()
                         .append(Component.text("Source: ", NamedTextColor.GRAY))
                         .append(damageSource.getIdentity().getName())
                         .appendNewline()
        );
        
        builder.append(
                Component.empty()
                         .append(Component.text("Entity: ", NamedTextColor.GRAY))
                         .append(entity.getName()).color(NamedTextColor.WHITE)
                         .appendNewline()
        );
        
        builder.append(
                Component.empty()
                         .append(Component.text("Attacker: ", NamedTextColor.GRAY))
                         .append(attacker != null ? attacker.getName() : Component.text("None!", NamedTextColor.DARK_GRAY))
                         .appendNewline()
        );
        
        builder.append(
                Component.empty()
                         .append(Component.text("Element: ", NamedTextColor.GRAY))
                         .append(Component.text(damageSource.getElementType().name(), NamedTextColor.WHITE))
                         .appendNewline()
        );
        
        builder.append(
                Component.empty()
                         .append(Component.text("Damage Type: ", NamedTextColor.GRAY))
                         .append(Component.text(damageSource.getDamageType().name(), NamedTextColor.WHITE))
                         .appendNewline()
        );
        
        builder.append(
                Component.empty()
                         .append(Component.text("Damage Flags: ", NamedTextColor.GRAY))
                         .append(
                                 (Component) damageSource.getDamageFlags()
                                                         .stream()
                                                         .map(Capitalizable::capitalize)
                                                         .collect(
                                                                 Collectors.collectingAndThen(Collectors.joining(", "), then -> then.isEmpty()
                                                                                                                                ? Component.text("None!", NamedTextColor.DARK_GRAY)
                                                                                                                                : Component.text(then, NamedTextColor.WHITE))
                                                         )
                         )
        );
        
        // Append steps
        builder.appendNewline();
        builder.appendNewline();
        builder.append(Component.text("COMPONENTS", NamedTextColor.GOLD, TextDecoration.BOLD));
        builder.appendNewline();
        
        for (int i = 0; i < stepList.size(); i++) {
            final Step step = stepList.get(i);
            
            if (i != 0) {
                builder.appendNewline();
            }
            
            builder.append(
                    Component.empty()
                             .append(Component.text(" ").append(Component.text(step.identify(), NamedTextColor.DARK_GRAY)))
                             .appendNewline()
                             .append(Component.text("  ").append(step))
            );
        }
        
        return HoverEvent.showText(builder.build());
    }
    
    void report(@NotNull Identified identified, double multiplier, double damageBeforeMultiplier) {
        this.stepList.add(new StepImpl(identified, multiplier, damageBeforeMultiplier));
    }
    
    public interface Step extends Identified, ComponentLike {
        @NotNull
        @Override
        String identify();
        
        @NotNull
        @Override
        Component asComponent();
    }
    
    public static class StepImpl implements Step {
        private final Identified identified;
        private final Component component;
        
        StepImpl(@NotNull Identified identified, double multiplier, double damageBeforeMultiplier) {
            this.identified = identified;
            this.component = Component.text("%,.1f * %,.3f = %,.1f".formatted(damageBeforeMultiplier, multiplier, damageBeforeMultiplier * multiplier), NamedTextColor.GRAY);
        }
        
        @NotNull
        @Override
        public String identify() {
            return identified.identify();
        }
        
        @NotNull
        @Override
        public Component asComponent() {
            return component;
        }
    }
    
    public static class StepBaseImpl implements Step {
        private final Component component;
        
        StepBaseImpl(double damage) {
            this.component = Component.text("%,.1f".formatted(damage), NamedTextColor.GRAY);
            
        }
        
        @NotNull
        @Override
        public String identify() {
            return "base";
        }
        
        @NotNull
        @Override
        public Component asComponent() {
            return component;
        }
    }
    
}
