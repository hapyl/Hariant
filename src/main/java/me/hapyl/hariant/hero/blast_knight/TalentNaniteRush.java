package me.hapyl.hariant.hero.blast_knight;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.attribute.AttributeScaling;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.effect.EffectType;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.entity.shield.Shield;
import me.hapyl.hariant.entity.shield.ShieldStrength;
import me.hapyl.hariant.event.Cancel;
import me.hapyl.hariant.event.HariantEffectEvent;
import me.hapyl.hariant.talent.TalentContext;
import me.hapyl.hariant.talent.TalentType;
import me.hapyl.hariant.talent.field.DisplayField;
import me.hapyl.hariant.talent.target.TalentTarget;
import me.hapyl.hariant.talent.ultimate.TalentUltimate;
import me.hapyl.hariant.talent.ultimate.UltimateResourceType;
import me.hapyl.hariant.task.executor.Executable;
import me.hapyl.hariant.task.executor.While;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public final class TalentNaniteRush extends TalentUltimate implements Listener {
    
    private final @DisplayField Decimal shieldStrengthAgainstAether = Decimal.ofPercentage(250);
    private final @DisplayField Decimal shieldDuration = Decimal.ofSeconds(30);
    private final @DisplayField Decimal shieldDebuffNullabilityLimit = Decimal.ofValue(3);
    
    private final @DisplayField AttributeScaling shieldMaximumCapacity = AttributeScaling.create(AttributeType.DEFENSE, 150);
    private final @DisplayField Decimal initialShieldCapacityOfMaximumCapacity = Decimal.ofPercentage(10);
    
    private final @DisplayField Decimal radius = Decimal.ofValue(6);
    
    private final ShieldStrength shieldStrength = ShieldStrength.builder()
                                                                .ofElement(ElementType.AETHER, shieldStrengthAgainstAether)
                                                                .build();
    
    private final Style effectResistanceStyle = AttributeType.EFFECT_RESISTANCE.getStyle();
    private final Cancel debuffCancel = Cancel.cancel(Component.empty().append(AttributeType.EFFECT_RESISTANCE.getPrefixStyled()).append(Component.text(" Phase Barrier", effectResistanceStyle)));
    
    public TalentNaniteRush(@NotNull Key key) {
        super(key, Component.text("Nanite Rush"), Icon.ofMaterial(Material.PURPLE_DYE), UltimateResourceType.ENERGY, 60);
        
        setTalentType(TalentType.DEFENSE);
        
        setDurationSeconds(1.5f);
        setCooldownSeconds(20);
        
        setDescription(
                Component.empty()
                         .append(Component.text("Release a "))
                         .append(Component.text("Nanite Swarm", Colors.LIGHT_PURPLE))
                         .append(Component.text(" that rushes upwards, creating and constantly regenerating "))
                         .append(Component.text("Phase Barrier", Colors.LIGHT_PURPLE))
                         .append(Component.text(" for nearby "))
                         .append(Component.text("teammates", Colors.GREEN))
                         .append(Component.text("."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("Phase Barrier", Colors.GOLD))
                         .appendNewline()
                         .append(Component.text("While protected by the shield, you cannot get affected by "))
                         .append(Component.text("de-buffs", Colors.RED))
                         .append(Component.text("."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("This effect can block up to %.0f de-buffs.".formatted(shieldDebuffNullabilityLimit.doubleValue()), Colors.DARK_GRAY))
        );
    }
    
    @Override
    public @NotNull Executable execute(@NotNull HariantPlayer player, @NotNull TalentContext context, double consumedResource) {
        final double maximumCapacity = shieldMaximumCapacity.getScaledValue(player);
        final double initialCapacity = maximumCapacity * initialShieldCapacityOfMaximumCapacity.doubleValue();
        final double shieldRegenerationPerTick = (maximumCapacity - initialCapacity) / (getDuration() - 1);
        
        return Executable.whilst(While.duration(this, tick -> {
            final Location location = player.getLocation();
            
            // Get nearby teammates
            player.collectNearbyEntities(location, radius)
                  .filter(player::isSelfOrTeammate)
                  .forEach(entity -> {
                      if (entity.getShield() instanceof PhaseBarrier existingShield) {
                          existingShield.regenerate(shieldRegenerationPerTick);
                      }
                      else {
                          entity.setShield(new PhaseBarrier(entity, player, maximumCapacity, initialCapacity));
                      }
                  });
            
            // Fx
            final float pitch = 0.5f + (1.5f * tick / getDuration());
            final double offsetXZ = radius.doubleValue() / 4;
            
            player.spawnWorldParticle(location, Particle.WITCH, 50, offsetXZ, 0.1d, offsetXZ, 1f);
            
            player.playWorldSound(location, Sound.ITEM_FLINTANDSTEEL_USE, pitch);
            player.playWorldSound(location, Sound.ENTITY_ENDER_DRAGON_FLAP, pitch);
            
            return false;
        }));
    }
    
    @Override
    public @NotNull TalentTarget target(@NotNull HariantPlayer player) {
        return TalentTarget.none();
    }
    
    @EventHandler
    public void handleHariantEffectEvent(HariantEffectEvent ev) {
        if (ev.getEffect().getEffectType() != EffectType.DEBUFF || ev.hasResisted()) {
            return;
        }
        
        final HariantEntity entity = ev.getEntity();
        
        if (entity.getShield() instanceof PhaseBarrier phaseBarrier) {
            if (phaseBarrier.nullateDebuff()) {
                ev.setCancel(debuffCancel);
            }
        }
    }
    
    public class PhaseBarrier extends Shield {
        
        private int debuffNullability;
        
        PhaseBarrier(@NotNull HariantEntity entity, @NotNull HariantEntity applier, double maximumCapacity, double initialCapacity) {
            super(entity, applier, shieldStrength, maximumCapacity, shieldDuration.intValue());
            this.setCapacity(initialCapacity);
            
            this.debuffNullability = shieldDebuffNullabilityLimit.intValue();
        }
        
        public boolean nullateDebuff() {
            if (debuffNullability <= 0) {
                return false;
            }
            
            // Decrement charges
            debuffNullability--;
            
            // Fx
            entity.playWorldSound(Sound.ENTITY_ILLUSIONER_CAST_SPELL, 0.75f);
            
            return true;
        }
        
        @Override
        public @NotNull Component asComponent() {
            final Component component = super.asComponent();
            
            return component.appendSpace()
                            .append(AttributeType.EFFECT_RESISTANCE.getPrefixStyled())
                            .append(Component.text(" %s".formatted(debuffNullability), effectResistanceStyle));
        }
        
    }
    
}