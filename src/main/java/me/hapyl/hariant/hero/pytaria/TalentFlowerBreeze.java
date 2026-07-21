package me.hapyl.hariant.hero.pytaria;

import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.CollectionUtils;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.modifier.AttributeModifier;
import me.hapyl.hariant.attribute.modifier.AttributeModifierType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.player.DelegateType;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.hero.HeroRegistry;
import me.hapyl.hariant.talent.Response;
import me.hapyl.hariant.talent.Talent;
import me.hapyl.hariant.talent.TalentContext;
import me.hapyl.hariant.talent.TalentType;
import me.hapyl.hariant.talent.field.DisplayField;
import me.hapyl.hariant.talent.target.TalentTarget;
import me.hapyl.hariant.task.HariantDurationTask;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public final class TalentFlowerBreeze extends Talent {
    
    private final @DisplayField Decimal healthSacrifice = Decimal.ofPercentage(20);
    private final @DisplayField Decimal attackIncrease = Decimal.ofPercentage(50);
    private final @DisplayField Decimal defenseIncrease = Decimal.ofPercentage(1000);
    
    private final ItemStack angryPytariaHead = ItemBuilder.playerHead("cb3a2c6fa906d782e9bf33cc79ebd043feae0e1284c7e6e43e31e24a59a5d6b1").asIcon();
    
    private final ItemStack[] fxFlowers = {
            new ItemStack(Material.DANDELION),
            new ItemStack(Material.POPPY),
            new ItemStack(Material.BLUE_ORCHID),
            new ItemStack(Material.ALLIUM),
            new ItemStack(Material.AZURE_BLUET),
            new ItemStack(Material.RED_TULIP),
            new ItemStack(Material.ORANGE_TULIP),
            new ItemStack(Material.WHITE_TULIP),
            new ItemStack(Material.PINK_TULIP),
            new ItemStack(Material.OXEYE_DAISY),
            new ItemStack(Material.CORNFLOWER),
            new ItemStack(Material.LILY_OF_THE_VALLEY),
            new ItemStack(Material.LILAC),
            new ItemStack(Material.ROSE_BUSH)
    };
    
    public TalentFlowerBreeze(@NotNull Key key) {
        super(key, Component.text("Flower Breeze"), Icon.ofMaterial(Material.RED_DYE));
        
        this.setCooldownSeconds(16);
        this.setDurationSeconds(5);
        
        this.setTalentType(TalentType.ENHANCE);
        
        this.setDescription(
                Component.empty()
                         .append(Component.text("Feel the breeze of beautiful flowers, that brings back terrible memories, "))
                         .append(Component.text(" hurts ", Colors.RED))
                         .append(Component.text("and enrages you for "))
                         .append(this.getDurationFormatted())
                         .append(Component.text("."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("While enraged, your "))
                         .append(AttributeType.ATTACK)
                         .append(Component.text(" and "))
                         .appendNewline()
                         .append(AttributeType.DEFENSE)
                         .append(Component.text(" are greatly increased."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("This talent cannot kill.", Colors.DARK_GRAY))
        );
    }
    
    @NotNull
    @Override
    public TalentTarget target(@NotNull HariantPlayer player) {
        return TalentTarget.none();
    }
    
    @NotNull
    @Override
    public Response execute(@NotNull HariantPlayer player, @NotNull TalentContext context) {
        final double healthSacrifice = Math.min(player.getHealth() - 5, player.getMaxHealth() * this.healthSacrifice.doubleValue());
        
        player.decrementHealth(healthSacrifice);
        
        // Apply modifiers
        player.getAttributes().addModifier(new ModifierFlowerBreeze(player));
        
        // Fx
        player.playWorldSound(Sound.ENTITY_HORSE_BREATHE, 0.0f);
        player.addVanillaEffect(PotionEffectType.SLOWNESS, 2, 10);
        
        player.delegate(
                // Just a fx that removes flowers, don't care keep it a task
                new HariantDurationTask(20) {
                    @Override
                    public void run(int tick, int duration) {
                        final Location location1 = player.getLocationOffsetRandomly(0.75);
                        final Location location2 = player.getLocationOffsetRandomly(0.75);
                        
                        this.spawnFlower(location1);
                        this.spawnFlower(location2);
                        
                        final float pitch = 0.5f + (1.5f * tick / duration);
                        
                        player.playWorldSound(Sound.BLOCK_LAVA_POP, pitch);
                        player.playWorldSound(Sound.BLOCK_AZALEA_PLACE, pitch);
                    }
                    
                    private void spawnFlower(@NotNull Location location) {
                        location.getWorld().dropItem(location, CollectionUtils.randomElementOrFirst(fxFlowers), self -> {
                            self.setPickupDelay(5000);
                            self.setTicksLived(5990);
                            
                            self.setVelocity(new Vector(player.getRandom().nextSignedDouble(0.25), 0.75, player.getRandom().nextSignedDouble(0.25)));
                        });
                    }
                },
                DelegateType.PERSISTENT
        );
        
        return Response.ok();
    }
    
    public @NotNull Component createComponent(@NotNull HariantPlayer player) {
        final AttributeModifier attributeModifier = player.getAttributes().getModifier(this.getKey()).orElse(null);
        
        return attributeModifier != null
               ? Component.empty()
                          .append(Component.text("\uD83D\uDCA2", Colors.ERROR))
                          .appendSpace()
                          .append(attributeModifier.currentTickFormatted())
               : Component.empty();
    }
    
    private class ModifierFlowerBreeze extends AttributeModifier {
        
        ModifierFlowerBreeze(@NotNull HariantEntity applier) {
            super(TalentFlowerBreeze.this, applier, TalentFlowerBreeze.this.getDuration());
            
            of(AttributeType.ATTACK, AttributeModifierType.MULTIPLICATIVE, attackIncrease.doubleValue());
            of(AttributeType.DEFENSE, AttributeModifierType.MULTIPLICATIVE, defenseIncrease.doubleValue());
        }
        
        @Override
        public void onApply(@NotNull HariantEntity entity, @NotNull HariantEntity applier, int duration) {
            entity.getEquipment().setHelmet(angryPytariaHead);
        }
        
        @Override
        public void onRemove(@NotNull HariantEntity entity, @NotNull HariantEntity applier) {
            entity.getEquipment().setHelmet(HeroRegistry.PYTARIA.getEquipment().helmet());
        }
    }
    
}