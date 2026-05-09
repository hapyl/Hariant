package me.hapyl.hariant.object;

import com.google.common.collect.Sets;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class ObjectManagerImpl implements ObjectManager {
    
    private final Set<HariantObject> objects;
    
    public ObjectManagerImpl() {
        this.objects = Sets.newHashSet();
    }
    
    @Override
    public void createObject(@NotNull HariantObject object) {
        object.create();
        
        objects.add(object);
    }
    
    @Override
    public void tick() {
        objects.removeIf(HariantObject::removeIfShould);
        objects.forEach(HariantObject::tick);
    }
    
    @Override
    public void reset() {
        objects.forEach(HariantObject::remove);
    }
}
