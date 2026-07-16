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
import me.hapyl.hariant.entity.WarningType;
import me.hapyl.hariant.entity.cooldown.Cooldown;
import me.hapyl.hariant.entity.damage.*;
import me.hapyl.hariant.entity.damage.component.DamageComponent;
import me.hapyl.hariant.entity.player.DelegateType;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.talent.Response;
import me.hapyl.hariant.talent.Talent;
import me.hapyl.hariant.talent.TalentContext;
import me.hapyl.hariant.talent.field.DisplayField;
import me.hapyl.hariant.talent.target.TalentTarget;
import me.hapyl.hariant.task.HariantTickingStepTask;
import me.hapyl.hariant.task.HariantTickingTask;
import me.hapyl.hariant.task.Scheduler;
import me.hapyl.hariant.util.BoundingBoxBlueprint;
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
    
    private final @DisplayField Decimal maximumDistance = Decimal.ofValue(20);
    
    private final @DisplayField Decimal maxHealthDecrease = Decimal.ofPercentage(10);
    private final @DisplayField Decimal maxHealthDecreaseDuration = Decimal.ofSeconds(8);
    
    private final @DisplayField AttributeScaling spikeDamage = AttributeScaling.create(AttributeType.ATTACK, 43);
    private final @DisplayField Decimal spikeElementalApplication = Decimal.ofElementalApplication(ElementType.AETHER, 250);
    private final @DisplayField Decimal spikeKnockbackStrength = Decimal.ofValue(0.8);
    
    private final @DisplayField Decimal roseBloomDelay = Decimal.ofSeconds(0.5f);
    
    private final @DisplayField BoundingBoxBlueprint spikeBoundingBox = BoundingBoxBlueprint.define(1, 2, 1);
    
    private final DamageSourceIdentity damageSourceIdentity = DamageSourceIdentity.create(
            this,
            DeathMessage.create("{player} was spiked to death [by {killer}]")
    );
    
    private final DisplayModel witherRoseModel = BDEngine.parse(
            "/summon block_display ~-0.5 ~ ~-0.5 {Passengers:[{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:wither_rose\",Properties:{}},transformation:[0.5745242597f,-0.1830127019f,-0.5549478203f,-0.043125f,0f,0.9659258263f,-0.2102904741f,-0.190625f,0.5745242597f,0.1830127019f,0.5549478203f,-0.40625f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:wither_rose\",Properties:{}},transformation:[0.1941142838f,0.25f,0.6997595264f,-0.321875f,0f,0.9659258263f,-0.1941142838f,-0.323125f,-0.7244443697f,0.0669872981f,0.1875f,0.11f,0f,0f,0f,1f]}]}"
    );
    
    private final DisplayModel spikeModel = BDEngine.parse(
            "/summon block_display ~-0.5 ~ ~-0.5 {Passengers:[{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:obsidian\",Properties:{}},transformation:[0.4667f,0f,0f,-0.3101919661f,0f,0.2483909257f,0.0202476462f,-0.085843288f,0f,-0.0101935295f,0.4933847108f,-0.1623124255f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:obsidian\",Properties:{}},transformation:[0.3407f,0f,0f,-0.2472782786f,0f,0.3692891638f,0.0170821576f,0.1751420906f,0f,-0.0151549818f,0.4162496365f,-0.1344122125f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:obsidian\",Properties:{}},transformation:[0.3129f,0f,0f,-0.2332767161f,0f,0.3048367445f,0.0611981421f,0.5446129764f,0f,-0.0474005193f,0.3935704224f,-0.1495746519f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:obsidian\",Properties:{}},transformation:[0.277f,0f,0f,-0.215625f,0f,0.3175338626f,-0.0102827772f,0.918125f,0f,0.0102730765f,0.3178337057f,-0.16125f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:obsidian\",Properties:{}},transformation:[0.2561f,0f,0f,-0.205f,0f,0.2472996315f,-0.0232833854f,1.265f,0f,0.021121843f,0.2726074907f,-0.126875f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:obsidian\",Properties:{}},transformation:[0.2269f,0f,0f,-0.19f,0f,0.2415084157f,-0.0600603469f,1.54125f,0f,0.0661350522f,0.2193251347f,-0.069375f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:crying_obsidian\",Properties:{}},transformation:[0.3053f,0f,0f,-0.2319699036f,0f,0.1528713259f,0.0112596267f,0.0708883493f,0f,-0.006273572f,0.2743690595f,-0.0068887962f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:crying_obsidian\",Properties:{}},transformation:[0.2885f,0f,0f,-0.2235689661f,0f,0.1528713259f,0.0144210149f,0.4776298894f,0f,-0.006273572f,0.3514042179f,-0.123659425f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:crying_obsidian\",Properties:{}},transformation:[0.2987f,0f,0f,-0.2017265286f,0f,0.1528713259f,0.0101238231f,0.2066059228f,0f,-0.006273572f,0.2466923554f,0.0352642894f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:crying_obsidian\",Properties:{}},transformation:[0.2987f,0f,0f,-0.3116854661f,0f,0.1528713259f,0.0101238231f,0.0200416124f,0f,-0.006273572f,0.2466923554f,-0.1521401723f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:crying_obsidian\",Properties:{}},transformation:[0.3171951766f,0.000798579f,-0.0004790785f,-0.2347702161f,-0.0015045314f,0.1524578728f,0.0425799292f,0.6482781507f,0.0008923547f,-0.0268141621f,0.2420833743f,-0.0115874509f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;-147679533,1505651642,1942719646,2043773913],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjQyNmFiODg4Mjk4YWRmMWVmZmQ4MTFjODA3NGRlZjA5NzgwZTdkOWQxMmJhNGM3N2I3M2ZkYTk5ODJkZDBmZSJ9fX0=\"}]}}},item_display:\"none\",transformation:[0.3071f,0f,0f,0.0282724714f,0f,0.1528713259f,0.0101238231f,0.2214101828f,0f,-0.006273572f,0.2466923554f,0.2397562385f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;-854602365,587614551,-1715240414,-119252126],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjQyNmFiODg4Mjk4YWRmMWVmZmQ4MTFjODA3NGRlZjA5NzgwZTdkOWQxMmJhNGM3N2I3M2ZkYTk5ODJkZDBmZSJ9fX0=\"}]}}},item_display:\"none\",transformation:[0.3071f,0f,0f,-0.1813775911f,0f,0.1528713259f,0.0101238231f,0.6998259404f,0f,-0.006273572f,0.2466923554f,0.2020531327f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;-965550667,1953947936,1364731667,-321727878],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjQyNmFiODg4Mjk4YWRmMWVmZmQ4MTFjODA3NGRlZjA5NzgwZTdkOWQxMmJhNGM3N2I3M2ZkYTk5ODJkZDBmZSJ9fX0=\"}]}}},item_display:\"none\",transformation:[0.3071f,0f,0f,-0.2926433411f,0f,0.1528713259f,0.0101238231f,0.1564539412f,0f,-0.006273572f,0.2466923554f,-0.1117144645f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:wither_rose\",Properties:{}},transformation:[0.014838747f,0.196367889f,0.1484622036f,-0.0725387786f,-0.0871316788f,0.1605352218f,-0.1601858049f,0.3727186335f,-0.2413929848f,-0.0448357129f,0.0663114051f,0.218720335f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:wither_rose\",Properties:{}},transformation:[0.1738707248f,-0.1023397254f,-0.1293207788f,-0.0598440286f,-0.0825976461f,0.1354184874f,-0.1614192088f,0.37149112f,0.175157424f,0.164705311f,0.0523118555f,0.1888088869f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:wither_rose\",Properties:{}},transformation:[0.0361614075f,-0.1961974616f,-0.1442773666f,-0.1285450286f,-0.0991042387f,0.1592489279f,-0.1460285482f,0.8227602262f,0.2047163708f,0.1117498323f,-0.0452077936f,0.0796319103f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:crying_obsidian\",Properties:{}},transformation:[0.2525f,0f,0f,-0.205f,0f,0.1528713259f,0.0120017943f,0.78875f,0f,-0.006273572f,0.2924538373f,-0.1525f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:crying_obsidian\",Properties:{}},transformation:[0f,-0.0061095571f,0.2305059797f,-0.188125f,0f,0.14887469f,0.0094595625f,1.15375f,-0.2225f,0f,0f,0.11f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:crying_obsidian\",Properties:{}},transformation:[0f,-0.0061095571f,0.1965345739f,-0.17125f,0f,0.14887469f,0.0080654354f,1.42125f,-0.2055f,0f,0f,0.155625f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:wither_rose\",Properties:{}},transformation:[-0.1588583687f,-0.1951748047f,-0.0333737617f,-0.04125f,-0.0991042387f,0.1592489279f,-0.1460285482f,1.29875f,0.1340912322f,-0.1135264929f,-0.1474648946f,-0.131875f,0f,0f,0f,1f]}]}"
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
        player.delegate(new WitherPath(player), DelegateType.INTERRUPTABLE);
        return Response.ok();
    }
    
    private class WitherPath extends HariantTickingStepTask {
        
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
                player.delegate(new WitherSpike(player, location, damageSource, playFx), DelegateType.PERSISTENT);
            });
            
            return distance++ > maximumDistance.doubleValue();
        }
        
    }
    
    private class WitherSpike extends HariantTickingTask implements EntityCollector, Coordinates {
        
        private static final Color OUTLINE_COLOR = Color.fromRGB(91, 5, 171);
        private static final BlockData SPIKE_BLOCK_DATA = Material.OBSIDIAN.createBlockData();
        
        private final HariantPlayer player;
        private final Location location;
        
        private final DisplayEntity displayRose;
        private final DisplayEntity displaySpike;
        
        private final BoundingBox boundingBox;
        private final DamageSource damageSource;
        
        private final boolean playFx;
        
        WitherSpike(@NotNull HariantPlayer player, @NotNull Location location, @NotNull DamageSource damageSource, boolean playFx) {
            super(Scheduler.ofTimer());
            
            this.player = player;
            
            this.location = LocationHelper.anchor(LocationHelper.copyOf(location));
            this.location.add(player.random.nextDouble(), player.random.nextDouble() * 0.5, player.random.nextDouble());
            this.location.setYaw(location.getYaw() + player.random.nextFloat(160, 200));
            this.location.setPitch(player.random.nextFloat() * 15);
            
            this.displayRose = witherRoseModel.spawn(this.location);
            this.displaySpike = spikeModel.spawn(LocationHelper.copyOf(this.location).subtract(0, 10, 0), self -> self.setTeleportDuration(2));
            
            this.boundingBox = spikeBoundingBox.create(this.location);
            this.damageSource = damageSource;
            this.playFx = playFx;
            
            // Play rose fx
            if (playFx) {
                player.playWorldSound(this.location, Sound.BLOCK_SWEET_BERRY_BUSH_PLACE, 0.5f);
            }
        }
        
        @Override
        public void run(int tick) {
            // Cleanup
            if (tick > getDuration()) {
                this.cancel();
                return;
            }
            
            // Bloom into a spike
            if (tick == roseBloomDelay.intValue()) {
                displaySpike.teleport(location);
                
                // Affect entities
                collectNearbyEntities(boundingBox)
                        .filter(player::canAffect)
                        .forEach(entity -> {
                            // Deal damage
                            entity.damage(damageSource);
                            entity.getAttributes().addModifierIfAbsent(new AttributeModifierWitherPath(player));
                            entity.knockback(KnockbackSource.create(this, spikeKnockbackStrength.doubleValue()));
                        });
                
                // Fx
                if (playFx) {
                    player.playWorldSound(location, Sound.ENTITY_EVOKER_FANGS_ATTACK, 0.75f);
                }
            }
            else if (tick < roseBloomDelay.intValue()) {
                // Display warning
                collectNearbyEntities(boundingBox)
                        .filter(player::canAffect)
                        .forEach(entity -> {
                            entity.showWarning(WarningType.WARNING, roseBloomDelay.intValue());
                        });
            }
            
        }
        
        @Override
        public void onCancel() {
            displayRose.remove();
            displaySpike.remove();
            
            // Fx
            player.spawnWorldParticle(location, Particle.BLOCK, 50, 0.5, 2, 0.5, 0.075f, SPIKE_BLOCK_DATA);
            player.playWorldSound(location, Sound.BLOCK_STONE_BREAK, 0.75f);
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
        DamageSourceWitherPath(@NotNull HariantPlayer player, double damage) {
            super(damageSourceIdentity, player, DamageType.TALENT, ElementType.AETHER, DamageComponent.common(), Set.of(), damage, spikeElementalApplication.doubleValue(), damageCooldown);
        }
    }
    
    private class AttributeModifierWitherPath extends AttributeModifier {
        AttributeModifierWitherPath(@NotNull HariantPlayer player) {
            super(TalentWitherPath.this, player, maxHealthDecreaseDuration.intValue());
            
            of(AttributeType.MAX_HEALTH, AttributeModifierType.ADDITIVE, -maxHealthDecrease.doubleValue());
        }
    }
    
}