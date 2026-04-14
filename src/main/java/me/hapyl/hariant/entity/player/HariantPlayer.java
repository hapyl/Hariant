package me.hapyl.hariant.entity.player;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.reflect.Reflect;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.HariantConstants;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.NormalAttack;
import me.hapyl.hariant.entity.PlayerState;
import me.hapyl.hariant.entity.WarningType;
import me.hapyl.hariant.entity.cooldown.CooldownHandler;
import me.hapyl.hariant.entity.damage.DamageSource;
import me.hapyl.hariant.entity.damage.DamageType;
import me.hapyl.hariant.entity.damage.tracker.CombatData;
import me.hapyl.hariant.entity.heal.HealingSource;
import me.hapyl.hariant.event.HariantPlayerRespawnEvent;
import me.hapyl.hariant.game.GameInstance;
import me.hapyl.hariant.hero.Hero;
import me.hapyl.hariant.hero.HeroData;
import me.hapyl.hariant.hero.HeroDataSupplier;
import me.hapyl.hariant.hero.HeroInstance;
import me.hapyl.hariant.profile.PlayerProfile;
import me.hapyl.hariant.profile.setting.Setting;
import me.hapyl.hariant.profile.setting.Settings;
import me.hapyl.hariant.profile.ui.ActionbarBuilder;
import me.hapyl.hariant.talent.Talent;
import me.hapyl.hariant.talent.TalentIndex;
import me.hapyl.hariant.talent.ultimate.RegenerationRule;
import me.hapyl.hariant.talent.ultimate.TalentUltimate;
import me.hapyl.hariant.talent.ultimate.TalentUltimateResource;
import me.hapyl.hariant.task.Cancellable;
import me.hapyl.hariant.task.HariantTickingTask;
import me.hapyl.hariant.task.Scheduler;
import me.hapyl.hariant.team.EnumTeam;
import me.hapyl.hariant.weapon.Weapon;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.TitlePart;
import net.kyori.adventure.util.TriState;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundCooldownPacket;
import net.minecraft.resources.Identifier;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class HariantPlayer extends HariantEntity implements CooldownHandler, HeroDataRetriever {
    
    public static final Map<Attribute, Double> DEFAULT_ATTRIBUTE_VALUES = Map.of(
            Attribute.MAX_HEALTH, HariantConstants.ABSOLUTE_MAX_HEALTH,
            Attribute.ARMOR, -100.0,       // This will remove armor bars, which are ugly and useless
            Attribute.ATTACK_SPEED, 2.0,   // This will remove the damage indicator
            Attribute.MOVEMENT_SPEED, 0.1, // This will ensure speed is reset properly
            Attribute.SAFE_FALL_DISTANCE, HariantConstants.FALL_DAMAGE_SAFE_FALL_DISTANCE
    );
    
    private static final String NO_COLLISION_BUKKIT_TEAM = "no_collision";
    private static final int[] HOT_BAR_SLOTS = { 0, 1, 2, 3, 4, 5, 6, 7, 8 };
    
    private final PlayerProfile profile;
    private final HeroInstance heroInstance;
    private final Set<Cancellable> delegatedCancellable;
    private final ActionbarCache actionbarCache;
    
    private final Map<Class<? extends Hero>, HeroData<? extends Hero>> heroData;
    
    private double ultimateResource;
    private int usedUltimateAt;
    
    private PlayerState state;
    private HeartStyle heartStyle;
    
    public HariantPlayer(@NotNull PlayerProfile profile, @NotNull Player player, @NotNull HeroInstance heroInstance) {
        super(player, heroInstance.getOrigin().getAttributes());
        
        this.profile = profile;
        this.heroInstance = heroInstance;
        this.delegatedCancellable = Sets.newHashSet();
        this.heroData = Maps.newHashMap();
        this.state = PlayerState.ALIVE;
        this.actionbarCache = new ActionbarCache();
        this.heartStyle = null;
    }
    
    @NotNull
    public PlayerProfile getProfile() {
        return profile;
    }
    
    @SuppressWarnings("unchecked")
    @NotNull
    public <H extends Hero, D extends HeroData<H>> D getHeroData(@NotNull H hero, @NotNull HeroDataSupplier<H, D> supplier) {
        return (D) this.heroData.computeIfAbsent(hero.getClass(), heroClass -> supplier.supply(hero, this));
    }
    
    public <H extends Hero> boolean hasHeroData(@NotNull H hero) {
        return heroData.containsKey(hero.getClass());
    }
    
    public boolean isUsingUltimate() {
        return usedUltimateAt > 0;
    }
    
    public void setUsingUltimate(boolean usingUltimate) {
        this.usedUltimateAt = usingUltimate ? this.getTicksAlive() : 0;
    }
    
    public int getUsedUltimateAt() {
        return usedUltimateAt;
    }
    
    /**
     * Increments the ultimate resource for this player.
     *
     * <p>
     * Note that this method computes the absolute value and multiplies it by the {@link RegenerationRule#getEffectiveAttribute()}, if
     * it exists.
     * </p>
     *
     * <p><b>Do not use this method to decrement the resource, use {@link #decrementUltimateResource(double)} instead!</b></p>
     *
     * @param value - The value by which to increment.
     */
    public void incrementUltimateResource(final double value) {
        double absoluteValue = Math.abs(value);
        
        final Hero hero = heroInstance.getOrigin();
        final TalentUltimate ultimateTalent = hero.getUltimateTalent();
        final TalentUltimateResource resource = ultimateTalent.getResource();
        
        final AttributeType effectiveAttribute = resource.getEffectiveAttribute();
        
        if (effectiveAttribute != null) {
            absoluteValue *= attributes.normalized(effectiveAttribute);
        }
        
        final double previousUltimateResource = ultimateResource;
        
        this.ultimateResource = Math.min(ultimateTalent.getResourceCost(), ultimateResource + absoluteValue);
        
        ultimateTalent.onResourceValue(this, previousUltimateResource, ultimateResource);
    }
    
    public void decrementUltimateResource(final double value) {
        final double absoluteValue = Math.abs(value);
        
        this.ultimateResource = Math.max(0, ultimateResource - absoluteValue);
    }
    
    public void chargeUltimate() {
        this.incrementUltimateResource(heroInstance.getOrigin().getUltimateTalent().getResourceCost());
    }
    
    public double getUltimateResource() {
        return ultimateResource;
    }
    
    @Override
    public void onCooldownChange(@NotNull Key key, int cooldown) {
        final NamespacedKey bukkitKey = key.asNamespacedKey();
        final int duration = cooldown == HariantConstants.INDEFINITE_COOLDOWN ? 1_000_000 : cooldown;
        
        this.sendPacket(new ClientboundCooldownPacket(Identifier.fromNamespaceAndPath(bukkitKey.getNamespace(), bukkitKey.getKey()), duration));
    }
    
    @NotNull
    @Override
    public final NormalAttack getMeleeAttack() {
        return heroInstance.getOrigin().getWeapon(this).getMeleeAttack();
    }
    
    @Nullable
    @Override
    public final NormalAttack getRangedAttack() {
        return heroInstance.getOrigin().getWeapon(this).getRangedAttack();
    }
    
    @Override
    public boolean isPersistent() {
        return true;
    }
    
    @Override
    public boolean heal(@NotNull HealingSource healingSource) {
        final boolean healed = super.heal(healingSource);
        
        // Add regeneration effect so it's obvious that the player is healed
        if (healed) {
            this.addVanillaEffect(PotionEffectType.REGENERATION, 0, 25);
        }
        
        return healed;
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
            final double energyRegenerationOnElimination = heroInstance.getOrigin().getUltimateTalent().getResource().regenerateOnElimination();
            
            this.incrementUltimateResource(energyRegenerationOnElimination);
            
            // Heal
            this.heal(HealingSource.create(this.getMaxHealth() * HariantConstants.HEALING_ON_PLAYER_ELIMINATION));
            
            // Reward for kill
            //CommonRewards.PLAYER_ELIMINATION.reward(profile);
        }
    }
    
    @Override
    public void onAssist(@NotNull HariantPlayer player) {
        this.heal(HealingSource.create(this.getMaxHealth() * HariantConstants.HEALING_ON_PLAYER_ASSIST));
        
        // Notify assist
        EliminationFeedback.ASSIST.feedback(this, player);
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
        super.onHealthChange(previousHealth, newHealth);
        
        this.updateHealth0(newHealth, this.getMaxHealth(), this.getMaxHearts());
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
        final List<? extends CombatData> assistingPlayers
                = this.combatTracker.assistingPlayers()
                                    // Filter self and lastAttacker
                                    .filter(data -> {
                                        final HariantEntity entity = data.getEntity();
                                        
                                        return !entity.equals(this) && !entity.equals(lastAttacker);
                                    })
                                    .toList();
        
        // Call onAssist() on assisting players
        assistingPlayers.forEach(data -> data.getEntity().onAssist(this));
        
        // Broadcast death message
        Bukkit.broadcast(damageSource.getIdentity().getDeathMessage().deathMessage(this, lastAttacker, assistingPlayers));
    }
    
    @Override
    public void onShoot(@NotNull DamageSource damageSource) {
        this.startAttackCooldown(false);
    }
    
    @Override
    public boolean isDead() {
        // Technically respawning means you are not alive, and if you are not alive then you are dead
        return this.state == PlayerState.DEAD || this.state == PlayerState.RESPAWNING;
    }
    
    @Override
    public boolean canSee(@NotNull HariantEntity entity) {
        // If the entity is a player is NOT in SURVIVAL, we consider that we can't see them
        if (entity instanceof HariantPlayer player && player.getGameMode() != GameMode.SURVIVAL) {
            return false;
        }
        
        return getHandle().canSee(entity.getHandle());
    }
    
    @Override
    public boolean canAttack(@NotNull HariantEntity entity) {
        // Check for weapon cooldown
        final Weapon weapon = heroInstance.getOrigin().getWeapon();
        
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
    public void tick() {
        super.tick();
        
        // Always tick heart style
        if (heartStyle != null) {
            heartStyle.tick();
            heartStyle.apply(this);
            
            if (heartStyle.isOver()) {
                heartStyle = null;
            }
        }
        
        if (!this.shouldActuallyTick()) {
            return;
        }
        
        // Tick hero data
        heroData.values().forEach(HeroData::tick);
        
        // Generate energy unless using ultimate
        final TalentUltimate ultimateTalent = heroInstance.getOrigin().getUltimateTalent();
        final TalentUltimateResource resource = ultimateTalent.getResource();
        
        if (!this.isUsingUltimate() && ultimateResource < ultimateTalent.getResourceCost()) {
            final double passiveRegeneration = resource.regeneratePassively();
            
            if (passiveRegeneration > 0.0) {
                incrementUltimateResource(passiveRegeneration);
            }
        }
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
    public void onCreate() {
        this.state = PlayerState.ALIVE;
        
        this.resetPlayer();
        this.updateAttributes();
        this.resetArtifactModifiers();
        this.resetInventory();
        this.resetHotBar();
        this.setGameMode(GameMode.SURVIVAL);
        
        // Call PlayerLifecycle
        heroInstance.getOrigin().getWeapon().onCreate(this);
    }
    
    @Override
    public void onDestroy() {
        // Reset `lastAttacker` because players can't die
        this.lastAttacker = null;
        
        // Cancel delegated tasks
        this.delegatedCancellable.forEach(Cancellable::cancel);
        this.delegatedCancellable.clear();
        
        // Reset hero data
        this.heroData.values().forEach(HeroData::dispose);
        this.heroData.clear();
        
        this.resetPlayer();
        this.resetUltimate();
        
        // Reset health
        this.health = this.getMaxHealth();
        
        // Call weapon PlayerLifecycle
        this.heroInstance.getOrigin().getWeapon().onDestroy(this);
        
        this.ticker.reset();
        this.attributes.reset();
        this.combatTracker.reset();
        this.effectMap.resetEffects();
        this.cooldownHandler.resetCooldowns();
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
    public void showWarning(@NotNull WarningType warningType, int duration) {
        this.sendTitle(warningType.asComponent(), 0, duration, 0);
    }
    
    @Override
    public void updateHealth(double maxHealth) {
        super.updateHealth(maxHealth);
        
        // Scale player's max hearts
        final double maxHearts = this.getMaxHearts();
        
        this.getVanillaAttribute(Attribute.MAX_HEALTH).setBaseValue(maxHearts);
        
        // Scale player's hearts
        this.updateHealth0(health, maxHealth, maxHearts);
    }
    
    public double getMaxHearts() {
        // Calculate max hearts over base max health
        final double absoluteMaxHealth = attributes.get(AttributeType.MAX_HEALTH);
        final double baseMaxHealth = attributes.base(AttributeType.MAX_HEALTH);
        
        final double maxHealth = Math.clamp(absoluteMaxHealth / baseMaxHealth, 0, 1);
        
        return Math.max(maxHealth * HariantConstants.ABSOLUTE_MAX_HEALTH, 0.5);
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
            
            if (attributeInstance != null) {
                // Remove all modifiers
                attributeInstance.getModifiers().stream().map(AttributeModifier::getKey).forEach(attributeInstance::removeModifier);
            }
        }
        
        // Reset default vanilla attributes
        DEFAULT_ATTRIBUTE_VALUES.forEach((attribute, value) -> {
            this.getVanillaAttribute(attribute).setBaseValue(value);
        });
        
        player.setHealth(HariantConstants.ABSOLUTE_MAX_HEALTH);
        player.setAbsorptionAmount(0.0);
        player.setFireTicks(0);
        player.setVisualFire(TriState.FALSE);
        player.setSaturation(0.0f);
        player.setFoodLevel(20);
        player.setInvulnerable(false);
        player.setArrowsInBody(0, false);
        player.setGlowing(false);
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
    
    public void delegate(@NotNull Cancellable cancellable) {
        this.delegatedCancellable.add(cancellable);
    }
    
    @NotNull
    public PlayerInventory getInventory() {
        return getHandle().getInventory();
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
    
    @NotNull
    public <I> I getSetting(@NotNull Setting<I> setting) {
        return profile.getDatabase().settings.getValue(setting);
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
                        Component.text("ʀᴇsᴘᴀᴡɴɪɴɢ", NamedTextColor.YELLOW, TextDecoration.BOLD),
                        Component.text(Tick.format(respawnIn - tick), Colors.FORMAT_TICK),
                        0, 25, 0
                );
                
                if (tick == 0 || this.modulo(20)) {
                    playSound(Sound.BLOCK_NOTE_BLOCK_PLING, 0.5f + (1.5f * ((float) tick / respawnIn)));
                }
            }
        };
    }
    
    public void respawn() {
        this.onCreate();
        
        // Teleport to a random location
        Hariant.getCurrentGameInstance().ifPresent(gameInstance -> teleport(gameInstance.getBattleground().getRandomSpawnLocation()));
        
        // TODO @Feb 24, 2026 (xanyjl) -> Add respawn resistance effect
        
        // Fx
        this.addVanillaEffect(PotionEffectType.BLINDNESS, 1, 20);
        this.sendTitleSubtitle(Component.text("ʀᴇsᴘᴀᴡɴᴇᴅ", Colors.SUCCESS, TextDecoration.BOLD), Component.empty(), 0, 20, 5);
        
        // Call respawn event
        new HariantPlayerRespawnEvent(this).callEvent();
    }
    
    @NotNull
    public Component createSuffix() {
        return Component.empty()
                        .append(Component.text(" | ", NamedTextColor.DARK_GRAY))
                        .append(
                                Component.empty()
                                         .append(Component.text("%,.0f".formatted(health), AttributeType.MAX_HEALTH.getStyle()))
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
    
    @NotNull
    public Input getInput() {
        return this.getHandle().getCurrentInput();
    }
    
    @Nullable
    public HeartStyle getHeartStyle() {
        return heartStyle;
    }
    
    public void setHeartStyle(@Nullable HeartStyle heartStyle) {
        this.heartStyle = heartStyle;
    }
    
    private void updateHealth0(double health, double maxHealth, double maxHearts) {
        // Player health is always scaled of `maxHearts`
        this.entity.setHealth(Math.max(health / maxHealth * maxHearts, HariantConstants.ABSOLUTE_MIN_HEALTH));
    }
    
    private void fetchGameInstance(@NotNull Consumer<GameInstance> consumer) {
        Hariant.getCurrentGameInstance().ifPresent(consumer);
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