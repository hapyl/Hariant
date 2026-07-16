package me.hapyl.hariant.hero.shark;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.attribute.AttributeScaling;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.element.anomaly.ElementalAnomalyType;
import me.hapyl.hariant.entity.EntityCollector;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.WarningType;
import me.hapyl.hariant.entity.damage.*;
import me.hapyl.hariant.entity.damage.component.DamageComponent;
import me.hapyl.hariant.entity.player.DelegateType;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.talent.Response;
import me.hapyl.hariant.talent.Talent;
import me.hapyl.hariant.talent.TalentContext;
import me.hapyl.hariant.talent.field.DisplayField;
import me.hapyl.hariant.talent.target.TalentTarget;
import me.hapyl.hariant.task.HariantTickingTask;
import me.hapyl.hariant.task.Scheduler;
import me.hapyl.hariant.util.BoundingBoxBlueprint;
import me.hapyl.hariant.util.Icon;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EvokerFangs;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public final class TalentSharkBite extends Talent {
    
    private final @DisplayField AttributeScaling damage = AttributeScaling.create(Map.of(
            AttributeType.ATTACK, 45.0,
            AttributeType.ELEMENTAL_MASTERY, 90.0
    ));
    
    private final @DisplayField BoundingBoxBlueprint boundingBox = BoundingBoxBlueprint.define(1.5, 3, 1.5);
    
    private final DamageSourceIdentity damageSourceIdentity = DamageSourceIdentity.create(
            this,
            DeathMessage.create("{player} got their toe bitten off [by {killer}]")
    );
    
    public TalentSharkBite(@NotNull Key key) {
        super(key, Component.text("Shark's Bite"), Icon.ofMaterial(Material.SHEARS));
        
        // Anything other than 8 ticks will look off because of the hardcoded animation
        setDuration(8);
        setCooldownSeconds(16);
        
        setDescription(
                Component.empty()
                         .append(Component.text("Command an ancient creature to emerge in front of you, dealing "))
                         .append(ElementType.WATER.asComponentAreaOfEffectDamage())
                         .append(Component.text(" and "))
                         .append(Component.text("forcefully", Style.style(TextDecoration.UNDERLINED)))
                         .append(Component.text(" triggering "))
                         .append(ElementalAnomalyType.BLEED)
                         .append(Component.text("."))
        );
        
    }
    
    @Override
    public @NotNull TalentTarget target(@NotNull HariantPlayer player) {
        return TalentTarget.none();
    }
    
    @Override
    public @NotNull Response execute(@NotNull HariantPlayer player, @NotNull TalentContext context) {
        final Location location = player.getLocationInFront(2);
        final double damage = this.damage.getScaledValue(player);
        
        player.delegate(new SharkBite(player, location, damage), DelegateType.INTERRUPTABLE);
        return Response.ok();
    }
    
    public class SharkBite extends HariantTickingTask implements EntityCollector {
        
        private final HariantPlayer player;
        private final Location location;
        private final EvokerFangs fangs;
        private final DamageSource damageSource;
        
        SharkBite(@NotNull HariantPlayer player, @NotNull Location location, final double damage) {
            super(Scheduler.ofTimer());
            
            this.player = player;
            this.location = location;
            this.fangs = player.getWorld().spawn(location, EvokerFangs.class, self -> {});
            this.damageSource = new SharkBiteDamageSource(player, damage);
        }
        
        @Override
        public void run(int tick) {
            final Stream<? extends HariantEntity> entities = collectNearbyEntities(boundingBox.create(location)).filter(player::canAffect);
            
            if (tick > getDuration()) {
                this.bite(entities);
                this.cancel();
                return;
            }
            
            // Warn players
            entities.forEach(entity -> entity.showWarning(WarningType.DANGER, 5));
        }
        
        @Override
        public @NotNull Location getLocation() {
            return location;
        }
        
        @Override
        public void onCancel() {
            fangs.remove();
        }
        
        private void bite(@NotNull Stream<? extends HariantEntity> entities) {
            entities.forEach(entity -> {
                entity.damage(damageSource);
                entity.triggerAnomaly(ElementalAnomalyType.BLEED, player);
            });
        }
        
    }
    
    private class SharkBiteDamageSource extends DamageSourceImpl {
        SharkBiteDamageSource(@Nullable HariantEntity source, double damage) {
            super(damageSourceIdentity, source, DamageType.TALENT, ElementType.PHYSICAL, DamageComponent.ofCommon(), Set.of(), damage, 0);
        }
    }
}