package me.hapyl.hariant.hero.shaman;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import me.hapyl.eterna.module.location.LocationHelper;
import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.NormalAttack;
import me.hapyl.hariant.entity.cooldown.Cooldown;
import me.hapyl.hariant.entity.damage.AssistSource;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.talent.Response;
import me.hapyl.hariant.talent.Talent;
import me.hapyl.hariant.talent.TalentContext;
import me.hapyl.hariant.talent.TalentType;
import me.hapyl.hariant.talent.target.TalentTarget;
import me.hapyl.hariant.talent.target.TalentTargetEntityRayCast;
import me.hapyl.hariant.task.HariantTickingTask;
import me.hapyl.hariant.task.Scheduler;
import me.hapyl.hariant.util.BoundingBoxBlueprint;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.decimal.Decimal;
import me.hapyl.hariant.util.field.DisplayField;
import me.hapyl.hariant.weapon.Weapon;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

public final class TalentImprisonment extends Talent implements Listener {
    
    private static final int[][] OFFSETS = {
            { -1, -2 }, { 2, -2 }, { 2, 1 }, { -1, 1 },
            { 0, -2 }, { 2, -1 }, { 1, 1 }, { -1, 0 },
            { 1, -2 }, { 2, 0 }, { 0, 1 }, { -1, -1 }
    };
    
    private static final List<? extends Material> WALLS = List.of(Material.COBBLESTONE_WALL, Material.MOSSY_COBBLESTONE_WALL);
    private static final BlockData BLOCK_DATA = Material.COBBLESTONE.createBlockData();
    
    private final @DisplayField Decimal maximumDistance = Decimal.ofValue(15);
    private final @DisplayField Decimal imprisonmentDelay = Decimal.ofSeconds(0.15f);
    private final @DisplayField Decimal buildingSpeed = Decimal.ofValue(4, v -> Component.text("%d Pillar/t".formatted((int) v)));
    private final @DisplayField Decimal prisonHealthOfMaxHealth = Decimal.ofPercentage(25);
    private final @DisplayField Decimal firePrisonDamageMultiplier = Decimal.ofPercentage(150);
    
    private final @DisplayField BoundingBoxBlueprint prisonSize = BoundingBoxBlueprint.define(1, 1.5, 1);
    
    private final Set<Imprisonment> globalImprisonments = Sets.newHashSet();
    
    private final Cooldown damageCooldown = Cooldown.create(Key.ofString("imprisonment_damage_cooldown"), Tick.ofSeconds(0.2f));
    
    public TalentImprisonment(@NotNull Key key) {
        super(key, Component.text("Imprisonment"), Icon.ofMaterial(Material.COBBLESTONE_WALL));
        
        setTalentType(TalentType.IMPAIR);
        
        setCooldown(Tick.ofSeconds(16));
        setDuration(Tick.ofSeconds(6));
        
        setDescription(
                Component.empty()
                         .append(Component.text("Imprison the "))
                         .append(Component.text("target", Colors.YELLOW))
                         .append(Component.text(" enemy in a cage, "))
                         .append(Component.text("impairing", Colors.ARCHETYPE_HEXBANE))
                         .append(Component.text(" their movement."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("The cage inherits a percentage of "))
                         .append(HeroShaman.SHAMAN_S)
                         .appendSpace()
                         .append(AttributeType.MAX_HEALTH)
                         .append(Component.text(", can be destroyed by the prisoner and is weak against "))
                         .append(ElementType.FIRE)
                         .append(Component.text("."))
        );
    }
    
    @EventHandler
    public void handlePlayerInteractEvent(PlayerInteractEvent ev) {
        if (ev.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }
        
        final Block clickedBlock = ev.getClickedBlock();
        
        if (clickedBlock == null) {
            return;
        }
        
        if (!WALLS.contains(clickedBlock.getType())) {
            return;
        }
        
        final HariantPlayer player = Hariant.getPlayerOrNull(ev.getPlayer());
        
        if (player == null) {
            return;
        }
        
        for (Imprisonment imprisonment : globalImprisonments) {
            if (!imprisonment.affectedBlocks.contains(clickedBlock)) {
                continue;
            }
            
            imprisonment.damage(player, clickedBlock);
            break;
        }
    }
    
    @Override
    public @NotNull TalentTarget target(@NotNull HariantPlayer player) {
        return TalentTarget.targetEntity(maximumDistance.doubleValue(), 0.7, TalentTargetEntityRayCast.BlockCollision.ALLOW_PASSABLE, TalentTargetEntityRayCast.EntityPriority.PLAYER_PRIORITY, player::canAffect);
    }
    
    @Override
    public @NotNull Response execute(@NotNull HariantPlayer player, @NotNull TalentContext context) {
        final HariantEntity entity = context.retrieve(HariantEntity.class);
        
        // Check for Effect RES
        if (entity.hasEffectResistance(AssistSource.create(player, this))) {
            player.messageError(entity.getName().append(Component.text(" has resisted your ").append(this.getName()).append(Component.text("!"))));
            return Response.ok();
        }
        
        new Imprisonment(player, entity);
        
        return Response.ok();
    }
    
    public class Imprisonment extends HariantTickingTask {
        
        private final HariantPlayer player;
        private final HariantEntity prisoner;
        private final Location origin;
        
        private final List<Block> affectedBlocks;
        
        private double health;
        private int index;
        
        Imprisonment(@NotNull HariantPlayer player, @NotNull HariantEntity prisoner) {
            super(Scheduler.ofTimer(imprisonmentDelay.intValue(), 1));
            
            this.player = player;
            this.prisoner = prisoner;
            this.origin = prisoner.getLocation();
            this.affectedBlocks = Lists.newArrayList();
            this.health = Math.max(1, player.getMaxHealth() * prisonHealthOfMaxHealth.doubleValue());
            
            // Sfx
            player.playWorldSound(origin, Sound.ENTITY_IRON_GOLEM_REPAIR, 0.75f);
            
            // Reference in the global lookup
            globalImprisonments.add(this);
        }
        
        public void damage(@NotNull HariantPlayer damager, @NotNull Block block) {
            // Only allow the prisoner to damage the case
            if (!damager.equals(prisoner)) {
                return;
            }
            
            if (damager.hasCooldown(damageCooldown)) {
                return;
            }
            
            damager.startCooldown(damageCooldown);
            
            // Kinda wonky way of using this, but we're just using the scaling from the normal attack
            final Weapon weapon = damager.getHero().getWeapon();
            final NormalAttack meleeAttack = weapon.getMeleeAttack();
            
            double damage = meleeAttack.getScaledValue(player);
            
            // Scale damage if the normal attack is FIRE
            if (meleeAttack.getElementType() == ElementType.FIRE) {
                damage *= firePrisonDamageMultiplier.doubleValue();
            }
            
            health -= damage;
            
            // Show the remaining prison health to the prisoner
            if (health > 0) {
                damager.showTitle(Title.title(Component.empty(), Component.text("%,.0f ❤".formatted(health), Colors.RED), 0, 10, 5));
            }
            else {
                damager.showTitle(Title.title(Component.empty(), Component.text("Escaped!", Colors.GOLD), 0, 15, 5));
            }
            
            // Sfx
            damager.playWorldSound(block.getLocation(), Sound.ENTITY_IRON_GOLEM_DAMAGE, 0.75f);
            damager.playWorldSound(block.getLocation(), Sound.BLOCK_STONE_BREAK, 0.75f);
        }
        
        @Override
        public void onCancel() {
            super.onCancel();
            
            // Clear affected blocks
            for (Block block : affectedBlocks) {
                block.setType(Material.AIR, false);
            }
            
            affectedBlocks.clear();
            
            // Sfx
            player.playWorldSound(origin, Sound.ENTITY_IRON_GOLEM_DAMAGE, 0.75f);
            player.spawnWorldParticle(origin, Particle.CRIT, 20, 1, 1, 1, 1);
            player.spawnWorldParticle(origin, Particle.BLOCK, 20, 1, 1, 1, 1, BLOCK_DATA);
            
            // Re-deference in the global lookup
            globalImprisonments.remove(this);
        }
        
        @Override
        public void run(int tick) {
            // If index is less than offsets, build the prison
            if (index < OFFSETS.length) {
                for (int i = 0; i < buildingSpeed.intValue() && index < OFFSETS.length; i++) {
                    final int x = OFFSETS[index][0];
                    final int z = OFFSETS[index][1];
                    
                    final Location location = LocationHelper.copyOf(origin).add(x, 0, z);
                    
                    // Find the top Y by adding 3 to the feet location
                    final int topY = Math.min((int) location.getY() + 3, player.getWorld().getMaxHeight() - 1);
                    
                    // Find the bottom Y by rooting down until we hit something
                    Block block = location.getBlock();
                    
                    do {
                        block = block.getRelative(BlockFace.DOWN);
                    }
                    while (block.getY() > 0 && !block.getType().isSolid());
                    
                    final int bottomY = block.getY() + 1;
                    
                    createPillar(location, bottomY, topY);
                    
                    // Default the Y to the feet level for sfx
                    location.setY(topY - 3);
                    
                    // Sfx
                    player.playWorldSound(location, Sound.BLOCK_PISTON_EXTEND, 0.75f);
                    player.playWorldSound(location, Sound.BLOCK_NETHERITE_BLOCK_PLACE, 0.75f);
                    
                    // Increment index
                    index++;
                }
                return;
            }
            
            // Else tick the prison
            if (tick > duration() || health <= 0) {
                this.cancel();
            }
            
        }
        
        private void createPillar(@NotNull Location location, int bottomY, int topY) {
            int y = bottomY;
            
            while (y < topY) {
                location.setY(y++);
                
                final Block block = location.getBlock();
                
                // Only allow air blocks
                if (block.getType().isAir()) {
                    affectedBlocks.add(block);
                    
                    block.setType(player.random.choice(WALLS), false);
                }
            }
        }
        
    }
    
}