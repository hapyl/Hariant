package me.hapyl.hariant.hero.pytaria.bee;

import me.hapyl.eterna.module.entity.Entities;
import me.hapyl.eterna.module.location.Distanced;
import me.hapyl.eterna.module.location.Located;
import me.hapyl.eterna.module.reflect.team.PacketTeamColor;
import me.hapyl.hariant.attribute.instance.Attributes;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.ImmunityResult;
import me.hapyl.hariant.entity.Pet;
import me.hapyl.hariant.entity.damage.DamageSource;
import me.hapyl.hariant.entity.damage.environment.EnvironmentDamageSource;
import me.hapyl.hariant.entity.player.HariantPlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Bee;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class BeePet extends HariantEntity implements Pet, Distanced, Located {
    
    private static final Attributes BEE_ATTRIBUTES = Attributes.base(1, 1, 1);
    
    private final HariantPlayer player;
    private final Component name;
    
    public @Nullable BeeTarget target;
    public int targetCooldown;
    
    BeePet(@NotNull HariantPlayer player, @NotNull Location location) {
        super(createBee(location), BEE_ATTRIBUTES);
        
        this.player = player;
        this.name = player.getName().append(Component.text("'s Bee"));
        this.setCollision(player, false);
        
        player.getPlayerTeam().addEntry(this);
        
        // Set glowing for owner
        setGlowing(player, PacketTeamColor.GOLD);
    }
    
    @NotNull
    @Override
    public HariantEntity owner() {
        return player;
    }
    
    @NotNull
    @Override
    public ImmunityResult isImmuneTo(@NotNull DamageSource source) {
        return ImmunityResult.ofBooleanSilent(source instanceof EnvironmentDamageSource);
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
    public @NotNull Component getName() {
        return name;
    }
    
    public void floatAround(@NotNull Location destination, double spread, int index, int tick) {
        final double radians = Math.toRadians(tick);
        final double currentSpread = (spread * index);
        
        final double x = Math.sin(radians * 4 + currentSpread) * 0.7;
        final double y = Math.sin(radians * 10 + currentSpread * 3) * 0.1;
        final double z = Math.cos(radians * 4 + currentSpread) * 0.7;
        
        final double randomSpread = 0.05;
        
        destination.add(x + random.nextSignedDouble(randomSpread), y, z + random.nextSignedDouble(randomSpread));
        destination.setYaw((float) Math.toDegrees(Math.atan2(-z, -x)));
        
        setLocation(destination);
        setAngry(false);
    }
    
    public void unsetTarget(int cooldown) {
        this.target = null;
        this.targetCooldown = cooldown;
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