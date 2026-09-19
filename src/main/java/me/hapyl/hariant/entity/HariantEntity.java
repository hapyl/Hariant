package me.hapyl.hariant.entity;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import me.hapyl.eterna.module.annotate.EventLike;
import me.hapyl.eterna.module.location.Coordinates;
import me.hapyl.eterna.module.location.Distanced;
import me.hapyl.eterna.module.location.Located;
import me.hapyl.eterna.module.location.LocationHelper;
import me.hapyl.eterna.module.math.geometry.Drawable;
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
import me.hapyl.hariant.element.anomaly.ElementalAnomaly;
import me.hapyl.hariant.element.anomaly.ElementalAnomalyType;
import me.hapyl.hariant.entity.cooldown.CooldownHandler;
import me.hapyl.hariant.entity.cooldown.CooldownHandlerImpl;
import me.hapyl.hariant.entity.cooldown.HariantCooldown;
import me.hapyl.hariant.entity.damage.*;
import me.hapyl.hariant.entity.damage.mutator.DamageMutator;
import me.hapyl.hariant.entity.effect.Effect;
import me.hapyl.hariant.entity.effect.EffectHandler;
import me.hapyl.hariant.entity.effect.EffectType;
import me.hapyl.hariant.entity.effect.status.StatusEffectHandler;
import me.hapyl.hariant.entity.effect.status.StatusEffectInstance;
import me.hapyl.hariant.entity.effect.status.StatusEffectMap;
import me.hapyl.hariant.entity.effect.status.StatusEffectType;
import me.hapyl.hariant.entity.ferocity.Ferocity;
import me.hapyl.hariant.entity.ferocity.FerocitySource;
import me.hapyl.hariant.entity.heal.HealingSource;
import me.hapyl.hariant.entity.mutator.HealthMutator;
import me.hapyl.hariant.entity.player.Delegatable;
import me.hapyl.hariant.entity.player.DelegateType;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.entity.shield.Shield;
import me.hapyl.hariant.entity.shield.ShieldResult;
import me.hapyl.hariant.entity.trap.Trap;
import me.hapyl.hariant.entity.trap.TrapEscape;
import me.hapyl.hariant.entity.trap.Trappable;
import me.hapyl.hariant.event.*;
import me.hapyl.hariant.event.effect.HariantEffectEvent;
import me.hapyl.hariant.handler.HariantProjectile;
import me.hapyl.hariant.handler.ProjectileHandler;
import me.hapyl.hariant.team.EnumTeam;
import me.hapyl.hariant.team.TeamEntry;
import me.hapyl.hariant.team.TeamEntryProvider;
import me.hapyl.hariant.ui.ComponentDisplay;
import me.hapyl.hariant.ui.ComponentDisplayAnimation;
import me.hapyl.hariant.util.*;
import me.hapyl.hariant.weapon.NormalAttackRanged;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.Style;
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
import org.bukkit.potion.PotionEffect;
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
        EffectHandler, TickSupplier, Trappable, Delegatable,
        ProjectileLauncher {
    
    private static final ComponentDisplay EFFECT_RESISTANCE_DISPLAY = new ComponentDisplay(
            Component.text("ᴇꜰꜰᴇᴄᴛ ʀᴇꜱ", AttributeType.EFFECT_RESISTANCE.getStyle()),
            ComponentDisplayAnimation.ofSineAscend(),
            20,
            1.75f
    );
    
    private static final String HEAD_TEXTURE_URL = "{\"textures\":{\"SKIN\":{\"url\":\"https://textures.minecraft.net/texture/%s\"}}}";
    private static final Component DEFAULT_HEAD_COMPONENT = createHeadComponent("da99b05b9a1db4d29b5e673d77ae54a77eab66818586035c8a2005aeb810602a");
    
    private static final Component COMPONENT_IMMUNE = Component.text("ɪᴍᴍᴜɴᴇ", Colors.DARK_GRAY);
    
    private static final ComponentDisplay COMPONENT_DISPLAY_IMMUNE = createImmuneComponentDisplay(null);
    
    private static final HariantCooldown HEALTH_MUTATOR_APPLICATION_COOLDOWN = HariantCooldown.ofSeconds(Key.ofString("health_mutator_cooldown"), 1.2f);
    private static final HariantCooldown FEROCITY_COOLDOWN = HariantCooldown.ofSeconds(Key.ofString("ferocity"), 0.2f);
    
    private static final HealthStyle DEFAULT_HEALTH_STYLE = HealthStyle.create(Style.style(Colors.ATTRIBUTE_MAX_HEALTH));
    
    private static final HealthComponentSupplier HEALTH_COMPONENT_SUPPLIER = (health, maxHealth) -> Component.text("%,.0f/%,.0f".formatted(health, maxHealth));
    private static final HealthComponentSupplier HEALTH_COMPONENT_SUPPLIER_SIMPLE = (health, maxHealth) -> Component.text("%,.0f".formatted(health));
    
    private static final Component COMPONENT_INVULNERABILITY = Component.text("\uD83D\uDEE1", Colors.INVULNERABILITY);
    
    public final HariantRandom random;
    
    protected final LivingEntity entity;
    protected final AttributesInstance attributes;
    protected final ElementData elementData;
    
    private final StatusEffectMap effectMap;
    private final CooldownHandlerImpl cooldownHandler;
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
    private @Nullable Liquid liquid;
    private @Nullable Invulnerability invulnerability;
    
    private int ticksAlive;
    
    public HariantEntity(@NotNull LivingEntity entity, @NotNull Attributes attributes) {
        this.entity = entity;
        this.attributes = new AttributesInstance(this, attributes);
        this.random = new HariantRandom();
        this.health = attributes.get(AttributeType.MAX_HEALTH);
        this.effectMap = new StatusEffectMap(this);
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
    
    public boolean addHealthMutator(@NotNull HealthMutator mutator) {
        if (this.hasCooldown(HEALTH_MUTATOR_APPLICATION_COOLDOWN)) {
            return false;
        }
        
        this.healthMutators.put(mutator.getClass(), mutator);
        this.setCooldown(HEALTH_MUTATOR_APPLICATION_COOLDOWN);
        
        // Play mutator fx
        mutator.onApply(this);
        
        return true;
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
    
    public boolean isImmuneTo(@NotNull DamageInstance damageInstance) {
        return false;
    }
    
    /**
     * Internal method for damaging the entity with the given, already-compiled {@link DamageInstance}.
     *
     * <p><b>
     *     Note that the given damage instance will be mutated by either the {@link HariantDamageComputeEvent} or other internal methods, therefore,
     *     a {@link DamageInstance#copyOf(DamageInstance)} must be provided for proper function.
     * </b></p>
     *
     * @param damageInstance - The damage instance to damage with.
     * @return the damage result.
     */
    @ApiStatus.Internal
    public final @NotNull DamageResult damage(@NotNull DamageInstance damageInstance) {
        // Call computation event
        final HariantDamageComputeEvent hariantDamageComputeEvent = new HariantDamageComputeEvent(damageInstance);
        hariantDamageComputeEvent.callEvent();
        
        if (hariantDamageComputeEvent.cancel() instanceof HariantDamageComputeEvent.Cancel cancel) {
            createImmuneComponentDisplay(cancel.getName()).display(getMidpointLocation());
            return DamageResult.IMMUNE;
        }
        
        return this.damage0(damageInstance);
    }
    
    public final @NotNull DamageResult damage(@NotNull DamageSource source) {
        return this.damage(createDamageInstance(source));
    }
    
    public @NotNull Set<? extends Entity> listGarbage() {
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
    
    public void damageFerocity(@NotNull FerocitySource ferocitySource, boolean force) {
        // Process ferocity cooldown unless forcefully triggering
        if (this.hasCooldown(FEROCITY_COOLDOWN) && !force) {
            return;
        }
        
        // Call ferocity event
        final HariantFerocityEvent event = new HariantFerocityEvent(this, ferocitySource);
        
        if (event.callEvent()) {
            return;
        }
        
        // Always delegate ferocity task to the entity
        this.delegate(new Ferocity(this, ferocitySource), DelegateType.PERSISTENT);
        
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
    public int cancelDelegates(@NotNull Predicate<DelegateCancellable> filter) {
        final Counter counter = Counter.counter();
        
        this.delegatedCancellable.removeIf(delegate -> {
            if (filter.test(delegate)) {
                delegate.cancel();
                counter.increment();
                return true;
            }
            
            return false;
        });
        
        return counter.count();
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
            ComponentDisplay.ofAscend(
                    Component.empty()
                             .append(Component.text("+", Colors.GREEN))
                             .append(DigitStyle.STYLE.asComponent((int) actualHealing).color(Colors.GREEN)),
                    this.getMidpointLocation(),
                    20, 1.75f
            );
            
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
    public void onDamageDealt(@NotNull DamageInstance damageInstance, @NotNull HariantEntity entity) {
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
    public void onProjectileLaunched(@NotNull HariantProjectile projectile) {
    }
    
    @EventLike
    public void onShoot() {
    }
    
    @EventLike
    public void onInteract(@NotNull HariantPlayer player) {
    }
    
    @EventLike
    public void onRemove(@NotNull RemovalReason removalReason) {
        entity.remove();
    }
    
    @EventLike
    public void onEffectResistance(@NotNull AssistSource assistSource, @NotNull EffectResistance effectResistance) {
    }
    
    @EventLike
    public void onElementalAnomaly(@NotNull HariantEntity elementData, @NotNull ElementalAnomalySource anomalySource) {
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
        
        entity.setVelocity(new Vector(
                velocity.getX() * 0.5 + dx * strength,
                entity.isOnGround() ? Math.min(0.4, velocity.getY() * 0.5 + strength) : velocity.getX(),
                velocity.getZ() * 0.5 + dz * strength
        ));
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
        
        this.ticksAlive++;
        
        this.attributes.tick();
        this.effectMap.tick();
        this.elementData.tick();
        this.cooldownHandler.tick();
        
        this.tickHealthMutators();
        this.tickShield();
        this.tickTrap();
        this.tickLiquid();
        this.tickInvulnerability();
        this.tickPortal();
        
        return true;
    }
    
    public void setFreezeTicks(int freezeTicks) {
        entity.setFreezeTicks(freezeTicks);
    }
    
    public void setOutline(@NotNull Outline outline) {
    }
    
    public double getYaw() {
        return entity.getYaw();
    }
    
    public double getPitch() {
        return entity.getPitch();
    }
    
    private void tickPortal() {
        // TODO (xanyjl @ Tuesday, September 15) -> I think this is the only way to keep portals
    }
    
    public final boolean compareEntity(@NotNull Entity entity) {
        return this.entity.equals(entity);
    }
    
    public @NotNull Location getCenterLocation() {
        return LocationHelper.center(entity.getLocation());
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
        this.onDestroy();
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
        
        ticksAlive = 0;
        attributes.reset();
        effectMap.clearEffects();
        cooldownHandler.resetCooldowns();
        elementData.reset();
        healthMutators.clear();
        
        effectResistance = null;
        invulnerability = null;
        
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
    
    @SuppressWarnings("unchecked")
    @Override
    public <P extends Projectile, H extends HariantProjectile> @NotNull H launchProjectile(@NotNull Class<P> projectileClass, @Nullable Vector velocity, @NotNull ProjectileLauncher.ProjectileCreator<P, H> creator) {
        final Object[] uglyStinkyObjectReference = new Object[1];
        
        entity.launchProjectile(projectileClass, velocity, self -> {
            // We must create a projectile here to not trigger the handler creation
            uglyStinkyObjectReference[0] = ProjectileHandler.createProjectile(creator.create(self, this));
        });
        
        return (H) uglyStinkyObjectReference[0];
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
        return ticksAlive;
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
        
        // FIXME (xanyjl @ Saturday, August 22) -> This and triggerEffect is kinda of confusing
        
        // Make sure we never resist self-debuffs
        if (this.equals(source)) {
            return false;
        }
        
        // Check for existing effect resistance
        if (effectResistance != null && effectResistance.hasNotExpired(this)) {
            return effectResistance.hasResisted();
        }
        
        final double chance = attributes.normalized(AttributeType.EFFECT_RESISTANCE);
        
        // Has resisted effect
        if (random.nextDouble() < chance) {
            effectResistance = new EffectResistance(true, this);
            
            EFFECT_RESISTANCE_DISPLAY.display(getLocation());
        }
        // Has not resisted effect
        else {
            effectResistance = new EffectResistance(false, this);
            
            // Reassign `lastAttacker`
            lastAttacker = source;
        }
        
        this.onEffectResistance(assistSource, effectResistance);
        return effectResistance.hasResisted();
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
        entity.addPotionEffect(new PotionEffect(potionEffectType, duration, amplifier, false, false, false));
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
    public int clearEffects() {
        return effectMap.clearEffects();
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
    public boolean applyElement(@NotNull ElementSource elementSource) {
        return elementData.applyElement(elementSource);
    }
    
    @Override
    public boolean triggerAnomaly(@NotNull ElementalAnomalySource anomalySource, boolean force) {
        return elementData.triggerAnomaly(anomalySource, force);
    }
    
    @Override
    public double getElementalUnit(@NotNull ElementType elementType) {
        return elementData.getElementalUnit(elementType);
    }
    
    @Override
    public @Nullable ElementType lastAppliedElement() {
        return elementData.lastAppliedElement();
    }
    
    @Override
    public @Nullable ElementalAnomalyType lastTriggeredAnomaly() {
        return elementData.lastTriggeredAnomaly();
    }
    
    @Override
    public boolean isElementalAnomalyActive(@NotNull ElementalAnomaly elementalAnomaly) {
        return elementData.isElementalAnomalyActive(elementalAnomaly);
    }
    
    @Override
    public boolean endElementalAnomaly(@NotNull ElementalAnomaly elementalAnomaly) {
        return elementData.endElementalAnomaly(elementalAnomaly);
    }
    
    @Override
    public int getElementalAnomalyQueueLength(@NotNull ElementalAnomaly elementalAnomaly) {
        return elementData.getElementalAnomalyQueueLength(elementalAnomaly);
    }
    
    public @NotNull Component getHealthFormatted() {
        return this.getHealthFormatted0(HEALTH_COMPONENT_SUPPLIER);
    }
    
    public @NotNull Component getHealthFormattedSimple() {
        return this.getHealthFormatted0(HEALTH_COMPONENT_SUPPLIER_SIMPLE);
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
        return invulnerability != null;
    }
    
    public int getInvulnerability() {
        return invulnerability != null ? invulnerability.currentTick() : 0;
    }
    
    public void setInvulnerability(@NotNull InvulnerabilitySource source) {
        this.invulnerability = new Invulnerability(source);
    }
    
    public @NotNull BoundingBox getBoundingBox() {
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
    
    public @NotNull HealthStyle getHealthStyle() {
        final Map.Entry<Class<? extends HealthMutator>, HealthMutator> lastMutatorEntry = healthMutators.lastEntry();
        
        return lastMutatorEntry != null ? lastMutatorEntry.getValue() : DEFAULT_HEALTH_STYLE;
    }
    
    public double getHeight() {
        return entity.getHeight();
    }
    
    public void swingHand() {
        entity.swingMainHand();
    }
    
    public void swingOffHand() {
        entity.swingOffHand();
    }
    
    @NotNull
    public DamageResult damage0(@NotNull DamageInstance damageInstance) {
        // Do not deal damage to dead entities
        if (damageInstance.getDamage() <= 0 || this.isDead()) {
            return DamageResult.IMMUNE;
        }
        
        // Check for internal immunity
        if (this.isImmuneTo(damageInstance)) {
            return DamageResult.IMMUNE;
        }
        
        // Check for damage cooldown, if exists
        if (damageInstance.cooldownExistsEntityOnCooldownElseStartCooldown(this)) {
            return DamageResult.IMMUNE;
        }
        
        // Check for invulnerability
        if (this.invulnerability != null && !damageInstance.isFlagged(DamageFlag.IGNORES_INVULNERABILITY)) {
            // Call invulnerability event
            final HariantInvulnerabilityEvent hariantInvulnerabilityEvent = new HariantInvulnerabilityEvent(this, invulnerability, damageInstance);
            
            if (!hariantInvulnerabilityEvent.callEvent()) {
                this.invulnerability.display(getMidpointLocation());
                return DamageResult.IMMUNE;
            }
        }
        
        // *-* Below this point, the damage cannot be cancelled *-* //
        
        final HariantEntity attacker = damageInstance.getAttacker();
        
        // This one is a little weird, but we have to calculate Ferocity because mutations
        // to DamageInstance are made... so do it here
        @Nullable FerocitySource ferocitySource = null;
        
        if (attacker != null && damageInstance.getDamageType().canTriggerFerocity()) {
            final int ferocityStrikes = attacker.calculateFerocityStrikes();
            
            if (ferocityStrikes > 0) {
                ferocitySource = FerocitySource.create(attacker, damageInstance, ferocityStrikes);
            }
        }
        
        // Process shields
        if (shield != null && shield.canShield(damageInstance)) {
            final double damage = damageInstance.getDamage();
            final ShieldResult shieldResult = shield.shield0(damage, damageInstance);
            
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
        
        final boolean isLethal = health - damage <= 0.0 && !damageInstance.isFlagged(DamageFlag.CANNOT_KILL);
        
        if (isLethal) {
            damageInstance.markLethal();
        }
        
        // Set last attacker so we know who to credit for the kill
        if (attacker != null) {
            this.lastAttacker = attacker;
            this.lastAttacker.onDamageDealt(damageInstance, this);
        }
        
        // Call EventLike method
        this.onDamageTaken(damageInstance, attacker);
        
        // Call damage event
        new HariantDamageEvent(damageInstance).callEvent();
        
        // Broadcast hurt
        this.broadcastHurt(damageInstance, !isLethal);
        
        // Check whether damage can kill and call death event
        if (isLethal) {
            if (new HariantDeathEvent(this, damageInstance).callEvent()) {
                return DamageResult.IMMUNE;
            }
            
            this.die(damageInstance.getDamageSource());
            return DamageResult.DEAD;
        }
        
        // Decrement health
        this.decrementHealth(damage);
        
        // Apply element
        this.applyElement(damageInstance);
        
        // Execute ferocity
        if (ferocitySource != null) {
            this.damageFerocity(ferocitySource, false);
        }
        
        return DamageResult.OK;
    }
    
    protected void playDamageFx(@NotNull Supplier<@Nullable SoundFx> supplier) {
        entity.playHurtAnimation(0);
        
        final SoundFx soundFx = supplier.get();
        
        if (soundFx != null) {
            this.playWorldSound(soundFx.sound(), soundFx.pitch());
        }
    }
    
    private void tickInvulnerability() {
        if (invulnerability != null) {
            invulnerability.tick();
            
            if (invulnerability.isOver()) {
                invulnerability = null;
            }
        }
    }
    
    private void tickLiquid() {
        final Liquid previousLiquid = this.liquid;
        
        if (this.entity.isInWater()) {
            this.liquid = Liquid.WATER;
        }
        else if (entity.isInLava()) {
            this.liquid = Liquid.LAVA;
        }
        else {
            this.liquid = null;
        }
        
        // Call event if liquid changed, call event
        if (previousLiquid != this.liquid) {
            new HariantEntityLiquidEvent(this, previousLiquid, this.liquid).callEvent();
        }
    }
    
    private @NotNull Component getHealthFormatted0(@NotNull HealthComponentSupplier healthComponentSupplier) {
        final double health = this.getFinalHealth();
        final double maxHealth = this.getMaxHealth();
        
        final HealthStyle healthStyle = this.getHealthStyle();
        
        final Component componentHealth = healthComponentSupplier.supply(health, maxHealth).style(healthStyle.getHealthStyle());
        final Component componentHeart = Component.text("❤", healthStyle.getHeartStyle());
        
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
        if (invulnerability != null) {
            builder.applyDeep(deep -> deep.style(Style.style(Colors.DARK_GRAY)));
            builder.appendSpace();
            builder.append(invulnerability.currentTickFormatted().color(Colors.INVULNERABILITY));
            builder.appendSpace();
            builder.append(COMPONENT_INVULNERABILITY);
        }
        
        return builder.build();
    }
    
    private int calculateFerocityStrikes() {
        final double ferocity = attributes.normalized(AttributeType.FEROCITY);
        
        if (ferocity <= 0) {
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
    
    public static @NotNull ComponentDisplay createImmuneComponentDisplay(@Nullable Component component) {
        return new ComponentDisplay(createImmuneComponent(component), ComponentDisplayAnimation.ofFalloff(), 20, 1.75f);
    }
    
    private static @NotNull Component createImmuneComponent(@Nullable Component component) {
        return component != null
               ? COMPONENT_IMMUNE.appendSpace()
                                 .append(Component.text("(", Colors.DARK_GRAY))
                                 .append(component.color(Colors.DARK_GRAY))
                                 .append(Component.text(")", Colors.DARK_GRAY))
               : COMPONENT_IMMUNE;
    }
    
    public interface HealthComponentSupplier {
        @NotNull Component supply(final double health, final double maxHealth);
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
    
}