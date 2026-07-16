package me.hapyl.hariant.entity;

import com.google.common.collect.Sets;
import me.hapyl.eterna.module.block.display.DisplayEntity;
import me.hapyl.eterna.module.block.display.DisplayModel;
import me.hapyl.eterna.module.entity.Entities;
import me.hapyl.hariant.attribute.instance.Attributes;
import me.hapyl.hariant.entity.damage.DamageSource;
import me.hapyl.hariant.entity.damage.DamageType;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.Set;

public class HariantDisplayEntity extends HariantEntity {
    
    protected final DisplayEntity displayEntity;
    
    public HariantDisplayEntity(@NotNull DisplayModel displayModel, @NotNull Location location, double size, @NotNull Attributes attributes) {
        super(createHitboxEntity(location), attributes);
        
        this.setHurtSound(null);
        this.setDeathSound(null);
        
        this.setSize(size);
        this.displayEntity = displayModel.spawn(location);
    }
    
    public void setSize(double size) {
        this.getVanillaAttribute(Attribute.SCALE).setBaseValue(size);
    }
    
    @Override
    @OverridingMethodsMustInvokeSuper
    @NotNull
    public ImmunityResult isImmuneTo(@NotNull DamageSource source) {
        final DamageType damageType = source.getDamageType();
        
        // Default immunity to all environment damage
        return ImmunityResult.ofBooleanSilent(damageType == DamageType.ENVIRONMENT);
    }
    
    @NotNull
    @Override
    public Set<? extends Entity> listGarbage() {
        final Set<Entity> garbage = Sets.newHashSet(entity);
        
        garbage.add(displayEntity.getHead());
        garbage.addAll(displayEntity.getChildren());
        
        return garbage;
    }
    
    @Override
    public @NotNull Component getName() {
        return super.getName();
    }
    
    @Override
    @OverridingMethodsMustInvokeSuper
    public void onDestroy() {
        // Remove the slime via `remove()` to not spawn particles
        entity.remove();
        
        this.displayEntity.remove();
    }
    
    @NotNull
    private static LivingEntity createHitboxEntity(@NotNull Location location) {
        return Entities.SLIME.spawn(location, self -> {
            // Set the size to 1 because we manage scale via attribute
            self.setSize(1);
            self.setAI(false);
            self.setInvisible(true);
            self.setSilent(true);
            self.setGravity(false);
        });
    }
}
