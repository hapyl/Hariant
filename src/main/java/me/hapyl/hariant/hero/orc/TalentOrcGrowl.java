package me.hapyl.hariant.hero.orc;

import me.hapyl.eterna.module.location.LocationHelper;
import me.hapyl.eterna.module.math.geometry.Geometry;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.modifier.AttributeModifier;
import me.hapyl.hariant.attribute.modifier.AttributeModifierType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.damage.AssistSource;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.talent.Response;
import me.hapyl.hariant.talent.Talent;
import me.hapyl.hariant.talent.TalentContext;
import me.hapyl.hariant.talent.TalentType;
import me.hapyl.hariant.talent.target.TalentTarget;
import me.hapyl.hariant.ui.ComponentDisplay;
import me.hapyl.hariant.ui.ComponentDisplayAnimation;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.decimal.Decimal;
import me.hapyl.hariant.util.field.DisplayField;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;

public final class TalentOrcGrowl extends Talent {
    
    private final @DisplayField Decimal radius = Decimal.ofValue(8);
    private final @DisplayField Decimal debuffDuration = Decimal.ofSeconds(6);
    private final @DisplayField Decimal attackDecrease = Decimal.ofPercentage(20);
    private final @DisplayField Decimal movementSpeedDecrease = Decimal.ofPercentage(50);
    
    private final Key modifierKey = Key.ofString("orc_growl_attribute_modifier");
    
    public TalentOrcGrowl(@NotNull Key key) {
        super(key, Component.text("Orc's Growl"), Icon.ofMaterial(Material.GOAT_HORN));
        
        setCooldownSeconds(20);
        
        setTalentType(TalentType.IMPAIR);
        
        setDescription(
                Component.empty()
                         .append(Component.text("Growl with your beautiful and dealy voice, scaring nearby "))
                         .append(Component.text("enemies", Colors.RED))
                         .append(Component.text("."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("Scared enemies have their "))
                         .append(AttributeType.ATTACK)
                         .append(Component.text(" and "))
                         .append(AttributeType.MOVEMENT_SPEED)
                         .append(Component.text(" reduced for "))
                         .append(debuffDuration)
                         .append(Component.text("."))
        );
    }
    
    @Override
    public @NotNull TalentTarget target(@NotNull HariantPlayer player) {
        return TalentTarget.none();
    }
    
    @Override
    public @NotNull Response execute(@NotNull HariantPlayer player, @NotNull TalentContext context) {
        player.collectNearbyEntities(radius)
              .filter(player::canAffect)
              .forEach(entity -> {
                  // Check for Effect RES
                  if (entity.hasEffectResistance(AssistSource.create(player, this))) {
                      return;
                  }
                  
                  entity.getAttributes().addModifier(new OrcGrowlAttributeModifier(player));
              });
        
        // Fx
        Geometry.drawPolygon(LocationHelper.anchor(player.getLocation()), 7, radius.doubleValue(), 0.5, location -> {
            player.spawnWorldParticle(location, Particle.ENCHANTED_HIT, 1, 0.5f);
            player.spawnWorldParticle(location, Particle.RAID_OMEN, 1, 0.1f);
        });
        
        player.playWorldSound(Sound.ENTITY_ENDER_DRAGON_GROWL, 2.0f);
        
        return Response.ok();
    }
    
    public class OrcGrowlAttributeModifier extends AttributeModifier {
        
        private static final ComponentDisplay COMPONENT_DISPLAY = new ComponentDisplay(Component.text("ꜱᴄᴀʀᴇᴅ", Colors.BERSERK), ComponentDisplayAnimation.ofSineAscend(), 20, 1.25f);
        
        OrcGrowlAttributeModifier(@NotNull HariantEntity source) {
            super(modifierKey, TalentOrcGrowl.this, source, debuffDuration.intValue());
            
            of(AttributeType.ATTACK, AttributeModifierType.MULTIPLICATIVE, -attackDecrease.doubleValue());
            of(AttributeType.MOVEMENT_SPEED, AttributeModifierType.MULTIPLICATIVE, -movementSpeedDecrease.doubleValue());
        }
        
        @Override
        public void display(@NotNull Location location) {
            COMPONENT_DISPLAY.display(location);
        }
        
    }
    
}