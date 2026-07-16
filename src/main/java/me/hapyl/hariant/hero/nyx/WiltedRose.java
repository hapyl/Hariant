package me.hapyl.hariant.hero.nyx;

import me.hapyl.eterna.module.block.display.BDEngine;
import me.hapyl.eterna.module.block.display.DisplayEntity;
import me.hapyl.eterna.module.block.display.DisplayModel;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.damage.*;
import me.hapyl.hariant.entity.damage.component.DamageComponent;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.task.HariantTickingTask;
import me.hapyl.hariant.task.Scheduler;
import me.hapyl.hariant.util.QuaternionRotation;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

public final class WiltedRose extends HariantTickingTask {
    
    private static final DisplayModel MODEL = BDEngine.parse(
            "/summon block_display ~-0.5 ~ ~-0.5 {Passengers:[{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;-1068371645,-1203952194,1458675595,49119003],properties:[{name:\"textures\",value:\"ewogICJ0aW1lc3RhbXAiIDogMTc4MTAxNjMxNjkzMCwKICAicHJvZmlsZUlkIiA6ICI0YWU5MTM5MzZhOGU0MWU0YWNlMTYyYjI4YmM0MzMwMyIsCiAgInByb2ZpbGVOYW1lIiA6ICJ6ZXJ2YXRpb24iLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTdjNWE3NTIwZjA5Yzk1OTUyNzUzNTc4ZTgzOTZiZTY5ZTA2MDdmODE3NmExM2EwNmE3YzY2YThlM2UxNzgwOCIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9\"}]}}},item_display:\"none\",transformation:[1f,0f,0f,0f,0f,1f,0f,0.5125f,0f,0f,1f,0f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;-67745515,-1044678911,-1011336281,20198111],properties:[{name:\"textures\",value:\"ewogICJ0aW1lc3RhbXAiIDogMTc4MTAxNDI0NzU0OSwKICAicHJvZmlsZUlkIiA6ICJhNWM5MmJlODg5MGY0NDU0OTdkNGEwOTM2Yjg1NDc5OSIsCiAgInByb2ZpbGVOYW1lIiA6ICJDbG93ZGVyVGVjaCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS82MGI3ZTUzNDcxMDk5NDIwNTQ5Mjg0Nzk1ZTllZjNlMjgyYjBhMWFiMTk2YWRhMzMzMGY5MTFiMTQ3ZjU5YzAzIgogICAgfQogIH0KfQ==\"}]}}},item_display:\"none\",transformation:[0f,-0.0625f,0f,-0.29625f,1f,0f,0f,0.2f,0f,0f,1.025f,0.00375f,0f,0f,0f,1f],Tags: [\"west\"]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;-235189945,-978033769,870310277,718942065],properties:[{name:\"textures\",value:\"ewogICJ0aW1lc3RhbXAiIDogMTc4MTAxNzMxNzA1MCwKICAicHJvZmlsZUlkIiA6ICJiMTM1MDRmMjMxOGI0OWNjYWFkZDcyYWVhYmMyNTQ1MCIsCiAgInByb2ZpbGVOYW1lIiA6ICJUeXBrZW4iLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTBjY2FhMmExMmM1NDgxMTY5YmEwMTI5NDU1OTU5YjBkYTlkYTZmMWJhZGY5NGI3MGVlZjZkOWY0MTYzM2E2ZSIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9\"}]}}},item_display:\"none\",transformation:[0f,0f,1.025f,-0.005625f,-1f,0f,0f,0.2f,0f,-0.0625f,0f,-0.293125f,0f,0f,0f,1f],Tags: [\"north\"]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;-1786685695,2115638748,-395777575,1042588237],properties:[{name:\"textures\",value:\"ewogICJ0aW1lc3RhbXAiIDogMTc4MTAxNDI0NzU0OSwKICAicHJvZmlsZUlkIiA6ICJhNWM5MmJlODg5MGY0NDU0OTdkNGEwOTM2Yjg1NDc5OSIsCiAgInByb2ZpbGVOYW1lIiA6ICJDbG93ZGVyVGVjaCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS82MGI3ZTUzNDcxMDk5NDIwNTQ5Mjg0Nzk1ZTllZjNlMjgyYjBhMWFiMTk2YWRhMzMzMGY5MTFiMTQ3ZjU5YzAzIgogICAgfQogIH0KfQ==\"}]}}},item_display:\"none\",transformation:[0f,0.0625f,0f,0.29f,1f,0f,0f,0.2f,0f,0f,-1.025f,-0.00375f,0f,0f,0f,1f],Tags: [\"east\"]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;-1292252864,-1893966665,-1131660275,1520973419],properties:[{name:\"textures\",value:\"ewogICJ0aW1lc3RhbXAiIDogMTc4MTAxNzMxNzA1MCwKICAicHJvZmlsZUlkIiA6ICJiMTM1MDRmMjMxOGI0OWNjYWFkZDcyYWVhYmMyNTQ1MCIsCiAgInByb2ZpbGVOYW1lIiA6ICJUeXBrZW4iLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTBjY2FhMmExMmM1NDgxMTY5YmEwMTI5NDU1OTU5YjBkYTlkYTZmMWJhZGY5NGI3MGVlZjZkOWY0MTYzM2E2ZSIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9\"}]}}},item_display:\"none\",transformation:[0f,0f,-1.025f,-0.000625f,-1f,0f,0f,0.2f,0f,0.0625f,0f,0.293125f,0f,0f,0f,1f],Tags: [\"south\"]}]}"
    );
    
    private static final Map<String, QuaternionRotation> PETAL_ROTATION_MAP = Map.of(
            "north", (quaternion, angle) -> quaternion.rotateLocalX(-angle),
            "south", (quaternion, angle) -> quaternion.rotateLocalX(+angle),
            "west", (quaternion, angle) -> quaternion.rotateLocalZ(+angle),
            "east", (quaternion, angle) -> quaternion.rotateLocalZ(-angle)
    );
    
    private static final DamageSourceIdentity DAMAGE_SOURCE_IDENTITY = DamageSourceIdentity.create(
            Key.ofString("wilted_rose"),
            Component.text("Wilted Rose"),
            DeathMessage.createWithDefaultKiller("{player} has wilted to death")
    );
    
    private static final float PETAL_ROTATION_RADIANS = (float) Math.toRadians(30);
    private static final Particle.DustOptions DUST_OPTIONS = new Particle.DustOptions(Color.fromRGB(126, 65, 80), 1);
    
    private final HariantPlayer player;
    private final Location location;
    private final DisplayEntity displayEntity;
    private final TalentReverberation talent;
    
    private final int bloomDelay;
    private final float rotationAngle;
    
    public WiltedRose(@NotNull HariantPlayer player, @NotNull Location location, @NotNull TalentReverberation talent) {
        super(Scheduler.ofTimer());
        
        this.player = player;
        this.location = location;
        this.displayEntity = MODEL.spawnInterpolated(location);
        this.talent = talent;
        this.bloomDelay = talent.roseBloomDelay.intValue();
        this.rotationAngle = PETAL_ROTATION_RADIANS / bloomDelay;
    }
    
    @Override
    public void run(int tick) {
        if (tick > bloomDelay) {
            this.cancel();
            this.bloom();
            return;
        }
        
        // Fx
        final Location location = displayEntity.getLocation();
        final double progress = (double) tick / bloomDelay;
        
        location.add(0, Math.sin(Math.PI * progress) * 0.125, 0);
        location.setYaw(location.getYaw() + 10);
        
        displayEntity.teleport(location);
        
        // Animate petals
        PETAL_ROTATION_MAP.forEach((tag, rotation) -> {
            displayEntity.stream(tag).forEach(display -> display.setRotation(rotation.rotate(display.getRotation(), rotationAngle)));
        });
        
        // Particle Fx to mask the ugly animation
        player.spawnWorldParticle(location, Particle.DUST, 5, 0.2, 0.2, 0.2, 0.1f, DUST_OPTIONS);
        player.playWorldSound(location, Sound.BLOCK_BIG_DRIPLEAF_BREAK, 0.5f + (float) tick / bloomDelay);
    }
    
    public void bloom() {
        final DamageSource damageSource = new WilterRoseDamageSource(player, talent.roseDamage.getScaledValue(player), talent.roseElementalApplication.doubleValue());
        
        player.collectNearbyEntities(location.add(0, 1, 0), talent.roseExplosionRadius)
              .filter(player::canAffect)
              .forEach(entity -> {
                  entity.damage(damageSource);
              });
        
        // Fx
        player.playWorldSound(location, Sound.ENTITY_WITHER_HURT, 1.5f);
        player.spawnWorldParticle(location, Particle.EXPLOSION_EMITTER, 1, 0);
    }
    
    @Override
    public void onCancel() {
        displayEntity.remove();
    }
    
    public static class WilterRoseDamageSource extends DamageSourceImpl {
        WilterRoseDamageSource(@Nullable HariantEntity source, double damage, double elementalApplication) {
            super(DAMAGE_SOURCE_IDENTITY, source, DamageType.TALENT, ElementType.AETHER, DamageComponent.ofCommon(), Set.of(), damage, elementalApplication);
        }
    }
    
}