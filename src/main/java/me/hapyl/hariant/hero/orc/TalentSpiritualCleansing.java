package me.hapyl.hariant.hero.orc;

import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.modifier.AttributeModifier;
import me.hapyl.hariant.attribute.modifier.AttributeModifierType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.effect.Effect;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.talent.TalentContext;
import me.hapyl.hariant.talent.TalentType;
import me.hapyl.hariant.talent.target.TalentTarget;
import me.hapyl.hariant.talent.ultimate.TalentUltimate;
import me.hapyl.hariant.talent.ultimate.UltimateResourceType;
import me.hapyl.hariant.task.executor.Executable;
import me.hapyl.hariant.task.executor.ExecutorService;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.SoundFx;
import me.hapyl.hariant.util.decimal.Decimal;
import me.hapyl.hariant.util.field.DisplayField;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class TalentSpiritualCleansing extends TalentUltimate {
    
    private static final Particle.Spell SPELL = new Particle.Spell(Color.fromRGB(143, 122, 199), 1);
    
    private final @DisplayField Decimal castingTime = Decimal.ofSeconds(0.6f);
    
    private final @DisplayField Decimal radius = Decimal.ofValue(7.5);
    private final @DisplayField Decimal effectResistanceIncrease = Decimal.ofAttribute(AttributeType.EFFECT_RESISTANCE, 50);
    
    private final Key modifierKey = Key.ofString("spiritiaul_cleansing_modifier");
    
    public TalentSpiritualCleansing(@NotNull Key key) {
        super(key, Component.text("Spiritual Cleansing"), Icon.ofMaterial(Material.MILK_BUCKET), UltimateResourceType.ENERGY, 60);
        
        setTalentType(TalentType.SUPPORT);
        
        setCooldown(Tick.ofSeconds(30));
        setDuration(Tick.ofSeconds(12));
        
        setSoundFx(SoundFx.create(Sound.ENTITY_GOAT_SCREAMING_MILK, 0.0f));
        
        setDescription(
                Component.empty()
                         .append(Component.text("Start chanting an ancient spell."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("After a short casting time, "))
                         .append(Component.text("cleanse", Colors.WHITE))
                         .append(Component.text(" all "))
                         .append(Component.text("negative", Colors.RED))
                         .append(Component.text(" effects from nearby teammates and increase their "))
                         .appendNewline()
                         .append(AttributeType.EFFECT_RESISTANCE)
                         .append(Component.text(" for "))
                         .append(this.asDurationComponentFormatted())
                         .append(Component.text("."))
        
        );
    }
    
    @Override
    public @NotNull Executable execute(@NotNull HariantPlayer player, @NotNull TalentContext context, double consumedResource) {
        return new ExecutorService()
                .then(Executable.execute(() -> {
                    // Fx
                    player.playWorldSound(Sound.ENTITY_ILLUSIONER_CAST_SPELL, 1.25f);
                    player.playWorldSound(Sound.ENTITY_ILLUSIONER_PREPARE_MIRROR, 1.25f);
                }))
                .then(Executable.later(() -> {
                    // We need a list capture because we need to know how many teammates were cleansed
                    final List<? extends HariantEntity> teammates = player.collectNearbyEntities(radius).filter(player::isSelfOrTeammate).toList();
                    
                    int totalEffectsCleansed = 0;
                    
                    for (HariantEntity teammate : teammates) {
                        // Cleanse negative effects
                        final int removedEffects = teammate.removeEffects(Effect::isDebuff);
                        totalEffectsCleansed += removedEffects;
                        
                        // Add modifier
                        teammate.getAttributes().addModifier(new AttributeModifierSpiritualCleansing(player));
                        
                        // Notify
                        if (removedEffects > 0) {
                            teammate.messageSuccess(
                                    player.equals(teammate)
                                    ? Component.text("You cleansed yourself off %s negative effects!".formatted(removedEffects))
                                    : player.getName().append(Component.text(" cleansed your off %s negative effects!".formatted(removedEffects)))
                            );
                        }
                        
                        // Fx
                        teammate.spawnWorldParticle(teammate.getMidpointLocation(), Particle.EFFECT, 20, 0.25, 0.5, 0.25, 0.7f, SPELL);
                        teammate.playWorldSound(Sound.ENTITY_WITCH_DRINK, 0.0f);
                    }
                }, castingTime.intValue()));
    }
    
    @Override
    public @NotNull TalentTarget target(@NotNull HariantPlayer player) {
        return TalentTarget.none();
    }
    
    public class AttributeModifierSpiritualCleansing extends AttributeModifier {
        
        AttributeModifierSpiritualCleansing(@NotNull HariantEntity applier) {
            super(modifierKey, TalentSpiritualCleansing.this, applier, Tick.INDEFINITE);
            
            of(AttributeType.EFFECT_RESISTANCE, AttributeModifierType.FLAT, effectResistanceIncrease);
        }
        
    }
    
}
