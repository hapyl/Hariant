package me.hapyl.hariant.hero.orc;

import me.hapyl.eterna.module.block.display.DisplayEntity;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.attribute.AttributeScaling;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.EntityCollector;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.damage.DamageSourceIdentity;
import me.hapyl.hariant.entity.damage.DamageSourceImpl;
import me.hapyl.hariant.entity.damage.DamageType;
import me.hapyl.hariant.entity.damage.DeathMessage;
import me.hapyl.hariant.entity.damage.component.DamageComponents;
import me.hapyl.hariant.entity.effect.status.StatusEffectType;
import me.hapyl.hariant.entity.player.DelegateType;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.talent.TalentRegistry;
import me.hapyl.hariant.talent.TalentType;
import me.hapyl.hariant.task.HariantTickingTask;
import me.hapyl.hariant.task.Scheduler;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.Models;
import me.hapyl.hariant.util.decimal.Decimal;
import me.hapyl.hariant.util.field.DisplayField;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.Set;

public final class TalentPoleaxeDash extends TalentPoleaxe {
    
    static final Component TALENT_NAME = Component.text("Poleaxe Dash");
    
    private final @DisplayField AttributeScaling damage = AttributeScaling.create(AttributeType.ATTACK, 172);
    
    private final @DisplayField Decimal radius = Decimal.ofValue(0.5);
    private final @DisplayField Decimal pullingStrength = Decimal.ofValue(0.6);
    private final @DisplayField Decimal stunDuration = Decimal.ofSeconds(5);
    
    private final DamageSourceIdentity damageSourceIdentity = DamageSourceIdentity.createOfNamed(
            this, Key.ofString("poleaxe_dash_damage_source"),
            DeathMessage.create("{player} was swept [by {killer}'s Poleaxe]")
    );
    
    public TalentPoleaxeDash(@NotNull Key key) {
        super(key, TALENT_NAME, Icon.ofMaterial(Material.NAUTILUS_SHELL));
        
        setDurationSeconds(1.5f);
        setCooldownSeconds(15);
        
        setTalentType(TalentType.MOVEMENT);
        
        setDescription(
                Component.empty()
                         .append(Component.text("Swing your poleaxe forward, pulling yourself with it."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("Colliding with an "))
                         .append(Component.text("enemy", Colors.RED))
                         .append(Component.text(" deals "))
                         .appendNewline()
                         .append(ElementType.PHYSICAL.asComponentDamage())
                         .append(Component.text(" and stuns them."))
                         .appendNewline()
                         .appendNewline()
                         .append(super.createCooldownComponent(TalentPoleaxeSpin.TALENT_NAME))
        );
    }
    
    @Override
    public @NotNull TalentPoleaxe otherTalent() {
        return TalentRegistry.POLEAXE_SPIN;
    }
    
    @Override
    public void execute(@NotNull HariantPlayer player) {
        player.delegate(new PoleaxeDash(player), DelegateType.PERSISTENT);
    }
    
    public class PoleaxeDash extends HariantTickingTask implements EntityCollector {
        
        private final HariantPlayer player;
        private final Vector vector;
        private final DisplayEntity display;
        
        PoleaxeDash(@NotNull HariantPlayer player) {
            super(Scheduler.ofTimer());
            
            this.player = player;
            
            // Calculate vector
            final Location location = player.getEyeLocation();
            
            this.vector = location.getDirection().setY(0).normalize().multiply(pullingStrength.doubleValue()).setY(-1);
            
            // Modify pitch so the axe is sideways
            location.setPitch(90);
            
            this.display = Models.ORC_WEAPON.spawn(location);
            
            // Fx
            player.playWorldSound(Sound.ENTITY_BREEZE_SHOOT, 0.75f);
            player.playWorldSound(Sound.ENTITY_BREEZE_CHARGE, 0.75f);
        }
        
        @Override
        public void run(int tick) {
            if (tick > getDuration() || (tick > 10 && player.isSneaking())) {
                this.cancel();
                return;
            }
            
            // Affect entities
            final HariantEntity entity = collectNearbyEntities(radius)
                    .filter(player::canAffect)
                    .findFirst()
                    .orElse(null);
            
            if (entity != null) {
                entity.damage(new PoleaxeDashDamageSource(player));
                entity.addEffect(StatusEffectType.STUNNED, stunDuration, player);
                this.cancel();
                return;
            }
            
            // Constantly pull player forward
            player.setVelocity(vector);
            
            // Rotate & sync display entity
            display.setRotation(new Vector3f(0, (float) (Math.PI * 0.1 * tick), 0));
            
            final Location location = this.getLocation();
            
            // Offset location a little bit and set pitch
            location.setYaw((float) Math.toDegrees(Math.atan2(-vector.getX(), vector.getZ())));
            location.setPitch(90);
            
            display.teleport(location);
            
            // Fx
            if (tick % 10 == 0) {
                player.playWorldSound(Sound.ENTITY_PLAYER_ATTACK_CRIT, 0.5f, 1.25f);
            }
        }
        
        @Override
        public void onCancel() {
            display.remove();
        }
        
        @Override
        public @NotNull Location getLocation() {
            return player.getLocationInFront(1.0).add(0, 1.0, 0);
        }
    }
    
    public class PoleaxeDashDamageSource extends DamageSourceImpl {
        
        PoleaxeDashDamageSource(@NotNull HariantPlayer source) {
            super(damageSourceIdentity, source, DamageType.TALENT, ElementType.PHYSICAL, DamageComponents.ofCommon(), Set.of(), damage.getScaledValue(source), 0);
        }
        
    }
    
}