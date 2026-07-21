package me.hapyl.hariant.hero.zealot;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.modifier.AttributeModifier;
import me.hapyl.hariant.attribute.modifier.AttributeModifierType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.hero.HeroRegistry;
import me.hapyl.hariant.talent.Response;
import me.hapyl.hariant.talent.Talent;
import me.hapyl.hariant.talent.TalentContext;
import me.hapyl.hariant.talent.TalentType;
import me.hapyl.hariant.talent.field.DisplayField;
import me.hapyl.hariant.talent.target.TalentTarget;
import me.hapyl.hariant.ui.ComponentDisplay;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;

public final class TalentPsionicOverload extends Talent {
    
    private final @DisplayField Decimal ferocityIncrease = Decimal.ofAttribute(AttributeType.FEROCITY, 100);
    private final @DisplayField Decimal movementSpeedIncrease = Decimal.ofAttribute(AttributeType.MOVEMENT_SPEED, 25);
    
    public TalentPsionicOverload(@NotNull Key key) {
        super(key, Component.text("Psionic Overload"), Icon.ofMaterial(Material.HORN_CORAL_FAN));
        
        setTalentType(TalentType.ENHANCE);
        
        setDurationSeconds(4);
        setCooldownSeconds(14);
        
        setDescription(
                Component.empty()
                         .append(Component.text("Overload your equipment with "))
                         .append(Component.text("Psionic", Colors.YELLOW))
                         .append(Component.text(" energy, gaining "))
                         .append(AttributeType.FEROCITY)
                         .append(Component.text(" and "))
                         .appendNewline()
                         .append(AttributeType.MOVEMENT_SPEED)
                         .append(Component.text(" buffs for "))
                         .append(this.getDurationFormatted())
                         .append(Component.text("."))
        );
    }
    
    @Override
    public @NotNull TalentTarget target(@NotNull HariantPlayer player) {
        return TalentTarget.none();
    }
    
    @Override
    public @NotNull Response execute(@NotNull HariantPlayer player, @NotNull TalentContext context) {
        player.getAttributes().addModifier(new ModifierPsionicOverload(player));
        
        return Response.ok();
    }
    
    private class ModifierPsionicOverload extends AttributeModifier {
        
        ModifierPsionicOverload(@NotNull HariantEntity applier) {
            super(TalentPsionicOverload.this, applier, TalentPsionicOverload.this.getDuration());
            
            of(AttributeType.FEROCITY, AttributeModifierType.FLAT, ferocityIncrease);
            of(AttributeType.MOVEMENT_SPEED, AttributeModifierType.FLAT, movementSpeedIncrease);
        }
        
        @Override
        public void onApply(@NotNull HariantEntity entity, @NotNull HariantEntity applier, int duration) {
            if (entity instanceof HariantPlayer player) {
                HeroRegistry.ZEALOT.equip(player, false);
            }
            
            // Fx
            entity.playWorldSound(Sound.ENTITY_ELDER_GUARDIAN_AMBIENT_LAND, 0.0f);
        }
        
        @Override
        public void onRemove(@NotNull HariantEntity entity, @NotNull HariantEntity applier) {
            // Return original equipment
            if (entity instanceof HariantPlayer player) {
                HeroRegistry.ZEALOT.equip(player, true);
            }
            
            // Fx
            entity.playWorldSound(Sound.ENTITY_ELDER_GUARDIAN_AMBIENT_LAND, 1.0f);
        }
        
        @Override
        public void display(@NotNull Location location) {
            ComponentDisplay.ofAscend(TalentPsionicOverload.this.getName().color(Colors.LIGHT_PURPLE), location, 20, 1.0f);
        }
        
    }
    
}