package me.hapyl.hariant.entity.damage;

import me.hapyl.hariant.Colors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DeathMessageImpl implements DeathMessage {
    
    static final String PLACEHOLDER_PLAYER = "{player}";
    static final String PLACEHOLDER_KILLER = "{killer}";
    
    static final String DEFAULT_KILLER_SUFFIX = "[with help from {killer}]";
    
    private final Component withoutKiller;
    private final Component withKiller;
    
    DeathMessageImpl(@NotNull String template) {
        if (!template.contains(PLACEHOLDER_PLAYER)) {
            throw new IllegalArgumentException("Template must contain `%s`!".formatted(PLACEHOLDER_PLAYER));
        }
        else if (!template.contains(PLACEHOLDER_KILLER)) {
            throw new IllegalArgumentException("Template must contain `%s`!".formatted(PLACEHOLDER_KILLER));
        }
        
        // Split template into two strings and just combine on deathMessage()
        final int optionalStart = template.indexOf('[');
        final int optionalEnd = template.indexOf(']');
        
        if (optionalStart == -1 || optionalEnd == -1) {
            throw new IllegalArgumentException("Template must contain optional part!");
        }
        
        // FIXME (xanyjl @ Thursday, July 16) -> This forces killer part to be at the end ???
        
        final String baseString = (template.substring(0, optionalStart) + template.substring(optionalEnd + 1)).trim();
        final String optionalString = template.substring(optionalStart + 1, optionalEnd).trim();
        
        this.withoutKiller = Component.text(baseString, Colors.GRAY);
        this.withKiller = Component.text(baseString, Colors.GRAY).appendSpace().append(Component.text(optionalString, Colors.GRAY));
    }
    
    @NotNull
    @Override
    public Component deathMessage(@NotNull DeathComponent player, @Nullable DeathComponent killer, @NotNull List<? extends DeathComponent> assists) {
        Component message = (killer == null ? withoutKiller : withKiller).replaceText(builder -> builder.matchLiteral(PLACEHOLDER_PLAYER).replacement(player.asDeathComponent()));
        
        if (killer != null) {
            message = message.replaceText(builder -> builder.matchLiteral(PLACEHOLDER_KILLER).replacement(killer.asDeathComponent()));
        }
        
        if (!assists.isEmpty()) {
            message = message.appendSpace().append(createAssistsComponent(assists));
        }
        
        return message;
    }
    
    @NotNull
    private static Component createAssistsComponent(@NotNull List<? extends DeathComponent> assists) {
        final TextComponent.Builder builder = Component.text();
        builder.append(Component.text("(", Colors.DARK_GRAY));
        
        for (int i = 0; i < assists.size(); i++) {
            if (i != 0) {
                builder.append(Component.text(", ", Colors.DARK_GRAY));
            }
            
            builder.append(assists.get(i).asAssistComponent());
        }
        
        return builder.append(Component.text(")", Colors.DARK_GRAY)).build();
    }
    
}