package me.hapyl.hariant.hero.bounty_hunter;

import me.hapyl.eterna.module.location.Located;
import me.hapyl.eterna.module.location.LocationHelper;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.entity.EntityGarbageCollector;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.damage.AssistSource;
import me.hapyl.hariant.entity.effect.status.StatusEffectType;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.hero.HeroRegistry;
import me.hapyl.hariant.talent.Response;
import me.hapyl.hariant.talent.TalentContext;
import me.hapyl.hariant.talent.TalentType;
import me.hapyl.hariant.util.field.DisplayField;
import me.hapyl.hariant.talent.rechargeable.RechargeType;
import me.hapyl.hariant.talent.rechargeable.RechargeableTalentData;
import me.hapyl.hariant.talent.rechargeable.TalentRechargeable;
import me.hapyl.hariant.talent.target.TalentTarget;
import me.hapyl.hariant.task.HariantTickingTask;
import me.hapyl.hariant.task.Scheduler;
import me.hapyl.hariant.util.BlockHelper;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.ThisClassShouldNeMovedToEternaAPI;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.Objects;

public final class TalentGrapple extends TalentRechargeable implements Listener {
    
    private static final Vector UP = new Vector(0, 1, 0);
    
    private static final String PREFIX = "\uD83E\uDE9D";
    
    private static final Component COMPONENT_PREFIX = Component.text(PREFIX, Colors.GRAPPLE);
    private static final Component COMPONENT_HOOKED_NO_CUTS = Component.text("ʏᴏᴜ'ʀᴇ ʜᴏᴏᴋᴇᴅ", Colors.GRAPPLE);
    
    private static final Component COMPONENT_ESCAPED = Component.text("ᴇꜱᴄᴀᴘᴇᴅ", Colors.GREEN);
    
    private static final TextColor COLOR_ESCAPE_0 = Colors.YELLOW;
    private static final TextColor COLOR_ESCAPE_1 = Colors.GOLD;
    
    private static final Material TALENT_MATERIAL = Material.LEAD;
    private static final ItemStack CACHED_ITEM_STACK = new ItemStack(TALENT_MATERIAL);
    
    // Making speed anything over 20 blocks per second will cause the collision checks to skip blocks at corners
    private final @DisplayField Decimal extendSpeed = Decimal.ofBlocksPerSecond(20);
    private final @DisplayField Decimal pullSpeed = Decimal.ofBlocksPerSecond(20);
    
    private final @DisplayField Decimal maximumDistance = Decimal.ofValue(50);
    
    private final @DisplayField Decimal clingRadius = Decimal.ofValue(0.8);
    private final @DisplayField Decimal dismountRadius = Decimal.ofValue(1.5);
    
    private final @DisplayField Decimal finalPushBlockMagnitude = Decimal.ofValue(0.25);
    private final @DisplayField Decimal finalPushBlockY = Decimal.ofValue(0.5);
    private final @DisplayField Decimal finalPushEntityMagnitude = Decimal.ofValue(0.3);
    private final @DisplayField Decimal finalPushEntityY = Decimal.ofValue(0.25);
    
    private final @DisplayField Decimal strafingSpeed = Decimal.ofBlocksPerSecond(10);
    private final @DisplayField Decimal strafingSmoothingFactor = Decimal.ofValue(0.5);
    
    private final @DisplayField Decimal cutsToEscape = Decimal.ofValue(3);
    
    public TalentGrapple(@NotNull Key key) {
        super(key, Component.text("Grapple"), Icon.ofMaterial(TALENT_MATERIAL), 3, RechargeType.DEPLETE_ALL);
        
        setTalentType(TalentType.MOVEMENT);
        
        setDurationSeconds(8); // Refers to maximum duration of grapple before it breaks
        setCooldownSeconds(10);
        
        setDescription(
                Component.empty()
                         .append(Component.text("Launch a grappling hook forward."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("Upon colliding with a "))
                         .append(Component.text("block", Colors.GREEN))
                         .append(Component.text(" or an "))
                         .append(Component.text("enemy", Colors.RED))
                         .append(Component.text(", the hook attaches to them and "))
                         .append(Component.text("pulls", Colors.GRAPPLE))
                         .append(Component.text(" you towards it."))
                         .appendNewline()
                         .append(Component.text("Hooked players can cut the hook off them.", Colors.DARK_GRAY))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("While pulling, you can "))
                         .append(Component.text("strafe", Colors.AQUA))
                         .append(Component.text(" horizontally to adjust the pulling direction."))
                         .appendNewline()
                         .appendNewline()
                         .append(this.getMaximumChargesComponent())
        );
    }
    
    @EventHandler
    public void handlePlayerToggleSneakEvent(PlayerToggleSneakEvent ev) {
        if (!ev.isSneaking() || !(Hariant.getEntityOrNull(ev.getPlayer()) instanceof HariantPlayer player)) {
            return;
        }
        
        Hariant.getPlayers().forEach(thatPlayer -> {
            if (!thatPlayer.hasHeroData(HeroRegistry.BOUNTY_HUNTER)) {
                return;
            }
            
            final Grapple grapple = thatPlayer.touchHeroData(HeroRegistry.BOUNTY_HUNTER, HeroDataBountyHunter.class, h -> h.grapple).orElse(null);
            
            if (grapple == null || grapple.anchor == null) {
                return;
            }
            
            grapple.tryEscape(player);
        });
    }
    
    @Override
    public @NotNull TalentTarget target(@NotNull HariantPlayer player) {
        return TalentTarget.none();
    }
    
    @Override
    public @NotNull Response execute(@NotNull HariantPlayer player, @NotNull TalentContext talentContext, @NotNull RechargeableTalentData rechargeableTalentData) {
        final HeroDataBountyHunter heroData = player.getHeroData(HeroRegistry.BOUNTY_HUNTER, HeroDataBountyHunter::new);
        
        // Only one grapple is allowed at any given time
        if (heroData.grapple != null) {
            heroData.grapple.cancel();
        }
        
        heroData.grapple = new Grapple(player, heroData);
        
        // Fx
        player.playWorldSound(Sound.ENTITY_BAT_TAKEOFF, 1.0f);
        player.playWorldSound(Sound.ITEM_LEAD_TIED, 0.0f);
        
        return Response.ok();
    }
    
    public class Grapple extends HariantTickingTask implements AssistSource {
        
        private final HariantPlayer player;
        private final HeroDataBountyHunter heroData;
        private final Vector vector;
        private final Slime hook;
        
        private @Nullable Anchor anchor;
        private Vector previousVelocity;
        
        private int cuts;
        private int airTicks;
        
        private Grapple(@NotNull HariantPlayer player, @NotNull HeroDataBountyHunter heroData) {
            super(Scheduler.ofTimer());
            
            this.player = player;
            this.heroData = heroData;
            this.vector = player.getEyeLocation().getDirection().normalize().multiply(extendSpeed.doubleValue());
            this.hook = createHook(player.getMidpointLocation());
            this.hook.setLeashHolder(player.getHandle());
            this.previousVelocity = new Vector();
        }
        
        @Override
        public void run(int tick) {
            final Location playerLocation = player.getLocation();
            
            // If player is sneaking, remove the grapple
            if (player.isSneaking()) {
                this.cancelWithReason(Component.text("You cut the rope!"));
                return;
            }
            
            // If there is no anchor, extend the hook
            if (anchor == null) {
                final Location location = hook.getLocation();
                
                // If flown too far, break the hook
                if (LocationHelper.distanceSquared(location, playerLocation) >= maximumDistance.doubleValueSquared()) {
                    this.cancelWithReason(Component.text("You didn't hook anything!"));
                    return;
                }
                
                location.add(vector);
                
                // Check block for collision
                final Block block = location.getBlock();
                
                if (BlockHelper.isSolid(block)) {
                    // Anchor to the top of the block
                    this.anchor = () -> block.getLocation().set(block.getX() + 0.5, block.getBoundingBox().getMaxY(), block.getZ() + 0.5);
                    return;
                }
                
                // Check for entity collision
                final HariantEntity closestEntity = player.collectNearbyEntities(location, clingRadius)
                                                          .filter(player::canAffect)
                                                          .min(Comparator.comparingDouble(entity -> entity.distanceToSquared(location)))
                                                          .orElse(null);
                
                if (closestEntity != null) {
                    // Check for Effect RES separately
                    if (closestEntity.hasEffectResistance(this)) {
                        this.cancelWithReason(Component.text("The hooked entity has resisted the grapple!"));
                        return;
                    }
                    
                    this.anchor = new Anchor() {
                        @Override
                        public @NotNull HariantEntity getEntity() {
                            return closestEntity;
                        }
                        
                        @Override
                        public @NotNull Location getLocation() {
                            return closestEntity.getMidpointLocation();
                        }
                    };
                }
                
                // Sync the hook
                hook.teleport(location);
            }
            // If anchor exists, pull player towards the hook
            else {
                final HariantEntity entity = anchor.getEntity();
                final Location anchorLocation = anchor.getLocation();
                final Vector towardsAnchor = anchorLocation.toVector().subtract(playerLocation.toVector()).normalize();
                
                // If close enough to the anchor, dismount
                if (LocationHelper.distanceSquared(playerLocation, anchorLocation) < dismountRadius.doubleValueSquared()) {
                    this.cancel();
                    
                    // If entity is hooked, pull towards the back of the entity
                    if (entity != null) {
                        final Location entityLocation = entity.getLocation();
                        
                        final Vector behindEntity = entityLocation.toVector().subtract(entityLocation.getDirection().setY(0).normalize());
                        final Vector towardsBack = behindEntity.subtract(player.getLocation().toVector()).setY(0).normalize();
                        
                        player.setVelocity(towardsBack.multiply(finalPushEntityMagnitude.doubleValue()).setY(finalPushEntityY.doubleValue()));
                    }
                    // Otherwise mount towards the top of the block
                    else {
                        player.setVelocity(towardsAnchor.multiply(finalPushBlockMagnitude.doubleValue()).setY(finalPushBlockY.doubleValue()));
                    }
                    
                    // Play fx on anchor
                    player.spawnWorldParticle(anchor.getLocation(), Particle.ITEM, 5, 0.2, 0.2, 0.2, 0.05f, CACHED_ITEM_STACK);
                    
                    return;
                }
                
                // If the hooked entity has died, unhook
                if (entity != null && entity.isDead()) {
                    this.cancelWithReason(Component.text("The hooked entity has died."));
                    return;
                }
                
                // Otherwise, push towards the anchor
                final Vector forward = playerLocation.getDirection().setY(0).normalize();
                final Vector perpendicular = forward.getCrossProduct(UP).normalize();
                
                final Input currentInput = player.getCurrentInput();
                final Vector inputVector = new Vector(0, 0, 0);
                
                if (currentInput.isLeft()) {
                    inputVector.subtract(perpendicular);
                }
                else if (currentInput.isRight()) {
                    inputVector.add(perpendicular);
                }
                
                // Normalize input and multiply by strafing speed
                if (inputVector.lengthSquared() > 0) {
                    inputVector.normalize().multiply(strafingSpeed.doubleValue());
                }
                
                // Calculate velocity
                final Vector velocity = towardsAnchor.add(inputVector).normalize().multiply(pullSpeed.doubleValue());
                
                // Apply smoothing
                final Vector velocitySmooth = copyVector(velocity)
                        .multiply(1 - strafingSmoothingFactor.doubleValue())
                        .add(previousVelocity.multiply(strafingSmoothingFactor.doubleValue()));
                
                player.setVelocity(velocitySmooth);
                
                // Assign previous velocity for smoothing
                previousVelocity = copyVector(velocitySmooth);
                
                // Synchronize hook
                hook.teleport(anchorLocation);
                
                // If an entity was hooked, display escape progress to them
                if (entity != null) {
                    final TextColor color = Hariant.currentTickMod20() ? COLOR_ESCAPE_0 : COLOR_ESCAPE_1;
                    
                    final Component title = cuts == 0
                                            ? COMPONENT_HOOKED_NO_CUTS
                                            : Component.text(PREFIX.repeat(cuts), Colors.GRAPPLE).append(Component.text(PREFIX.repeat(cutsToEscape.intValue() - cuts), Colors.DARK_GRAY));
                    
                    final Component subtitle = Component.empty()
                                                        .append(Component.text("SNEAK", color, TextDecoration.BOLD))
                                                        .append(Component.text(" to escape!", color));
                    
                    entity.showTitle(Title.title(title, subtitle, 0, 10, 5));
                }
            }
            
            // Check for air ticks
            if (airTicks++ > getDuration()) {
                this.cancelWithReason(Component.text("Your hook broke because it got too tired!", Colors.RED));
                return;
            }
        }
        
        @Override
        public void onCancel() {
            super.onCancel();
            
            heroData.grapple = null;
            hook.remove();
            
            // Always give fall damage resistance
            player.addEffect(StatusEffectType.FALL_DAMAGE_RESISTANCE, 100, player);
        }
        
        @Override
        public @NotNull HariantEntity source() {
            return player;
        }
        
        @Override
        public @NotNull Component getName() {
            return TalentGrapple.this.getName();
        }
        
        public void tryEscape(@NotNull HariantPlayer escapee) {
            if (anchor == null || !Objects.equals(anchor.getEntity(), escapee)) {
                return;
            }
            
            final int cutsToEscape = TalentGrapple.this.cutsToEscape.intValue();
            
            if (++cuts >= cutsToEscape) {
                this.cancel();
                this.cancelWithReason(escapee.getName().append(Component.text(" has escaped!", Colors.GREEN)));
                
                escapee.showTitle(Title.title(Component.text(PREFIX.repeat(cutsToEscape), Colors.GRAPPLE), COMPONENT_ESCAPED, 0, 20, 5));
                
                player.playSound(Sound.ENTITY_SHEEP_SHEAR, 0.75f);
            }
            
            // Escapee fx
            escapee.playSound(Sound.ENTITY_SHEEP_SHEAR, 0.5f + 0.25f * cuts);
        }
        
        private void cancelWithReason(@NotNull Component component) {
            this.cancel();
            
            player.sendMessage(COMPONENT_PREFIX.appendSpace().append(component));
            player.playSound(Sound.ITEM_LEAD_BREAK, 0.75f);
        }
        
        private static @NotNull Slime createHook(@NotNull Location location) {
            return location.getWorld().spawn(location, Slime.class, self -> {
                self.setInvulnerable(true);
                self.setInvisible(true);
                self.setSilent(true);
                self.setAI(false);
                self.setGravity(false);
                self.setSize(1);
                
                EntityGarbageCollector.add(self);
            });
        }
        
    }
    
    public interface Anchor extends Located {
        
        default @Nullable HariantEntity getEntity() {
            return null;
        }
        
        @Override
        @NotNull Location getLocation();
    }
    
    @ThisClassShouldNeMovedToEternaAPI
    private static @NotNull Vector copyVector(@NotNull Vector vector) {
        return new Vector(vector.getX(), vector.getY(), vector.getZ());
    }
    
}