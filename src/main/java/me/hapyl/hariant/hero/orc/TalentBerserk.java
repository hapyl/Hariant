package me.hapyl.hariant.hero.orc;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.modifier.AttributeModifier;
import me.hapyl.hariant.attribute.modifier.AttributeModifierType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.Outline;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.talent.TalentContext;
import me.hapyl.hariant.talent.TalentType;
import me.hapyl.hariant.talent.target.TalentTarget;
import me.hapyl.hariant.talent.ultimate.TalentUltimate;
import me.hapyl.hariant.talent.ultimate.UltimateResourceType;
import me.hapyl.hariant.task.executor.Executable;
import me.hapyl.hariant.ui.ComponentDisplay;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.decimal.Decimal;
import me.hapyl.hariant.util.field.DisplayField;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;

public final class TalentBerserk extends TalentUltimate {
    
    public static final Component COMPONENT_BERSERK_PREFIX = Component.text("💢", Colors.BERSERK);
    public static final Component COMPONENT_BERSERK = COMPONENT_BERSERK_PREFIX.append(Component.text(" Berserk", Colors.BERSERK));
    
    private final @DisplayField Decimal attackIncrease = Decimal.ofPercentage(100);
    private final @DisplayField Decimal critChanceIncrease = Decimal.ofAttribute(AttributeType.CRIT_CHANCE, 25);
    private final @DisplayField Decimal movementSpeedIncrease = Decimal.ofAttribute(AttributeType.MOVEMENT_SPEED, 25);
    
    private final Key modifierKey = Key.ofString("berserk_modifier");
    
    public TalentBerserk(@NotNull Key key) {
        super(key, Component.text("Berserk"), Icon.ofMaterial(Material.NETHER_WART), UltimateResourceType.ENERGY, 60);
        
        setDurationSeconds(10);
        setCooldownSeconds(20);
        
        setTalentType(TalentType.ENHANCE);
        
        setDescription(
                Component.empty()
                         .append(Component.text("Enter "))
                         .append(COMPONENT_BERSERK)
                         .append(Component.text(" for "))
                         .append(getDurationFormatted())
                         .append(Component.text("."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("Berserk", Colors.GOLD))
                         .appendNewline()
                         .append(Component.text("Drastically increases your "))
                         .append(AttributeType.ATTACK)
                         .append(Component.text(", "))
                         .appendNewline()
                         .append(AttributeType.CRIT_CHANCE)
                         .append(Component.text(" and "))
                         .append(AttributeType.MOVEMENT_SPEED)
                         .append(Component.text("."))
        );
    }
    
    @Override
    public @NotNull Executable execute(@NotNull HariantPlayer player, @NotNull TalentContext context, double consumedResource) {
        return Executable.execute(() -> enterBerserk(player, this.getDuration()));
    }
    
    @Override
    public @NotNull TalentTarget target(@NotNull HariantPlayer player) {
        return TalentTarget.none();
    }
    
    public void enterBerserk(@NotNull HariantPlayer player, int duration) {
        player.getAttributes().addModifier(new BerserkAttributeModifier(player, duration));
    }
    
    public boolean hasBerserk(HariantPlayer player) {
        return player.getAttributes().hasModifier(modifierKey);
    }
    
    public class BerserkAttributeModifier extends AttributeModifier {
        
        BerserkAttributeModifier(@NotNull HariantEntity applier, int duration) {
            super(modifierKey, TalentBerserk.this, applier, duration);
            
            of(AttributeType.ATTACK, AttributeModifierType.MULTIPLICATIVE, attackIncrease);
            of(AttributeType.MOVEMENT_SPEED, AttributeModifierType.FLAT, movementSpeedIncrease);
            of(AttributeType.CRIT_CHANCE, AttributeModifierType.FLAT, critChanceIncrease);
        }
        
        @Override
        public void onApply(@NotNull HariantEntity entity, @NotNull HariantEntity applier, int duration) {
            // Fx
            entity.setOutline(Outline.RED);
            entity.playWorldSound(Sound.ENTITY_ZOMBIFIED_PIGLIN_HURT, 3, 0.75f);
        }
        
        @Override
        public void onRemove(@NotNull HariantEntity entity, @NotNull HariantEntity applier) {
            // Fx
            entity.setOutline(Outline.CLEAR);
            entity.playWorldSound(Sound.ENTITY_PIGLIN_RETREAT, 1.25f);
        }
        
        @Override
        public void onTick(@NotNull HariantEntity entity, @NotNull HariantEntity applier, int tick, int duration) {
            // Fx
            if (tick % 20 == 0) {
                entity.playWorldSound(Sound.ENTITY_PIGLIN_AMBIENT, 0.75f);
                entity.playWorldSound(Sound.ENTITY_PIGLIN_ANGRY, 1.25f);
            }
            
            if (tick % 5 == 0) {
                entity.spawnWorldParticle(entity.getMidpointLocation(), Particle.LAVA, 2, 0.15, 0.25, 0.15, 0.1f);
            }
        }
        
        @Override
        public void display(@NotNull Location location) {
            ComponentDisplay.ofAscend(COMPONENT_BERSERK, location, 20, 1.25f);
        }
        
    }
    
}