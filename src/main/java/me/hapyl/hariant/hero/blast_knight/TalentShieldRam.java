package me.hapyl.hariant.hero.blast_knight;

import com.google.common.collect.Sets;
import me.hapyl.eterna.module.location.LocationHelper;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.effect.status.StatusEffectType;
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
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public final class TalentShieldRam extends Talent {
    
    private static final Vector UP = new Vector(0, 1, 0);
    
    private final @DisplayField Decimal stunDuration = Decimal.ofSeconds(3f);
    private final @DisplayField Decimal maximumDistance = Decimal.ofValue(4);
    
    private final @DisplayField Decimal slamDelay = Decimal.ofSeconds(0.1f);
    private final @DisplayField Decimal slamPeriod = Decimal.ofSeconds(0.2f);
    
    private final @DisplayField BoundingBoxBlueprint bashBoundingBox = BoundingBoxBlueprint.define(0.5, 1.5, 0.5);
    
    public TalentShieldRam(@NotNull Key key) {
        super(key, Component.text("Shield Slam"), Icon.ofMaterial(Material.GOAT_HORN));
        
        setCooldownSeconds(12);
        setTalentType(TalentType.IMPAIR);
        
        setDescription(
                Component.empty()
                         .append(Component.text("Slam your shield into the ground, launching a "))
                         .append(Component.text("quake", Colors.ORANGE))
                         .append(Component.text(" forward that crumbles blocks and "))
                         .append(Component.text("stuns", Colors.RED))
                         .append(Component.text(" enemies."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("Enemies can only be stunned once.", Colors.DARK_GRAY))
        );
    }
    
    @Override
    public @NotNull TalentTarget target(@NotNull HariantPlayer player) {
        return TalentTarget.none();
    }
    
    @Override
    public @NotNull Response execute(@NotNull HariantPlayer player, @NotNull TalentContext context) {
        new ShieldRam(player);
        
        // Fx
        player.addVanillaEffect(PotionEffectType.SLOWNESS, 5, 10);
        
        player.playWorldSound(Sound.ENTITY_IRON_GOLEM_HURT, 0.75f);
        
        return Response.ok();
    }
    
    private class ShieldRam extends HariantTickingTask {
        
        private final HariantPlayer player;
        private final Location location;
        private final Vector vectorForward;
        private final Vector vectorRight;
        private final Set<Entity> fallingBlocks;
        private final Set<HariantEntity> hitEntities;
        
        private ShieldRam(@NotNull HariantPlayer player) {
            super(Scheduler.ofTimer(slamDelay.intValue(), slamPeriod.intValue()));
            
            this.player = player;
            this.location = player.getLocation();
            this.vectorForward = player.getDirection().setY(0).normalize();
            this.vectorRight = vectorForward.getCrossProduct(UP).normalize().setY(0);
            this.fallingBlocks = Sets.newHashSet();
            this.hitEntities = Sets.newHashSet();
        }
        
        @Override
        public void run(int tick) {
            if (tick > maximumDistance.intValue()) {
                this.cancel();
                return;
            }
            
            final double x = vectorForward.getX() * tick;
            final double z = vectorForward.getZ() * tick;
            
            location.add(x, 0, z);
            
            for (int w = -tick + 1; w < tick; w++) {
                final double rx = vectorRight.getX() * w;
                final double rz = vectorRight.getZ() * w;
                
                location.add(rx, 0, rz);
                
                final Location locationAnchored = LocationHelper.anchor(location);
                final Block block = locationAnchored.getBlock().getRelative(BlockFace.DOWN);
                
                // Collect entities
                player.collectNearbyEntities(bashBoundingBox.create(locationAnchored))
                      .filter(player::canAffect)
                      .forEach(entity -> {
                          if (hitEntities.add(entity)) {
                              entity.addEffect(StatusEffectType.STUNNED, stunDuration.intValue(), player);
                              
                              // Fx
                              entity.playWorldSound(locationAnchored, Sound.ITEM_SHIELD_BREAK, 0.5f, 0.0f);
                          }
                      });
                
                
                if (!block.isEmpty()) {
                    // Falling blocks are weird and will be removed on their own, but just to be safe we're storing them
                    fallingBlocks.add(createFallingBlock(player, locationAnchored, block.getBlockData()));
                    
                    // Fx
                    final Sound breakSound = block.getBlockSoundGroup().getBreakSound();
                    
                    player.playWorldSound(locationAnchored, breakSound, 1f, 0.75f);
                    player.playWorldSound(locationAnchored, breakSound, 1f, 0.50f);
                }
                
                location.subtract(rx, 0, rz);
            }
            
            location.subtract(x, 0, z);
        }
        
        @Override
        public void onCancel() {
            fallingBlocks.forEach(Entity::remove);
            fallingBlocks.clear();
        }
    }
    
    private static @NotNull FallingBlock createFallingBlock(@NotNull HariantPlayer player, @NotNull Location location, @NotNull BlockData blockData) {
        return location.getWorld().spawn(location, FallingBlock.class, self -> {
            self.setCancelDrop(true);
            self.setInvulnerable(true);
            self.setHurtEntities(false);
            self.setBlockData(blockData);
            self.setVelocity(new Vector(player.random.nextSignedDouble(0.15), 0.15, player.random.nextSignedDouble(0.15)));
        });
    }
    
}