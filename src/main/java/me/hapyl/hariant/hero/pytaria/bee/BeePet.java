package me.hapyl.hariant.hero.pytaria.bee;

import me.hapyl.eterna.module.entity.Entities;
import me.hapyl.eterna.module.location.Distanced;
import me.hapyl.eterna.module.location.Located;
import me.hapyl.eterna.module.util.Removable;
import me.hapyl.hariant.attribute.instance.Attributes;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.Pet;
import me.hapyl.hariant.entity.damage.DamageSource;
import me.hapyl.hariant.entity.damage.EnvironmentDamage;
import me.hapyl.hariant.entity.player.HariantPlayer;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Bee;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class BeePet extends HariantEntity implements Pet, Distanced, Located, Removable {
    
    private static final Attributes BEE_ATTRIBUTES = Attributes.base(1, 1, 1);
    
    private final HariantPlayer player;
    
    public HariantEntity target;
    
    BeePet(@NotNull HariantPlayer player, @NotNull Location location) {
        super(createBee(location), BEE_ATTRIBUTES);
        
        this.player = player;
        this.setCollision(player, false);
        
        player.getPlayerTeam().addEntry(this);
    }
    
    @NotNull
    @Override
    public HariantEntity owner() {
        return player;
    }
    
    @Override
    public boolean isImmuneTo(@NotNull DamageSource source) {
        return source instanceof EnvironmentDamage;
    }
    
    @NotNull
    @Override
    public Bee getHandle() {
        return (Bee) super.getHandle();
    }
    
    public void setAngry(boolean angry) {
        this.getHandle().setAnger(angry ? Integer.MAX_VALUE : 0);
    }
    
    @Override
    public void onDestroy() {
        entity.remove();
    }
    
    @NotNull
    private static Bee createBee(@NotNull Location location) {
        return Entities.BEE.spawn(location, self -> {
            Objects.requireNonNull(self.getAttribute(Attribute.SCALE)).setBaseValue(0.25);
            
            self.setAI(false);
            self.setSilent(true);
        });
    }
}
