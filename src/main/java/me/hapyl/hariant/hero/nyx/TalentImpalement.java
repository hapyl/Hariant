package me.hapyl.hariant.hero.nyx;

import me.hapyl.eterna.module.block.display.BDEngine;
import me.hapyl.eterna.module.block.display.DisplayEntity;
import me.hapyl.eterna.module.block.display.DisplayModel;
import me.hapyl.eterna.module.location.LocationHelper;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.attribute.AttributeScaling;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.element.anomaly.EnumAnomaly;
import me.hapyl.hariant.entity.EntityCollector;
import me.hapyl.hariant.entity.damage.*;
import me.hapyl.hariant.entity.damage.component.DamageComponent;
import me.hapyl.hariant.entity.player.DelegateType;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.hero.HeroRegistry;
import me.hapyl.hariant.talent.TalentContext;
import me.hapyl.hariant.talent.field.DisplayField;
import me.hapyl.hariant.talent.target.TalentTarget;
import me.hapyl.hariant.talent.ultimate.ChargeLevel;
import me.hapyl.hariant.talent.ultimate.TalentUltimateOvercharge;
import me.hapyl.hariant.talent.ultimate.UltimateResourceType;
import me.hapyl.hariant.task.HariantTask;
import me.hapyl.hariant.task.HariantTickingStepTask;
import me.hapyl.hariant.task.HariantTickingTask;
import me.hapyl.hariant.task.Scheduler;
import me.hapyl.hariant.task.executor.Executable;
import me.hapyl.hariant.util.BoundingBoxBlueprint;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.entity.TextDisplay;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public final class TalentImpalement extends TalentUltimateOvercharge {
    
    private static final Component BULLET = Component.text("› ", Colors.DARK_GRAY);
    
    @DisplayField private final Decimal radius = Decimal.ofValue(4);
    @DisplayField private final Decimal radiusOvercharged = Decimal.ofValue(5);
    
    @DisplayField private final AttributeScaling damage = AttributeScaling.create(AttributeType.ATTACK, 54);
    @DisplayField private final AttributeScaling damageOvercharged = AttributeScaling.create(AttributeType.ATTACK, 81);
    
    @DisplayField private final Decimal duration = Decimal.ofSeconds(2f);
    @DisplayField private final Decimal durationOvercharged = Decimal.ofSeconds(2.8f);
    
    @DisplayField private final Decimal elementalApplication = Decimal.ofElementalApplication(ElementType.AETHER, 100);
    @DisplayField private final Decimal elementalApplicationOvercharged = Decimal.ofElementalApplication(ElementType.AETHER, 125);
    
    @DisplayField private final Decimal portalOpeningDelay = Decimal.ofSeconds(1.2f);
    @DisplayField private final Decimal portalSpearPeriod = Decimal.ofSeconds(0.1f);
    
    @DisplayField private final BoundingBoxBlueprint spearBoundingBox = BoundingBoxBlueprint.define(1.8, 5, 1.8);
    
    private final DamageSourceIdentity damageSourceImpalement = DamageSourceIdentity.create(this, DeathMessage.create("{player} was impaled to death [by {killer}]"));
    
    public DisplayModel model = BDEngine.parse(
            "{Passengers:[{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:polished_blackstone_wall\",Properties:{up:\"true\"}},transformation:[0.1875f,0f,0f,-0.095f,0f,3.6875f,0f,0.846875f,0f,0f,0.1875f,-0.101875f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;-1557317004,-389561592,-1124914678,-822040624],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzRkNDJmOWM0NjFjZWUxOTk3YjY3YmYzNjEwYzY0MTFiZjg1MmI5ZTVkYjYwN2JiZjYyNjUyN2NmYjQyOTEyYyJ9fX0=\"}]}}},item_display:\"none\",transformation:[0f,0f,0.1795f,-0.001875f,0f,1f,0f,2.970625f,-0.1875f,0f,0f,-0.0075f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;818785008,-1102424605,-1725003209,1182407917],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzRkNDJmOWM0NjFjZWUxOTk3YjY3YmYzNjEwYzY0MTFiZjg1MmI5ZTVkYjYwN2JiZjYyNjUyN2NmYjQyOTEyYyJ9fX0=\"}]}}},item_display:\"none\",transformation:[-0.1875f,0f,0f,-0.001875f,0f,1f,0f,2.44f,0f,0f,-0.1785f,-0.008125f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;-1172206389,-566905959,-69734431,-891956055],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzRkNDJmOWM0NjFjZWUxOTk3YjY3YmYzNjEwYzY0MTFiZjg1MmI5ZTVkYjYwN2JiZjYyNjUyN2NmYjQyOTEyYyJ9fX0=\"}]}}},item_display:\"none\",transformation:[0.1795f,0f,0f,-0.001875f,0f,1f,0f,1.909375f,0f,0f,0.1885f,-0.01f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;-768288044,-88546942,875754297,2074501220],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzRkNDJmOWM0NjFjZWUxOTk3YjY3YmYzNjEwYzY0MTFiZjg1MmI5ZTVkYjYwN2JiZjYyNjUyN2NmYjQyOTEyYyJ9fX0=\"}]}}},item_display:\"none\",transformation:[0.25f,0f,0f,0f,0f,1.3125f,0f,4.110625f,0f,0f,0.1955f,-0.00875f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;498073589,-807031689,902695473,-1566760464],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzRkNDJmOWM0NjFjZWUxOTk3YjY3YmYzNjEwYzY0MTFiZjg1MmI5ZTVkYjYwN2JiZjYyNjUyN2NmYjQyOTEyYyJ9fX0=\"}]}}},item_display:\"none\",transformation:[-0.0195990869f,0f,-0.180505724f,0.00125f,0f,1f,0f,3.505625f,0.1864728554f,0f,-0.0189719161f,-0.008125f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;642598747,-223337188,1997633581,1487634616],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzRkNDJmOWM0NjFjZWUxOTk3YjY3YmYzNjEwYzY0MTFiZjg1MmI5ZTVkYjYwN2JiZjYyNjUyN2NmYjQyOTEyYyJ9fX0=\"}]}}},item_display:\"none\",transformation:[-0.1675f,0f,0f,0.001875f,0f,1f,0f,4.025f,0f,0f,-0.1795f,-0.0125f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:hopper\",Count:1},item_display:\"none\",transformation:[-0.449f,0f,0f,-0.00625f,0f,-1.943f,0f,4.641875f,0f,0f,0.837f,-0.010625f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:hopper\",Count:1},item_display:\"none\",transformation:[0f,0f,0.837f,-0.00625f,0f,-1.943f,0f,4.641875f,0.449f,0f,0f,-0.010625f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:flint\",Count:1},item_display:\"none\",transformation:[-0.4290488111f,-0.1344776353f,0f,0.048125f,0.085576677f,-0.6742195604f,0f,4.01875f,0f,0f,1f,-0.011875f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:flint\",Count:1},item_display:\"none\",transformation:[0f,0f,-1f,-0.00625f,0.085576677f,-0.6742195604f,0f,4.01875f,-0.4290488111f,-0.1344776353f,0f,0.025f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:flint\",Count:1},item_display:\"none\",transformation:[0.4290488111f,0.1344776353f,0f,-0.063125f,0.085576677f,-0.6742195604f,0f,4.01875f,0f,0f,-1f,-0.013125f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:flint\",Count:1},item_display:\"none\",transformation:[0f,0f,1f,-0.00875f,0.085576677f,-0.6742195604f,0f,4.01875f,0.4290488111f,0.1344776353f,0f,-0.05f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:flint\",Count:1},item_display:\"none\",transformation:[-0.2497120966f,0.0479781285f,0f,0.000625f,-0.0119945321f,-0.9988483865f,0f,0.791875f,0f,0f,1f,-0.013125f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:flint\",Count:1},item_display:\"none\",transformation:[-0.2380989499f,0.2095942056f,0f,0.035625f,-0.0762160748f,-0.6547721123f,0f,0.8325f,0f,0f,0.896f,-0.013125f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:flint\",Count:1},item_display:\"none\",transformation:[0.2443077766f,-0.1458721496f,0f,0.02125f,-0.053044418f,-0.6718463857f,0f,0.46875f,0f,0f,-0.843f,-0.013125f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;-1562835358,-851674999,-623966372,1423927731],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzRkNDJmOWM0NjFjZWUxOTk3YjY3YmYzNjEwYzY0MTFiZjg1MmI5ZTVkYjYwN2JiZjYyNjUyN2NmYjQyOTEyYyJ9fX0=\"}]}}},item_display:\"none\",transformation:[0.25f,0f,0f,0f,0f,1.3125f,0f,1.44f,0f,0f,0.1885f,-0.00875f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:flint\",Count:1},item_display:\"none\",transformation:[0.1232964856f,0.0822655827f,0f,0.04375f,-0.0205663957f,0.4931859425f,0f,2.183125f,0f,0f,0.5625f,-0.013125f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:flint\",Count:1},item_display:\"none\",transformation:[-0.124392733f,-0.0492256811f,0f,-0.044375f,-0.0123064203f,0.4975709319f,0f,2.77375f,0f,0f,-0.5625f,-0.013125f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;567064690,-1832248891,393517656,-263464660],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzg3YTMyNjMzMWZlMjdmN2VlMDc0Zjk3NzI3NjA0YzQ5NWY5NWMzNzgxMjEzMDJlODc5ZTFmNDBiYTRkMjBhOCJ9fX0=\"}]}}},item_display:\"none\",transformation:[0.305897421f,0f,0.3238236721f,-0.005625f,0f,0.506f,0f,4.170625f,-0.2811885805f,0f,0.3522789794f,-0.013125f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:flint\",Count:1},item_display:\"none\",transformation:[1.9e-9f,0f,-0.5625f,0.00375f,-0.0205663957f,0.4931859425f,-1.4e-9f,1.669375f,0.1232964856f,0.0822655827f,8.3e-9f,0.038125f,0f,0f,0f,1f]}]}"
    );
    
    public TalentImpalement(@NotNull Key key) {
        super(key, Component.text("Impalement"), Icon.ofMaterial(Material.DRIED_KELP), UltimateResourceType.ENERGY, 60, 100);
        
        setDescription(
                Component.empty()
                         .append(Component.text("Open a "))
                         .append(Component.text("Void Portal", Colors.VOID))
                         .append(Component.text(" in front of you."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("After a short casting time, "))
                         .append(Component.text("spears", Colors.GOLD))
                         .append(Component.text(" will rise from the "))
                         .append(Component.text("portal", Colors.VOID))
                         .append(Component.text(", dealing rapid "))
                         .append(ElementType.AETHER.asComponentDamage())
                         .append(Component.text(" and applying "))
                         .appendNewline()
                         .append(ElementType.AETHER)
                         .append(Component.text(" anomaly."))
        );
    }
    
    @Override
    public @NotNull Component overchargeDescription() {
        return Component.empty()
                        .append(bullet(
                                Component.empty()
                                         .append(Component.text("Increases the radius by "))
                                         .append(Component.text("%.0f%%".formatted((radiusOvercharged.divide(radius) - 1) * 100), Colors.SUCCESS))
                                         .append(Component.text("."))
                        ))
                        .appendNewline()
                        .append(bullet(
                                Component.empty()
                                         .append(Component.text("Increases the damage by "))
                                         .append(Component.text("%.0f%%".formatted((damageOvercharged.divide(damage) - 1) * 100), Colors.ERROR))
                                         .append(Component.text("."))
                        ))
                        .appendNewline()
                        .append(bullet(
                                Component.empty()
                                         .append(Component.text("Increases the duration by "))
                                         .append(Component.text("%.0f%%".formatted((durationOvercharged.divide(duration) - 1) * 100), Colors.NUMBER))
                                         .append(Component.text("."))
                        ))
                        .appendNewline()
                        .append(bullet(
                                Component.empty()
                                         .append(Component.text("Increases the elemental application by "))
                                         .append(Component.text("%.0f%%".formatted((elementalApplicationOvercharged.divide(elementalApplication) - 1) * 100), Colors.ATTRIBUTE_ELEMENTAL_MASTERY))
                                         .append(Component.text("."))
                        ))
                        .appendNewline()
                        .append(bullet(
                                Component.empty()
                                         .append(Component.text("Adds a final slash, that "))
                                         .append(Component.text("forcefully", Colors.GRAY, TextDecoration.UNDERLINED))
                                         .append(Component.text(" triggers "))
                                         .append(EnumAnomaly.INTANGIBILITY)
                                         .append(Component.text("."))
                        ));
    }
    
    @Override
    public @NotNull Executable execute(@NotNull HariantPlayer player, @NotNull TalentContext context, @NotNull ChargeLevel chargeLevel) {
        return Executable.execute(() -> {
            final Location location = LocationHelper.anchor(player.getLocationInFront(2));
            location.setYaw(0);
            location.setPitch(0);
            
            new Impalement(player, location, chargeLevel);
        });
    }
    
    @Override
    public @NotNull TalentTarget target(@NotNull HariantPlayer player) {
        return TalentTarget.none();
    }
    
    private static @NotNull Component bullet(@NotNull Component component) {
        return Component.empty().append(BULLET).append(component);
    }
    
    public class Impalement extends HariantTickingTask implements EntityCollector {
        
        private static final Color OUTLINE_COLOR = Color.fromRGB(46, 63, 191);
        
        private final HariantPlayer player;
        private final Location location;
        private final ChargeLevel chargeLevel;
        private final TextDisplay textDisplay;
        
        private final double portalRadius;
        
        private final int portalDurationWithOpeningDelay;
        private final int portalOpeningDelay;
        
        private final DamageSource damageSource;
        
        Impalement(@NotNull HariantPlayer player, @NotNull Location location, @NotNull ChargeLevel chargeLevel) {
            super(Scheduler.ofTimer(1));
            
            this.player = player;
            this.location = location;
            this.chargeLevel = chargeLevel;
            this.textDisplay = player.getWorld().spawn(location, TextDisplay.class, self -> {
                self.setShadowStrength(64);
                self.setShadowRadius(0);
            });
            
            this.portalRadius = chargeLevel.either(radius, radiusOvercharged).doubleValue();
            this.portalOpeningDelay = TalentImpalement.this.portalOpeningDelay.intValue();
            this.portalDurationWithOpeningDelay = chargeLevel.either(duration, durationOvercharged).intValue() + portalOpeningDelay;
            
            this.damageSource = new DamageSourceImpalement(
                    player,
                    chargeLevel.either(damage, damageOvercharged).getScaledValue(player),
                    chargeLevel.either(elementalApplication, elementalApplicationOvercharged).doubleValue()
            );
            
            // Fx
            player.playWorldSound(location, Sound.ENTITY_WARDEN_ROAR, 1.25f);
        }
        
        @Override
        public void onCancel() {
            this.textDisplay.remove();
        }
        
        @Override
        public void run(int tick) {
            // If portal has opened, spawn spears
            if (tick > portalOpeningDelay) {
                if (tick > portalDurationWithOpeningDelay) {
                    // If fully charged, execute the final slash
                    if (chargeLevel.isOvercharged()) {
                        this.executeFinalSlash();
                    }
                    
                    this.cancel();
                    return;
                }
                
                if (modulo(portalSpearPeriod)) {
                    this.summonSpear();
                }
                
                return;
            }
            
            // Otherwise increase the text display shadow radius and spawn particles
            textDisplay.setShadowRadius((float) ((double) tick / portalOpeningDelay * portalRadius));
            player.spawnWorldParticle(location, Particle.ASH, 100, portalRadius * 0.8, 0.1, portalRadius * 0.8, 0.02f);
        }
        
        public void executeFinalSlash() {
            collectNearbyEntities(portalRadius)
                    .filter(player::canAffect)
                    .forEach(entity -> {
                        entity.triggerAnomaly(EnumAnomaly.INTANGIBILITY, player);
                    });
            
            // Fx
            new HariantTickingStepTask(Scheduler.ofTimer(), 16) {
                private double theta;
                
                @Override
                public boolean run(int tick, int step) {
                    final double x = Math.sin(theta) * portalRadius - 0.5;
                    final double y = Math.atan(theta / (Math.PI * 2)) * portalRadius - 0.5;
                    final double z = Math.cos(theta) * portalRadius - 0.5;
                    
                    LocationHelper.offset(location, x, y, z, () -> HeroRegistry.NYX.spawnParticle(player, location));
                    
                    theta += Math.PI / 32;
                    return theta > Math.PI * (Math.PI * 0.5);
                }
            };
            
            player.playWorldSound(location, Sound.ENTITY_COPPER_GOLEM_DEATH, 0.0f);
            player.playWorldSound(location, Sound.ENTITY_WARDEN_DEATH, 2.0f);
        }
        
        public void summonSpear() {
            final double x = player.random.nextSignedDouble(portalRadius - 1);
            final double z = player.random.nextSignedDouble(portalRadius - 1);
            
            final Location locationFrom = LocationHelper.copyOf(location).add(x, -1, z);
            final Location locationTo = LocationHelper.copyOf(location).add(x, player.random.nextDouble(5, 7), z);
            
            // Deal damage
            collectNearbyEntities(spearBoundingBox.create(locationFrom))
                    .filter(player::canAffect)
                    .forEach(entity -> {
                        entity.damage(damageSource);
                    });
            
            // Fx
            LocationHelper.offset(location, x, 0, z, () -> {
                final DisplayEntity display = model.spawn(locationFrom, self -> self.setTeleportDuration(2));
                
                HariantTask.later(() -> display.teleport(locationTo), 2);
                
                player.delegate(HariantTask.remove(() -> display, 5), DelegateType.PERSISTENT);
            });
            
            player.playWorldSound(location, Sound.ITEM_FLINTANDSTEEL_USE, 1.75f);
            player.playWorldSound(location, Sound.ENTITY_COPPER_GOLEM_DEATH, 1.25f);
            player.playWorldSound(location, Sound.ENTITY_BLAZE_HURT, 1.75f);
        }
        
        @Override
        public @NotNull Location getLocation() {
            return location;
        }
        
        @Override
        public @NotNull Color outlineColor() {
            return OUTLINE_COLOR;
        }
    }
    
    private class DamageSourceImpalement extends DamageSourceImpl {
        public DamageSourceImpalement(@NotNull HariantPlayer player, double damage, double elementalApplication) {
            super(damageSourceImpalement, player, DamageType.ULTIMATE, ElementType.AETHER, DamageComponent.common(), Set.of(), damage, elementalApplication);
        }
    }
    
}