package me.hapyl.hariant.hero.blast_knight;

import me.hapyl.hariant.entity.cooldown.Cooldown;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.hero.HeroData;
import me.hapyl.hariant.profile.ui.ActionbarSupplier;
import me.hapyl.hariant.talent.TalentRegistry;
import me.hapyl.hariant.util.Definition;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class HeroDataBlastKnight extends HeroData<HeroBlastKnight> implements ActionbarSupplier {
    
    private @Nullable TalentQuantumWard.StoneCastle stoneCastle;
    private @NotNull ShieldState shieldState;
    
    private int quantumEnergy;
    
    public HeroDataBlastKnight(@NotNull HeroBlastKnight hero, @NotNull HariantPlayer player) {
        super(hero, player);
        
        this.shieldState = ShieldState.NOT_BLOCKING;
    }
    
    public @Nullable TalentQuantumWard.StoneCastle getStoneCastle() {
        return stoneCastle;
    }
    
    public int getQuantumEnergy() {
        return quantumEnergy;
    }
    
    @Override
    public void dispose() {
        this.removeStoneCastle();
        this.quantumEnergy = 0;
        this.shieldState = ShieldState.NOT_BLOCKING;
    }
    
    @Override
    public void tick() {
        if (this.stoneCastle != null) {
            this.stoneCastle.tick();
            
            // If the target entity has died, remove the ward
            if (this.stoneCastle.shouldRemove()) {
                this.removeStoneCastle();
                this.player.setCooldown(TalentRegistry.QUANTUM_WARD);
            }
        }
    }
    
    public void createStoneCastle(@NotNull TalentQuantumWard.StoneCastle stoneCastle) {
        this.removeStoneCastle();
        this.stoneCastle = stoneCastle;
    }
    
    public void removeStoneCastle() {
        if (stoneCastle != null) {
            stoneCastle.remove();
            stoneCastle = null;
        }
    }
    
    public @NotNull ShieldState getShieldState() {
        return shieldState;
    }
    
    public void setShieldState(@NotNull ShieldState shieldState) {
        this.shieldState = shieldState;
    }
    
    public void incrementQuantumEnergy(int value) {
        this.quantumEnergy = Math.min(this.quantumEnergy + value, TalentRegistry.QUANTUM_SHIELD.getMaximumQuantumEnergy().intValue());
    }
    
    public void resetQuantumEnergy() {
        this.quantumEnergy = 0;
    }
    
    @Override
    public @NotNull List<Component> supplyActionbar(@NotNull HariantPlayer player) {
        final Style styleQuantumEnergy = Definition.QUANTUM_ENERGY.getStyle();
        
        return List.of(
                Component.empty()
                         .append(Definition.QUANTUM_ENERGY.getPrefixStyled())
                         .appendSpace()
                         .append(Component.text(quantumEnergy, styleQuantumEnergy)),
                stoneCastle != null ? stoneCastle.asComponent() : Component.empty()
        );
    }
    
    public void incrementQuantumEnergyIfNotOnCooldown(@NotNull Decimal energy, @NotNull Cooldown cooldown) {
        if (player.hasCooldown(cooldown)) {
            return;
        }
        
        incrementQuantumEnergy(energy.intValue());
        player.setCooldown(cooldown);
    }
    
}