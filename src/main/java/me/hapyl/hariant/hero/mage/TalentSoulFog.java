package me.hapyl.hariant.hero.mage;

import me.hapyl.eterna.module.block.display.BDEngine;
import me.hapyl.eterna.module.block.display.DisplayEntity;
import me.hapyl.eterna.module.block.display.DisplayModel;
import me.hapyl.eterna.module.block.display.DisplayPart;
import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.eterna.module.location.LocationHelper;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.achievement.AchievementMageSoulHarvested;
import me.hapyl.hariant.attribute.AttributeScaling;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.element.ElementSource;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.PullSource;
import me.hapyl.hariant.entity.WarningType;
import me.hapyl.hariant.entity.damage.*;
import me.hapyl.hariant.entity.damage.component.DamageComponents;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.handler.HariantProjectile;
import me.hapyl.hariant.talent.Response;
import me.hapyl.hariant.talent.Talent;
import me.hapyl.hariant.talent.TalentContext;
import me.hapyl.hariant.talent.TalentType;
import me.hapyl.hariant.talent.field.DisplayField;
import me.hapyl.hariant.talent.target.TalentTarget;
import me.hapyl.hariant.task.HariantTickingTask;
import me.hapyl.hariant.task.Scheduler;
import me.hapyl.hariant.util.Counter;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.ThrowableProjectile;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Set;

public final class TalentSoulFog extends Talent {
    
    private static final ItemStack SKULL_TEXTURE = ItemBuilder.playerHead("c7513a587d075f1475b1181874d0dd77eccee3690b9cbf024ad6b885fc3faa57").asIcon();
    
    private final @DisplayField Decimal soulFogDelay = Decimal.ofSeconds(0.1f);
    private final @DisplayField Decimal soulFogRadius = Decimal.ofValue(2.5);
    private final @DisplayField Decimal soulFogAetherAnomalyApplication = Decimal.ofElementalApplication(ElementType.AETHER, 15);
    
    private final @DisplayField Decimal soulFogExplosionRadius = Decimal.ofValue(3);
    private final @DisplayField Decimal soulFogExplosionAetherAnomalyApplication = Decimal.ofElementalApplication(ElementType.AETHER, 200);
    private final @DisplayField Decimal soulFogExplosionSoulGenerationPerEnemyHit = Decimal.ofValue(2);
    
    private final @DisplayField Decimal pullStrength = Decimal.ofValue(0.2);
    private final @DisplayField Decimal pullResistance = Decimal.ofValue(0.6);
    
    private final @DisplayField AttributeScaling soulFogExplosionDamage = AttributeScaling.create(AttributeType.ATTACK, 144);
    
    private final DamageSourceIdentity damageSourceIdentity = DamageSourceIdentity.createOfNamed(
            this,
            Key.ofString("soul_fog_damage_source"),
            DeathMessage.create("{player} lost their way in the soul fog [created by {killer}]")
    );
    
    private final DisplayModel model = BDEngine.parse(
            "/summon block_display ~-0.5 ~ ~-0.5 {Passengers:[{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_portal\",Properties:{}},transformation:[1f,0f,0f,0.125f,0f,1.1875f,0f,-0.658125f,0f,0f,1f,1.125f,0f,0f,0f,1f],Tags: [\"animate\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_portal\",Properties:{}},transformation:[1f,0f,0f,1.125f,0f,1f,0f,-0.658125f,0f,0f,1f,1.1875f,0f,0f,0f,1f],Tags: [\"animate\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_portal\",Properties:{}},transformation:[1f,0f,0f,0.125f,0f,1.125f,0f,-0.658125f,0f,0f,1f,0.1875f,0f,0f,0f,1f],Tags: [\"animate\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_portal\",Properties:{}},transformation:[1f,0f,0f,1.125f,0f,1.3125f,0f,-0.658125f,0f,0f,1f,0.1875f,0f,0f,0f,1f],Tags: [\"animate\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_portal\",Properties:{}},transformation:[1f,0f,0f,-1.875f,0f,1f,0f,-0.658125f,0f,0f,1.112f,1.1875f,0f,0f,0f,1f],Tags: [\"animate\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_portal\",Properties:{}},transformation:[1f,0f,0f,-0.875f,0f,1.125f,0f,-0.658125f,0f,0f,1f,1.1875f,0f,0f,0f,1f],Tags: [\"animate\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_portal\",Properties:{}},transformation:[1f,0f,0f,-1.875f,0f,1.125f,0f,-0.658125f,0f,0f,1f,0.1875f,0f,0f,0f,1f],Tags: [\"animate\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_portal\",Properties:{}},transformation:[1f,0f,0f,-0.875f,0f,1.1875f,0f,-0.658125f,0f,0f,1f,0.1875f,0f,0f,0f,1f],Tags: [\"animate\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_portal\",Properties:{}},transformation:[1f,0f,0f,-1.875f,0f,1.25f,0f,-0.658125f,0f,0f,1f,-0.8125f,0f,0f,0f,1f],Tags: [\"animate\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_portal\",Properties:{}},transformation:[1f,0f,0f,-0.875f,0f,1.1875f,0f,-0.658125f,0f,0f,1f,-0.8125f,0f,0f,0f,1f],Tags: [\"animate\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_portal\",Properties:{}},transformation:[1f,0f,0f,-1.875f,0f,1f,0f,-0.658125f,0f,0f,1f,-1.8125f,0f,0f,0f,1f],Tags: [\"animate\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_portal\",Properties:{}},transformation:[1f,0f,0f,-0.875f,0f,1.0625f,0f,-0.658125f,0f,0f,1f,-1.8125f,0f,0f,0f,1f],Tags: [\"animate\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_portal\",Properties:{}},transformation:[1f,0f,0f,0.125f,0f,1.25f,0f,-0.658125f,0f,0f,1f,-0.8125f,0f,0f,0f,1f],Tags: [\"animate\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_portal\",Properties:{}},transformation:[1f,0f,0f,1.125f,0f,1.25f,0f,-0.658125f,0f,0f,1f,-0.8125f,0f,0f,0f,1f],Tags: [\"animate\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_portal\",Properties:{}},transformation:[1f,0f,0f,0.125f,0f,1f,0f,-0.658125f,0f,0f,1f,-1.8125f,0f,0f,0f,1f],Tags: [\"animate\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_portal\",Properties:{}},transformation:[1f,0f,0f,1.125f,0f,1f,0f,-0.658125f,0f,0f,1f,-1.8125f,0f,0f,0f,1f],Tags: [\"animate\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_portal\",Properties:{}},transformation:[1f,0f,0f,0.125f,0f,0.9375f,0f,-0.658125f,0f,0f,1f,-2.0625f,0f,0f,0f,1f],Tags: [\"animate\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_portal\",Properties:{}},transformation:[1f,0f,0f,-0.875f,0f,0.9375f,0f,-0.658125f,0f,0f,1f,-2.5f,0f,0f,0f,1f],Tags: [\"animate\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_portal\",Properties:{}},transformation:[1f,0f,0f,-2.125f,0f,0.9375f,0f,-0.658125f,0f,0f,1f,-2.125f,0f,0f,0f,1f],Tags: [\"animate\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_portal\",Properties:{}},transformation:[1f,0f,0f,-1.875f,0f,0.875f,0f,-0.658125f,0f,0f,1f,-2.3125f,0f,0f,0f,1f],Tags: [\"animate\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_portal\",Properties:{}},transformation:[1f,0f,0f,-2.3125f,0f,1.0625f,0f,-0.658125f,0f,0f,1f,-1.1875f,0f,0f,0f,1f],Tags: [\"animate\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_portal\",Properties:{}},transformation:[1f,0f,0f,-2.375f,0f,0.9375f,0f,-0.658125f,0f,0f,1f,-0.1875f,0f,0f,0f,1f],Tags: [\"animate\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_portal\",Properties:{}},transformation:[1f,0f,0f,1.5f,0f,0.916f,0f,-0.658125f,0f,0f,1f,0.5625f,0f,0f,0f,1f],Tags: [\"animate\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_portal\",Properties:{}},transformation:[1f,0f,0f,1.5625f,0f,0.916f,0f,-0.658125f,0f,0f,1f,-0.4375f,0f,0f,0f,1f],Tags: [\"animate\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_portal\",Properties:{}},transformation:[1f,0f,0f,-0.875f,0f,1.0625f,0f,-0.658125f,0f,0f,1f,1.4375f,0f,0f,0f,1f],Tags: [\"animate\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:end_portal\",Properties:{}},transformation:[1f,0f,0f,0.0625f,0f,1.0625f,0f,-0.658125f,0f,0f,1f,1.625f,0f,0f,0f,1f],Tags: [\"animate\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:warped_roots\",Properties:{}},transformation:[0.7071067812f,0f,0.7071067812f,-0.081875f,0f,1f,0f,-0.3125f,-0.7071067812f,0f,0.7071067812f,1.6875f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:warped_roots\",Properties:{}},transformation:[0.8660254038f,0f,0.5f,-1.245625f,0f,1f,0f,-0.095625f,-0.5f,0f,0.8660254038f,0.379375f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:warped_roots\",Properties:{}},transformation:[0.9659258263f,0f,-0.2588190451f,0.27125f,0f,1f,0f,-0.125f,0.2588190451f,0f,0.9659258263f,-1.1125f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:warped_roots\",Properties:{}},transformation:[0.8660254038f,0f,0.5f,-1.870625f,0f,1f,0f,-0.4375f,-0.5f,0f,0.8660254038f,-1.558125f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;-1643898080,-2012659211,-637162894,-533445154],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDVmNjdjOWQ3NzAyNTlmOWIwNDk5ZGEzNjcwYTUyNmI5NWE0NmUxYjdlNzg2OWEyM2VhZmJlMjg3Y2E2ODJjZiJ9fX0=\"}]}}},item_display:\"none\",transformation:[-0.6830127019f,-0.2588190451f,0.6830127019f,-1.375f,-0.1830127019f,0.9659258263f,0.1830127019f,0.25f,-0.7071067812f,0f,-0.7071067812f,-0.3125f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;386371346,1468748912,670716955,-1989917635],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDVmNjdjOWQ3NzAyNTlmOWIwNDk5ZGEzNjcwYTUyNmI5NWE0NmUxYjdlNzg2OWEyM2VhZmJlMjg3Y2E2ODJjZiJ9fX0=\"}]}}},item_display:\"none\",transformation:[0.6376791336f,-0.5218214329f,-0.5666283745f,1.5f,0.1394060463f,0.8016237943f,-0.5813477846f,0.25f,0.7575825215f,0.2917219302f,0.5839238295f,0.75f,0f,0f,0f,1f]}]}"
    );
    
    public TalentSoulFog(@NotNull Key key) {
        super(key, Component.text("Soul Fog"), Icon.ofMaterial(Material.HEART_OF_THE_SEA));
        
        setTalentType(TalentType.IMPAIR);
        
        setDurationSeconds(2f);
        setCooldownSeconds(12f);
        
        setDescription(
                Component.empty()
                         .append(Component.text("Throw a projectile concentrated with lost souls forward."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("On landing, the souls combine into "))
                         .append(Component.text("Fog of Lost Souls", Colors.SOUL))
                         .append(Component.text(" that constantly pulls nearby "))
                         .append(Component.text("enemies", Colors.RED))
                         .append(Component.text(" and applies "))
                         .append(ElementType.AETHER)
                         .append(Component.text(" anomaly."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("After "))
                         .append(this.getDurationFormatted())
                         .append(Component.text(", the fog releases the souls, dealing "))
                         .append(ElementType.AETHER.asComponentAreaOfEffectDamage())
                         .append(Component.text(" and applies a high amount of "))
                         .append(ElementType.AETHER)
                         .append(Component.text(" anomaly."))
        );
    }
    
    @Override
    public @NotNull TalentTarget target(@NotNull HariantPlayer player) {
        return TalentTarget.none();
    }
    
    @Override
    public @NotNull Response execute(@NotNull HariantPlayer player, @NotNull TalentContext context) {
        player.launchProjectile(Snowball.class, null, SoulFogProjectile::new);
        
        // Fx
        player.playWorldSound(Sound.ENTITY_EGG_THROW, 0.5f);
        player.playWorldSound(Sound.ENTITY_WARDEN_TENDRIL_CLICKS, 0.75f);
        
        return Response.ok();
    }
    
    private class SoulFog extends HariantTickingTask {
        
        private final HariantEntity entity;
        private final Location location;
        private final DisplayEntity displayEntity;
        private final ElementSource elementSource;
        private final PullSource pullSource;
        
        SoulFog(@NotNull HariantEntity entity, @NotNull Location location) {
            super(Scheduler.ofTimer(soulFogDelay.intValue(), 1));
            
            this.entity = entity;
            this.location = location;
            this.displayEntity = model.spawn(location);
            this.elementSource = ElementSource.create(ElementType.AETHER, entity, soulFogAetherAnomalyApplication.doubleValue());
            this.pullSource = new PullSourceSoulFog(entity, location);
            
            // Fx
            entity.playWorldSound(location, Sound.ENTITY_WARDEN_ROAR, 0.75f);
        }
        
        @Override
        public void onCancel() {
            displayEntity.remove();
            pullSource.cancel();
        }
        
        @Override
        public void run(int tick) {
            if (tick > getDuration()) {
                this.explode();
                this.cancel();
                return;
            }
            
            final double progress = (double) tick / getDuration();
            
            entity.collectNearbyEntities(location, soulFogRadius)
                  .filter(entity::canAffect)
                  .forEach(entity -> {
                      entity.applyElement(elementSource);
                      entity.showWarning(WarningType.DANGER, 5);
                  });
            
            // Fx
            final double radius = soulFogRadius.doubleValue() * 0.5;
            
            entity.spawnWorldParticle(location, Particle.GLOW, 10, radius, radius * 0.5, radius, 0.25f);
            
            int count = 0;
            final double spread = Math.PI / Math.max(1, displayEntity.size());
            
            for (DisplayPart display : displayEntity) {
                if (!display.isTagged("animate")) {
                    continue;
                }
                
                final Vector3f translation = display.getTranslation();
                translation.y += (float) Math.cos(Math.PI * 4 * progress + (count++ * spread)) * 0.05f;
                
                display.setTranslation(translation);
            }
        }
        
        public void explode() {
            final DamageSource damageSource = new SoulFogExplosionDamageSource(entity);
            final ElementSource elementSource = ElementSource.create(ElementType.AETHER, entity, soulFogExplosionAetherAnomalyApplication.doubleValue());
            final Counter numberOfEnemiesHit = Counter.counter();
            
            entity.collectNearbyEntities(location, soulFogExplosionRadius)
                  .filter(entity::canAffect)
                  .forEach(entity -> {
                      entity.damage(damageSource);
                      entity.applyElement(elementSource);
                      
                      numberOfEnemiesHit.increment();
                  });
            
            // Fx
            entity.playWorldSound(location, Sound.ENTITY_WARDEN_SONIC_BOOM, 1.25f);
            entity.spawnWorldParticle(location, Particle.EXPLOSION_EMITTER, 1, 0);
            
            // Achievement
            if (entity instanceof HariantPlayer player) {
                AchievementMageSoulHarvested.progress(player.getProfile(), numberOfEnemiesHit);
            }
        }
    }
    
    private class SoulFogProjectile extends HariantProjectile {
        
        SoulFogProjectile(@NotNull ThrowableProjectile projectile, @NotNull HariantEntity shooter) {
            super(projectile, shooter);
            
            projectile.setItem(SKULL_TEXTURE);
        }
        
        @Override
        public void onHit(@Nullable HariantEntity entity, @Nullable Block block) {
            final HariantEntity shooter = this.getShooter();
            final Location location = LocationHelper.anchor(this.getLocation());
            
            location.setYaw(shooter.random.nextFloat() * 180);
            
            // Don't delegate soul fog
            new SoulFog(shooter, location);
            
            // Fx
            shooter.playWorldSound(location, Sound.BLOCK_SCULK_SPREAD, 0.0f);
        }
        
    }
    
    private class SoulFogExplosionDamageSource extends DamageSourceImpl {
        
        SoulFogExplosionDamageSource(@NotNull HariantEntity source) {
            super(damageSourceIdentity, source, DamageType.TALENT, ElementType.AETHER, DamageComponents.ofCommon(), Set.of(), soulFogExplosionDamage.getScaledValue(source), 0);
        }
    }
    
    private class PullSourceSoulFog extends PullSource {
        
        PullSourceSoulFog(@NotNull HariantEntity source, @NotNull Location centre) {
            super(source, centre, TalentSoulFog.this.getName(), TalentSoulFog.this.getDuration(), soulFogRadius.doubleValue(), pullStrength.doubleValue(), pullResistance.doubleValue());
        }
        
    }
    
}