package me.hapyl.hariant.object;

import me.hapyl.eterna.module.util.Removable;
import me.hapyl.eterna.module.util.Ticking;

public interface HariantObject extends Ticking, Removable {
    
    @Override
    void tick();
    
    @Override
    void remove();
}
