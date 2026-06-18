package me.hapyl.hariant.hero.nyx;

import me.hapyl.eterna.module.block.display.BDEngine;
import me.hapyl.eterna.module.block.display.DisplayEntity;
import me.hapyl.eterna.module.block.display.DisplayModel;
import me.hapyl.eterna.module.location.Coordinates;
import me.hapyl.eterna.module.location.LocationHelper;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.attribute.AttributeScaling;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.modifier.AttributeModifier;
import me.hapyl.hariant.attribute.modifier.AttributeModifierType;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.EntityCollector;
import me.hapyl.hariant.entity.HariantRandom;
import me.hapyl.hariant.entity.WarningType;
import me.hapyl.hariant.entity.cooldown.Cooldown;
import me.hapyl.hariant.entity.damage.*;
import me.hapyl.hariant.entity.damage.component.DamageComponent;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.talent.Response;
import me.hapyl.hariant.talent.Talent;
import me.hapyl.hariant.talent.TalentContext;
import me.hapyl.hariant.talent.field.DisplayField;
import me.hapyl.hariant.talent.target.TalentTarget;
import me.hapyl.hariant.task.HariantTickingStepTask;
import me.hapyl.hariant.task.HariantTickingTask;
import me.hapyl.hariant.task.Scheduler;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public final class TalentWitherPath extends Talent {
    
    @DisplayField private final Decimal maximumDistance = Decimal.ofValue(20);
    
    @DisplayField private final Decimal maxHealthDecrease = Decimal.ofPercentage(10);
    @DisplayField private final Decimal maxHealthDecreaseDuration = Decimal.ofSeconds(8);
    
    @DisplayField private final AttributeScaling spikeDamage = AttributeScaling.of(AttributeType.ATTACK, 43);
    @DisplayField private final Decimal spikeElementalApplication = Decimal.ofElementalApplication(ElementType.AETHER, 250);
    @DisplayField private final Decimal spikeKnockbackStrength = Decimal.ofValue(0.8);
    
    @DisplayField private final Decimal roseBloomDelay = Decimal.ofSeconds(0.6f);
    
    private final DamageSourceIdentity damageSourceIdentity = DamageSourceIdentity.create(
            this,
            DeathMessage.create("{player} was spiked to death [by {killer}]")
    );
    
    private final DisplayModel witherRoseModel = BDEngine.parse(
            "/summon block_display ~-0.5 ~ ~-0.5 {Passengers:[{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:wither_rose\",Properties:{}},transformation:[0.5745242597f,-0.1830127019f,-0.5549478203f,-0.043125f,0f,0.9659258263f,-0.2102904741f,-0.190625f,0.5745242597f,0.1830127019f,0.5549478203f,-0.40625f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:wither_rose\",Properties:{}},transformation:[0.1941142838f,0.25f,0.6997595264f,-0.321875f,0f,0.9659258263f,-0.1941142838f,-0.323125f,-0.7244443697f,0.0669872981f,0.1875f,0.11f,0f,0f,0f,1f]}]}"
    );
    
    private final DisplayModel spikeModel = BDEngine.parse(
            "/summon block_display ~-0.5 ~ ~-0.5 {Passengers:[{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:obsidian\",Properties:{}},transformation:[0.9176f,0f,0f,-0.534375f,0f,1.1217289526f,0.114521332f,0.1875f,0f,-0.1168631544f,1.0992506104f,-0.4525f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:obsidian\",Properties:{}},transformation:[1.1147f,0f,0f,-0.633125f,0f,1.3499401278f,-0.048395637f,-0.043125f,0f,0.1181425467f,0.552986322f,-1f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:obsidian\",Properties:{}},transformation:[0.8125f,0f,0f,-0.481875f,0f,1.1623829612f,-0.0590454078f,1.1875f,0f,0.0780439075f,0.8794200361f,-0.454375f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:obsidian\",Properties:{}},transformation:[0.6262f,0f,0f,-0.375f,0f,0.9000424421f,-0.0876635895f,2.3275f,0f,0.1045983387f,0.7543231768f,-0.3125f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:obsidian\",Properties:{}},transformation:[0.3528f,0f,0f,-0.25f,0f,0.8469721019f,-0.1460859529f,3.176875f,0f,0.2517681842f,0.4914470311f,-0.0625f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:crying_obsidian\",Properties:{}},transformation:[0.8462f,0f,0f,-0.50625f,0f,0.5625f,0f,-0.0625f,0f,0f,0.9744f,-0.4375f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;-257834520,-1136529793,1624999221,1637553739],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGU3NjBiYmMxMTNjMjczZmFjNDA4OTZmYTIwODlhNTZjYzc0NmE3OWE3YTgyNzVmNjM4NTdlNjllNmY3NzAzYSJ9fX0=\"}]}}},item_display:\"none\",transformation:[0.7538f,0f,0f,-0.0625f,0f,0.5604578268f,-0.0795079552f,2f,0f,0.0657382258f,0.6778530335f,-0.4375f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:crying_obsidian\",Properties:{}},transformation:[0.9302881162f,0.0025997107f,-0.001420246f,-0.53875f,-0.0040171574f,0.5601862817f,0.0831780627f,0.5475f,0.0024440459f,-0.0687890351f,0.6773102338f,-0.05875f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:crying_obsidian\",Properties:{}},transformation:[0.876f,0f,0f,-0.385f,0f,0.5615217897f,-0.03737921f,1.2775f,0f,0.0447232561f,0.4693137913f,-0.75f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;1678623160,-488207078,-486924982,-179125769],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjQyNmFiODg4Mjk4YWRmMWVmZmQ4MTFjODA3NGRlZjA5NzgwZTdkOWQxMmJhNGM3N2I3M2ZkYTk5ODJkZDBmZSJ9fX0=\"}]}}},item_display:\"none\",transformation:[0.9005f,0f,0f,-0.3825f,0f,0.5625f,0f,0.704375f,0f,0f,0.684f,0.53875f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;-1297495472,-891907469,-494113125,-555964497],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjQyNmFiODg4Mjk4YWRmMWVmZmQ4MTFjODA3NGRlZjA5NzgwZTdkOWQxMmJhNGM3N2I3M2ZkYTk5ODJkZDBmZSJ9fX0=\"}]}}},item_display:\"none\",transformation:[0.9005f,0f,0f,0.305625f,0f,0.5625f,0f,1.513125f,0f,0f,0.684f,-0.75f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:wither_rose\",Properties:{}},transformation:[0.089718521f,-0.650683435f,-0.3945320663f,-0.2275f,-0.3023181059f,0.3932802613f,-0.4985845398f,1.174375f,0.5806987083f,0.3052772403f,-0.1986129788f,0.21375f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:wither_rose\",Properties:{}},transformation:[-0.4841963689f,0.0397041695f,0.5971701425f,0.375f,-0.1722238588f,0.5705147725f,-0.2171430732f,1.625f,-0.5433665694f,-0.216209235f,-0.463315947f,-0.3575f,0f,0f,0f,1f]}]}"
    );
    
    private final Cooldown damageCooldown = Cooldown.ofSeconds(Key.ofString("wither_path_damage_cooldown"), 0.5f);
    
    public TalentWitherPath(@NotNull Key key) {
        super(key, Component.text("Wither Path"), Icon.ofMaterial(Material.WITHER_ROSE));
        
        setDurationSeconds(1.6f);
        setCooldownSeconds(8);
        
        setDescription(
                Component.empty()
                         .append(Component.text("Launch a path of wither roses forward."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("After a short delay, the roses bloom into "))
                         .append(Component.text("spikes", Colors.VOID))
                         .append(Component.text(" that deal "))
                         .append(ElementType.AETHER.asComponentDamage())
                         .append(Component.text(", "))
                         .append(Component.text("impair", Colors.ARCHETYPE_HEXBANE))
                         .append(Component.text(" and knockback "))
                         .append(Component.text("enemies", Colors.RED))
                         .append(Component.text("."))
        );
    }
    
    @Override
    public @NotNull TalentTarget target(@NotNull HariantPlayer player) {
        return TalentTarget.none();
    }
    
    @Override
    public @NotNull Response execute(@NotNull HariantPlayer player, @NotNull TalentContext context) {
        new WitherPath(player);
        return Response.ok();
    }
    
    private class WitherPath extends HariantTickingStepTask implements EntityCollector, Coordinates {
        
        private static final Color OUTLINE_COLOR = Color.fromRGB(91, 5, 171);
        private static final BlockData SPIKE_BLOCK_DATA = Material.OBSIDIAN.createBlockData();
        
        private final HariantPlayer player;
        private final Location location;
        private final Vector direction;
        private final DamageSource damageSource;
        
        private double distance;
        
        WitherPath(@NotNull HariantPlayer player) {
            super(Scheduler.ofTimer(), 3);
            
            this.player = player;
            this.location = player.getLocation();
            this.direction = location.getDirection().normalize().setY(0);
            this.damageSource = new DamageSourceWitherPath(player, spikeDamage.getScaledValue(player));
        }
        
        @Override
        public boolean run(int tick, int step) {
            final double x = direction.getX() * distance;
            final double y = direction.getY() * distance;
            final double z = direction.getZ() * distance;
            
            final boolean playFx = modulo(3);
            
            LocationHelper.offset(location, x, y, z, () -> {
                createSpike(location, playFx);
            });
            
            return distance++ > maximumDistance.doubleValue();
        }
        
        public void createSpike(@NotNull Location location, boolean playFx) {
            final Location anchored = LocationHelper.anchor(location);
            final HariantRandom random = player.random;
            
            anchored.add(random.nextDouble(), random.nextDouble() * 0.5, random.nextDouble());
            anchored.setYaw(location.getYaw() + player.random.nextFloat(160, 200));
            anchored.setPitch(random.nextFloat() * 15);
            
            final DisplayEntity displayRose = witherRoseModel.spawn(anchored);
            final DisplayEntity displaySpike = spikeModel.spawn(LocationHelper.copyOf(anchored).subtract(0, 10, 0), self -> {
                self.setTeleportDuration(2);
            });
            
            // Play rose fx
            if (playFx) {
                player.playWorldSound(location, Sound.BLOCK_SWEET_BERRY_BUSH_PLACE, 0.5f);
            }
            
            final BoundingBox boundingBox = LocationHelper.toBoundingBox(anchored, 1, 2, 1);
            final int roseBloomDelay = TalentWitherPath.this.roseBloomDelay.intValue();
            
            player.delegate(
                    new HariantTickingTask(Scheduler.ofTimer()) {
                        @Override
                        public void run(int tick) {
                            // Cleanup
                            if (tick > getDuration()) {
                                this.cancel();
                                return;
                            }
                            
                            // Bloom into a spike
                            if (tick == roseBloomDelay) {
                                displaySpike.teleport(anchored);
                                
                                // Affect entities
                                collectNearbyEntities(boundingBox)
                                        .filter(player::canAffect)
                                        .forEach(entity -> {
                                            // Deal damage
                                            entity.damage(damageSource);
                                            
                                            entity.getAttributes().addModifierIfAbsent(new AttributeModifierWitherPath(player));
                                            
                                            // Check for effect resistance and knockback
                                            if (!entity.hasEffectResistance(AssistSource.create(player, TalentWitherPath.this))) {
                                                entity.knockback(KnockbackSource.create(WitherPath.this, spikeKnockbackStrength.doubleValue()));
                                            }
                                        });
                                
                                // Fx
                                if (playFx) {
                                    player.playWorldSound(location, Sound.ENTITY_EVOKER_FANGS_ATTACK, 0.75f);
                                }
                            }
                            else if (tick < roseBloomDelay) {
                                // Display warning
                                collectNearbyEntities(boundingBox)
                                        .filter(player::canAffect)
                                        .forEach(entity -> {
                                            entity.showWarning(WarningType.WARNING, roseBloomDelay);
                                        });
                            }
                            
                        }
                        
                        @Override
                        public void onCancel() {
                            super.onCancel();
                            
                            displayRose.remove();
                            displaySpike.remove();
                            
                            // Fx
                            player.spawnWorldParticle(anchored, Particle.BLOCK, 50, 0.5, 2, 0.5, 0.075f, SPIKE_BLOCK_DATA);
                            player.playWorldSound(anchored, Sound.BLOCK_STONE_BREAK, 0.75f);
                        }
                    }
            );
        }
        
        @Override
        public @NotNull Location getLocation() {
            return location;
        }
        
        @Override
        public @NotNull Color outlineColor() {
            return OUTLINE_COLOR;
        }
        
        @Override
        public double x() {
            return location.x();
        }
        
        @Override
        public double y() {
            return location.y();
        }
        
        @Override
        public double z() {
            return location.z();
        }
    }
    
    private class DamageSourceWitherPath extends DamageSourceImpl {
        public DamageSourceWitherPath(@NotNull HariantPlayer player, double damage) {
            super(damageSourceIdentity, player, DamageType.TALENT, ElementType.AETHER, DamageComponent.common(), Set.of(), damage, spikeElementalApplication.doubleValue(), damageCooldown);
        }
    }
    
    private class AttributeModifierWitherPath extends AttributeModifier {
        public AttributeModifierWitherPath(@NotNull HariantPlayer player) {
            super(TalentWitherPath.this, player, maxHealthDecreaseDuration.intValue());
            
            of(AttributeType.MAX_HEALTH, AttributeModifierType.ADDITIVE, -maxHealthDecrease.doubleValue());
        }
    }
    
}