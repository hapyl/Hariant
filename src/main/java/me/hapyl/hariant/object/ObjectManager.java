package me.hapyl.hariant.object;

import me.hapyl.eterna.module.util.Ticking;
import me.hapyl.hariant.util.Resettable;
import org.jetbrains.annotations.NotNull;

public interface ObjectManager extends Ticking, Resettable {
    
    void createObject(@NotNull HariantObject object);
    
    @Override
    void tick();
    
    @Override
    void reset();
}
