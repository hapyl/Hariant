package me.hapyl.hariant.entity;

import me.hapyl.eterna.module.annotate.EventLike;
import me.hapyl.eterna.module.location.Coordinates;
import me.hapyl.eterna.module.location.Distanced;
import me.hapyl.eterna.module.location.Located;
import me.hapyl.eterna.module.reflect.glowing.Glowing;
import me.hapyl.eterna.module.reflect.team.PacketTeamColor;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.Handle;
import me.hapyl.eterna.module.util.Removable;
import me.hapyl.eterna.module.util.Ticking;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.HariantLogger;
import me.hapyl.hariant.attribute.Attributable;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.instance.Attributes;
import me.hapyl.hariant.attribute.instance.AttributesInstance;
import me.hapyl.hariant.element.*;
import me.hapyl.hariant.element.anomaly.ElementalAnomaly;
import me.hapyl.hariant.entity.cooldown.Cooldown;
import me.hapyl.hariant.entity.cooldown.CooldownHandler;
import me.hapyl.hariant.entity.cooldown.CooldownHandlerImpl;
import me.hapyl.hariant.entity.damage.*;
import me.hapyl.hariant.entity.damage.tracker.CombatTracker;
import me.hapyl.hariant.entity.effect.EffectHandler;
import me.hapyl.hariant.entity.effect.EffectType;
import me.hapyl.hariant.entity.effect.status.EnumStatusEffect;
import me.hapyl.hariant.entity.effect.status.StatusEffectHandler;
import me.hapyl.hariant.entity.effect.status.StatusEffectInstance;
import me.hapyl.hariant.entity.effect.status.StatusEffectMap;
import me.hapyl.hariant.entity.frozen.FrozenHandler;
import me.hapyl.hariant.entity.heal.HealingSource;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.entity.ticker.EntityTicker;
import me.hapyl.hariant.event.*;
import me.hapyl.hariant.handler.ProjectileHandler;
import me.hapyl.hariant.team.EnumTeam;
import me.hapyl.hariant.team.TeamEntry;
import me.hapyl.hariant.team.TeamEntryProvider;
import me.hapyl.hariant.ui.ComponentDisplay;
import me.hapyl.hariant.ui.ComponentDisplayAnimation;
import me.hapyl.hariant.util.MathFont;
import me.hapyl.hariant.util.SoundFx;
import me.hapyl.hariant.util.UniquelyIdentified;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.object.ObjectContents;
import net.kyori.adventure.text.object.PlayerHeadObjectContents;
import net.kyori.adventure.util.TriState;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class HariantEntity
        implements
        Handle<LivingEntity>, Ticking, Attributable, Located,
        ForwardingAudience.Single, UniquelyIdentified, Lifecycle,
        Removable, Coordinates, SoundPlayer, ParticleSpawner,
        EntityCollector, Distanced, Attacker, TeamEntryProvider,
        StatusEffectHandler, HeadComponent, DeathComponent, CooldownHandler,
        Elemental, ElementHandler, HariantLogger.Sender, EffectHandler {
    
    private static final ComponentDisplay EFFECT_RESISTANCE_DISPLAY = new ComponentDisplay(
            Component.text("ᴇꜰꜰᴇᴄᴛ ʀᴇꜱ", AttributeType.EFFECT_RESISTANCE.getStyle()),
            ComponentDisplayAnimation.ofSineAscend(),
            20,
            1.75f
    );
    
    private static final Component DEFAULT_HEAD_COMPONENT = Component.object(
            ObjectContents.playerHead()
                          .profileProperty(PlayerHeadObjectContents.property(
                                  "textures",
                                  "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGE5OWIwNWI5YTFkYjRkMjliNWU2NzNkNzdhZTU0YTc3ZWFiNjY4MTg1ODYwMzVjOGEyMDA1YWViODEwNjAyYSJ9fX0="
                          ))
                          .build()
    ).color(NamedTextColor.WHITE);
    
    private static final Cooldown COOLDOWN_EFFECT_RESISTANCE = Cooldown.ofSeconds(Key.ofString("environment_no_damage_ticks"), 1);
    
    protected final LivingEntity entity;
    protected final AttributesInstance attributes;
    protected final StatusEffectMap effectMap;
    protected final CombatTracker combatTracker;
    protected final CooldownHandler cooldownHandler;
    protected final EntityTicker ticker;
    protected final ElementData elementData;
    
    private final HariantRandom random;
    
    @Nullable
    protected HariantEntity lastAttacker;
    
    protected double health;
    protected boolean deferDeath;
    
    @Nullable private SoundFx soundHurt;
    @Nullable private SoundFx soundDeath;
    
    @Nullable private FrozenHandler frozenHandler;
    
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
        
        this.updateAttributes();
        
        this.soundHurt = SoundFx.createNullable(entity.getHurtSound());
        this.soundDeath = SoundFx.createNullable(entity.getDeathSound());
    }
    
    @NotNull
    public CombatTracker getCombatTracker() {
        return combatTracker;
    }
    
    @Override
    public void setCooldown(@NotNull Key key, @Range(from = 0, to = Integer.MAX_VALUE) int duration) {
        cooldownHandler.setCooldown(key, duration);
    }
    
    @Override
    public int getCooldownTimeLeft(@NotNull Key key) {
        return cooldownHandler.getCooldownTimeLeft(key);
    }
    
    @Override
    public boolean isOnCooldown(@NotNull Key key) {
        return cooldownHandler.isOnCooldown(key);
    }
    
    @Override
    public void resetCooldowns() {
        cooldownHandler.resetCooldowns();
    }
    
    @EventLike
    public void onCooldownChange(@NotNull Key key, int cooldown) {
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
    
    @Nullable
    @Override
    public NormalAttack getRangedAttack() {
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
        
        this.health = newHealth;
        this.onHealthChange(previousHealth, newHealth);
    }
    
    public boolean isImmuneTo(@NotNull DamageSource source) {
        return false;
    }
    
    @NotNull
    public DamageResult damage(@NotNull DamageSource source) {
        // Ignore the damage if we immune to it or damage is 0 or negative
        if (this.isImmuneTo(source) || source.getDamage() <= 0) {
            return DamageResult.IMMUNE;
        }
        
        // Check for cooldown
        final Key cooldownKey = source.getCooldownKey();
        final boolean doesNotIgnoreCooldown = !source.isFlagged(DamageFlag.IGNORES_INTERNAL_COOLDOWN);
        
        if (source.hasCooldown() && doesNotIgnoreCooldown && this.isOnCooldown(source)) {
            return DamageResult.IMMUNE;
        }
        
        // Check for invulnerability ticks
        if (this.ticker.invulnerability.value() > 0 && doesNotIgnoreCooldown) {
            return DamageResult.IMMUNE;
        }
        
        final DamageInstance damageInstance = new DamageInstance(this, source);
        final HariantDamageEvent damageEvent = new HariantDamageEvent(damageInstance);
        
        // TODO @Apr 12, 2026 (xanyjl) -> Implement shields
        
        if (damageEvent.callEvent()) {
            return DamageResult.IMMUNE;
        }
        
        final HariantEntity attacker = damageInstance.getAttacker();
        
        final double damage = damageInstance.getDamage();
        final boolean isLethal = health - damage <= 0.0;
        
        // Set last attacker so we know who to credit for the kill
        if (attacker != null) {
            this.lastAttacker = attacker;
            this.lastAttacker.onDamageDealt(source, this);
        }
        
        // Increment damage deal in the tracker; we use self as the attacker for environment damage
        this.combatTracker.incrementDamageDealt(attacker != null ? attacker : this, source.getIdentity(), damage);
        
        // FIXME > TEMP DEBUG
        Hariant.getPlayerProfiles().filter(profile -> profile.getRank().isStaff()).forEach(profile -> {
            HariantLogger.system(
                    profile.getPlayer(),
                    Component.empty()
                             .append(Component.text("%s took %.0f damage!".formatted(this, damage)))
                             .hoverEvent(damageInstance.getDamageReport().createHoverEvent())
            );
        });
        
        // Broadcast hurt
        this.broadcastHurt(damageInstance, !isLethal);
        
        // Check whether damage can kill and call death event
        if (isLethal) {
            if (source.isFlagged(DamageFlag.CANNOT_KILL) || new HariantDeathEvent(this, damageInstance).callEvent()) {
                return DamageResult.IMMUNE;
            }
            
            this.die(source);
            
            return DamageResult.DEAD;
        }
        
        // Decrement health
        this.decrementHealth(damage);
        
        // Call EventLike method
        this.onDamageTaken(damageInstance, attacker);
        
        // Apply element
        this.applyElement(source);
        
        // Start cooldown if the damage was actually dealt
        if (!cooldownKey.isEmpty()) {
            this.setCooldown(source);
        }
        
        return DamageResult.OK;
    }
    
    public boolean die(@NotNull DamageSource source) {
        if (this.deferDeath) {
            return false;
        }
        
        this.deferDeath = true;
        this.health = 0.0;
        
        // Call `onKill` on damager
        if (this.lastAttacker != null) {
            this.lastAttacker.onKill(this, source);
        }
        
        // Call `onDeath` on this
        this.onDeath(source);
        return true;
    }
    
    @NotNull
    public DamageResult attack(@NotNull HariantEntity entity, @NotNull DamageSource damageSource, @NotNull KnockbackSource knockbackSource) {
        // Check whether we can actually attack the entity
        if (!this.canAttack(entity)) {
            return DamageResult.IMMUNE;
        }
        
        final AffectResult affection = this.getAffection(entity);
        
        if (affection != AffectResult.CAN_AFFECT) {
            // If teammates, show the message
            if (affection == AffectResult.CANNOT_AFFECT_TEAMMATE) {
                this.sendMessage(Component.text("Cannot damage teammates!", Colors.ERROR));
            }
            
            return DamageResult.IMMUNE;
        }
        
        // Deal damage to the entity
        final DamageResult damageResult = entity.damage(damageSource);
        
        // If the damage was successful, apply knockback
        if (damageResult == DamageResult.OK) {
            entity.knockback(knockbackSource);
        }
        
        return damageResult;
    }
    
    @NotNull
    public DamageResult attack(@NotNull HariantEntity entity) {
        final NormalAttack meleeAttack = this.getMeleeAttack();
        
        return this.attack(entity, meleeAttack.createDamageSource(this).build(), meleeAttack.createKnockbackCause(this));
    }
    
    public boolean heal(@NotNull HealingSource healingSource) {
        @Nullable final HariantEntity healer = healingSource.healer();
        double healingAmount = healingSource.amount();
        
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
        final double excessHealing = Math.max(0, healthAfterHealing - maxHealth);
        
        // Call healing event
        if (new HariantHealEvent(this, healthBeforeHealing, healthAfterHealing, actualHealing, excessHealing).callEvent()) {
            return false;
        }
        
        this.health = healthAfterHealing;
        
        this.onHeal(healthBeforeHealing, healthAfterHealing, actualHealing, excessHealing);
        this.onHealthChange(healthBeforeHealing, healthAfterHealing);
        
        return true;
    }
    
    @EventLike
    public void onHeal(double healthBeforeHealing, double healthAfterHealing, double actualHealing, double excessHealing) {
        // Show healing display
        if (actualHealing > 1) {
            ComponentDisplay.ofAscend(Component.text("+" + MathFont.format((int) actualHealing), NamedTextColor.GREEN), this.getMidpointLocation(), 20, 1.75f);
            
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
        new HariantHealthChangeEvent(this, previousHealth, newHealth).callEvent();
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
    
    /**
     * Gets whether this {@link HariantEntity} can affect the given {@link HariantEntity} in any context.
     *
     * @param entity - The entity to check.
     * @return {@code true} if this entity can affect the other one; {@code false} otherwise.
     */
    public final boolean canAffect(@NotNull HariantEntity entity) {
        return getAffection(entity) == AffectResult.CAN_AFFECT;
    }
    
    /**
     * Gets the {@link AffectResult} for the given {@link HariantEntity}.
     *
     * @param entity - The entity to check.
     * @return the affect result.
     */
    @NotNull
    public final AffectResult getAffection(@NotNull HariantEntity entity) {
        if (this.isSelf(entity)) {
            return AffectResult.CANNOT_AFFECT_SELF;
        }
        else if (this.isDead()) {
            return AffectResult.CANNOT_AFFECT_DEAD;
        }
        else if (this.isTeammate(entity)) {
            return AffectResult.CANNOT_AFFECT_TEAMMATE;
        }
        else if (!this.canSee(entity)) {
            return AffectResult.CANNOT_AFFECT_INVISIBLE;
        }
        else if (entity instanceof HariantMarkerEntity) {
            return AffectResult.CANNOT_AFFECT_MARKER;
        }
        
        return AffectResult.CAN_AFFECT;
    }
    
    public boolean isDead() {
        return entity.isDead();
    }
    
    public boolean canSee(@NotNull HariantEntity entity) {
        // TODO @Apr 14, 2026 (xanyjl) -> Implement invisibility
        return true;
    }
    
    public boolean isInvisible() {
        // TODO @Apr 14, 2026 (xanyjl) -> Implement invisibility
        return false;
    }
    
    public boolean canAttack(@NotNull HariantEntity entity) {
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
        // If the entity is frozen, skip the calculations
        if (this.isFrozen()) {
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
        
        // TODO @Mar 10, 2026 (xanyjl) -> Maybe add knockback event?
        
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
    
    public double getMaxHealth() {
        return attributes.get(AttributeType.MAX_HEALTH);
    }
    
    @Override
    @NotNull
    public LivingEntity getHandle() {
        return entity;
    }
    
    @Override
    @OverridingMethodsMustInvokeSuper
    public void tick() {
        if (!shouldTick()) {
            return;
        }
        
        // Always tick the ticker
        ticker.tick();
        
        // Tick frozen
        if (frozenHandler != null) {
            frozenHandler.tick();
            
            if (frozenHandler.isOver()) {
                this.unfreeze();
            }
        }
        
        // Only tick if not frozen
        if (!isFrozen()) {
            // Tick attributes
            attributes.tick();
            
            // Tick effects
            effectMap.tick();
            
            // Tick element data
            elementData.tick();
        }
    }
    
    @ApiStatus.Internal
    public final void tick0() {
        // Handler deferred death
        if (this.deferDeath) {
            this.deferDeath = false;
            this.onDestroy();
            return;
        }
        
        this.tick();
    }
    
    public final boolean shouldActuallyTick() {
        return shouldTick() && !isFrozen();
    }
    
    public boolean shouldTick() {
        return !entity.isDead();
    }
    
    public boolean isFrozen() {
        return frozenHandler != null;
    }
    
    @Nullable
    public FrozenHandler getFrozenHandler() {
        return frozenHandler;
    }
    
    public void freeze(@NotNull FrozenHandler frozenHandler) {
        // If already frozen, unfreeze
        if (this.frozenHandler != null) {
            this.frozenHandler.unfreeze();
        }
        
        this.frozenHandler = frozenHandler;
        this.frozenHandler.freeze();
    }
    
    public void unfreeze() {
        if (this.frozenHandler != null) {
            this.frozenHandler.unfreeze();
            this.frozenHandler = null;
        }
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
    
    @NotNull
    public org.bukkit.attribute.AttributeInstance getVanillaAttribute(@NotNull Attribute attribute) {
        return Objects.requireNonNull(entity.getAttribute(attribute), "Unsupported attribute: %s".formatted(attribute.getKey().getKey()));
    }
    
    @Override
    @NotNull
    public UUID getUuid() {
        return entity.getUniqueId();
    }
    
    @Override
    public final void remove() {
        // Removal is a little wonky and done via the following:
        // 1. `onDestroy()` is called, that MUST remove the bukkit entity (Unless it's a player)
        // 2. Bukkit entity removed
        // 3. `Hariant#tick` calls `entities#removeIf()` that removes HariantEntity if the `shouldRemove` is true, which
        //    only is true if the bukkit entity is dead.
        // 4. Done! Both bukkit entity and HariantEntity is removed.
        this.onDestroy();
    }
    
    @Override
    public final boolean shouldRemove() {
        // Always unregister dead bukkit entities
        return entity.isDead();
    }
    
    /**
     * Called whenever this {@link HariantEntity} is created.
     */
    @Override
    public void onCreate() {
    }
    
    /**
     * Called whenever this {@link HariantEntity} is destroyed, and <b>must</b> remove the bukkit entity, by either setting
     * its health to {@code 0} or by calling {@link Entity#remove()} method.
     *
     * <p>
     * Players must reset the states but not remove the player.
     * </p>
     */
    @Override
    public void onDestroy() {
        // We play the death animation, so set the health to 0 instead of calling remove()
        entity.setHealth(0.0);
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
        this.playWorldSound(getLocation(), sound, pitch, pitch);
    }
    
    @Override
    public void playWorldSound(@NotNull Location location, @NotNull Sound sound, float volume, @Range(from = 0, to = 2) float pitch) {
        getWorld().playSound(location, sound, soundCategory(), volume, Math.clamp(pitch, 0f, 2f));
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
    
    public int getTicksAlive() {
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
    
    public boolean isSelfOrTeammateOrCannotSee(@Nullable HariantEntity other) {
        return other != null && (this.equals(other) || this.isTeammate(other) || !this.canSee(other));
    }
    
    public boolean isTeammate(@Nullable HariantEntity other) {
        if (other == null) {
            return false;
        }
        
        final EnumTeam thisTeam = EnumTeam.getEntryTeam(TeamEntry.create(this));
        final EnumTeam thatTeam = EnumTeam.getEntryTeam(TeamEntry.create(other));
        
        return thisTeam != null && thisTeam == thatTeam;
    }
    
    public boolean hasEffectResistance(@Nullable HariantEntity attacker, @NotNull AssistSource assistSource) {
        // Make sure we never resist self-debuffs
        if (this.equals(attacker)) {
            return false;
        }
        
        if (this.isOnCooldown(COOLDOWN_EFFECT_RESISTANCE)) {
            return true;
        }
        
        final double effectResistance = attributes.normalized(AttributeType.EFFECT_RESISTANCE);
        
        // Resisted the effect
        if (random.nextDouble() < effectResistance) {
            this.setCooldown(COOLDOWN_EFFECT_RESISTANCE);
            EFFECT_RESISTANCE_DISPLAY.display(getLocation());
            return true;
        }
        
        // Otherwise reassign last damager and add as assister
        if (attacker != null) {
            lastAttacker = attacker;
            
            combatTracker.assist(attacker, assistSource);
        }
        
        return false;
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
    
    public void hide(@NotNull HariantPlayer player) {
        // Don't hide for self or teammates
        if (this.isSelfOrTeammate(player)) {
            return;
        }
        
        player.getHandle().hideEntity(Hariant.getPlugin(), this.entity);
    }
    
    public void hide() {
        Hariant.getPlayers().forEach(this::hide);
    }
    
    public void show(@NotNull HariantPlayer player) {
        if (this.isSelfOrTeammate(player)) {
            return;
        }
        
        player.getHandle().showEntity(Hariant.getPlugin(), this.entity);
    }
    
    public void show() {
        Hariant.getPlayers().forEach(this::show);
    }
    
    public void strikeLightning() {
        entity.getWorld().strikeLightningEffect(entity.getLocation().add(0, entity.getEyeHeight() + 1, 0));
    }
    
    public void addVanillaEffect(@NotNull PotionEffectType potionEffectType, int amplifier, int duration) {
        entity.addPotionEffect(potionEffectType.createEffect(duration, amplifier));
    }
    
    public void removeVanillaEffect(@NotNull PotionEffectType potionEffectType) {
        entity.removePotionEffect(potionEffectType);
    }
    
    @Override
    public void addEffect(@NotNull EnumStatusEffect effect, int duration, @Nullable HariantEntity applier) {
        effectMap.addEffect(effect, duration, applier);
    }
    
    @Override
    public void removeEffect(@NotNull EnumStatusEffect effect) {
        effectMap.removeEffect(effect);
    }
    
    @Override
    public void resetEffects() {
        effectMap.resetEffects();
    }
    
    @Override
    public boolean hasEffect(@NotNull EnumStatusEffect effect) {
        return effectMap.hasEffect(effect);
    }
    
    @NotNull
    @Override
    public Optional<StatusEffectInstance> getEffect(@NotNull EnumStatusEffect effect) {
        return effectMap.getEffect(effect);
    }
    
    @NotNull
    @Override
    public Stream<StatusEffectInstance> getEffects() {
        return effectMap.getEffects();
    }
    
    @Override
    public void triggerBuff(@NotNull HariantEntity applier) {
        HariantEffectEvent.triggerDummyEvent(this, applier, true);
    }
    
    @Override
    public void triggerDebuff(@NotNull HariantEntity applier) {
        HariantEffectEvent.triggerDummyEvent(this, applier, false);
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
    public void triggerAnomaly(@NotNull ElementalAnomaly elementalAnomaly, @Nullable HariantEntity source) {
        elementData.triggerAnomaly(elementalAnomaly, source);
    }
    
    @NotNull
    public Component getHealthFormatted() {
        // TODO @Mar 02, 2026 (xanyjl) -> Shields
        return Component.text("%,.0f/%,.0f ❤".formatted(health, getMaxHealth()), Colors.ATTRIBUTE_MAX_HEALTH);
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
    
    public void setVisualFire(@Nullable Boolean visualFire) {
        entity.setVisualFire(visualFire == null ? TriState.NOT_SET : visualFire ? TriState.TRUE : TriState.FALSE);
    }
    
    @NotNull
    private Location getLocationInFront0(double distance, boolean fromEyes) {
        final Location location = fromEyes ? this.getEyeLocation() : this.getLocation();
        final Vector vector = location.getDirection().normalize().multiply(distance);
        
        return location.add(vector);
    }
    
    private void playDamageFx(@NotNull Supplier<@Nullable SoundFx> supplier) {
        entity.playHurtAnimation(0);
        
        final SoundFx soundFx = supplier.get();
        
        if (soundFx != null) {
            this.playWorldSound(soundFx.sound(), soundFx.pitch());
        }
    }
    
}
