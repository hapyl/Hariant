package me.hapyl.hariant.hero.blast_knight;

import io.papermc.paper.event.player.PlayerStopUsingItemEvent;
import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.cooldown.HariantCooldown;
import me.hapyl.hariant.entity.effect.status.StatusEffectType;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.event.HariantAttackEvent;
import me.hapyl.hariant.hero.HeroRegistry;
import me.hapyl.hariant.talent.TalentPassive;
import me.hapyl.hariant.talent.field.DisplayField;
import me.hapyl.hariant.util.BoundingBoxBlueprint;
import me.hapyl.hariant.util.Definition;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ShieldMeta;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class TalentQuantumShield extends TalentPassive implements Listener {
    
    public final ItemStack shieldItem;
    
    private final @DisplayField Decimal maximumQuantumEnergy = Decimal.ofValue(15);
    
    private final @DisplayField Decimal shieldBlockingAngle = Decimal.ofValue(60, value -> Component.text("%.0f °".formatted(value)));
    
    private final @DisplayField Decimal parryWindow = Decimal.ofSeconds(0.5f);
    private final @DisplayField Decimal parryCooldownReduction = Decimal.ofPercentage(50);
    private final @DisplayField Decimal parryStunDuration = Decimal.ofSeconds(3f);
    private final @DisplayField Decimal parryQuantumEnergyGeneration = Decimal.ofValue(5);
    
    private final @DisplayField Decimal blockingQuantumEnergyGeneration = Decimal.ofValue(1);
    private final @DisplayField HariantCooldown blockingQuantumEnergyGenerationCooldown = HariantCooldown.ofSeconds(Key.ofString("quantum_energy_generation_cooldown"), 0.2f);
    
    private final @DisplayField BoundingBoxBlueprint parryBoundingBox = BoundingBoxBlueprint.define(1, 2.5, 1);
    
    public TalentQuantumShield(@NotNull Key key) {
        super(key, Component.text("Quantum Shield"), Icon.ofMaterial(Material.SHIELD));
        
        this.shieldItem = createShieldBuilder(
                DyeColor.BLACK, List.of(
                        new Pattern(DyeColor.WHITE, PatternType.STRIPE_DOWNLEFT),
                        new Pattern(DyeColor.PURPLE, PatternType.RHOMBUS),
                        new Pattern(DyeColor.BLACK, PatternType.STRIPE_DOWNLEFT),
                        new Pattern(DyeColor.PINK, PatternType.CIRCLE),
                        new Pattern(DyeColor.BLACK, PatternType.FLOWER)
                )).setName(this.getName().color(Colors.LIGHT_PURPLE))
                  .asIcon();
        
        setCooldownSeconds(5);
        
        setDescription(
                Component.empty()
                         .append(Component.text("Raise your shield to block incoming damage, converting it into "))
                         .append(Definition.QUANTUM_ENERGY)
                         .append(Component.text("."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("Parry", Colors.GOLD))
                         .appendNewline()
                         .append(Component.text("Releasing the shield within a "))
                         .append(Component.text("brief window", Colors.AQUA))
                         .append(Component.text(" after raising it while blocking damage triggers a "))
                         .append(Component.text("parry", Colors.GOLD))
                         .append(Component.text(" that stuns enemies and generates "))
                         .append(parryQuantumEnergyGeneration)
                         .appendSpace()
                         .append(Definition.QUANTUM_ENERGY)
                         .append(Component.text("."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("Additionally, the cooldown is reduced by "))
                         .append(parryCooldownReduction)
                         .append(Component.text("."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("Cooldown of this talent starts whenever the shield is released.", Colors.DARK_GRAY))
        );
        
    }
    
    public @NotNull Decimal getMaximumQuantumEnergy() {
        return maximumQuantumEnergy;
    }
    
    @EventHandler
    public void handlePlayerInteractEvent(PlayerInteractEvent ev) {
        final HariantPlayer player = Hariant.getPlayer(ev.getPlayer()).orElse(null);
        final ItemStack item = ev.getItem();
        
        if (player == null || item == null || ev.getHand() == EquipmentSlot.HAND) {
            return;
        }
        
        final HeroDataBlastKnight heroData = getBlastKnightDataIfPlayerIsBlastKnightAndItemIsShieldAndNotOnCooldownOrElseNull(player, item);
        
        if (heroData == null) {
            return;
        }
        
        player.getHeroData(HeroRegistry.BLAST_KNIGHT, HeroDataBlastKnight::new).setShieldState(ShieldState.BLOCKING);
    }
    
    @EventHandler
    public void handlePlayerStopUsingItemEvent(PlayerStopUsingItemEvent ev) {
        final HariantPlayer player = Hariant.getPlayer(ev.getPlayer()).orElse(null);
        final ItemStack item = ev.getItem();
        
        if (player == null) {
            return;
        }
        
        final HeroDataBlastKnight heroData = getBlastKnightDataIfPlayerIsBlastKnightAndItemIsShieldAndNotOnCooldownOrElseNull(player, item);
        
        if (heroData == null) {
            return;
        }
        
        // If the player has blocked the damage, check if we can parry
        boolean hasParried = false;
        
        if (heroData.getShieldState() == ShieldState.BLOCKED) {
            final int ticksHeldFor = ev.getTicksHeldFor();
            
            if (ticksHeldFor < parryWindow.intValue()) {
                hasParried = true;
                this.parry(player);
                
                // Increment quantum energy
                heroData.incrementQuantumEnergy(parryQuantumEnergyGeneration.intValue());
            }
        }
        
        // Reset shield
        heroData.setShieldState(ShieldState.NOT_BLOCKING);
        
        // Start cooldown
        this.startCooldown(player, hasParried);
    }
    
    // Make sure to run event as LOWEST priority so the block is the first thing processed
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void handleHariantAttackEvent(HariantAttackEvent ev) {
        final HariantEntity attacker = ev.getAttacker();
        final HariantEntity entity = ev.getEntity();
        
        if (!(entity instanceof HariantPlayer player)) {
            return;
        }
        
        if (!player.getHero().equals(HeroRegistry.BLAST_KNIGHT)) {
            return;
        }
        
        if (attacker.isSelfOrTeammate(entity)) {
            return;
        }
        
        final HeroDataBlastKnight heroData = player.getHeroData(HeroRegistry.BLAST_KNIGHT, HeroDataBlastKnight::new);
        
        if (heroData.getShieldState() == ShieldState.NOT_BLOCKING) {
            return;
        }
        
        // Check for blocking angle
        final Vector directionAttacker = attacker.getLocation().toVector().subtract(entity.getLocation().toVector()).normalize();
        final Vector directionEntity = entity.getLocation().getDirection().normalize();
        
        final double degrees = Math.toDegrees(Math.acos(directionAttacker.dot(directionEntity)));
        
        if (degrees > shieldBlockingAngle.doubleValue()) {
            return;
        }
        
        // Block the damage
        ev.setCancelled(true);
        
        heroData.setShieldState(ShieldState.BLOCKED);
        heroData.incrementQuantumEnergyIfNotOnCooldown(blockingQuantumEnergyGeneration, blockingQuantumEnergyGenerationCooldown);
        
        // Fx
        player.playWorldSound(Sound.ITEM_SHIELD_BLOCK, 1.0f);
    }
    
    public void startCooldown(@NotNull HariantPlayer player, boolean hasParried) {
        // We have to start the vanilla cooldown because it's a vanilla item
        player.getHandle().setCooldown(
                Material.SHIELD,
                (int) (this.getCooldown() * (hasParried ? parryCooldownReduction.doubleValue() : 1.0))
        );
    }
    
    public void parry(@NotNull HariantPlayer player) {
        final Location location = player.getLocationInFront(1);
        
        player.collectNearbyEntities(parryBoundingBox.create(location))
              .filter(player::canAffect)
              .forEach(entity -> {
                  // Stun entity
                  entity.addEffect(StatusEffectType.STUNNED, parryStunDuration, player);
              });
        
        // Fx
        player.playWorldSound(Sound.ENTITY_BREEZE_WIND_BURST, 0.0f);
        player.playWorldSound(Sound.ENTITY_COPPER_GOLEM_DEATH, 0.0f);
        
        player.spawnWorldParticle(location, Particle.ENCHANTED_HIT, 10, 0.5, 0.5, 0.5, 0.125f);
    }
    
    private @Nullable HeroDataBlastKnight getBlastKnightDataIfPlayerIsBlastKnightAndItemIsShieldAndNotOnCooldownOrElseNull(@NotNull HariantPlayer player, @NotNull ItemStack itemStack) {
        if (!player.getHero().equals(HeroRegistry.BLAST_KNIGHT)) {
            return null;
        }
        // We don't really care about checking for keys or whatever, material check if fine
        else if (itemStack.getType() != Material.SHIELD) {
            return null;
        }
        // If player has cooldown, also return null, since we can neither block nor release blocking
        else if (player.getHandle().hasCooldown(Material.SHIELD)) {
            return null;
        }
        
        return player.getHeroData(HeroRegistry.BLAST_KNIGHT, HeroDataBlastKnight::new);
    }
    
    public static @NotNull ItemBuilder createShieldBuilder(@NotNull DyeColor baseColor, @NotNull List<Pattern> patterns) {
        return new ItemBuilder(Material.SHIELD).editMeta(ShieldMeta.class, meta -> {
            meta.setBaseColor(baseColor);
            meta.setPatterns(patterns);
        });
    }
    
}
