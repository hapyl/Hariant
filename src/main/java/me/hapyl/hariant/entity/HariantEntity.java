package me.hapyl.hariant.entity;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import me.hapyl.eterna.module.annotate.EventLike;
import me.hapyl.eterna.module.location.Coordinates;
import me.hapyl.eterna.module.location.Distanced;
import me.hapyl.eterna.module.location.Located;
import me.hapyl.eterna.module.location.LocationHelper;
import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.math.geometry.Drawable;
import me.hapyl.eterna.module.math.geometry.Geometry;
import me.hapyl.eterna.module.reflect.glowing.Glowing;
import me.hapyl.eterna.module.reflect.team.PacketTeamColor;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.Handle;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.HariantLogger;
import me.hapyl.hariant.attribute.Attributable;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.instance.Attributes;
import me.hapyl.hariant.attribute.instance.AttributesInstance;
import me.hapyl.hariant.element.*;
import me.hapyl.hariant.element.anomaly.ElementalAnomalyType;
import me.hapyl.hariant.entity.cooldown.CooldownHandler;
import me.hapyl.hariant.entity.cooldown.CooldownHandlerImpl;
import me.hapyl.hariant.entity.cooldown.HariantCooldown;
import me.hapyl.hariant.entity.damage.*;
import me.hapyl.hariant.entity.damage.mutator.DamageMutator;
import me.hapyl.hariant.entity.damage.tracker.CombatData;
import me.hapyl.hariant.entity.damage.tracker.CombatTracker;
import me.hapyl.hariant.entity.effect.Effect;
import me.hapyl.hariant.entity.effect.EffectHandler;
import me.hapyl.hariant.entity.effect.EffectType;
import me.hapyl.hariant.entity.effect.status.StatusEffectHandler;
import me.hapyl.hariant.entity.effect.status.StatusEffectInstance;
import me.hapyl.hariant.entity.effect.status.StatusEffectMap;
import me.hapyl.hariant.entity.effect.status.StatusEffectType;
import me.hapyl.hariant.entity.heal.HealingSource;
import me.hapyl.hariant.entity.mutator.HealthMutator;
import me.hapyl.hariant.entity.player.Delegatable;
import me.hapyl.hariant.entity.player.DelegateType;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.entity.shield.Shield;
import me.hapyl.hariant.entity.shield.ShieldResult;
import me.hapyl.hariant.entity.ticker.EntityTicker;
import me.hapyl.hariant.entity.trap.Trap;
import me.hapyl.hariant.entity.trap.TrapEscape;
import me.hapyl.hariant.entity.trap.Trappable;
import me.hapyl.hariant.event.*;
import me.hapyl.hariant.event.effect.HariantEffectEvent;
import me.hapyl.hariant.handler.ProjectileHandler;
import me.hapyl.hariant.task.HariantTickingTask;
import me.hapyl.hariant.task.Scheduler;
import me.hapyl.hariant.team.EnumTeam;
import me.hapyl.hariant.team.TeamEntry;
import me.hapyl.hariant.team.TeamEntryProvider;
import me.hapyl.hariant.ui.ComponentDisplay;
import me.hapyl.hariant.ui.ComponentDisplayAnimation;
import me.hapyl.hariant.util.Cancellable;
import me.hapyl.hariant.util.MathFont;
import me.hapyl.hariant.util.SoundFx;
import me.hapyl.hariant.util.UniquelyIdentified;
import me.hapyl.hariant.weapon.NormalAttackRanged;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.object.ObjectContents;
import net.kyori.adventure.text.object.PlayerHeadObjectContents;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.TitlePart;
import net.kyori.adventure.util.TriState;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.time.Duration;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class HariantEntity
        implements
        Handle<LivingEntity>, Attributable, Located, ForwardingAudience.Single,
        UniquelyIdentified, Lifecycle, Coordinates, SoundPlayer,
        ParticleSpawner, EntityCollector, Distanced, Attacker,
        TeamEntryProvider, StatusEffectHandler, HeadComponent, DeathComponent,
        CooldownHandler, Elemental, ElementHandler, HariantLogger.Sender,
        EffectHandler, TickSupplier, Trappable, Delegatable {
    
    private static final ComponentDisplay EFFECT_RESISTANCE_DISPLAY = new ComponentDisplay(
            Component.text("ᴇꜰꜰᴇᴄᴛ ʀᴇꜱ", AttributeType.EFFECT_RESISTANCE.getStyle()),
            ComponentDisplayAnimation.ofSineAscend(),
            20,
            1.75f
    );
    
    private static final String HEAD_TEXTURE_URL = "{\"textures\":{\"SKIN\":{\"url\":\"https://textures.minecraft.net/texture/%s\"}}}";
    private static final Component DEFAULT_HEAD_COMPONENT = createHeadComponent("da99b05b9a1db4d29b5e673d77ae54a77eab66818586035c8a2005aeb810602a");
    
    private static final ComponentDisplay COMPONENT_DISPLAY_IMMUNE = new ComponentDisplay(
            Component.text("ɪᴍᴍᴜɴᴇ", Colors.DARK_GRAY),
            ComponentDisplayAnimation.ofFalloff(),
            20, 1.75f
    );
    
    private static final HariantCooldown HEALTH_MUTATOR_APPLICATION_COOLDOWN = HariantCooldown.ofSeconds(Key.ofString("health_mutator_cooldown"), 1.2f);
    private static final HariantCooldown FEROCITY_COOLDOWN = HariantCooldown.ofSeconds(Key.ofString("ferocity"), 0.2f);
    
    private static final Style DEFAULT_HEALTH_STYLE = Style.style(Colors.ATTRIBUTE_MAX_HEALTH);
    
    public final HariantRandom random;
    
    protected final LivingEntity entity;
    protected final AttributesInstance attributes;
    protected final CombatTracker combatTracker;
    protected final ElementData elementData;
    
    private final StatusEffectMap effectMap;
    private final CooldownHandlerImpl cooldownHandler;
    private final EntityTicker ticker;
    private final LinkedHashMap<Class<? extends HealthMutator>, HealthMutator> healthMutators;
    private final Set<DelegateCancellable> delegatedCancellable;
    
    protected @Nullable HariantEntity lastAttacker;
    
    protected double health;
    
    private @Nullable Trap trap;
    private @Nullable Shield shield;
    private @Nullable SitHandler sitHandler;
    private @Nullable RemovalReason removalReason;
    private @Nullable SoundFx soundHurt;
    private @Nullable SoundFx soundDeath;
    private @Nullable EffectResistance effectResistance;
    
    public HariantEntity(@NotNull LivingEntity entity, @NotNull Attributes attributes) {
        this.entity = entity;
        this.attributes = new AttributesInstance(this, attributes);
        this.ticker = new EntityTicker(this);
        this.random = new HariantRandom();
        this.health = attributes.get(AttributeType.MAX_HEALTH);
        this.effectMap = new StatusEffectMap(this);
        this.combatTracker = new CombatTracker(this);
        this.cooldownHandler = new CooldownHandlerImpl(this);
        this.elementData = new ElementData(this);
        this.healthMutators = Maps.newLinkedHashMap();
        this.delegatedCancellable = Sets.newHashSet();
        
        this.updateAttributes();
        
        this.soundHurt = SoundFx.createNullable(entity.getHurtSound());
        this.soundDeath = SoundFx.createNullable(entity.getDeathSound());
    }
    
    public @Nullable SitHandler getSitHandler() {
        return sitHandler;
    }
    
    public @NotNull SitHandler setSitting(@NotNull SitHandler sitHandler) {
        this.unsetSitting();
        
        this.sitHandler = sitHandler;
        this.sitHandler.onMount();
        
        return sitHandler;
    }
    
    public @NotNull SitHandler setSitting(@NotNull Location location, boolean allowDismount) {
        return this.setSitting(new SitHandlerImpl(this, location, allowDismount));
    }
    
    public void unsetSitting() {
        if (this.sitHandler != null) {
            this.sitHandler.onDismount();
            this.sitHandler = null;
        }
    }
    
    public @Nullable Shield getShield() {
        return shield;
    }
    
    public void setShield(@Nullable Shield shield) {
        if (shield != null) {
            // If current shield has higher priority, cancel
            if (this.shield != null && this.shield.hasHigherPriority(shield)) {
                return;
            }
            
            // Call event
            if (new HariantShieldCreateEvent(shield).callEvent()) {
                return;
            }
            
            this.shield = shield;
            this.shield.onCreate0();
            return;
        }
        
        // If entity already has a shield, call removal on it
        if (this.shield != null) {
            this.shield.onRemove0(Shield.Cause.REPLACED);
            this.shield = null;
        }
    }
    
    public boolean hasHealthMutator(@NotNull Class<? extends HealthMutator> mutatorClass) {
        return healthMutators.containsKey(mutatorClass);
    }
    
    public void addHealthMutator(@NotNull HealthMutator mutator) {
        if (this.hasCooldown(HEALTH_MUTATOR_APPLICATION_COOLDOWN)) {
            return;
        }
        
        this.healthMutators.put(mutator.getClass(), mutator);
        this.setCooldown(HEALTH_MUTATOR_APPLICATION_COOLDOWN);
        
        // Play mutator fx
        mutator.onApply(this);
    }
    
    @NotNull
    public CombatTracker getCombatTracker() {
        return combatTracker;
    }
    
    @Override
    public void setCooldown(@NotNull HariantCooldown cooldown, @Range(from = 0, to = Integer.MAX_VALUE) int duration, @Nullable AttributeType cooldownReducingAttribute) {
        cooldownHandler.setCooldown(cooldown, duration, cooldownReducingAttribute);
    }
    
    @Override
    public int getCooldownTimeLeft(@NotNull HariantCooldown cooldown) {
        return cooldownHandler.getCooldownTimeLeft(cooldown);
    }
    
    @Override
    public boolean hasCooldown(@NotNull HariantCooldown cooldown) {
        return cooldownHandler.hasCooldown(cooldown);
    }
    
    @Override
    public void resetCooldowns() {
        cooldownHandler.resetCooldowns();
    }
    
    public void updateAttributes() {
        for (AttributeType attributeType : AttributeType.values()) {
            this.attributes.updateAttribute(attributeType);
        }
    }
    
    @NotNull
    @Override
    public NormalAttack getMeleeAttack() {
        return NormalAttack.common();
    }
    
    @Override
    public NormalAttackRanged getRangedAttack() {
        // Explicit super call
        return Attacker.super.getRangedAttack();
    }
    
    @NotNull
    public EntityTicker getTicker() {
        return ticker;
    }
    
    public boolean isPersistent() {
        return false;
    }
    
    public void decrementHealth(double amount) {
        // Silently fail for <= 0 decrements
        if (amount <= 0) {
            return;
        }
        
        final double previousHealth = health;
        final double newHealth = Math.clamp(health - amount, 0.0, this.getMaxHealth());
        
        // Call event
        final HariantHealthChangeEvent event = new HariantHealthChangeEvent(this, previousHealth, newHealth);
        event.callEvent();
        
        this.setHealth(event.getNewHealth());
    }
    
    public @NotNull ImmunityResult isImmuneTo(@NotNull DamageSource source) {
        return ImmunityResult.NOT_IMMUNE;
    }
    
    public @NotNull DamageResult damage(@NotNull DamageInstance damageInstance) {
        final HariantDamageEvent damageEvent = new HariantDamageEvent(damageInstance);
        final DamageSource damageSource = damageInstance.getDamageSource();
        
        if (damageEvent.callEvent()) {
            if (damageEvent.isStartCooldownIfCancelled()) {
                damageSource.startCooldownIfExists(this);
            }
            
            return broadcastImmune();
        }
        
        final HariantEntity attacker = damageInstance.getAttacker();
        
        // Handle shields
        if (shield != null && shield.canShield(damageSource)) {
            final double damage = damageInstance.getDamage();
            final ShieldResult shieldResult = shield.shield0(damage, damageSource);
            
            // Always mark shielded, regardless if the shield broke or not
            damageInstance.markShielded();
            
            // Decrement the damage
            damageInstance.mutateDamage(shield, DamageMutator.subtract(), shieldResult.decrement());
            
            // Display the damage shielded
            if (shieldResult.shielded() > 0) {
                shield.display(shieldResult.shielded(), this.getMidpointLocation());
            }
            
            // If capacity is lower or equals to 0, the shield broke
            if (shieldResult.capacityAfterHit() <= 0) {
                shield.onRemove0(Shield.Cause.BROKE);
                shield = null;
            }
        }
        
        final double damage = damageInstance.getDamage();
        final double health = getFinalHealth();
        
        final boolean isLethal = health - damage <= 0.0 && !damageSource.isFlagged(DamageFlag.CANNOT_KILL);
        
        if (isLethal) {
            damageInstance.markLethal();
        }
        
        // Set last attacker so we know who to credit for the kill
        if (attacker != null) {
            this.lastAttacker = attacker;
            this.lastAttacker.onDamageDealt0(damageInstance, this);
        }
        
        // Pass the damage to trackers
        this.incrementTrackerDamage(attacker, damageSource, damage, isLethal);
        
        // Call monitor event
        new HariantMonitorDamageEvent(this, damageInstance).callEvent();
        
        // Broadcast hurt
        this.broadcastHurt(damageInstance, !isLethal);
        
        // Check whether damage can kill and call death event
        if (isLethal) {
            if (new HariantDeathEvent(this, damageInstance).callEvent()) {
                return DamageResult.IMMUNE;
            }
            
            this.die(damageSource);
            return DamageResult.DEAD;
        }
        
        // Decrement health
        this.decrementHealth(damage);
        
        // Call EventLike method
        this.onDamageTaken(damageInstance, attacker);
        
        // Apply element
        this.applyElement(damageSource);
        
        // Start cooldown if the damage was actually dealt
        damageSource.startCooldownIfExists(this);
        
        return DamageResult.OK;
    }
    
    public final @NotNull DamageResult damage(@NotNull DamageSource source) {
        final ImmunityResult immunityResult = this.isImmuneTo(source);
        
        // Don't damage already dead entities
        if (source.getDamage() <= 0 || this.isDead()) {
            return DamageResult.IMMUNE;
        }
        
        if (immunityResult.isImmune()) {
            return immunityResult.isSilent() ? DamageResult.IMMUNE : broadcastImmune();
        }
        
        // Check for cooldown
        if (source.hasCooldown() && this.hasCooldown(source)) {
            // Don't show the IMMUNE component display for cooldowns
            return DamageResult.IMMUNE;
        }
        
        // Check for invulnerability ticks
        if (this.ticker.invulnerability.value() > 0 && !source.isFlagged(DamageFlag.IGNORES_INVULNERABILITY)) {
            return broadcastImmune();
        }
        
        return this.damage(createDamageInstance(source));
    }
    
    @NotNull
    public Set<? extends Entity> listGarbage() {
        return Set.of(entity);
    }
    
    public boolean die(@NotNull DamageSource damageSource) {
        // If already scheduled for removal, return
        if (this.removalReason != null) {
            return false;
        }
        
        this.removalReason = RemovalReason.DIED;
        this.health = 0.0;
        
        // Reassign damager if exists
        final HariantEntity source = damageSource.getSource();
        
        if (source != null) {
            this.lastAttacker = source;
        }
        
        // Call `onKill` on damager
        if (this.lastAttacker != null) {
            this.lastAttacker.onKill(this, damageSource);
        }
        
        // Call `onDeath` on this
        this.onDeath(damageSource);
        return true;
    }
    
    public void attack(@NotNull HariantEntity entity, @NotNull DamageSource damageSource, @NotNull KnockbackSource knockbackSource) {
        // Check whether we can actually attack the entity
        if (!this.canAttack(entity, damageSource.getDamageType())) {
            return;
        }
        
        AffectResult affection = this.getAffection(entity);
        
        final HariantAttackEvent event = new HariantAttackEvent(this, entity, damageSource, affection);
        final boolean eventCancelled = event.callEvent();
        
        // If the event was cancelled, return here, otherwise update the affection from the event
        if (eventCancelled) {
            return;
        }
        else {
            affection = event.getAffectResult();
        }
        
        if (affection != AffectResult.CAN_AFFECT) {
            // If teammates, show the message
            if (affection == AffectResult.CANNOT_AFFECT_TEAMMATE) {
                this.sendMessage(Component.text("Cannot damage teammates!", Colors.ERROR));
            }
            
            return;
        }
        
        // Deal damage to the entity
        final DamageResult damageResult = entity.damage(damageSource);
        
        // If the damage was successful, apply knockback
        if (damageResult == DamageResult.OK) {
            entity.knockback(knockbackSource);
        }
    }
    
    public void attack(@NotNull HariantEntity entity) {
        final NormalAttack meleeAttack = this.getMeleeAttack();
        
        this.attack(entity, meleeAttack.createDamageSource(this).build(), meleeAttack.createKnockbackCause(this));
    }
    
    public void damageFerocity(@NotNull DamageInstance damageInstance, @Range(from = 1, to = Integer.MAX_VALUE) int ferocityStrikes, boolean force) {
        // Check for cooldown
        if (this.hasCooldown(FEROCITY_COOLDOWN) && !force) {
            return;
        }
        
        // Always delegate ferocity task to the entity
        this.delegate(new FerocityTask(this, damageInstance, ferocityStrikes), DelegateType.PERSISTENT);
        
        // Start ferocity cooldown
        if (!force) {
            this.setCooldown(FEROCITY_COOLDOWN);
        }
        
        // Fx
        this.playWorldSound(entity.getLocation(), Sound.ITEM_FLINTANDSTEEL_USE, 6, 0.0f);
    }
    
    public void delegate(@NotNull Cancellable cancellable, @NotNull DelegateType delegateType) {
        this.delegatedCancellable.add(new DelegateCancellable(cancellable, delegateType));
    }
    
    @Override
    public void cancelDelegates(@NotNull Predicate<DelegateCancellable> filter) {
        this.delegatedCancellable.removeIf(delegate -> {
            if (filter.test(delegate)) {
                delegate.cancel();
                return true;
            }
            
            return false;
        });
    }
    
    public boolean heal(@NotNull HealingSource healingSource) {
        if (this.isDead()) {
            return false;
        }
        
        @Nullable final HariantEntity healer = healingSource.getHealer();
        double healingAmount = healingSource.getAmount();
        
        // If healer exists, and it's not self, increment the healing by MENDING
        if (healer != null && !this.isSelf(healer)) {
            healingAmount *= healer.getAttributes().normalized(AttributeType.MENDING);
        }
        
        // Increment healing by VITALITY
        healingAmount *= this.getAttributes().normalized(AttributeType.VITALITY);
        
        final double maxHealth = this.getMaxHealth();
        
        final double healthBeforeHealing = health;
        final double healthAfterHealing = Math.min(maxHealth, health + healingAmount);
        
        final double actualHealing = healthAfterHealing - healthBeforeHealing;
        
        // Call event
        final HariantHealEvent event = new HariantHealEvent(this, healingSource, healthBeforeHealing, healthAfterHealing, actualHealing);
        
        if (event.callEvent()) {
            return false;
        }
        
        this.setHealth(event.getNewHealth());
        this.onHeal(healthBeforeHealing, healthAfterHealing, actualHealing);
        
        return true;
    }
    
    @EventLike
    public void onHeal(double healthBeforeHealing, double healthAfterHealing, double actualHealing) {
        // Show healing display
        if (actualHealing > 1) {
            ComponentDisplay.ofAscend(Component.text("+" + MathFont.format((int) actualHealing), Colors.GREEN), this.getMidpointLocation(), 20, 1.75f);
            
            this.spawnWorldParticle(this.getEyeLocation().add(0, 0.5, 0), Particle.HEART, (int) Math.clamp(actualHealing / 100, 1, 10), 0.45, 0.2, 0.45, 0.015f);
            this.playSound(Sound.ENTITY_ZOMBIE_INFECT, 2.0f);
        }
    }
    
    @EventLike
    public void onKill(@NotNull HariantEntity entity, @NotNull DamageSource damageSource) {
    }
    
    @EventLike
    public void onAssist(@NotNull HariantPlayer player) {
    }
    
    @EventLike
    public void onDamageDealt(@NotNull DamageSource damageSource, @NotNull HariantEntity entity) {
    }
    
    @EventLike
    public void onDamageTaken(@NotNull DamageInstance damageInstance, @Nullable HariantEntity attacker) {
    }
    
    @EventLike
    public void onHealthChange(double previousHealth, double newHealth) {
    }
    
    @EventLike
    public void onDeath(@NotNull DamageSource damageSource) {
    }
    
    @EventLike
    public void onShoot(@NotNull DamageSource damageSource) {
    }
    
    @EventLike
    public void onInteract(@NotNull HariantPlayer player) {
    }
    
    @EventLike
    public void onRemove(@NotNull RemovalReason removalReason) {
        entity.remove();
    }
    
    /**
     * Gets whether this {@link HariantEntity} can affect the given {@link HariantEntity} in any context.
     *
     * @param entity - The entity to check.
     * @return {@code true} if this entity can affect the other one; {@code false} otherwise.
     */
    public final boolean canAffect(@NotNull HariantEntity entity) {
        return this.getAffection(entity) == AffectResult.CAN_AFFECT;
    }
    
    /**
     * Gets the {@link AffectResult} for the given {@link HariantEntity}.
     *
     * @param entity - The entity to check.
     * @return the affect result.
     */
    @NotNull
    public AffectResult getAffection(@NotNull HariantEntity entity) {
        // If the entity is itself, cannot affect
        if (entity.isSelf(this)) {
            return AffectResult.CANNOT_AFFECT_SELF;
        }
        // If the entity is dead, cannot affect
        else if (entity.isDead()) {
            return AffectResult.CANNOT_AFFECT_DEAD;
        }
        // If the entity is a teammate, cannot affect
        else if (entity.isTeammate(this)) {
            return AffectResult.CANNOT_AFFECT_TEAMMATE;
        }
        // If the entity is invisible, and we cannot see it's invisibility, cannot affect
        else if (entity.isInvisible() && !this.canSeeInvisible(entity)) {
            return AffectResult.CANNOT_AFFECT_INVISIBLE;
        }
        // If the entity is invulnerable, cannot affect
        else if (entity.isInvulnerable()) {
            return AffectResult.CANNOT_AFFECT_INVULNERABLE;
        }
        
        // Otherwise, can affect
        return AffectResult.CAN_AFFECT;
    }
    
    public boolean isDead() {
        return entity.isDead();
    }
    
    /**
     * Gets whether this entity can see the other while it's invisible.
     *
     * @param entity - The entity to check.
     * @return {@code true} if this entity can see the other while it's invisible.
     */
    public boolean canSeeInvisible(@NotNull HariantEntity entity) {
        return isTeammate(entity);
    }
    
    /**
     * Gets whether this entity is invisible
     *
     * @return {@code true} if this entity is invisible; {@code false} otherwise.
     */
    public boolean isInvisible() {
        return hasEffect(StatusEffectType.INVISIBILITY);
    }
    
    public boolean canAttack(@NotNull HariantEntity entity, @NotNull DamageType damageType) {
        return true;
    }
    
    @Nullable
    public SoundFx getHurtSound() {
        return soundHurt;
    }
    
    public void setHurtSound(@Nullable SoundFx soundHurt) {
        this.soundHurt = soundHurt;
    }
    
    @Nullable
    public SoundFx getDeathSound() {
        return soundDeath;
    }
    
    public void setDeathSound(@Nullable SoundFx soundDeath) {
        this.soundDeath = soundDeath;
    }
    
    public void broadcastHurt(@NotNull DamageInstance damageInstance, boolean hurt) {
        this.playDamageFx(hurt ? this::getHurtSound : this::getDeathSound);
        this.spawnDamageDisplay(damageInstance);
    }
    
    public void broadcastDeath(@NotNull DamageInstance damageInstance) {
        this.playDamageFx(this::getDeathSound);
        this.spawnDamageDisplay(damageInstance);
    }
    
    public void spawnDamageDisplay(@NotNull DamageInstance damageInstance) {
        ComponentDisplay.ofDamage(damageInstance, this.getMidpointLocation());
    }
    
    public void knockback(@NotNull KnockbackSource cause) {
        // If the entity is trapped, skip the calculations
        if (this.isTrapped()) {
            return;
        }
        
        final Location location = getLocation();
        
        double dx = location.getX() - cause.x();
        double dz = location.getZ() - cause.z();
        
        while (dx * dx + dz * dz < 1.0E-5) {
            dx = (random.nextDouble() - random.nextDouble()) * 0.01;
            dz = (random.nextDouble() - random.nextDouble()) * 0.01;
        }
        
        final double strength = cause.strength() * (1 - attributes.normalized(AttributeType.KNOCKBACK_RESISTANCE));
        final double length = Math.sqrt(dx * dx + dz * dz);
        
        dx /= length;
        dz /= length;
        
        final Vector velocity = entity.getVelocity();
        
        if (new HariantKnockbackEvent(this, cause, velocity).callEvent()) {
            return;
        }
        
        entity.setVelocity(
                new Vector(
                        velocity.getX() * 0.5 + dx * strength,
                        entity.isOnGround() ? Math.min(0.4, velocity.getY() * 0.5 + strength) : velocity.getX(),
                        velocity.getZ() * 0.5 + dz * strength
                )
        );
    }
    
    @NotNull
    public HariantRandom getRandom() {
        return random;
    }
    
    public double getHealth() {
        return health;
    }
    
    public void setHealth(double health) {
        final double previousHealth = this.health;
        final double newHealth = Math.clamp(health, this.getMinHealth(), this.getMaxHealth());
        
        this.health = newHealth;
        this.onHealthChange(previousHealth, newHealth);
    }
    
    public double getFinalHealth() {
        double health = this.health;
        
        // Apply mutators
        for (HealthMutator mutator : healthMutators.values()) {
            health = mutator.mutate(health);
        }
        
        return health;
    }
    
    public double getMinHealth() {
        return AttributeType.MAX_HEALTH.minValue();
    }
    
    public double getMaxHealth() {
        return attributes.get(AttributeType.MAX_HEALTH);
    }
    
    @Override
    @NotNull
    public LivingEntity getHandle() {
        return entity;
    }
    
    public boolean tick() {
        if (!shouldTick()) {
            return false;
        }
        
        this.ticker.tick();
        this.attributes.tick();
        this.effectMap.tick();
        this.elementData.tick();
        this.cooldownHandler.tick();
        
        this.tickHealthMutators();
        this.tickShield();
        this.tickTrap();
        
        return true;
    }
    
    public final boolean compareEntity(@NotNull Entity entity) {
        return this.entity.equals(entity);
    }
    
    public @NotNull Location getCenterLocation() {
        return LocationHelper.center(entity.getLocation());
    }
    
    public void assist(@NotNull AssistSource assistSource) {
        combatTracker.assist(assistSource);
    }
    
    public boolean isFullHealth() {
        return health >= getMaxHealth();
    }
    
    @ApiStatus.Internal
    public final void tick0() {
        // Handle removal
        if (this.removalReason != null) {
            this.onRemove(this.removalReason);
            
            // We're fine to nullate the removal reason, because `onRemove()` promises to remove the bukkit entity, and if
            // it doesn't it means that we don't care to remove it (eg: player)
            this.removalReason = null;
            return;
        }
        
        this.tick();
    }
    
    public boolean shouldTick() {
        return !entity.isDead();
    }
    
    @Override
    public boolean trap(@NotNull Trap trap) {
        // If already trapped, check for priority
        if (this.trap != null && this.trap.hasHigherPriority(trap)) {
            return false;
        }
        
        // Traps are affected by EFFECT RES, so check for it
        if (this.hasEffectResistance(trap)) {
            return false;
        }
        
        // Always remove previous trap
        this.untrap(TrapEscape.REPLACED);
        
        this.trap = trap;
        this.trap.onTrap0();
        
        return true;
    }
    
    @Override
    public boolean untrap(@NotNull TrapEscape trapEscape) {
        if (this.trap == null) {
            return false;
        }
        
        this.trap.onEscape0(trapEscape);
        this.trap = null;
        return true;
    }
    
    @Override
    public @Nullable Trap getTrap() {
        return trap;
    }
    
    @Override
    public boolean isTrapped() {
        return trap != null;
    }
    
    @Override
    @NotNull
    public AttributesInstance getAttributes() {
        return attributes;
    }
    
    @NotNull
    public Location getEyeLocation() {
        return entity.getEyeLocation();
    }
    
    @NotNull
    public Location getMidpointLocation() {
        return entity.getLocation().add(0.0, entity.getHeight() * 0.5, 0.0);
    }
    
    @Override
    @NotNull
    public Location getLocation() {
        return entity.getLocation();
    }
    
    @Override
    public void setLocation(@NotNull Location location) {
        entity.teleport(location);
    }
    
    @NotNull
    public Location getLocationOffsetRandomly(final double maxOffset) {
        return this.getLocation().add(random.nextSignedDouble(maxOffset), random.nextSignedDouble(maxOffset), random.nextSignedDouble(maxOffset));
    }
    
    @Override
    public final int hashCode() {
        return Objects.hashCode(this.entity.getUniqueId());
    }
    
    @Override
    public final boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        
        final HariantEntity that = (HariantEntity) object;
        return Objects.equals(this.entity.getUniqueId(), that.entity.getUniqueId());
    }
    
    @Override
    public String toString() {
        final String uuidString = getUuid().toString();
        
        return "%s(%s)".formatted(entity.getType().getKey().getKey(), uuidString.substring(0, uuidString.indexOf("-")));
    }
    
    @NotNull
    public Component getName() {
        return Component.text(this.toString());
    }
    
    @NotNull
    @Override
    public Audience audience() {
        return entity;
    }
    
    @Override
    @NotNull
    public UUID getUuid() {
        return entity.getUniqueId();
    }
    
    /**
     * Marks this entity for removal.
     */
    public final void remove() {
        this.removalReason = RemovalReason.REMOVAL;
    }
    
    /**
     * Removes this entity forcefully without scheduling the removal.
     */
    public final void removeForcefully() {
        this.onRemove(RemovalReason.REMOVAL);
    }
    
    /**
     * Gets whether this entity should be destroyed.
     */
    public boolean shouldRemove() {
        return entity.isDead();
    }
    
    @Override
    public void onCreate() {
        // Mark as garbage entity
        EntityGarbageCollector.add(this);
    }
    
    @OverridingMethodsMustInvokeSuper
    @Override
    public void onDestroy() {
        lastAttacker = null;
        health = 0;
        
        ticker.reset();
        attributes.reset();
        combatTracker.reset();
        effectMap.resetEffects();
        cooldownHandler.resetCooldowns();
        elementData.reset();
        healthMutators.clear();
        
        effectResistance = null;
        
        // Cancel all delegated tasks
        delegatedCancellable.forEach(Cancellable::cancel);
        delegatedCancellable.clear();
        
        if (trap != null) {
            trap.onEscape0(TrapEscape.DIED);
            trap = null;
        }
        
        if (shield != null) {
            shield.onRemove0(Shield.Cause.ENTITY_DIED);
            shield = null;
        }
        
        if (sitHandler != null) {
            sitHandler.onDismount();
            sitHandler = null;
        }
    }
    
    @NotNull
    public <P extends Projectile> P launchProjectile(@NotNull Class<P> projectileClass, @NotNull DamageSource damageSource, @Nullable Consumer<P> consumer) {
        return entity.launchProjectile(projectileClass, null, self -> {
            if (consumer != null) {
                consumer.accept(self);
            }
            
            // We must manually create the projectile so it's not created via the event
            ProjectileHandler.createProjectile(self, damageSource);
        });
    }
    
    @NotNull
    public <P extends Projectile> P launchProjectile(@NotNull Class<P> projectileClass, @NotNull DamageSource damageSource) {
        return this.launchProjectile(projectileClass, damageSource, null);
    }
    
    @Override
    public double x() {
        return entity.getX();
    }
    
    @Override
    public double y() {
        return entity.getY();
    }
    
    @Override
    public double z() {
        return entity.getZ();
    }
    
    @Override
    public void playSound(@NotNull Sound sound, float volume, @Range(from = 0, to = 2) float pitch) {
    }
    
    @Override
    public void playSound(@NotNull Location location, @NotNull Sound sound, float volume, @Range(from = 0, to = 2) float pitch) {
    }
    
    @Override
    public void playWorldSound(@NotNull Sound sound, float volume, @Range(from = 0, to = 2) float pitch) {
        this.playWorldSound0(this.getLocation(), sound, volume, pitch);
    }
    
    @Override
    public void playWorldSound(@NotNull Location location, @NotNull Sound sound, float volume, @Range(from = 0, to = 2) float pitch) {
        this.playWorldSound0(location, sound, volume, pitch);
    }
    
    @NotNull
    @Override
    public SoundCategory soundCategory() {
        return SoundCategory.UI;
    }
    
    @Override
    public <T> void spawnParticle(@NotNull Location location, @NotNull Particle particle, int amount, double x, double y, double z, float speed, @Nullable T data) {
    }
    
    @Override
    public <T> void spawnWorldParticle(@NotNull Location location, @NotNull Particle particle, int amount, double x, double y, double z, float speed, @Nullable T data) {
        getWorld().spawnParticle(particle, location, amount, x, y, z, speed, data);
    }
    
    @NotNull
    public Vector getVelocity() {
        return entity.getVelocity();
    }
    
    public void setVelocity(@NotNull Vector vector) {
        entity.setVelocity(vector);
    }
    
    @NotNull
    public Vector getAbsoluteVelocity() {
        final Vector velocity = getVelocity();
        
        return new Vector(Math.abs(velocity.getX()), Math.abs(velocity.getY()), Math.abs(velocity.getZ()));
    }
    
    @NotNull
    public Vector getDirection() {
        return entity.getLocation().getDirection();
    }
    
    @NotNull
    public Vector getVectorLeft(final double offset) {
        final Vector vector = entity.getLocation().getDirection();
        
        return new Vector(vector.getZ(), 0.0, -vector.getX()).normalize().multiply(offset);
    }
    
    @NotNull
    public Vector getVectorRight(final double offset) {
        final Vector vector = entity.getLocation().getDirection();
        
        return new Vector(-vector.getZ(), 0.0, vector.getX()).normalize().multiply(offset);
    }
    
    @Override
    public int localTicks() {
        return ticker.life.value();
    }
    
    public boolean hasLineOfSight(@Nullable HariantEntity entity) {
        return entity != null && this.entity.hasLineOfSight(entity.entity);
    }
    
    public boolean isSelf(@Nullable HariantEntity other) {
        return this.equals(other);
    }
    
    public boolean isSelfOrTeammate(@Nullable HariantEntity other) {
        return this.equals(other) || this.isTeammate(other);
    }
    
    public boolean isTeammate(@Nullable HariantEntity other) {
        // Explicit self check, we're NOT out own teammate
        if (other == null || this.equals(other)) {
            return false;
        }
        
        final EnumTeam thisTeam = EnumTeam.getEntryTeam(TeamEntry.create(this));
        final EnumTeam thatTeam = EnumTeam.getEntryTeam(TeamEntry.create(other));
        
        return thisTeam != null && thisTeam == thatTeam;
    }
    
    /**
     * Checks whether the entity has effect resistance.
     *
     * <p>
     * Note that this method should be treated as internal and not be called manually, prefer using {@link EffectHandler#triggerEffect(HariantEntity, Effect)},
     * which has proper handling of effects, including effect resistance.
     * </p>
     *
     * @param assistSource - The assist source.
     * @return {@code true} if the entity has effect resistance; {@code false} otherwise.
     */
    public boolean hasEffectResistance(@NotNull AssistSource assistSource) {
        final HariantEntity source = assistSource.source();
        
        // Make sure we never resist self-debuffs
        if (this.equals(source)) {
            return false;
        }
        
        // Check for existing effect resistance
        if (effectResistance != null && effectResistance.hasNotExpired(this)) {
            return effectResistance.value();
        }
        
        final double chance = attributes.normalized(AttributeType.EFFECT_RESISTANCE);
        
        // Has resisted effect
        if (random.nextDouble() < chance) {
            effectResistance = new EffectResistance(true, this);
            
            EFFECT_RESISTANCE_DISPLAY.display(getLocation());
            return true;
        }
        // Has not resisted effect
        else {
            effectResistance = new EffectResistance(false, this);
            
            // Reassign `lastAttacker` and save the assist
            lastAttacker = source;
            combatTracker.assist(assistSource);
            
            return false;
        }
    }
    
    @NotNull
    public Optional<EnumTeam> getTeam() {
        return Optional.ofNullable(EnumTeam.getEntryTeam(this));
    }
    
    @NotNull
    @Override
    public TeamEntry teamEntry() {
        return TeamEntry.create(this);
    }
    
    public void teleport(@NotNull Location location) {
        entity.teleport(location);
    }
    
    /**
     * Hides <b>this</b> entity for the given {@link HariantPlayer}.
     *
     * @param player - The player for whom to hide.
     */
    public void hide(@NotNull HariantPlayer player) {
        player.getHandle().hideEntity(Hariant.getPlugin(), this.entity);
    }
    
    /**
     * Hides <b>this</b> entity according to the {@link StreamRules}.
     *
     * <p>
     * Note thas it's completely safe to hide the entity for self, since bukkit does an explicit self check.
     * </p>
     *
     * @param streamRules - The stream rules to follow.
     */
    public void hide(@NotNull StreamRules streamRules) {
        this.streamPlayers(streamRules).forEach(this::hide);
    }
    
    /**
     * Shows <b>this</b> entity for the given {@link HariantPlayer}.
     *
     * @param player - The player for whom to show.
     */
    public void show(@NotNull HariantPlayer player) {
        player.getHandle().showEntity(Hariant.getPlugin(), this.entity);
    }
    
    /**
     * Shows <b>this</b> entity according to the {@link StreamRules}.
     *
     * <p>
     * Note thas it's completely safe to show the entity for self, since bukkit does an explicit self check.
     * </p>
     *
     * @param streamRules - The stream rules to follow.
     */
    public void show(@NotNull StreamRules streamRules) {
        this.streamPlayers(streamRules).forEach(this::show);
    }
    
    /**
     * Gets a {@link Stream} of {@link HariantPlayer} according to the {@link StreamRules}.
     *
     * @param rule - The rules to follow.
     * @return a stream of players according to the rules.
     */
    @NotNull
    public Stream<? extends HariantPlayer> streamPlayers(@NotNull StreamRules rule) {
        return Hariant.getPlayers().filter(player -> {
            if (this.isSelf(player)) {
                return rule.includeSelf();
            }
            else if (this.isTeammate(player)) {
                return rule.includeTeammates();
            }
            
            return rule.includeOthers();
        });
    }
    
    public void strikeLightningEffect() {
        entity.getWorld().strikeLightningEffect(entity.getLocation().add(0, entity.getEyeHeight() + 1, 0));
    }
    
    public void addVanillaEffect(@NotNull PotionEffectType potionEffectType, int amplifier, int duration) {
        entity.addPotionEffect(potionEffectType.createEffect(duration, amplifier));
    }
    
    public void removeVanillaEffect(@NotNull PotionEffectType potionEffectType) {
        entity.removePotionEffect(potionEffectType);
    }
    
    @Override
    public void addEffect(@NotNull StatusEffectType effect, int duration, @NotNull HariantEntity applier) {
        effectMap.addEffect(effect, duration, applier);
    }
    
    @Override
    public void removeEffect(@NotNull StatusEffectType effect) {
        effectMap.removeEffect(effect);
    }
    
    @Override
    public void resetEffects() {
        effectMap.resetEffects();
    }
    
    @Override
    public boolean hasEffect(@NotNull StatusEffectType effect) {
        return effectMap.hasEffect(effect);
    }
    
    @NotNull
    @Override
    public Optional<StatusEffectInstance> getEffect(@NotNull StatusEffectType effect) {
        return effectMap.getEffect(effect);
    }
    
    @NotNull
    @Override
    public Stream<StatusEffectInstance> getEffects() {
        return effectMap.getEffects();
    }
    
    @Override
    public boolean triggerEffect(@NotNull HariantEntity applier, @NotNull Effect effect) {
        return HariantEffectEvent.callEvent(this, applier, effect, HariantEffectEvent::new);
    }
    
    @Override
    public int countEffects(@NotNull EffectType effectType) {
        return (int) Stream.concat(effectMap.getEffects().map(StatusEffectInstance::getEffect), attributes.getModifiers().stream())
                           .filter(effect -> effect.getEffectType() == effectType)
                           .count();
    }
    
    @NotNull
    @Override
    public Component asHeadComponent() {
        return DEFAULT_HEAD_COMPONENT;
    }
    
    @NotNull
    @Override
    public Component asDeathComponent() {
        final Style teamStyle = this.getTeam().map(EnumTeam::getStyle).orElse(Style.empty());
        
        return this.asHeadComponent().appendSpace().append(this.getName().style(teamStyle));
    }
    
    @NotNull
    @Override
    public Component asAssistComponent() {
        final Component teamPrefix = this.getTeam().map(team -> team.getPrefix().style(team.getStyle())).orElse(Component.empty());
        
        return this.asHeadComponent().appendSpace().append(teamPrefix);
    }
    
    public void setCollision(@NotNull HariantPlayer player, boolean collision) {
        final org.bukkit.scoreboard.Team collisionTeam = player.getOrCreateNoCollisionTeam();
        
        if (collision) {
            collisionTeam.removeEntity(entity);
        }
        else {
            collisionTeam.addEntity(entity);
        }
    }
    
    @NotNull
    @Override
    public ElementData getElementData() {
        return elementData;
    }
    
    @Override
    public void applyElement(@NotNull ElementSource elementSource) {
        elementData.applyElement(elementSource);
    }
    
    @Override
    public double getElementalUnit(@NotNull ElementType elementType) {
        return elementData.getElementalUnit(elementType);
    }
    
    @Override
    public void triggerAnomaly(@NotNull ElementalAnomalyType elementalAnomaly, @Nullable HariantEntity source) {
        elementData.triggerAnomaly(elementalAnomaly, source);
    }
    
    @NotNull
    public Component getHealthFormatted() {
        final double health = getFinalHealth();
        
        Component componentHealth = Component.text("%,.0f/%,.0f".formatted(health, getMaxHealth()), DEFAULT_HEALTH_STYLE);
        Component componentHeart = Component.text("❤", DEFAULT_HEALTH_STYLE);
        
        final Map.Entry<Class<? extends HealthMutator>, HealthMutator> lastMutatorEntry = healthMutators.lastEntry();
        
        if (lastMutatorEntry != null) {
            final HealthMutator lastMutator = lastMutatorEntry.getValue();
            
            componentHealth = componentHealth.style(lastMutator.getHealthStyle());
            componentHeart = componentHeart.style(lastMutator.getHeartStyle());
        }
        
        final TextComponent.Builder builder = Component.text();
        
        builder.append(componentHealth);
        builder.appendSpace();
        builder.append(componentHeart);
        
        // Shields
        if (shield != null) {
            builder.appendSpace();
            builder.append(shield);
        }
        
        // If entity has invulnerability frames, gray out the health and show the time left on invulnerability
        if (this.isInvulnerable()) {
            final int invulnerability = this.getInvulnerability();
            
            builder.applyDeep(deep -> deep.style(Style.style(Colors.DARK_GRAY)));
            builder.appendSpace();
            builder.append(Component.text("%s \uD83D\uDEE1".formatted(Tick.format(invulnerability)), TextColor.color(0x6A8FD9)));
        }
        
        return builder.build();
    }
    
    public void showWarning(@NotNull WarningType warningType, int duration) {
    }
    
    @NotNull
    public Location getLocationInFront(double distance) {
        return this.getLocationInFront0(distance, false);
    }
    
    @NotNull
    public Location getLocationInFrontFromEyes(double distance) {
        return this.getLocationInFront0(distance, true);
    }
    
    public void setGlowing(@NotNull HariantPlayer player, @NotNull PacketTeamColor color) {
        Glowing.setGlowing(player.getHandle(), entity, color, Glowing.INFINITE_DURATION);
    }
    
    public void stopGlowing(@NotNull HariantPlayer player) {
        Glowing.stopGlowing(player.getHandle(), entity);
    }
    
    public void updateHealth(double maxHealth) {
        if (health > maxHealth) {
            health = maxHealth;
        }
    }
    
    public void updateHealth() {
        this.updateHealth(this.getMaxHealth());
    }
    
    public void setVisualFire(@Nullable Boolean visualFire) {
        entity.setVisualFire(visualFire == null ? TriState.NOT_SET : visualFire ? TriState.TRUE : TriState.FALSE);
    }
    
    public void sendTitleSubtitle(@NotNull Component title, @NotNull Component subtitle, int fadeIn, int stay, int fadeOut) {
        this.sendTitle0(title, subtitle, fadeIn, stay, fadeOut);
    }
    
    public void sendTitle(@NotNull Component title, int fadeIn, int stay, int fadeOut) {
        this.sendTitle0(title, null, fadeIn, stay, fadeOut);
    }
    
    public void sendSubtitle(@NotNull Component subtitle, int fadeIn, int stay, int fadeOut) {
        // Subtitle cannot be displayed without a title, so we send an empty title, which will override the main title, if any showing
        this.sendTitle0(Component.empty(), subtitle, fadeIn, stay, fadeOut);
    }
    
    public void sendSubtitleKeepTitle(@NotNull Component subtitle, int fadeIn, int stay, int fadeOut) {
        this.sendTitle0(null, subtitle, fadeIn, stay, fadeOut);
    }
    
    public boolean isInvulnerable() {
        return ticker.invulnerability.value() > 0;
    }
    
    public int getInvulnerability() {
        return ticker.invulnerability.value();
    }
    
    public void setInvulnerability(int duration) {
        ticker.invulnerability.value(duration);
    }
    
    @NotNull
    public BoundingBox getBoundingBox() {
        return entity.getBoundingBox();
    }
    
    public void addVanillaAttributeModifier(@NotNull VanillaAttributeModifier vanillaAttributeModifier) {
        final AttributeInstance attribute = this.getVanillaAttribute(vanillaAttributeModifier.getAttribute());
        
        // Always remove the attribute because bukkit likes to throw exception when you breathe
        attribute.removeModifier(vanillaAttributeModifier.getModifierKey());
        
        // Apply modifier if the value isn't 0
        final AttributeModifier modifier = vanillaAttributeModifier.getModifier();
        
        if (modifier.getAmount() != 0) {
            // We use transient modifier because we don't care about restarts
            attribute.addTransientModifier(modifier);
        }
    }
    
    public void removeVanillaAttributeModifier(@NotNull VanillaAttributeModifier vanillaAttributeModifier) {
        this.getVanillaAttribute(vanillaAttributeModifier.getAttribute()).removeModifier(vanillaAttributeModifier.getModifier().getKey());
    }
    
    public @NotNull <T> Drawable drawableOf(@NotNull Particle particle, int amount, double x, double y, double z, float speed, T data) {
        return location -> spawnParticle(location, particle, amount, x, y, z, speed, data);
    }
    
    public @NotNull Drawable drawableOf(@NotNull Particle particle, int amount, double x, double y, double z, float speed) {
        return drawableOf(particle, amount, x, y, z, speed, null);
    }
    
    public @NotNull EntityEquipment getEquipment() {
        return Objects.requireNonNull(entity.getEquipment(), "Equipment is not supported for %s!".formatted(entity));
    }
    
    @NotNull
    public Input getCurrentInput() {
        return NoInput.INSTANCE;
    }
    
    public @NotNull org.bukkit.attribute.AttributeInstance getVanillaAttribute(@NotNull Attribute attribute) {
        return Objects.requireNonNull(entity.getAttribute(attribute), "Unsupported attribute: %s".formatted(attribute.getKey().getKey()));
    }
    
    public boolean isSneaking() {
        return entity.isSneaking();
    }
    
    public double getEyeHeight() {
        return entity.getEyeHeight();
    }
    
    public void onCooldownStarted(@NotNull HariantCooldown cooldown, int duration) {
    }
    
    public void onCooldownEnded(@NotNull HariantCooldown cooldown) {
    }
    
    public @NotNull DamageInstance createDamageInstance(@NotNull DamageSource damageSource) {
        return new DamageInstance(this, damageSource);
    }
    
    protected void playDamageFx(@NotNull Supplier<@Nullable SoundFx> supplier) {
        entity.playHurtAnimation(0);
        
        final SoundFx soundFx = supplier.get();
        
        if (soundFx != null) {
            this.playWorldSound(soundFx.sound(), soundFx.pitch());
        }
    }
    
    private void onDamageDealt0(@NotNull DamageInstance damageInstance, @NotNull HariantEntity entity) {
        this.onDamageDealt(damageInstance.getDamageSource(), entity);
        
        // Handle ferocity
        if (!damageInstance.getDamageSource().canTriggerFerocity()) {
            return;
        }
        
        final int ferocityStrikes = this.calculateFerocityStrikes();
        
        if (ferocityStrikes > 0) {
            entity.damageFerocity(damageInstance, ferocityStrikes, false);
        }
    }
    
    private int calculateFerocityStrikes() {
        final double ferocity = attributes.normalized(AttributeType.FEROCITY);
        
        if (ferocity == 0) {
            return 0;
        }
        
        int strikes = (int) ferocity;
        final double remainder = ferocity % 1;
        
        if (remainder > 0 && random.nextDouble() < remainder) {
            strikes++;
        }
        
        return strikes;
    }
    
    private void tickTrap() {
        if (trap != null) {
            trap.tick();
            
            if (trap.isOver()) {
                this.untrap(TrapEscape.TIME_LIMIT);
            }
        }
    }
    
    private void incrementTrackerDamage(@Nullable HariantEntity attacker, @NotNull DamageSource source, double damage, boolean isLethal) {
        final @NotNull HariantEntity that = attacker != null ? attacker : this;
        
        this.combatTracker.incrementDamage(CombatData.Type.INCOMING, that, source.getIdentity(), damage, isLethal);
        that.combatTracker.incrementDamage(CombatData.Type.OUTGOING, this, source.getIdentity(), damage, isLethal);
    }
    
    private void playWorldSound0(@NotNull Location location, @NotNull Sound sound, float volume, @Range(from = 0, to = 2) float pitch) {
        getWorld().playSound(location, sound, soundCategory(), volume, Math.clamp(pitch, 0f, 2f));
    }
    
    private void tickShield() {
        if (shield == null) {
            return;
        }
        
        shield.tick();
        
        if (shield.isOver()) {
            shield.onRemove0(Shield.Cause.EXPIRED);
            shield = null;
        }
    }
    
    private void tickHealthMutators() {
        if (healthMutators.isEmpty()) {
            return;
        }
        
        final Iterator<HealthMutator> iterator = healthMutators.values().iterator();
        
        while (iterator.hasNext()) {
            final HealthMutator mutator = iterator.next();
            
            mutator.tick(this);
            updateHealth();
            
            if (mutator.isOver()) {
                mutator.onRemove(this);
                iterator.remove();
            }
        }
    }
    
    private @NotNull DamageResult broadcastImmune() {
        COMPONENT_DISPLAY_IMMUNE.display(getMidpointLocation());
        
        return DamageResult.IMMUNE;
    }
    
    @NotNull
    private Location getLocationInFront0(double distance, boolean fromEyes) {
        final Location location = fromEyes ? this.getEyeLocation() : this.getLocation();
        final Vector vector = location.getDirection().setY(0);
        
        return vector.lengthSquared() > 0 ? location.add(vector.normalize().multiply(distance)) : location;
    }
    
    private void sendTitle0(@Nullable Component title, @Nullable Component subtitle, int fadeIn, int stay, int fadeOut) {
        this.sendTitlePart(TitlePart.TIMES, Title.Times.times(Duration.ofMillis(fadeIn * 50L), Duration.ofMillis(stay * 50L), Duration.ofMillis(fadeOut * 50L)));
        
        if (title != null) {
            this.sendTitlePart(TitlePart.TITLE, title);
        }
        
        if (subtitle != null) {
            this.sendTitlePart(TitlePart.SUBTITLE, subtitle);
        }
    }
    
    public static @NotNull Component createHeadComponent(@NotNull String texture) {
        return Component.object(
                ObjectContents.playerHead()
                              .profileProperty(PlayerHeadObjectContents.property("textures", Base64.getEncoder().encodeToString(HEAD_TEXTURE_URL.formatted(texture).getBytes())))
                              .build()
        ).color(Colors.WHITE);
    }
    
    public static class NoInput implements Input {
        
        public static final NoInput INSTANCE = new NoInput();
        
        private NoInput() {
        }
        
        @Override
        public boolean isForward() {
            return false;
        }
        
        @Override
        public boolean isBackward() {
            return false;
        }
        
        @Override
        public boolean isLeft() {
            return false;
        }
        
        @Override
        public boolean isRight() {
            return false;
        }
        
        @Override
        public boolean isJump() {
            return false;
        }
        
        @Override
        public boolean isSneak() {
            return false;
        }
        
        @Override
        public boolean isSprint() {
            return false;
        }
        
    }
    
    private static class FerocityTask extends HariantTickingTask {
        
        private static final int FEROCITY_DELAY = 9;
        private static final int FEROCITY_PERIOD = 3;
        
        private static final Scheduler SCHEDULER = Scheduler.ofTimer(FEROCITY_DELAY, FEROCITY_PERIOD);
        private static final Particle.DustTransition DUST_TRANSITION = new Particle.DustTransition(Color.fromRGB(77, 2, 8), Color.fromRGB(181, 43, 54), 0.8f);
        
        private final HariantEntity entity;
        private final DamageInstance damageSource;
        private final int ferocityStrikes;
        
        FerocityTask(@NotNull HariantEntity entity, @NotNull DamageInstance damageInstance, int ferocityStrikes) {
            super(SCHEDULER);
            
            this.entity = entity;
            this.damageSource = DamageInstance.copyOf(damageInstance, builder -> {
                // Set damage type to FEROCITY and zero elemental units
                return builder.damageType(DamageType.FEROCITY).elementalUnits(0);
            });
            this.ferocityStrikes = ferocityStrikes;
        }
        
        @Override
        public void run(int tick) {
            // If entity is no longer valid for ferocity or exceeding the strike limit, cancel
            if (entity.isDead() || tick >= ferocityStrikes) {
                this.cancel();
                return;
            }
            
            // Damage the entity
            entity.damage(damageSource);
            
            // Fx
            this.spawnFerocityFx();
        }
        
        private void spawnFerocityFx() {
            final Location location = entity.getLocation();
            
            entity.playWorldSound(location, Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 0.5f, 1.75f);
            entity.playWorldSound(location, Sound.ENTITY_DONKEY_HURT, 0.5f, 1.25f);
            
            final double eyeHeight = entity.getEyeHeight();
            
            final double x = entity.random.nextSignedDouble(1.25);
            final double z = entity.random.nextSignedDouble(1.25);
            
            Geometry.drawLine(
                    LocationHelper.copyOfPosition(location).add(x, eyeHeight, z),
                    LocationHelper.copyOfPosition(location).subtract(x, 0, z), 0.2d,
                    _location -> entity.spawnWorldParticle(_location, Particle.DUST_COLOR_TRANSITION, 1, 0, 0, 0, 0, DUST_TRANSITION)
            );
        }
        
    }
    
}