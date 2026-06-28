package me.hapyl.hariant.hero.nyx;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.talent.Response;
import me.hapyl.hariant.talent.Talent;
import me.hapyl.hariant.talent.TalentContext;
import me.hapyl.hariant.talent.TalentRegistry;
import me.hapyl.hariant.talent.field.DisplayField;
import me.hapyl.hariant.talent.target.TalentTarget;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TalentWiltBlink extends Talent {
    
    @DisplayField private final Decimal maxBlinkDistance = Decimal.ofValue(5);
    
    public TalentWiltBlink(@NotNull Key key) {
        super(key, Component.text("Wilted Blink"), Icon.ofMaterial(Material.CLOSED_EYEBLOSSOM));
        
        setCooldownSeconds(8);
        
        setDescription(
                Component.empty()
                         .append(Component.text("Leave a "))
                         .append(Component.text("Wilter Rose", Colors.ARCHETYPE_HEXBANE))
                         .append(Component.text(" rose at your current location and "))
                         .append(Component.text("blink", Colors.LIGHT_PURPLE))
                         .append(Component.text(" forward."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("Wilted Rose", Colors.GOLD))
                         .appendNewline()
                         .append(
                                 Component.empty()
                                          .append(Component.text("The rose blooms to life once again and explodes, dealing "))
                                          .append(ElementType.AETHER.asComponentDamage())
                                          .append(Component.text(" and applies "))
                                          .append(ElementType.AETHER)
                                          .append(Component.text(" anomaly."))
                         )
        );
    }
    
    @Override
    public @NotNull TalentTarget target(@NotNull HariantPlayer player) {
        return new TalentTarget() {
            @Override
            public @Nullable TalentContext createContext(@NotNull HariantPlayer player) {
                final Location location = player.getLocation();
                final Vector vector = location.getDirection().normalize().setY(0);
                
                final double originX = location.getX();
                final double originY = location.getY();
                final double originZ = location.getZ();
                
                for (double d = 0; d < maxBlinkDistance.doubleValue(); d += 0.5) {
                    final double x = originX + vector.getX() * d;
                    final double y = originY + vector.getY() * d;
                    final double z = originZ + vector.getZ() * d;
                    
                    location.set(x, y, z);
                    
                    // If we hit a block, go back and break
                    if (location.getBlock().isSolid()) {
                        location.subtract(vector);
                        break;
                    }
                }
                
                // Check whether the location is safe
                final Block block = location.getBlock();
                
                if (block.isPassable() && block.getRelative(BlockFace.UP).isPassable()) {
                    return TalentContext.create(location);
                }
                
                return null;
            }
            
            @Override
            public @NotNull Component errorMessage() {
                return Component.text("Nowhere to blink.");
            }
        };
    }
    
    @Override
    public @NotNull Response execute(@NotNull HariantPlayer player, @NotNull TalentContext context) {
        final Location location = player.getLocation();
        location.setPitch(0);
        
        player.teleport(context.retrieve(Location.class));
        
        TalentRegistry.REVERBERATION.createRose(player, location);
        
        // Fx
        player.playWorldSound(Sound.ENTITY_WITHER_SHOOT, 1.75f);
        player.spawnWorldParticle(player.getLocation(), Particle.ASH, 100, 0.2, 0.8, 0.2, 0.1f);
        
        return Response.ok();
    }
    
}