package me.hapyl.hariant.entity.damage.report;

import com.google.common.collect.Lists;
import me.hapyl.eterna.module.text.Capitalizable;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.damage.DamageInstance;
import me.hapyl.hariant.entity.damage.DamageSource;
import me.hapyl.hariant.entity.damage.mutator.DamageMutator;
import me.hapyl.hariant.util.Hoverable;
import me.hapyl.hariant.util.Identified;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class DamageReport implements Hoverable {
    
    private final DamageInstance damageInstance;
    private final List<Step> stepList;
    
    public DamageReport(@NotNull DamageInstance damageInstance) {
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
        
        builder.append(Component.text("DAMAGE REPORT", Colors.GOLD, TextDecoration.BOLD));
        builder.appendNewline();
        
        // Add source data
        builder.append(
                Component.empty()
                         .append(Component.text("SRC ", Colors.GRAY))
                         .append(damageSource.getIdentity().getName())
                         .appendNewline()
        );
        
        builder.append(
                Component.empty()
                         .append(Component.text("ENT ", Colors.GRAY))
                         .append(entity.getName()).color(Colors.WHITE)
                         .appendNewline()
        );
        
        builder.append(
                Component.empty()
                         .append(Component.text("ATK ", Colors.GRAY))
                         .append(attacker != null ? attacker.getName() : Component.text("None!", Colors.DARK_GRAY))
                         .appendNewline()
        );
        
        builder.append(
                Component.empty()
                         .append(Component.text("ELM ", Colors.GRAY))
                         .append(damageSource.getElementType().getName().color(Colors.WHITE))
                         .appendNewline()
        );
        
        builder.append(
                Component.empty()
                         .append(Component.text("DTP ", Colors.GRAY))
                         .append(damageSource.getDamageType().getName().color(Colors.WHITE))
                         .appendNewline()
        );
        
        builder.append(
                Component.empty()
                         .append(Component.text("FLG ", Colors.GRAY))
                         .append(
                                 (Component) damageSource.getDamageFlags()
                                                         .stream()
                                                         .map(Capitalizable::capitalize)
                                                         .collect(
                                                                 Collectors.collectingAndThen(Collectors.joining(", "), then -> then.isEmpty()
                                                                                                                                ? Component.text("None!", Colors.DARK_GRAY)
                                                                                                                                : Component.text(then, Colors.WHITE))
                                                         )
                         )
        );
        
        // Append steps
        builder.appendNewline();
        builder.appendNewline();
        builder.append(Component.text("COMPONENTS", Colors.GOLD, TextDecoration.BOLD));
        builder.appendNewline();
        
        for (int i = 0; i < stepList.size(); i++) {
            final Step step = stepList.get(i);
            
            if (i != 0) {
                builder.appendNewline();
            }
            
            builder.append(
                    Component.empty()
                             .append(Component.text(" ", Colors.DARK_GRAY).append(Component.text(step.identify(), Colors.DARK_GRAY)))
                             .appendNewline()
                             .append(Component.text("  ").append(step))
            );
        }
        
        return HoverEvent.showText(builder.build());
    }
    
    public void report(@NotNull Identified identified, @NotNull DamageMutator damageMutator, double value, double damageBeforeMutation, double damageAfterMutation) {
        this.stepList.add(new StepImpl(identified, damageMutator, value, damageBeforeMutation, damageAfterMutation));
    }
    
}
