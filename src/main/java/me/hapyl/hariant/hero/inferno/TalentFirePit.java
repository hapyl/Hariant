package me.hapyl.hariant.hero.inferno;

import com.google.common.collect.Maps;
import me.hapyl.eterna.module.location.LocationHelper;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.attribute.AttributeScaling;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.WarningType;
import me.hapyl.hariant.entity.damage.*;
import me.hapyl.hariant.entity.damage.component.DamageComponents;
import me.hapyl.hariant.entity.player.DelegateType;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.talent.Response;
import me.hapyl.hariant.talent.Talent;
import me.hapyl.hariant.talent.TalentContext;
import me.hapyl.hariant.talent.field.DisplayField;
import me.hapyl.hariant.talent.target.TalentTarget;
import me.hapyl.hariant.task.HariantTickingTask;
import me.hapyl.hariant.task.Scheduler;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.data.BlockData;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;

public final class TalentFirePit extends Talent {
    
    private final int[][] firePitsOffsets = {
            { 0, 0 },
            { 3, 3 },
            { 3, -3 },
            { -3, 3 },
            { -3, -3 }
    };
    
    private final int[][] infernoFireOffsets = {
            { 0, 0 },
            { 1, 0 },
            { 0, 1 },
            { -1, 0 },
            { 0, -1 }
    };
    
    private final BlockData[] firePitsMaterials = {
            Material.BLACK_TERRACOTTA.createBlockData(),
            Material.BROWN_TERRACOTTA.createBlockData(),
            Material.ORANGE_TERRACOTTA.createBlockData(),
            Material.RED_TERRACOTTA.createBlockData()
    };
    
    private final @DisplayField Decimal transformationDelay = Decimal.ofSeconds(1f);
    private final @DisplayField Decimal totalStages = Decimal.ofValue(firePitsMaterials.length);
    
    private final @DisplayField AttributeScaling damage = AttributeScaling.create(AttributeType.ATTACK, 67.2);
    private final @DisplayField Decimal damagePeriod = Decimal.ofSeconds(0.25f);
    private final @DisplayField Decimal elementalApplication = Decimal.ofElementalApplication(ElementType.FIRE, 500);
    
    private final int transformationDelayPerStage = transformationDelay.intValue() / totalStages.intValue();
    private final Key damageCooldownKey = Key.ofString("fire_pit_damage");
    
    private final DamageSourceIdentity damageSourceIdentity = DamageSourceIdentity.createOfNamed(
            this,
            Key.ofString("fire_pit_damage_source"),
            DeathMessage.createWithDefaultKiller("{player} was hellburnt to death")
    );
    
    public TalentFirePit(@NotNull Key key) {
        super(key, Component.text("Fire Pit"), Icon.ofMaterial(Material.FIRE_CHARGE));
        
        setDurationSeconds(2.5f);
        setCooldownSeconds(20);
        
        setDescription(
                Component.empty()
                         .append(Component.text("Create five "))
                         .append(Component.text("fire pits", Colors.RED))
                         .append(Component.text(" around yourself which transform into "))
                         .append(Component.text("soul fire", Colors.AQUA))
                         .append(Component.text(" after "))
                         .append(transformationDelay)
                         .append(Component.text("."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("Stepping into fire deals "))
                         .append(ElementType.FIRE.asComponentDamage())
                         .append(Component.text(" and applies large amount of "))
                         .append(ElementType.FIRE.asComponent())
                         .append(Component.text(" anomaly."))
        );
    }
    
    @Override
    public @NotNull TalentTarget target(@NotNull HariantPlayer player) {
        return TalentTarget.none();
    }
    
    @Override
    public @NotNull Response execute(@NotNull HariantPlayer player, @NotNull TalentContext context) {
        final Location location = LocationHelper.anchor(LocationHelper.center(player.getLocation()));
        
        for (int[] offset : firePitsOffsets) {
            final Location firePitLocation = LocationHelper.anchor(LocationHelper.copyOf(location).add(offset[0], 0, offset[1]));
            
            player.delegate(new FirePit(player, firePitLocation), DelegateType.INTERRUPTABLE);
        }
        
        return Response.ok();
    }
    
    private class FirePit extends HariantTickingTask {
        
        private final HariantPlayer player;
        private final Location origin;
        private final BoundingBox boundingBox;
        private final Map<Location, InfernoFire> firePits;
        private final DamageSource damageSource;
        
        private final int transformationDelay = TalentFirePit.this.transformationDelay.intValue();
        
        FirePit(@NotNull HariantPlayer player, @NotNull Location origin) {
            super(Scheduler.ofTimer());
            
            this.player = player;
            this.origin = origin;
            this.boundingBox = LocationHelper.toBoundingBox(origin, 1.5, 1, 1.5);
            this.firePits = Maps.newHashMap();
            this.damageSource = new FirePitDamageSource(player);
            
            // Prepare fire pit locations
            for (int[] offset : infernoFireOffsets) {
                final Location firePitLocation = LocationHelper.copyOf(origin).add(offset[0], 0, offset[1]);
                
                // If location is NOT air, skip it
                if (!firePitLocation.getBlock().isEmpty()) {
                    continue;
                }
                
                final InfernoFire infernoFire = new InfernoFire(player, firePitLocation);
                final Location location = firePitLocation.subtract(0, 1, 0);
                
                firePits.put(location, infernoFire);
            }
        }
        
        @Override
        public void run(int tick) {
            final int stage = tick / transformationDelayPerStage;
            final boolean isLit = tick >= transformationDelay;
            
            if (tick > getDuration() + transformationDelay) {
                this.cancel();
                return;
            }
            
            // Change the stage if not at max stage yet
            if (tick % transformationDelayPerStage == 0 && stage < totalStages.intValue()) {
                firePits.keySet().forEach(location -> {
                    Hariant.globalBlockChange(location, firePitsMaterials[stage]);
                    
                    // Fx
                    player.playWorldSound(location, Sound.ITEM_FLINTANDSTEEL_USE, 0.25f, 0.75f + (0.75f * stage / totalStages.intValue()));
                });
            }
            // Otherwise light the fire
            else if (tick == transformationDelay) {
                firePits.values().forEach(InfernoFire::light);
            }
            
            // Tick fire pits
            player.collectNearbyEntities(boundingBox)
                  .filter(player::canAffect)
                  .forEach(entity -> {
                      final BoundingBox entityBoundingBox = entity.getBoundingBox();
                      
                      for (InfernoFire infernoFire : firePits.values()) {
                          if (entityBoundingBox.overlaps(infernoFire.getBoundingBox())) {
                              // If the fire is lit, deal damage
                              if (isLit) {
                                  entity.damage(damageSource);
                              }
                              
                              // Always show danger
                              entity.showWarning(WarningType.DANGER, 2);
                              break;
                          }
                      }
                  });
        }
        
        @Override
        public void onCancel() {
            this.extinguish();
        }
        
        public void extinguish() {
            firePits.forEach((location, firePit) -> {
                Hariant.globalBlockChange(location);
                firePit.dispose();
            });
            
            // Fx
            player.playWorldSound(origin, Sound.BLOCK_FIRE_EXTINGUISH, 0.25f, 0.75f);
        }
    }
    
    private class FirePitDamageSource extends DamageSourceImpl {
        
        FirePitDamageSource(@NotNull HariantPlayer source) {
            super(damageSourceIdentity, source, DamageType.TALENT, ElementType.FIRE, DamageComponents.ofTrueDamage(), Set.of(), damage.getScaledValue(source), elementalApplication.doubleValue(), damageCooldownKey, damagePeriod.intValue());
        }
        
    }
    
}