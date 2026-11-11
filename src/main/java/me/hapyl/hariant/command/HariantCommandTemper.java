package me.hapyl.hariant.command;

import me.hapyl.eterna.module.command.ArgumentList;
import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.StringList;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.HariantLogger;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.instance.AttributesInstance;
import me.hapyl.hariant.attribute.modifier.AttributeModifierType;
import me.hapyl.hariant.database.rank.PlayerRank;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.util.Ticks;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class HariantCommandTemper extends HariantPlayerCommand {
    
    private static final Key MODIFIER_KEY = Key.ofString("command");
    
    public HariantCommandTemper(@NotNull String name) {
        super(name, PlayerRank.ADMIN);
    }
    
    @Override
    public void execute(@NotNull Player player, @NotNull ArgumentList args, @NotNull PlayerRank playerRank) {
        // temper (attribute) (modifier_type) (amount)
        final AttributeType attributeType = args.get(0).toEnum(AttributeType.class);
        
        if (attributeType == null) {
            HariantLogger.error(player, Component.text("Invalid attribute!"));
            return;
        }
        
        final AttributeModifierType modifierType = args.get(1).toEnum(AttributeModifierType.class);
        
        if (modifierType == null) {
            HariantLogger.error(player, Component.text("Invalid modifier type!"));
            return;
        }
        
        final Tick value = Tick.ofTicks(args.get(2).toInt());
        
        final HariantPlayer hariantPlayer = Hariant.getPlayer(player).orElse(null);
        
        if (hariantPlayer == null) {
            HariantLogger.error(player, Component.text("You must be in a game to temper with attributes!"));
            return;
        }
        
        final AttributesInstance attributes = hariantPlayer.getAttributes();
        
        if (value.isZero()) {
            final boolean removed = attributes.removeModifier(MODIFIER_KEY);
            
            if (removed) {
                hariantPlayer.messageSuccess(Component.text("Removed modifier!"));
            }
            else {
                hariantPlayer.messageError(Component.text("You don't have the modifier applied!"));
            }
        }
        else {
            attributes.addModifier(MODIFIER_KEY, Ticks.INDEFINITE, hariantPlayer, adder -> adder.of(attributeType, modifierType, value.ticks()));
            
            hariantPlayer.messageSuccess(
                    Component.empty()
                             .append(Component.text("Added modifier for "))
                             .append(attributeType)
                             .append(Component.text(": "))
                             .append(modifierType.format(attributeType, value.ticks()))
                             .appendSpace()
                             .append(Component.text("(%s)".formatted(modifierType.name()), Colors.DARK_GRAY))
            );
        }
    }
    
    @Override
    public @NotNull List<String> tabComplete(@NotNull Player player, @NotNull ArgumentList args, @NotNull PlayerRank playerRank) {
        return switch (args.length) {
            case 1 -> StringList.ofEnumConstantLowercaseNames(AttributeType.class);
            case 2 -> StringList.ofEnumConstantLowercaseNames(AttributeModifierType.class);
            default -> List.of();
        };
    }
    
}