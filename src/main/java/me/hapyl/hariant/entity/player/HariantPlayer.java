package me.hapyl.hariant.entity.player;

import com.google.common.collect.Maps;
import me.hapyl.eterna.Eterna;
import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.reflect.Reflect;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.HariantConstants;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.database.rank.FormatRules;
import me.hapyl.hariant.entity.*;
import me.hapyl.hariant.entity.cooldown.CooldownHandler;
import me.hapyl.hariant.entity.cooldown.HariantCooldown;
import me.hapyl.hariant.entity.damage.AssistSource;
import me.hapyl.hariant.entity.damage.DamageSource;
import me.hapyl.hariant.entity.damage.DamageType;
import me.hapyl.hariant.entity.damage.tracker.CombatData;
import me.hapyl.hariant.entity.effect.status.StatusEffectType;
import me.hapyl.hariant.entity.heal.HealingSource;
import me.hapyl.hariant.event.HariantPlayerCreateEvent;
import me.hapyl.hariant.game.GameInstance;
import me.hapyl.hariant.handler.PlayerHandler;
import me.hapyl.hariant.hero.Hero;
import me.hapyl.hariant.hero.HeroData;
import me.hapyl.hariant.hero.HeroDataSupplier;
import me.hapyl.hariant.hero.HeroInstance;
import me.hapyl.hariant.profile.NameFormatter;
import me.hapyl.hariant.profile.PlayerProfile;
import me.hapyl.hariant.profile.setting.Setting;
import me.hapyl.hariant.profile.setting.SettingRetriever;
import me.hapyl.hariant.profile.setting.Settings;
import me.hapyl.hariant.profile.ui.ActionbarBuilder;
import me.hapyl.hariant.talent.Talent;
import me.hapyl.hariant.talent.TalentIndex;
import me.hapyl.hariant.talent.rechargeable.RechargeableTalentData;
import me.hapyl.hariant.talent.rechargeable.TalentRechargeable;
import me.hapyl.hariant.talent.ultimate.TalentUltimate;
import me.hapyl.hariant.talent.ultimate.UltimateResourceType;
import me.hapyl.hariant.task.HariantTickingTask;
import me.hapyl.hariant.task.InternalTasks;
import me.hapyl.hariant.task.Scheduler;
import me.hapyl.hariant.team.EnumTeam;
import me.hapyl.hariant.weapon.NormalAttackRanged;
import me.hapyl.hariant.weapon.Weapon;
import me.hapyl.hariant.weapon.WeaponBow;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.util.TriState;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundCooldownPacket;
import net.minecraft.resources.Identifier;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

public class HariantPlayer extends HariantEntity implements CooldownHandler, HeroDataRetriever, SettingRetriever, NameFormatter {
    
    /**
     * Defines default attributes that are reset each time player is spawner or removed, used to ensure
     * correctness, event if base values are never touched.
     */
    private static final Map<Attribute, Double> DEFAULT_ATTRIBUTE_VALUES = Map.of(
            Attribute.MAX_HEALTH, HariantConstants.ABSOLUTE_MAX_HEALTH,
            Attribute.ARMOR, -100.0,        // This will remove armor bars, which are ugly and useless
            Attribute.ATTACK_SPEED, 2.0,    // This will remove the damage indicator
            Attribute.MOVEMENT_SPEED, 0.1,  // This will ensure speed is reset properly
            Attribute.JUMP_STRENGTH, 0.42,  // This will ensure jump strength is reset properly
            Attribute.MAX_ABSORPTION, 20.0, // This will ensure max hearts for shields being within limit
            Attribute.SAFE_FALL_DISTANCE, HariantConstants.FALL_DAMAGE_SAFE_FALL_DISTANCE,
            Attribute.WATER_MOVEMENT_EFFICIENCY, 0.0
    );
    
    private static final String NO_COLLISION_BUKKIT_TEAM = "no_collision";
    private static final int[] HOT_BAR_SLOTS = { 0, 1, 2, 3, 4, 5, 6, 7, 8 };
    
    private static final @NotNull Component HEALING_SOURCE_PLAYER_ELIMINATION = Component.text("Player Elimination");
    private static final @NotNull Component HEALING_SOURCE_PLAYER_ASSIST = Component.text("Player Assist");
    
    private static final @NotNull Component PREFIX_DEATH = PlayerHandler.createPrefix(Component.text("☠", Colors.DARK_RED));
    
    private final PlayerProfile profile;
    private final HeroInstance heroInstance;
    private final ActionbarCache actionbarCache;
    
    private final Map<Class<? extends Hero>, HeroData<? extends Hero>> heroData;
    private final Map<TalentRechargeable, RechargeableTalentData> rechargeableTalentData;
    
    private double ultimateResource;
    private int usedUltimateAt;
    
    private PlayerState state;
    
    public HariantPlayer(@NotNull PlayerProfile profile, @NotNull Player player, @NotNull HeroInstance heroInstance) {
        super(player, heroInstance.getOrigin().getAttributes());
        
        this.profile = profile;
        this.heroInstance = heroInstance;
        this.heroData = Maps.newHashMap();
        this.state = PlayerState.ALIVE;
        this.actionbarCache = new ActionbarCache();
        this.rechargeableTalentData = Maps.newHashMap();
    }
    
    public void interrupt(@NotNull AssistSource source) {
        // Interruption counts as negative effect, so check for effect resistance
        if (this.hasEffectResistance(source)) {
            return;
        }
        
        // Cancel interruptible delegates
        cancelDelegates(DelegateCancellable::isInterruptable);
        
        // Interrupt weapon
        final PlayerInventory inventory = getInventory();
        
        final int weaponSlot = heroInstance.getOrigin().getWeaponSlot(this);
        
        inventory.setHeldItemSlot(8);
        InternalTasks.later(() -> inventory.setHeldItemSlot(weaponSlot), 2);
        
        // Add to assisters
        combatTracker.assist(source);
        
        // Fx
        playSound(Sound.ENTITY_ELDER_GUARDIAN_CURSE, 2.0f);
        playSound(Sound.ENCHANT_THORNS_HIT, 0.0f);
    }
    
    @NotNull
    public PlayerProfile getProfile() {
        return profile;
    }
    
    @SuppressWarnings("unchecked")
    @NotNull
    @Override
    public <H extends Hero, D extends HeroData<H>> D getHeroData(@NotNull H hero, @NotNull HeroDataSupplier<H, D> supplier) {
        return (D) this.heroData.computeIfAbsent(hero.getClass(), heroClass -> supplier.supply(hero, this));
    }
    
    @Override
    public <H extends Hero> boolean hasHeroData(@NotNull H hero) {
        return heroData.containsKey(hero.getClass());
    }
    
    @Override
    public <H extends Hero, D extends HeroData<H>> void touchHeroData(@NotNull H hero, @NotNull Class<D> heroDataClass, @NotNull Consumer<@NotNull D> consumer) {
        final D data = this.touchData0(hero, heroDataClass);
        
        if (data != null) {
            consumer.accept(heroDataClass.cast(data));
        }
    }
    
    @Override
    public <H extends Hero, D extends HeroData<H>, R> @NotNull Optional<R> touchHeroData(@NotNull H hero, @NotNull Class<D> heroDataClass, @NotNull Function<@NotNull D, @Nullable R> function) {
        final D data = this.touchData0(hero, heroDataClass);
        
        return data != null ? Optional.ofNullable(function.apply(data)) : Optional.empty();
    }
    
    public boolean isUsingUltimate() {
        return usedUltimateAt > 0;
    }
    
    public void setUsingUltimate(boolean usingUltimate) {
        this.usedUltimateAt = usingUltimate ? this.localTicks() : 0;
    }
    
    public int getUsedUltimateAt() {
        return usedUltimateAt;
    }
    
    /**
     * Increments the ultimate resource for this player.
     *
     * @param value                      - The value by which to increment.
     * @param offsetByEffectiveAttribute - {@code true} to decrement the value by the effective attribute.
     */
    public void incrementUltimateResource(final double value, boolean offsetByEffectiveAttribute) {
        double absoluteValue = Math.abs(value);
        
        final Hero hero = heroInstance.getOrigin();
        final TalentUltimate ultimateTalent = hero.getUltimateTalent();
        
        // Offset by effective attribute
        if (offsetByEffectiveAttribute) {
            final AttributeType effectiveAttribute = ultimateTalent.getUltimateResourceType().getEffectiveAttribute();
            
            if (effectiveAttribute != null) {
                absoluteValue *= attributes.normalized(effectiveAttribute);
            }
        }
        
        final double previousUltimateResource = ultimateResource;
        final double newUltimateResource = Math.min(ultimateTalent.getMaximumCost(), ultimateResource + absoluteValue);
        
        this.ultimateResource = newUltimateResource;
        
        // Call ultimate talent
        ultimateTalent.onResourceValue(this, previousUltimateResource, newUltimateResource);
    }
    
    public void incrementUltimateResource(final double value) {
        this.incrementUltimateResource(value, true);
    }
    
    public void decrementUltimateResource(final double value) {
        final double absoluteValue = Math.abs(value);
        
        this.ultimateResource = Math.max(0, ultimateResource - absoluteValue);
    }
    
    public void chargeUltimate() {
        this.incrementUltimateResource(heroInstance.getOrigin().getUltimateTalent().getMaximumCost());
    }
    
    public double getUltimateResource() {
        return ultimateResource;
    }
    
    @NotNull
    @Override
    public final NormalAttack getMeleeAttack() {
        return heroInstance.getOrigin().getWeapon(this).getMeleeAttack();
    }
    
    @Override
    public final NormalAttackRanged getRangedAttack() {
        return heroInstance.getOrigin().getWeapon(this).getRangedAttack();
    }
    
    @Override
    public boolean isPersistent() {
        return true;
    }
    
    @NotNull
    @Override
    public final Set<? extends Entity> listGarbage() {
        return Set.of();
    }
    
    @Override
    public void onHeal(double healthBeforeHealing, double healthAfterHealing, double actualHealing) {
        super.onHeal(healthBeforeHealing, healthAfterHealing, actualHealing);
        this.addVanillaEffect(PotionEffectType.REGENERATION, 0, 25);
    }
    
    @Override
    public void onKill(@NotNull HariantEntity entity, @NotNull DamageSource damageSource) {
        if (entity instanceof HariantPlayer player) {
            // Make sure it's not suicide nor teammate
            if (this.isSelfOrTeammate(player)) {
                return;
            }
            
            fetchGameInstance(gameInstance -> gameInstance.onKill(gameInstance, this, player));
            
            this.sendEliminationFeedback(EliminationFeedback.KILL, player);
            
            // Generate energy
            this.incrementUltimateResource(heroInstance.getOrigin().getUltimateTalent().getUltimateResourceType().regenerateOnElimination());
            
            // Heal
            this.heal(HealingSource.create(this.getMaxHealth() * HariantConstants.HEALING_ON_PLAYER_ELIMINATION, HEALING_SOURCE_PLAYER_ELIMINATION));
            
            // Reward for kill
            //CommonRewards.PLAYER_ELIMINATION.reward(profile);
        }
    }
    
    @Override
    public void onAssist(@NotNull HariantPlayer player) {
        // Regenerate energy
        this.incrementUltimateResource(heroInstance.getOrigin().getUltimateTalent().getUltimateResourceType().regenerateOnAssist());
        
        // Heal
        this.heal(HealingSource.create(this.getMaxHealth() * HariantConstants.HEALING_ON_PLAYER_ASSIST, HEALING_SOURCE_PLAYER_ASSIST));
        
        // Notify assist
        this.sendEliminationFeedback(EliminationFeedback.ASSIST, player);
    }
    
    @Override
    public void onDamageDealt(@NotNull DamageSource damageSource, @NotNull HariantEntity entity) {
        // Only start the cooldown if the damage is melee
        if (damageSource.getDamageType() == DamageType.MELEE) {
            this.startAttackCooldown(true);
        }
    }
    
    @Override
    public void onHealthChange(double previousHealth, double newHealth) {
        this.updateHealth0(newHealth, this.getMaxHealth(), this.getMaxVanillaHearts());
    }
    
    @Override
    public void onDeath(@NotNull DamageSource damageSource) {
        final Player player = getHandle();
        
        this.state = PlayerState.DEAD;
        player.setGameMode(GameMode.SPECTATOR);
        
        // Increment deaths for the team if the game is in progress
        fetchGameInstance(gameInstance -> gameInstance.onDeath(gameInstance, this));
        
        this.sendTitle(Component.text("ʏᴏᴜ ᴅɪᴇᴅ", Colors.ERROR, TextDecoration.BOLD), 5, 25, 10);
        this.playSound(Sound.ENTITY_BLAZE_DEATH, 1.0f);
        
        // Award eliminations & assists
        final List<? extends HariantEntity> assistingPlayers
                = combatTracker.assistingEntities()
                               // Filter self and lastAttacker
                               .filter(entity -> {
                                   // Skip self and last attacker
                                   return !entity.equals(this) && !entity.equals(lastAttacker);
                               })
                               .toList();
        
        // Call onAssist() on assisting players
        assistingPlayers.forEach(data -> data.onAssist(this));
        
        // Broadcast death message
        Bukkit.broadcast(damageSource.getIdentity().getDeathMessage().deathMessage(this, lastAttacker, assistingPlayers));
        
        // Show death
        this.sendDeathDamageReport();
    }
    
    @Override
    public void onShoot(@NotNull DamageSource damageSource) {
        this.startAttackCooldown(false);
    }
    
    @Override
    public final void onRemove(@Nullable RemovalReason removalReason) {
        // Don't remove players, just call `onDestroy()`
        this.onDestroy();
    }
    
    @Override
    public boolean isDead() {
        // Technically respawning means you are not alive, and if you are not alive then you are dead
        return this.state == PlayerState.DEAD || this.state == PlayerState.RESPAWNING;
    }
    
    @Override
    public boolean canSeeInvisible(@NotNull HariantEntity entity) {
        // If the entity is a player is NOT in SURVIVAL, we consider that we can't see them
        if (entity instanceof HariantPlayer player && player.getGameMode() != GameMode.SURVIVAL) {
            return false;
        }
        
        return getHandle().canSee(entity.getHandle());
    }
    
    @Override
    public boolean isInvisible() {
        // Consider spectators invisible
        final Player player = getHandle();
        
        if (player.getGameMode() == GameMode.SPECTATOR) {
            return true;
        }
        
        return super.isInvisible();
    }
    
    @Override
    public boolean canAttack(@NotNull HariantEntity entity, @NotNull DamageType damageType) {
        final Weapon weapon = heroInstance.getOrigin().getWeapon();
        
        // If the weapon is bow, and the damage type is RANGED, we always return true, because for ranged weapons the
        // cooldown determines whether you can shoot the weapon, not whether the damage can be dealt
        if (weapon instanceof WeaponBow && damageType == DamageType.RANGED) {
            return true;
        }
        
        // Otherwise, check for weapon cooldown
        if (weapon.hasCooldown(this)) {
            if (getSetting(Settings.ATTACK_COOLDOWN_FEEDBACK)) {
                playSound(Sound.BLOCK_LAVA_POP, 2.0f);
            }
            
            return false;
        }
        
        return true;
    }
    
    @NotNull
    @Override
    public Player getHandle() {
        return (Player) super.getHandle();
    }
    
    @Override
    public boolean tick() {
        if (!super.tick()) {
            return false;
        }
        
        // Tick hero data
        this.heroData.values().forEach(HeroData::tick);
        
        // Generate energy unless using ultimate
        final TalentUltimate ultimateTalent = heroInstance.getOrigin().getUltimateTalent();
        final UltimateResourceType resource = ultimateTalent.getUltimateResourceType();
        
        if (!this.isUsingUltimate() && ultimateResource < ultimateTalent.getMaximumCost()) {
            final double passiveRegeneration = resource.regeneratePassively();
            
            if (passiveRegeneration > 0.0) {
                incrementUltimateResource(passiveRegeneration);
            }
        }
        
        return true;
    }
    
    @Override
    public boolean shouldTick() {
        if (this.isDead()) {
            return false;
        }
        
        if (Hariant.isGameInProgressButNotActive()) {
            return false;
        }
        
        return true;
    }
    
    @Override
    public final String toString() {
        return entity.getName();
    }
    
    @NotNull
    @Override
    public Component getName() {
        return Hariant.getPlayerProfile(this.getHandle()).getName();
    }
    
    @Override
    public final boolean shouldRemove() {
        // Players should never be removed
        return false;
    }
    
    @Override
    public void onCreate() {
        this.state = PlayerState.ALIVE;
        
        this.resetArtifactModifiers();
        this.updateAttributes();
        
        // Update health after artifacts and modifiers
        this.health = this.getMaxHealth();
        
        this.resetPlayer();
        this.resetInventory();
        this.resetHotBar();
        this.setGameMode(GameMode.SURVIVAL);
        
        // Call PlayerLifecycle
        this.heroInstance.getOrigin().onCreate(this);
        
        // Call event
        new HariantPlayerCreateEvent(this).callEvent();
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        
        // Reset hero data
        this.heroData.values().forEach(HeroData::dispose);
        this.heroData.clear();
        
        this.resetPlayer();
        this.resetUltimate();
        
        // Call weapon PlayerLifecycle
        this.heroInstance.getOrigin().onDestroy(this);
        
        this.rechargeableTalentData.clear();
    }
    
    @Override
    public void playSound(@NotNull Sound sound, float volume, @Range(from = 0, to = 2) float pitch) {
        // Using entity as location parameter will play always the sound at entity location, even if the entity moves
        getHandle().playSound(entity, sound, soundCategory(), volume, Math.clamp(pitch, 0f, 2f));
    }
    
    @Override
    public void playSound(@NotNull Location location, @NotNull Sound sound, float volume, @Range(from = 0, to = 2) float pitch) {
        getHandle().playSound(location, sound, soundCategory(), volume, Math.clamp(pitch, 0f, 2f));
    }
    
    @Override
    public <T> void spawnParticle(@NotNull Location location, @NotNull Particle particle, int amount, double x, double y, double z, float speed, @Nullable T data) {
        getHandle().spawnParticle(particle, location, amount, x, y, z, speed, data);
    }
    
    @NotNull
    @Override
    public Component asHeadComponent() {
        // Return hero's head, not player's
        return heroInstance.getOrigin().asHeadComponent();
    }
    
    @Override
    public final void setCollision(@NotNull HariantPlayer player, boolean collision) throws Error {
        throw new IllegalStateException("Cannot set collision on a player");
    }
    
    @Override
    public void showWarning(@NotNull WarningType warningType, int duration) {
        this.sendTitle(warningType.asComponent(), 0, duration, 0);
    }
    
    @Override
    public void updateHealth(double maxHealth) {
        super.updateHealth(maxHealth);
        
        // Scale player's max hearts
        final double maxHearts = this.getMaxVanillaHearts();
        
        this.getVanillaAttribute(Attribute.MAX_HEALTH).setBaseValue(maxHearts);
        
        // Scale player's hearts
        this.updateHealth0(getFinalHealth(), maxHealth, maxHearts);
    }
    
    @Override
    public @NotNull Input getCurrentInput() {
        return this.getHandle().getCurrentInput();
    }
    
    @Override
    public void onCooldownStarted(@NotNull HariantCooldown cooldown, int duration) {
        // Flip -1 cooldown to something big that vanilla supports, 1,000,000 seem to work fine, and it's
        // equal to about 13 real life hours, so you're never seeing it move
        this.sendCooldownPacket(cooldown, duration == HariantConstants.INDEFINITE_COOLDOWN ? 1_000_000 : duration);
    }
    
    @Override
    public void onCooldownEnded(@NotNull HariantCooldown cooldown) {
        // Send the packet with 0 duration to reset the visual cooldown
        this.sendCooldownPacket(cooldown, 0);
    }
    
    public @NotNull String getEntityName() {
        return entity.getName();
    }
    
    public double getMaxVanillaHearts() {
        // Calculate max hearts over base max health
        final double maxHealth = Math.clamp(
                attributes.get(AttributeType.MAX_HEALTH) / attributes.base(AttributeType.MAX_HEALTH),
                0,
                1
        );
        
        return Math.max(maxHealth * HariantConstants.ABSOLUTE_MAX_HEALTH, HariantConstants.ABSOLUTE_MIN_HEALTH);
    }
    
    @NotNull
    public EnumTeam getPlayerTeam() {
        return profile.getTeam();
    }
    
    public void resetPlayer() {
        // Prepare bukkit entity
        final Player player = getHandle();
        
        for (Attribute attribute : Registry.ATTRIBUTE) {
            final AttributeInstance attributeInstance = player.getAttribute(attribute);
            
            // Remove all modifiers
            if (attributeInstance != null) {
                final Double defaultAttributeValue = DEFAULT_ATTRIBUTE_VALUES.get(attribute);
                
                // Set default attribute value
                if (defaultAttributeValue != null) {
                    attributeInstance.setBaseValue(defaultAttributeValue);
                }
                
                // Remove all modifiers
                attributeInstance.getModifiers().stream().map(AttributeModifier::getKey).forEach(attributeInstance::removeModifier);
            }
        }
        
        player.setHealth(HariantConstants.ABSOLUTE_MAX_HEALTH);
        player.setAbsorptionAmount(0.0);
        player.setFireTicks(0);
        player.setVisualFire(TriState.FALSE);
        player.setSaturation(0.0f);
        player.setFoodLevel(20);
        player.setInvulnerable(false);
        player.setArrowsInBody(0, false);
        player.setGlowing(false);
        player.closeInventory();
        player.clearActivePotionEffects();
        
        // Show the player
        this.show(StreamRules.ALL);
    }
    
    public void sendEliminationFeedback(@NotNull EliminationFeedback feedback, @NotNull HariantPlayer player) {
        if (this.getSetting(Settings.ELIMINATION_FEEDBACK)) {
            feedback.feedback(this, player);
        }
    }
    
    @NotNull
    public GameMode getGameMode() {
        return getHandle().getGameMode();
    }
    
    public void setGameMode(@NotNull GameMode mode) {
        getHandle().setGameMode(mode);
    }
    
    public final void resetInventory() {
        final PlayerInventory inventory = this.getInventory();
        
        // Clear inventory and snap to weapon
        inventory.clear();
        
        // Give hero equipment
        heroInstance.getOrigin().getEquipment().equip(this);
    }
    
    public final void resetHotBar() {
        final PlayerInventory inventory = this.getInventory();
        final Hero hero = heroInstance.getOrigin();
        
        this.clearHotBar();
        
        // Give talents items
        for (TalentIndex talentIndex : TalentIndex.ofActive()) {
            final Talent talent = hero.getTalent(talentIndex);
            
            inventory.setItem(talentIndex.getSlot(), talent.createItem());
        }
        
        // Give weapon
        heroInstance.getOrigin().giveWeapon(this);
    }
    
    public final void resetArtifactModifiers() {
        // Add affix modifier
        final Map<? extends @NotNull AttributeType, ? extends @NotNull Double> summedArtifactAffixes = heroInstance.sumArtifactAffixes();
        
        if (!summedArtifactAffixes.isEmpty()) {
            attributes.addModifier(new ArtifactAffixAttributeModifier(this, summedArtifactAffixes));
        }
        
        // Realistically we don't need to manually remove the attribute modifiers, since this is only called on `onCreate`, which is
        // either called whenever the game is started, or whenever the player respawns, which removes any modifiers, so we're good
        this.heroInstance.countArtifactSetPieces().forEach(((artifactSet, pieceCount) -> artifactSet.applyEffect(this, pieceCount)));
    }
    
    @NotNull
    public Hero getHero() {
        return heroInstance.getOrigin();
    }
    
    @NotNull
    public HeroInstance getHeroInstance() {
        return heroInstance;
    }
    
    public void sendPacket(@NotNull Packet<?> packet) {
        Reflect.sendPacket(getHandle(), packet);
    }
    
    public boolean compareHero(@NotNull Hero hero) {
        return heroInstance.getOrigin().equals(hero);
    }
    
    @NotNull
    public PlayerInventory getInventory() {
        return getHandle().getInventory();
    }
    
    @Override
    public @NotNull <I> I getSetting(@NotNull Setting<I> setting) {
        return profile.getSetting(setting);
    }
    
    @Override
    public <I> void setSetting(@NotNull Setting<I> setting, @NotNull I value) {
        profile.setSetting(setting, value);
    }
    
    public void respawn(int respawnIn) {
        // We can't actually check for DEAD state because the state is set after the
        // death event is called, so just make sure player isn't already respawning
        if (this.state == PlayerState.RESPAWNING) {
            return;
        }
        
        this.state = PlayerState.RESPAWNING;
        
        // Doesn't matter whether we delegate this task or not, since if the game ends all tasks are cancelled anyway
        new HariantTickingTask(Scheduler.ofTimer(10, 1)) {
            @Override
            public void run(int tick) {
                if (tick >= respawnIn) {
                    HariantPlayer.this.respawn();
                    cancel();
                    return;
                }
                
                // Fx
                sendTitleSubtitle(
                        Component.text("ʀᴇsᴘᴀᴡɴɪɴɢ", Colors.YELLOW, TextDecoration.BOLD),
                        Component.text(Tick.format(respawnIn - tick), Colors.TICK),
                        0, 25, 0
                );
                
                if (tick == 0 || this.modulo(20)) {
                    playSound(Sound.BLOCK_NOTE_BLOCK_PLING, 0.5f + (1.5f * ((float) tick / respawnIn)));
                }
            }
        };
    }
    
    public void respawn() {
        // Teleport to a random location
        Hariant.getCurrentGameInstance().ifPresent(gameInstance -> teleport(gameInstance.getBattleground().getRandomSpawnLocation()));
        
        // Call onCreate after teleport
        this.onCreate();
        
        // Add respawn resistance
        addEffect(StatusEffectType.RESPAWN_RESISTANCE, 40, this);
        
        // Fx
        addVanillaEffect(PotionEffectType.BLINDNESS, 1, 20);
        sendTitleSubtitle(Component.text("ʀᴇsᴘᴀᴡɴᴇᴅ", Colors.SUCCESS, TextDecoration.BOLD), Component.empty(), 0, 20, 5);
    }
    
    @NotNull
    public Component createSuffix() {
        return Component.empty()
                        .append(Component.text(" | ", Colors.DARK_GRAY))
                        .append(
                                Component.empty()
                                         .append(Component.text("%,.0f".formatted(getFinalHealth()), AttributeType.MAX_HEALTH.getStyle()))
                                         .appendSpace()
                                         .append(AttributeType.MAX_HEALTH.getPrefix().style(AttributeType.MAX_HEALTH.getStyle()))
                        )
                        .append(Component.text("   "))
                        .append(this.getHero().getUltimateTalent().getComponent(this));
    }
    
    public void tickActionbar() {
        final ActionbarBuilder builder = new ActionbarBuilder();
        final Hero hero = heroInstance.getOrigin();
        
        // Always append health and ultimate
        builder.append(this.getHealthFormatted());
        builder.append(hero.getUltimateTalent().getComponent(this));
        
        // Append hero actionbar
        final List<Component> heroComponents = hero.supplyActionbar(this);
        
        if (!heroComponents.isEmpty()) {
            for (Component component : heroComponents) {
                if (Component.IS_NOT_EMPTY.test(component)) {
                    builder.append(component);
                }
            }
        }
        
        // Append cache
        this.actionbarCache.stream().forEach(builder::append);
        
        this.sendActionBar(builder);
    }
    
    public void actionbar(@NotNull Class<?> origin, @NotNull Component component) {
        this.actionbarCache.add(origin, component);
    }
    
    @NotNull
    public Team getOrCreateNoCollisionTeam() {
        final Player player = this.getHandle();
        final Scoreboard scoreboard = player.getScoreboard();
        
        Team team = scoreboard.getTeam(NO_COLLISION_BUKKIT_TEAM);
        
        if (team == null) {
            team = scoreboard.registerNewTeam(NO_COLLISION_BUKKIT_TEAM);
            team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
        }
        
        return team;
    }
    
    public void resetUltimate() {
        this.ultimateResource = 0;
        this.usedUltimateAt = 0;
    }
    
    public void clearHotBar() {
        final PlayerInventory inventory = this.getInventory();
        
        for (int slot : HOT_BAR_SLOTS) {
            inventory.setItem(slot, null);
        }
    }
    
    public void setHotBarItem(@Range(from = 0, to = 8) int slot, @NotNull ItemStack itemStack) {
        this.getInventory().setItem(slot, itemStack);
    }
    
    public void snapTo(@Range(from = 0, to = 8) int slot) {
        this.getInventory().setHeldItemSlot(slot);
    }
    
    public void snapToWeapon() {
        this.snapTo(heroInstance.getOrigin().getWeaponSlot(this));
    }
    
    @Override
    public @NotNull Component getNameFormatted(@NotNull FormatRules formatRules) {
        return profile.getNameFormatted(formatRules);
    }
    
    @Override
    public @NotNull Component getNameFormatted() {
        return profile.getNameFormatted();
    }
    
    @Override
    public @NotNull Component getNameFormattedSocial() {
        return profile.getNameFormatted();
    }
    
    public @NotNull RechargeableTalentData getRechargeableTalentData(@NotNull TalentRechargeable talent) {
        return rechargeableTalentData.computeIfAbsent(talent, _ -> new RechargeableTalentData(this, talent));
    }
    
    private void sendCooldownPacket(@NotNull HariantCooldown cooldown, int duration) {
        final Identifier vanillaKey = Identifier.fromNamespaceAndPath(Eterna.getPlugin().namespace(), cooldown.getCooldownKey().getKey());
        
        this.sendPacket(new ClientboundCooldownPacket(vanillaKey, duration));
    }
    
    private void sendDeathDamageReport() {
        if (!getSetting(Settings.COMBAT_FEEDBACK)) {
            return;
        }
        
        sendMessage(
                Component.empty()
                         .append(PREFIX_DEATH)
                         .append(Component.text(" Damage report since last death ", Colors.GRAY))
                         .append(
                                 Component.empty()
                                          .append(Component.text("[Outgoing]", Colors.YELLOW, TextDecoration.UNDERLINED))
                                          .hoverEvent(combatTracker.createHoverEvent(CombatData.Type.OUTGOING))
                         )
                         .append(Component.text("  "))
                         .append(
                                 Component.empty()
                                          .append(Component.text("[Incoming]", Colors.YELLOW, TextDecoration.UNDERLINED))
                                          .hoverEvent(combatTracker.createHoverEvent(CombatData.Type.INCOMING))
                         )
        );
    }
    
    private <H extends Hero, D extends HeroData<H>> @Nullable D touchData0(@NotNull H hero, @NotNull Class<D> heroDataClass) {
        final HeroData<? extends Hero> data = this.heroData.get(hero.getClass());
        
        return heroDataClass.isInstance(data) ? heroDataClass.cast(data) : null;
    }
    
    private void updateHealth0(double health, double maxHealth, double maxHearts) {
        final double radio = Math.clamp(health / maxHealth, 0, 1);
        
        this.entity.setHealth(Math.max(radio * maxHearts, HariantConstants.ABSOLUTE_MIN_HEALTH));
    }
    
    private void fetchGameInstance(@NotNull Consumer<GameInstance> consumer) {
        Hariant.getCurrentGameInstance().ifPresent(consumer);
    }
    
    private void startAttackCooldown(boolean isMeleeAttack) {
        final Weapon weapon = heroInstance.getOrigin().getWeapon();
        
        if (isMeleeAttack) {
            weapon.startCooldown(this, weapon.getMeleeAttack().getAttackCooldown());
        }
        else {
            final NormalAttack rangedAttack = weapon.getRangedAttack();
            
            if (rangedAttack != null) {
                weapon.startCooldown(this, rangedAttack.getAttackCooldown());
            }
        }
    }
}