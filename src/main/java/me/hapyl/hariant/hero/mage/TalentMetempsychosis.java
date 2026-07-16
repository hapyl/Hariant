package me.hapyl.hariant.hero.mage;

import me.hapyl.eterna.module.location.LocationHelper;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.entity.player.DelegateType;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.talent.Response;
import me.hapyl.hariant.talent.Talent;
import me.hapyl.hariant.talent.TalentContext;
import me.hapyl.hariant.talent.TalentType;
import me.hapyl.hariant.talent.field.DisplayField;
import me.hapyl.hariant.talent.target.TalentTarget;
import me.hapyl.hariant.task.HariantTickingTask;
import me.hapyl.hariant.task.Scheduler;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.RayTraceResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class TalentMetempsychosis extends Talent {
    
    @DisplayField private final Decimal maxRadius = Decimal.ofValue(30);
    @DisplayField private final Decimal transmigrationDuration = Decimal.ofSeconds(0.5f);
    
    public TalentMetempsychosis(@NotNull Key key) {
        super(key, Component.text("Metempsychosis"), Icon.ofMaterial(Material.ECHO_SHARD));
        
        setDurationSeconds(1f);
        setCooldownSeconds(12);
        
        setTalentType(TalentType.MOVEMENT);
        
        setDescription(
                Component.empty()
                         .append(Component.text("Initiate metempsychosis onto the "))
                         .append(Component.text("target block", Colors.WHITE, TextDecoration.UNDERLINED))
                         .append(Component.text("."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("After a short delay, transmigrate your "))
                         .append(Component.text("soul", Colors.SOUL))
                         .append(Component.text(" to the target block."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("You are invulnerable during the transmigration.", Colors.DARK_GRAY))
        );
    }
    
    @NotNull
    @Override
    public TalentTarget target(@NotNull HariantPlayer player) {
        return new TalentTargetMetempsychosis();
    }
    
    @NotNull
    @Override
    public Response execute(@NotNull HariantPlayer player, @NotNull TalentContext context) {
        final Location targetLocation = context.retrieve(Location.class);
        
        player.delegate(new Metempsychosis(player, targetLocation), DelegateType.INTERRUPTABLE);
        return Response.ok();
    }
    
    private class TalentTargetMetempsychosis implements TalentTarget {
        
        @Override
        public @Nullable TalentContext createContext(@NotNull HariantPlayer player) {
            final RayTraceResult rayTraceResult = player.getHandle().rayTraceBlocks(maxRadius.doubleValue());
            
            if (rayTraceResult == null) {
                return null;
            }
            
            final Block hitBlock = rayTraceResult.getHitBlock();
            
            if (hitBlock == null) {
                return null;
            }
            
            // If hit block is valid, return it
            if (isBlockValid(hitBlock)) {
                return createContext(hitBlock);
            }
            // Otherwise go back one step and anchor the block down
            else {
                final BlockFace hitBlockFace = rayTraceResult.getHitBlockFace();
                
                if (hitBlockFace == null) {
                    return null;
                }
                
                Block offsetBlock = hitBlock.getRelative(hitBlockFace);
                
                // Anchor the block
                while (!offsetBlock.isSolid() && offsetBlock.getY() > offsetBlock.getWorld().getMinHeight()) {
                    offsetBlock = offsetBlock.getRelative(BlockFace.DOWN);
                }
                
                if (isBlockValid(offsetBlock)) {
                    return createContext(offsetBlock);
                }
            }
            
            return null;
        }
        
        @Override
        public @NotNull Component errorMessage() {
            return Component.text("No valid block in sight!");
        }
        
        private static boolean isBlockValid(@NotNull Block block) {
            return block.isSolid() && block.getRelative(BlockFace.UP).isEmpty() && block.getRelative(BlockFace.UP, 2).isEmpty();
        }
        
        private static @NotNull TalentContext createContext(@NotNull Block block) {
            return TalentContext.create(block.getLocation().add(0.5, block.getBoundingBox().getMaxY() - block.getY(), 0.5));
        }
    }
    
    private class Metempsychosis extends HariantTickingTask {
        
        private final @NotNull HariantPlayer player;
        private final Location targetLocation;
        private final int duration;
        
        public Metempsychosis(@NotNull HariantPlayer player, Location targetLocation) {
            super(Scheduler.ofTimer());
            this.player = player;
            this.targetLocation = targetLocation;
            this.duration = getDuration();
        }
        
        @Override
        public void run(int tick) {
            if (tick > duration) {
                transmigrate(player, targetLocation);
                this.cancel();
            }
            
            // Fx
            final double radians = Math.toRadians(tick * 15);
            
            final double x = Math.sin(radians) * 0.8;
            final double z = Math.cos(radians) * 0.8;
            
            LocationHelper.offset(targetLocation, x, 0, z, () -> player.spawnWorldParticle(targetLocation, Particle.SOUL, 0, 0, 0.25f, 0, 0.75f));
            LocationHelper.offset(targetLocation, -x, 0, -z, () -> player.spawnWorldParticle(targetLocation, Particle.SCULK_SOUL, 0, 0, 0.25f, 0, 0.75f));
            
            if (modulo(2)) {
                final float pitch = 0.75f + (float) tick / duration;
                
                player.playWorldSound(targetLocation, Sound.ENTITY_WARDEN_LISTENING, pitch);
            }
        }
        
        public void transmigrate(@NotNull HariantPlayer player, @NotNull Location destination) {
            final Location location = player.getLocation();
            
            final double fromX = location.getX();
            final double fromY = location.getY();
            final double fromZ = location.getZ();
            
            final double toX = destination.getX();
            final double toY = destination.getY();
            final double toZ = destination.getZ();
            
            player.setGameMode(GameMode.SPECTATOR);
            
            // Don't delegate, player is invulnerable
            new HariantTickingTask(Scheduler.ofTimer()) {
                @Override
                public void run(int tick) {
                    final double progress = (double) tick / transmigrationDuration.intValue();
                    
                    if (progress > 1.0) {
                        player.setGameMode(GameMode.SURVIVAL);
                        this.cancel();
                        return;
                    }
                    
                    final double x = fromX + (toX - fromX) * progress;
                    final double y = fromY + (toY - fromY) * progress + Math.sin(progress * Math.PI) * 1.25;
                    final double z = fromZ + (toZ - fromZ) * progress;
                    
                    location.set(x, y, z);
                    
                    player.teleport(location);
                    
                    // Offset higher for fx AFTER teleportation
                    location.add(0, 0.5, 0);
                    
                    // Fx
                    player.spawnWorldParticle(location, Particle.SOUL, 3, 0.2, 0.2, 0.2, 0.075f);
                }
            };
            
            // Fx
            player.playWorldSound(Sound.BLOCK_SOUL_SAND_BREAK, 0.0f);
            player.playWorldSound(Sound.PARTICLE_SOUL_ESCAPE, 0.75f);
            player.playWorldSound(Sound.ENTITY_WARDEN_SONIC_CHARGE, 1.25f);
        }
    }
    
}