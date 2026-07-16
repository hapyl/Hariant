package me.hapyl.hariant.hero.mage;

import me.hapyl.eterna.module.block.display.BDEngine;
import me.hapyl.eterna.module.block.display.DisplayEntity;
import me.hapyl.eterna.module.block.display.DisplayModel;
import me.hapyl.eterna.module.block.display.DisplayPart;
import me.hapyl.eterna.module.location.LocationHelper;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.attribute.AttributeScaling;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.modifier.AttributeModifier;
import me.hapyl.hariant.attribute.modifier.AttributeModifierType;
import me.hapyl.hariant.element.ElementSource;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.WarningType;
import me.hapyl.hariant.entity.damage.DamageSource;
import me.hapyl.hariant.entity.damage.DamageSourceIdentity;
import me.hapyl.hariant.entity.damage.DamageType;
import me.hapyl.hariant.entity.damage.DeathMessage;
import me.hapyl.hariant.entity.damage.component.DamageComponent;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.hero.HeroRegistry;
import me.hapyl.hariant.talent.Response;
import me.hapyl.hariant.talent.Talent;
import me.hapyl.hariant.talent.TalentContext;
import me.hapyl.hariant.talent.TalentType;
import me.hapyl.hariant.talent.field.DisplayField;
import me.hapyl.hariant.talent.target.TalentTarget;
import me.hapyl.hariant.task.HariantTickingTask;
import me.hapyl.hariant.task.Scheduler;
import me.hapyl.hariant.util.Definition;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public final class TalentSoulFog extends Talent {
    
    private final @DisplayField Decimal soulFogDelay = Decimal.ofSeconds(0.1f);
    private final @DisplayField Decimal soulFogRadius = Decimal.ofValue(2.5);
    private final @DisplayField Decimal soulFogAetherAnomalyApplication = Decimal.ofElementalApplication(ElementType.AETHER, 15);
    
    private final @DisplayField Decimal soulFogExplosionRadius = Decimal.ofValue(3);
    private final @DisplayField Decimal soulFogExplosionAetherAnomalyApplication = Decimal.ofElementalApplication(ElementType.AETHER, 200);
    private final @DisplayField Decimal soulFogExplosionSoulGenerationPerEnemyHit = Decimal.ofValue(2);
    
    private final @DisplayField Decimal speedDecrease = Decimal.ofPercentage(30);
    
    private final @DisplayField AttributeScaling soulFogExplosionDamage = AttributeScaling.create(AttributeType.ATTACK, 144);
    
    private final DamageSourceIdentity damageSourceIdentity = DamageSourceIdentity.create(
            this,
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
                         .append(Component.text("Create a "))
                         .append(Component.text("Fog of Souls", Colors.GRAY, TextDecoration.UNDERLINED))
                         .append(Component.text(" in front of you that slows "))
                         .append(Component.text("enemies", Colors.RED))
                         .append(Component.text(" and constantly applies "))
                         .append(ElementType.AETHER)
                         .append(Component.text(" anomaly."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("After "))
                         .append(this.getDurationFormatted())
                         .append(Component.text(", the fog explodes violently, dealing "))
                         .append(ElementType.AETHER.asComponentAreaOfEffectDamage())
                         .append(Component.text(", applies high amount of "))
                         .append(ElementType.AETHER)
                         .append(Component.text(" anomaly and generates "))
                         .append(soulFogExplosionSoulGenerationPerEnemyHit)
                         .appendSpace()
                         .append(Definition.SOUL_FRAGMENT)
                         .append(Component.text(" per enemy hit."))
        );
    }
    
    @NotNull
    @Override
    public TalentTarget target(@NotNull HariantPlayer player) {
        return TalentTarget.none();
    }
    
    @Override
    public @NotNull Response execute(@NotNull HariantPlayer player, @NotNull TalentContext context) {
        final Location location = LocationHelper.anchor(player.getLocationInFront(2));
        location.setYaw(0);
        location.setPitch(0);
        
        new SoulFog(player, location);
        
        // Fx
        player.spawnWorldParticle(location, Particle.SCULK_SOUL, 20, 1, 0.2, 1, 0.1f);
        
        return Response.ok();
    }
    
    private class SoulFog extends HariantTickingTask {
        
        private final HariantPlayer player;
        private final Location location;
        private final DisplayEntity displayEntity;
        
        SoulFog(@NotNull HariantPlayer player, @NotNull Location location) {
            super(Scheduler.ofTimer(soulFogDelay.intValue(), 1));
            
            this.player = player;
            this.location = location;
            this.displayEntity = model.spawn(location);
            
            // Fx
            player.playWorldSound(location, Sound.ENTITY_WARDEN_ROAR, 0.75f);
        }
        
        @Override
        public void onCancel() {
            displayEntity.remove();
        }
        
        @Override
        public void run(int tick) {
            if (tick > getDuration()) {
                this.explode();
                this.cancel();
                return;
            }
            
            final double progress = (double) tick / getDuration();
            
            player.collectNearbyEntities(location, soulFogRadius)
                  .filter(player::canAffect)
                  .forEach(entity -> {
                      entity.getAttributes().addModifier(new SoulFogModifier(player));
                      
                      entity.applyElement(ElementSource.create(ElementType.AETHER, player, soulFogAetherAnomalyApplication.doubleValue()));
                      entity.showWarning(WarningType.DANGER, 5);
                  });
            
            // Fx
            final double radius = soulFogRadius.doubleValue() * 0.5;
            
            player.spawnWorldParticle(location, Particle.GLOW, 10, radius, radius * 0.5, radius, 0.25f);
            
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
            final DamageSource damageSource = DamageSource.builder(damageSourceIdentity, soulFogExplosionDamage.getScaledValue(player))
                                                          .source(player)
                                                          .damageType(DamageType.TALENT)
                                                          .elementType(ElementType.AETHER)
                                                          .components(DamageComponent.ofCommon())
                                                          .build();
            
            final ElementSource elementSource = ElementSource.create(ElementType.AETHER, player, soulFogExplosionAetherAnomalyApplication.doubleValue());
            
            player.collectNearbyEntities(location, soulFogExplosionRadius)
                  .filter(player::canAffect)
                  .forEach(entity -> {
                      entity.damage(damageSource);
                      entity.applyElement(elementSource);
                      
                      // Increment souls
                      player.getHeroData(HeroRegistry.MAGE, HeroDataMage::new).incrementSouls(soulFogExplosionSoulGenerationPerEnemyHit.intValue());
                  });
            
            // Fx
            player.playWorldSound(location, Sound.ENTITY_WARDEN_SONIC_BOOM, 1.25f);
            player.spawnWorldParticle(location, Particle.EXPLOSION_EMITTER, 1, 0);
        }
    }
    
    private class SoulFogModifier extends AttributeModifier {
        SoulFogModifier(@NotNull HariantPlayer player) {
            super(TalentSoulFog.this, player, 5);
            
            of(AttributeType.MOVEMENT_SPEED, AttributeModifierType.ADDITIVE, -speedDecrease.doubleValue());
        }
        
        @Override
        public void display(@NotNull Location location) {
        }
    }
}