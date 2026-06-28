package me.hapyl.hariant.hero.troll;

import com.google.common.collect.Maps;
import me.hapyl.eterna.module.block.display.BDEngine;
import me.hapyl.eterna.module.block.display.DisplayEntity;
import me.hapyl.eterna.module.block.display.DisplayModel;
import me.hapyl.eterna.module.location.LocationHelper;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.Disposable;
import me.hapyl.eterna.module.util.Ticking;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.modifier.AttributeModifier;
import me.hapyl.hariant.attribute.modifier.AttributeModifierType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.VanillaAttributeModifier;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.hero.HeroRegistry;
import me.hapyl.hariant.talent.TalentContext;
import me.hapyl.hariant.talent.TalentType;
import me.hapyl.hariant.talent.field.DisplayField;
import me.hapyl.hariant.talent.target.TalentTarget;
import me.hapyl.hariant.talent.ultimate.TalentUltimate;
import me.hapyl.hariant.talent.ultimate.UltimateResourceType;
import me.hapyl.hariant.task.executor.Executable;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class TalentStickySituation extends TalentUltimate implements Listener {
    
    private static final TextColor COBWEB_COLOR = TextColor.color(0xE6DBD1);
    
    @DisplayField private final Decimal speedReduction = Decimal.ofPercentage(50);
    @DisplayField private final Decimal speedReductionDuration = Decimal.ofSeconds(3);
    
    @DisplayField private final Decimal jumpStrengthReduction = Decimal.ofPercentage(75);
    
    private final Key modifierKey = Key.ofString("sticky_situation_modifier");
    
    private final VanillaAttributeModifier vanillaAttributeModifier = VanillaAttributeModifier.create(
            modifierKey,
            Attribute.JUMP_STRENGTH,
            VanillaAttributeModifier.Operation.MULTIPLICATIVE,
            -jumpStrengthReduction.doubleValue()
    );
    
    public TalentStickySituation(@NotNull Key key) {
        super(key, Component.text("Sticky Situation"), Icon.ofMaterial(Material.COBWEB), UltimateResourceType.ENERGY, 50);
        
        setTalentType(TalentType.IMPAIR);
        
        setDescription(
                Component.empty()
                         .append(Component.text("Create a batch of "))
                         .append(Component.text("sticky cobwebs", COBWEB_COLOR))
                         .append(Component.text(" at your current location."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("Enemies ", Colors.RED))
                         .append(Component.text("touching the "))
                         .append(Component.text("cobwebs", COBWEB_COLOR))
                         .append(Component.text(" will be covered in it, "))
                         .append(Component.text("impairing", Colors.ARCHETYPE_HEXBANE))
                         .append(Component.text(" their movement."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("The "))
                         .append(Component.text("cobwebs", COBWEB_COLOR))
                         .append(Component.text(" can be cleared by hand or by touching them."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("Only one batch of cobwebs may exist at any given time.", Colors.DARK_GRAY))
        );
    }
    
    @Override
    public @NotNull Executable execute(@NotNull HariantPlayer player, @NotNull TalentContext context, double consumedResource) {
        return Executable.execute(() -> {
            player.getHeroData(HeroRegistry.TROLL, HeroDataTroll::new).createStickSituation(new StickySituation(player));
            
            // Fx
            player.playWorldSound(Sound.ENTITY_SPIDER_AMBIENT, 1.0f);
        });
    }
    
    @Override
    public @NotNull TalentTarget target(@NotNull HariantPlayer player) {
        return TalentTarget.none();
    }
    
    @EventHandler
    public void handlePlayerInteractEvent(PlayerInteractEvent ev) {
        final Block clickedBlock = ev.getClickedBlock();
        
        if (clickedBlock == null || ev.getHand() == EquipmentSlot.OFF_HAND) {
            return;
        }
        
        // Iterate over all players to and call `clearBlock` on troll data
        Hariant.getPlayers()
               .forEach(player -> player.touchHeroData(HeroRegistry.TROLL, HeroDataTroll.class, heroData -> {
                   if (heroData.stickySituation != null) {
                       heroData.stickySituation.clearBlock(clickedBlock);
                   }
               }));
    }
    
    public class StickySituation implements Ticking, Disposable {
        
        private static final DisplayModel MODEL = BDEngine.parse(
                "{Passengers:[{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:cobweb\",Properties:{}},transformation:[1f,0f,0f,0f,0f,1f,0f,0f,0f,0f,1f,0f,0f,0f,0f,1f]}]}"
        );
        
        private final HariantPlayer player;
        
        private final Map<Block, DisplayEntity> blockDisplayEntityMap;
        private final BoundingBox boundingBox;
        
        StickySituation(@NotNull HariantPlayer player) {
            this.player = player;
            this.blockDisplayEntityMap = Maps.newHashMap();
            
            // Create cobwebs
            final Location location = LocationHelper.anchor(LocationHelper.center(player.getLocation()));
            location.setYaw(0.0f);
            location.setPitch(0.0f);
            
            this.boundingBox = LocationHelper.toBoundingBox(location, 2.5, 1, 2.5);
            
            location.subtract(2, 0, 2);
            
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 5; j++) {
                    // Skip corners, looks prettier 😊
                    if ((i == 0 || i == 4) && (j == 0 || j == 4)) {
                        continue;
                    }
                    
                    LocationHelper.offset(location, i, 0.5, j, () -> {
                        final Block block = location.getBlock();
                        
                        if (block.isEmpty()) {
                            // We use the tripwire to act as a hitbox for the cobwebs
                            block.setType(Material.TRIPWIRE, false);
                            blockDisplayEntityMap.put(block, MODEL.spawn(block.getLocation()));
                        }
                    });
                }
            }
        }
        
        public boolean isEmpty() {
            return blockDisplayEntityMap.isEmpty();
        }
        
        @Override
        public void dispose() {
            blockDisplayEntityMap.forEach(this::clearBlock0);
        }
        
        @Override
        public void tick() {
            // The collision is done via checking if a nearby entity collides with the bounding box of the cobweb,
            // which isn't the best solution, but that's what I came up with in CF, and could not find a better
            // way to recreate it. -h
            final List<HariantEntity> entitiesWithinBoundingBox = player.collectNearbyEntities(boundingBox)
                                                                        .filter(player::canAffect)
                                                                        .filter(entity -> !entity.getAttributes().hasModifier(modifierKey))
                                                                        .toList();
            
            final Iterator<Map.Entry<Block, DisplayEntity>> iterator = blockDisplayEntityMap.entrySet().iterator();
            
            while (iterator.hasNext()) {
                final Map.Entry<Block, DisplayEntity> entry = iterator.next();
                
                final Block block = entry.getKey();
                final DisplayEntity displayEntity = entry.getValue();
                
                // Check for whether the entities who are within the bounding box overlay with the block's bounding box
                final BoundingBox blockBoundingBox = block.getBoundingBox().expand(0, 1, 0);
                
                for (HariantEntity entity : entitiesWithinBoundingBox) {
                    if (entity.getBoundingBox().overlaps(blockBoundingBox)) {
                        // Apply modifier
                        entity.getAttributes().addModifier(new ModifierStickySituation(player));
                        
                        // Remove from the hash map
                        iterator.remove();
                        
                        // Restore the block and remove the display entity
                        this.clearBlock0(block, displayEntity);
                        break;
                    }
                }
            }
        }
        
        public void clearBlock(@NotNull Block block) {
            final DisplayEntity displayEntity = blockDisplayEntityMap.remove(block);
            
            if (displayEntity == null) {
                return;
            }
            
            this.clearBlock0(block, displayEntity);
            
            // Fx
            final Location location = block.getLocation().add(0.5, 0.5, 0.5);
            
            player.spawnWorldParticle(location, Particle.POOF, 5, 0.3, 0.3, 0.3, 0.03f);
            player.playWorldSound(location, Sound.BLOCK_COBWEB_BREAK, 0.0f);
        }
        
        private void clearBlock0(@NotNull Block block, @NotNull DisplayEntity displayEntity) {
            block.setType(Material.AIR, false);
            displayEntity.remove();
        }
    }
    
    private class ModifierStickySituation extends AttributeModifier {
        
        ModifierStickySituation(@NotNull HariantPlayer player) {
            super(modifierKey, TalentStickySituation.this.getName(), player, speedReductionDuration.intValue());
            super.of(AttributeType.MOVEMENT_SPEED, AttributeModifierType.ADDITIVE, -speedReduction.doubleValue());
        }
        
        @Override
        public void onApply(@NotNull HariantEntity entity, @NotNull HariantEntity applier, int duration) {
            entity.addVanillaAttributeModifier(vanillaAttributeModifier);
        }
        
        @Override
        public void onRemove(@NotNull HariantEntity entity, @NotNull HariantEntity applier) {
            entity.removeVanillaAttributeModifier(vanillaAttributeModifier);
        }
    }
    
}