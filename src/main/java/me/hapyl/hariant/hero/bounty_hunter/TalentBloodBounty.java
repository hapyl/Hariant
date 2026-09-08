package me.hapyl.hariant.hero.bounty_hunter;

import com.google.common.collect.Maps;
import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.damage.AssistSource;
import me.hapyl.hariant.entity.damage.DamageInstance;
import me.hapyl.hariant.entity.damage.mutator.DamageMutator;
import me.hapyl.hariant.entity.heal.HealingSource;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.event.HariantDamageComputeEvent;
import me.hapyl.hariant.talent.Response;
import me.hapyl.hariant.talent.Talent;
import me.hapyl.hariant.talent.TalentContext;
import me.hapyl.hariant.talent.field.DisplayField;
import me.hapyl.hariant.talent.target.TalentTarget;
import me.hapyl.hariant.talent.target.TalentTargetEntityRayCast;
import me.hapyl.hariant.task.HariantTickingTask;
import me.hapyl.hariant.task.Scheduler;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.MatrixUtils;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.ShadowColor;
import net.kyori.adventure.text.object.ObjectContents;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public final class TalentBloodBounty extends Talent implements Listener {
    
    public static final Component COMPONENT_SPRITE = Component.object(ObjectContents.sprite(
            net.kyori.adventure.key.Key.key("particles"),
            net.kyori.adventure.key.Key.key("raid_omen")
    )).shadowColor(ShadowColor.shadowColor(0, 0, 0, 200));
    
    private static final Color NO_BACKGROUND = Color.fromARGB(0, 0, 0, 0);
    
    private final @DisplayField Decimal maximumDistance = Decimal.ofValue(25);
    
    private final @DisplayField Decimal damageIncrease = Decimal.ofPercentage(25);
    private final @DisplayField Decimal elementalApplicationIncrease = Decimal.ofPercentage(50);
    
    private final @DisplayField Decimal healingOfMaxHealth = Decimal.ofPercentage(15);
    
    private final Map<HariantEntity, BloodBounty> bloodBounties;
    
    public TalentBloodBounty(@NotNull Key key) {
        super(key, Component.text("Blood Bounty"), Icon.ofMaterial(Material.STRIDER_SPAWN_EGG));
        
        setDurationSeconds(30);
        setCooldownSeconds(30);
        
        setDescription(
                Component.empty()
                         .append(Component.text("Apply a "))
                         .append(Component.text("Blood Bounty", Colors.BLOOD))
                         .append(Component.text(" to the target enemy."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("Blood Bounty", Colors.GOLD))
                         .appendNewline()
                         .append(Component.text("The affected entity takes "))
                         .append(damageIncrease)
                         .append(Component.text(" more "))
                         .append(Component.text("DMG", Colors.RED))
                         .append(Component.text(" from all sources and "))
                         .append(Component.text("elemental build-up", Colors.ATTRIBUTE_ELEMENTAL_MASTERY))
                         .append(Component.text(" is increased by "))
                         .append(elementalApplicationIncrease)
                         .append(Component.text("."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("When the entity dies, "))
                         .append(Component.text("Bounty Hunter", Colors.WHITE))
                         .append(Component.text(" always gets an "))
                         .append(Component.text("assist", Colors.GREEN))
                         .append(Component.text(" as well as additional healing equal to "))
                         .append(healingOfMaxHealth)
                         .append(Component.text(" of "))
                         .appendNewline()
                         .append(AttributeType.MAX_HEALTH)
                         .append(Component.text("."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("Cooldown of this talent starts when the bounty is complete.", Colors.DARK_GRAY))
        );
        
        bloodBounties = Maps.newHashMap();
    }
    
    @EventHandler
    public void handleHariantDamageComputeEvent(HariantDamageComputeEvent ev) {
        final HariantEntity entity = ev.getEntity();
        
        if (!(bloodBounties.get(entity) instanceof BloodBounty bloodBounty)) {
            return;
        }
        
        // Increase the DMG dealt
        final DamageInstance damageInstance = ev.getDamageInstance();
        
        damageInstance.mutateDamage(this, DamageMutator.multiply(), 1 + damageIncrease.doubleValue());
        
        // Increase the elemental application
        damageInstance.setElementUnits(damageInstance.getElementUnits() * (1 + elementalApplicationIncrease.doubleValue()));
        
        // If entity is a player, add the applier to assisters
        if (entity instanceof HariantPlayer playerEntity) {
            playerEntity.getCombatTracker().assist(AssistSource.create(bloodBounty.player, this));
        }
    }
    
    @Override
    public @NotNull TalentTarget target(@NotNull HariantPlayer player) {
        return TalentTarget.targetEntity(
                maximumDistance.doubleValue(),
                1.0,
                TalentTargetEntityRayCast.BlockCollision.ALLOW_PASSABLE,
                TalentTargetEntityRayCast.EntityPriority.PLAYER_PRIORITY,
                entity -> player.canAffect(entity) && !bloodBounties.containsKey(entity)
        );
    }
    
    @Override
    public @NotNull Response execute(@NotNull HariantPlayer player, @NotNull TalentContext context) {
        final HariantEntity target = context.retrieve(HariantEntity.class);
        
        if (bloodBounties.containsKey(target)) {
            return Response.error("The target already has bounty somehow!");
        }
        
        // Don't allow re-applying bounty
        if (hasCounty(player)) {
            return Response.error("You already have a bounty active!");
        }
        
        // Apply bounty, do not delegate
        bloodBounties.put(target, new BloodBounty(player, target));
        
        // Fx
        player.playSound(Sound.ITEM_FLINTANDSTEEL_USE, 0.0f);
        player.playSound(Sound.ENTITY_EVOKER_FANGS_ATTACK, 0.0f);
        
        return Response.await();
    }
    
    public boolean hasCounty(@NotNull HariantPlayer player) {
        for (BloodBounty bloodBounty : bloodBounties.values()) {
            if (bloodBounty.player.equals(player)) {
                return true;
            }
        }
        
        return false;
    }
    
    public class BloodBounty extends HariantTickingTask {
        
        private final HariantPlayer player;
        private final HariantEntity entity;
        
        private final TextDisplay textDisplay;
        
        private final Component componentPlayerToEntity;
        private final Component componentEntityToPlayer;
        
        private BloodBounty(@NotNull HariantPlayer player, @NotNull HariantEntity entity) {
            super(Scheduler.ofTimer());
            
            this.player = player;
            this.entity = entity;
            this.textDisplay = createTextDisplay(getTextDisplayLocation());
            this.componentEntityToPlayer = createComponent(entity, true);
            this.componentPlayerToEntity = createComponent(player, false);
        }
        
        @Override
        public void run(int tick) {
            // If player or entity is dead, cancel the bounty
            if (tick >= getDuration() || player.isDead() || entity.isDead()) {
                this.cancel();
                return;
            }
            
            // Sync text display
            final float scale = (float) (2f + Math.sin(Math.toRadians(tick * 15)));
            
            textDisplay.setTransformation(MatrixUtils.scale(scale));
            textDisplay.teleport(getTextDisplayLocation());
            
            if (tick % 5 == 0) {
                final Component timeLeft = Component.space().append(Component.text(Tick.format(getDuration() - tick), Colors.NUMBER));
                
                player.actionbar(BloodBounty.class, componentEntityToPlayer.append(timeLeft));
                
                if (entity instanceof HariantPlayer playerEntity) {
                    playerEntity.actionbar(TalentBloodBounty.class, componentPlayerToEntity.append(timeLeft));
                }
            }
        }
        
        @Override
        public void onCancel() {
            textDisplay.remove();
            
            bloodBounties.remove(entity);
            
            // Start the cooldown when bounty ends
            player.setCooldown(TalentBloodBounty.this);
            
            // If bounty is complete, heal bounty hunter
            if (entity.isDead()) {
                player.heal(HealingSource.create(player.getMaxHealth() * healingOfMaxHealth.doubleValue(), TalentBloodBounty.this));
            }
        }
        
        private @NotNull Location getTextDisplayLocation() {
            return entity.getLocation().add(0, entity.getHeight(), 0).add(0, 0.2, 0);
        }
        
        private static @NotNull TextDisplay createTextDisplay(@NotNull Location location) {
            return location.getWorld().spawn(location, TextDisplay.class, self -> {
                self.setBillboard(Display.Billboard.VERTICAL);
                self.setTeleportDuration(1);
                self.setTransformation(MatrixUtils.scale(2));
                self.setDefaultBackground(false);
                self.setBackgroundColor(NO_BACKGROUND);
                
                self.text(COMPONENT_SPRITE);
            });
        }
        
        private static @NotNull Component createComponent(@NotNull HariantEntity entity, boolean self) {
            return Component.empty()
                            .append(COMPONENT_SPRITE)
                            .appendSpace()
                            .append(Component.text("[", self ? Colors.GREEN : Colors.RED))
                            .append(entity.asHeadComponent())
                            .append(Component.text("]", self ? Colors.GREEN : Colors.RED));
        }
    }
    
}