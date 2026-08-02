package me.hapyl.hariant.entity.player.combat;

import me.hapyl.hariant.entity.damage.DamageSourceIdentity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.jetbrains.annotations.NotNull;

public class Damage implements ComponentLike, Comparable<Damage> {
    
    private final DamageSourceIdentity identity;
    
    protected double damage;
    
    protected int totalHits;
    protected int lethalHits;
    
    Damage(@NotNull DamageSourceIdentity identity) {
        this.identity = identity;
        this.damage = 0;
    }
    
    public void sum(@NotNull Damage other) {
        this.damage += other.damage;
        this.totalHits += other.totalHits;
        this.lethalHits += other.lethalHits;
    }
    
    public @NotNull DamageSourceIdentity getIdentity() {
        return identity;
    }
    
    public double getDamage() {
        return damage;
    }
    
    public int getTotalHits() {
        return totalHits;
    }
    
    public int getLethalHits() {
        return lethalHits;
    }
    
    @NotNull
    @Override
    public Component asComponent() {
        return Component.empty()
                        .append(identity.getName())
                        .append(Component.text(" %,.0f (%s)".formatted(damage, identity.getKeyAsString())));
    }
    
    @Override
    public String toString() {
        return "Damage{" +
               "identity=" + identity +
               ", damage=" + damage +
               ", isLethal=" + lethalHits +
               ", totalHits=" + totalHits +
               '}';
    }
    
    @Override
    public int compareTo(@NotNull Damage that) {
        return Double.compare(that.damage, this.damage);
    }
    
}