package me.hapyl.hariant.hero.alchemist;

import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.text.RomanNumber;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.entity.damage.DamageSource;
import me.hapyl.hariant.entity.damage.DamageSourceIdentity;
import me.hapyl.hariant.entity.damage.DeathMessage;
import me.hapyl.hariant.entity.effect.status.StatusEffectType;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.hero.HeroData;
import me.hapyl.hariant.profile.ui.ActionbarSupplier;
import me.hapyl.hariant.talent.TalentRegistry;
import me.hapyl.hariant.util.Definition;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HeroDataAlchemist extends HeroData<HeroAlchemist> implements ActionbarSupplier {
    
    private static final DamageSourceIdentity OVERDOSE = DamageSourceIdentity.create(
            Key.ofString("overdose"),
            Component.text("Abyssal Contamination"),
            DeathMessage.createWithDefaultKiller("{player} was contaminated by the Abyss")
    );
    
    @NotNull private State state;
    
    @Nullable private AlchemistPotionInstance potionInstance;
    @Nullable private HariantEntityAlchemicalCauldron alchemicalCauldron;
    
    private double abyssalCorrosion;
    private int alchemicalMadness;
    
    public HeroDataAlchemist(@NotNull HeroAlchemist hero, @NotNull HariantPlayer player) {
        super(hero, player);
        
        this.state = State.NORMAL;
    }
    
    @Nullable
    public HariantEntityAlchemicalCauldron getAlchemicalCauldron() {
        return alchemicalCauldron;
    }
    
    public void setAlchemicalCauldron(@Nullable HariantEntityAlchemicalCauldron newCauldron) {
        if (alchemicalCauldron != null) {
            alchemicalCauldron.remove();
        }
        
        alchemicalCauldron = newCauldron;
    }
    
    public int getAlchemicalMadness() {
        return alchemicalMadness;
    }
    
    public void setAlchemicalMadness(int alchemicalMadness) {
        this.alchemicalMadness = alchemicalMadness;
    }
    
    @Override
    public void dispose() {
        if (alchemicalCauldron != null) {
            alchemicalCauldron.remove();
        }
    }
    
    @Override
    public void tick() {
        // Tick abyssal corrosion
        if (abyssalCorrosion > 0) {
            // If corrosion is higher than or equals to maximum, just die
            if (abyssalCorrosion >= TalentRegistry.ABYSSAL_CORROSION.maximumCorrosion.doubleValue()) {
                player.die(DamageSource.death(OVERDOSE).build());
                return;
            }
            
            abyssalCorrosion = Math.max(0, abyssalCorrosion - TalentRegistry.ABYSSAL_CORROSION.corrosionDecrementPerTick);
            
            // Affect corrosion
            final int corrosionLevel = this.getAbyssalCorrosionLevel();
            
            if (corrosionLevel >= 1) {
                player.addEffect(StatusEffectType.ABYSSAL_CORROSION_1, 5, player);
            }
            
            if (corrosionLevel >= 2) {
                player.addEffect(StatusEffectType.ABYSSAL_CORROSION_2, 5, player);
            }
            
            if (corrosionLevel >= 3) {
                player.addEffect(StatusEffectType.ABYSSAL_CORROSION_3, 5, player);
            }
        }
        
        // Tick potion instance
        if (potionInstance != null) {
            final boolean isBreak = potionInstance.tick();
            
            if (isBreak || potionInstance.isOver()) {
                potionInstance = null;
            }
        }
        
        // Tick madness
        if (alchemicalMadness > 0) {
            alchemicalMadness--;
            
            // Bump weapon update
            if (alchemicalMadness == 0) {
                hero.giveWeapon(player);
            }
        }
        
        // Tick cauldron removal
        if (alchemicalCauldron != null && alchemicalCauldron.isDead()) {
            alchemicalCauldron = null;
            hero.giveWeapon(player);
        }
    }
    
    @Nullable
    public AlchemistPotionInstance getPotionInstance() {
        return potionInstance;
    }
    
    public void setPotionInstance(@Nullable AlchemistPotionInstance potionInstance) {
        this.potionInstance = potionInstance;
    }
    
    @NotNull
    public State getState() {
        return state;
    }
    
    public void setState(@NotNull State state) {
        if (this.state == state) {
            this.player.messageError(Component.text("Already in this state!"));
            return;
        }
        
        this.state = state;
        this.state.apply(player);
    }
    
    public double getAbyssalCorrosion() {
        return abyssalCorrosion;
    }
    
    public int getAbyssalCorrosionLevel() {
        final TalentAbyssalCorrosion talent = TalentRegistry.ABYSSAL_CORROSION;
        
        if (abyssalCorrosion >= talent.corrosionThreshold3.doubleValue()) {
            return 3;
        }
        else if (abyssalCorrosion >= talent.corrosionThreshold2.doubleValue()) {
            return 2;
        }
        else if (abyssalCorrosion >= talent.corrosionThreshold1.doubleValue()) {
            return 1;
        }
        
        return 0;
    }
    
    public void incrementCorrosion(double abyssalCorrosion) {
        this.abyssalCorrosion += abyssalCorrosion;
    }
    
    public @NotNull Component getAlchemicalMadnessFormatted() {
        final Definition definition = Definition.ALCHEMICAL_MADNESS;
        final Style style = definition.getStyle();
        
        return Component.empty()
                        .append(definition.getPrefixStyled())
                        .appendSpace()
                        .append(Component.text(Tick.format(alchemicalMadness), style));
    }
    
    @Override
    public @NotNull List<Component> supplyActionbar(@NotNull HariantPlayer player) {
        final Style abyssalCorrosionStyle = Definition.ABYSSAL_CORROSION.getStyle();
        final int abyssalCorrosionLevel = this.getAbyssalCorrosionLevel();
        
        return List.of(
                // Append corrosion
                Component.empty()
                         .append(Definition.ABYSSAL_CORROSION.getPrefixStyled())
                         .appendSpace()
                         .append(Component.text("%.0f".formatted(abyssalCorrosion), abyssalCorrosionStyle))
                         .appendSpace()
                         .append(
                                 abyssalCorrosionLevel == 0
                                 ? Component.text("✗", abyssalCorrosionStyle)
                                 : Component.text(RomanNumber.toRoman(abyssalCorrosionLevel), abyssalCorrosionStyle)
                         ),
                
                // Append current potion
                potionInstance != null
                ? Component.empty()
                           .append(Component.text("\uD83E\uDDEA ", Colors.DARK_PURPLE))
                           .append(Component.text(Tick.format(potionInstance.currentTick()), Colors.LIGHT_PURPLE))
                : Component.empty(),
                
                // Append cauldron
                alchemicalCauldron != null ? alchemicalCauldron.asComponent() : Component.empty(),
                
                // Append madness
                alchemicalMadness > 0 ? this.getAlchemicalMadnessFormatted() : Component.empty()
        );
    }
}
