package me.hapyl.hariant.command;

import me.hapyl.eterna.module.command.ArgumentList;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.TypeConverter;
import me.hapyl.hariant.HariantConstants;
import me.hapyl.hariant.HariantLogger;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.instance.AttributesInstance;
import me.hapyl.hariant.attribute.modifier.AttributeModifierType;
import me.hapyl.hariant.database.rank.PlayerRank;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.stream.Collectors;

public class HariantTemperCommand extends HariantPlayerCommand {
    
    private static final Key MODIFIER_KEY = Key.ofString("command");
    
    public HariantTemperCommand(@NotNull String name) {
        super(name, PlayerRank.ADMIN);
    }
    
    @Override
    protected void execute(@NotNull Player player, @NotNull ArgumentList args, @NotNull PlayerRank playerRank) {
        final TypeConverter arg0 = args.get(0);
        final AttributeType attributeType = arg0.toEnum(AttributeType.class);
        
        final TypeConverter arg1 = args.get(1);
        final AttributeModifierType modifierType = arg1.toEnum(AttributeModifierType.class);
        
        final double value = args.get(2).toDouble();
        
        if (attributeType == null) {
            HariantLogger.error(player, Component.text("Invalid attribute `%s`! Valid attributes: %s".formatted(
                    arg0,
                    Arrays.stream(AttributeType.values()).map(Enum::name).collect(Collectors.joining(", "))
            )));
            return;
        }
        
        if (modifierType == null) {
            HariantLogger.error(player, Component.text("Invalid operation `%s`! Valid operations: %s".formatted(
                    arg1,
                    Arrays.stream(AttributeModifierType.values()).map(Enum::name).collect(Collectors.joining(", "))
            )));
            return;
        }
        
        asHariantPlayer(player, harp -> {
            final AttributesInstance attributes = harp.getAttributes();
            
            if (value == 0) {
                final boolean removed = attributes.removeModifier(MODIFIER_KEY);
                
                if (removed) {
                    harp.messageSuccess(Component.text("Removed modifier!"));
                }
                else {
                    harp.messageError(Component.text("You don't have the modifier applied!"));
                }
            }
            else {
                attributes.addModifier(MODIFIER_KEY, HariantConstants.INDEFINITE_DURATION, harp, adder -> adder.of(attributeType, modifierType, value));
                
                harp.messageSuccess(
                        Component.empty()
                                 .append(Component.text("Added modifier for "))
                                 .append(attributeType)
                                 .appendSpace()
                                 .append(modifierType.format(value))
                                 .appendSpace()
                                 .append(Component.text(" (%s)".formatted(modifierType.name()), NamedTextColor.DARK_GRAY))
                );
            }
        });
    }
}
