package me.hapyl.hariant.util.decimal;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public class DecimalAngleImpl extends DecimalImpl {
    
    public DecimalAngleImpl(double angle) {
        super(angle, DecimalAngleImpl::format);
    }
 
    public static @NotNull Component format(double angle) {
        return Component.text(angle).append(Component.text("°"));
    }
    
}