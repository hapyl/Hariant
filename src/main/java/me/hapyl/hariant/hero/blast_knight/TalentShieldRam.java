package me.hapyl.hariant.hero.blast_knight;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.VanillaAttributeModifier;
import me.hapyl.hariant.entity.effect.status.EnumStatusEffect;
import me.hapyl.hariant.entity.player.DelegateType;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.talent.Response;
import me.hapyl.hariant.talent.Talent;
import me.hapyl.hariant.talent.TalentContext;
import me.hapyl.hariant.talent.TalentType;
import me.hapyl.hariant.talent.field.DisplayField;
import me.hapyl.hariant.talent.target.TalentTarget;
import me.hapyl.hariant.task.HariantTickingTask;
import me.hapyl.hariant.task.Scheduler;
import me.hapyl.hariant.util.BoundingBoxBlueprint;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public final class TalentShieldRam extends Talent {
    
    private final @DisplayField Decimal ramStrength = Decimal.ofValue(0.9);
    private final @DisplayField Decimal ramStunDuration = Decimal.ofSeconds(2f);
    
    private final @DisplayField BoundingBoxBlueprint bashBoundingBox = BoundingBoxBlueprint.define(0.75, 1.5, 0.75);
    
    private final VanillaAttributeModifier vanillaAttributeModifier = VanillaAttributeModifier.create(
            Key.ofString("shield_ram"),
            Attribute.STEP_HEIGHT,
            VanillaAttributeModifier.Operation.FLAT,
            1.0
    );
    
    private final int ramDuration = (int) Math.floor(ramStrength.doubleValue() * 10 - 2);
    private final int ramStunCollisionModulo = (int) Math.floor((double) ramDuration / 4);
    
    public TalentShieldRam(@NotNull Key key) {
        super(key, Component.text("Shield Ram"), Icon.ofMaterial(Material.GOAT_HORN));
        
        setCooldownSeconds(12);
        setTalentType(TalentType.IMPAIR);
        
        setDescription(
                Component.empty()
                         .append(Component.text("Ram with your "))
                         .append(Component.text("shield", Colors.GREEN))
                         .append(Component.text(" forward, travelling a short distance forward and "))
                         .append(Component.text("stunning", Colors.YELLOW))
                         .append(Component.text(" the first "))
                         .append(Component.text("enemy", Colors.RED))
                         .append(Component.text(" hit."))
        );
    }
    
    @Override
    public @NotNull TalentTarget target(@NotNull HariantPlayer player) {
        return TalentTarget.none();
    }
    
    @Override
    public @NotNull Response execute(@NotNull HariantPlayer player, @NotNull TalentContext context) {
        final Vector vector = player.getDirection().setY(0).normalize().multiply(ramStrength.doubleValue());
        
        // Perform dash
        player.setVelocity(vector);
        player.addVanillaAttributeModifier(vanillaAttributeModifier);
        
        // Perform collision
        player.delegate(new ShieldRam(player), DelegateType.INTERRUPTABLE);
        
        return Response.ok();
    }
    
    private class ShieldRam extends HariantTickingTask {
        
        private final @NotNull HariantPlayer player;
        
        ShieldRam(@NotNull HariantPlayer player) {
            super(Scheduler.ofTimer());
            this.player = player;
        }
        
        @Override
        public void run(int tick) {
            if (tick > ramDuration) {
                this.cancel();
            }
            else if (tick % ramStunCollisionModulo == 0) {
                final HariantEntity entity = player.collectNearbyEntities(bashBoundingBox.create(player.getLocation()))
                                                   .filter(player::canAffect)
                                                   .findFirst()
                                                   .orElse(null);
                
                if (entity == null) {
                    return;
                }
                
                // Stun the entity
                entity.addEffect(EnumStatusEffect.STUNNED, ramStunDuration.intValue(), player);
                this.cancel();
                
                // Fx
                player.playWorldSound(Sound.ENTITY_ITEM_BREAK, 0.0f);
            }
        }
        
        @Override
        public void onCancel() {
            player.removeVanillaAttributeModifier(vanillaAttributeModifier);
        }
    }
    
}