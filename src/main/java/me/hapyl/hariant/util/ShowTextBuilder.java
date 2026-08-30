package me.hapyl.hariant.util;

import me.hapyl.eterna.module.annotate.SelfReturn;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.event.HoverEventSource;
import org.jetbrains.annotations.NotNull;

import java.util.function.UnaryOperator;

public final class ShowTextBuilder implements Hoverable, HoverEventSource<Component> {
    
    private final TextComponent.Builder builder;
    
    ShowTextBuilder() {
        this.builder = Component.text();
    }
    
    @SelfReturn
    public ShowTextBuilder append(@NotNull Component component) {
        return this.append0(component);
    }
    
    @SelfReturn
    public ShowTextBuilder append(@NotNull ComponentLike component) {
        return this.append0(component.asComponent());
    }
    
    @SelfReturn
    public ShowTextBuilder append(@NotNull Component... components) {
        for (Component component : components) {
            this.append0(component);
        }
        
        return this;
    }
    
    @SelfReturn
    public ShowTextBuilder append(@NotNull Iterable<? extends Component> components) {
        for (Component component : components) {
            this.append0(component);
        }
        
        return this;
    }
    
    @SelfReturn
    public ShowTextBuilder appendNewline() {
        this.builder.appendNewline();
        return this;
    }
    
    @Override
    public @NotNull HoverEvent<Component> createHoverEvent() {
        return HoverEvent.showText(builder);
    }
    
    @Override
    public @NotNull HoverEvent<Component> asHoverEvent(@NotNull UnaryOperator<Component> op) {
        return this.createHoverEvent();
    }
    
    @SelfReturn
    private ShowTextBuilder append0(@NotNull Component component) {
        this.builder.append(component).appendNewline();
        return this;
    }
    
    public static @NotNull ShowTextBuilder builder() {
        return new ShowTextBuilder();
    }
    
}
