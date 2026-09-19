package me.hapyl.hariant.hero.alchemist;

import me.hapyl.eterna.module.entity.Entities;
import me.hapyl.eterna.module.location.LocationHelper;
import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.Removable;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.achievement.AchievementRegistry;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.instance.Attributes;
import me.hapyl.hariant.attribute.modifier.AttributeModifier;
import me.hapyl.hariant.attribute.modifier.AttributeModifierType;
import me.hapyl.hariant.element.ElementSource;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.HariantDisplayEntity;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.WarningType;
import me.hapyl.hariant.entity.damage.DamageInstance;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.hero.HeroRegistry;
import me.hapyl.hariant.ui.ComponentDisplay;
import me.hapyl.hariant.util.Definition;
import me.hapyl.hariant.util.Models;
import me.hapyl.hariant.util.TickingDown;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HariantEntityAlchemicalCauldron extends HariantDisplayEntity implements Removable, TickingDown, ComponentLike {
    
    private static final Component CAULDRON_PREFIX = Component.text("\uD83C\uDF75", Colors.WHITE, TextDecoration.BOLD);
    
    private static final ItemStack ITEM_STACK_STICK = new ItemStack(Material.STICK);
    private static final ItemStack ITEM_STACK_AIR = new ItemStack(Material.AIR);
    
    private static final BlockData[] DESTROY_BLOCK_DATA = new BlockData[] {
            Material.POLISHED_BLACKSTONE.createBlockData(),
            Material.SCULK_VEIN.createBlockData()
    };
    
    private static final Particle.DustOptions DUST_OPTIONS = new Particle.DustOptions(bukkitColorFromStyle(ElementType.TOXIC.getStyle()), 1);
    
    private static final double RAD_2 = Math.toRadians(2);
    
    private final HariantPlayer player;
    private final TalentAlchemicalCauldron talent;
    private final ElementSource elementSource;
    private final ArmorStand stickAnimation;
    
    private boolean isBrewing;
    private int tick;
    
    HariantEntityAlchemicalCauldron(@NotNull HariantPlayer player, @NotNull Location location, @NotNull TalentAlchemicalCauldron talent) {
        super(Models.ALCHEMICAL_CAULDRON, location, 2, Attributes.base(talent.cauldronHealth.doubleValue(), 100, 100));
        
        this.player = player;
        this.talent = talent;
        this.elementSource = ElementSource.create(ElementType.TOXIC, player, talent.cauldronElementalApplication.doubleValue());
        this.tick = talent.getDuration();
        this.stickAnimation = Entities.ARMOR_STAND.spawn(
                LocationHelper.copyOf(location).subtract(0.0, 0.75, 0.0), self -> {
                    self.setMarker(true);
                    self.setSmall(true);
                    self.setSilent(true);
                    self.setInvisible(true);
                    self.setSmall(false);
                    
                    final EntityEquipment equipment = self.getEquipment();
                    
                    equipment.setItemInMainHand(ITEM_STACK_AIR);
                    self.setRightArmPose(new EulerAngle(Math.toRadians(-85.0d), Math.toRadians(-90), 0));
                }
        );
        
        player.getPlayerTeam().addEntry(this);
    }
    
    @Override
    public void onDamageTaken(@NotNull DamageInstance damageInstance, @Nullable HariantEntity attacker) {
        this.playWorldSound(Sound.ENTITY_IRON_GOLEM_STEP, 0.0f);
        this.playWorldSound(Sound.BLOCK_METAL_BREAK, 0.75f);
    }
    
    @Override
    public void onInteract(@NotNull HariantPlayer player) {
        // Ignore non-owner interactions
        if (!this.player.equals(player)) {
            player.playSound(Sound.BLOCK_LAVA_POP, 0.0f);
            return;
        }
        
        // If brewing is done, return the stick and apply infusion
        if (this.isOver()) {
            // applyInfusion() implicitly calls `remove()`
            this.applyInfusion();
            return;
        }
        
        this.toggleBrewing();
    }
    
    @Override
    public boolean tick() {
        if (!super.tick()) {
            return false;
        }
        
        if (isBrewing) {
            tick--;
            
            // Brewing done
            if (tick == 0) {
                isBrewing = false;
                
                // Fx
                player.sendTitleSubtitle(CAULDRON_PREFIX, Component.text("BREWING FINISHED!", Colors.SUCCESS, TextDecoration.BOLD), 5, 20, 5);
                player.sendMessage(
                        Component.empty()
                                 .append(CAULDRON_PREFIX)
                                 .appendSpace()
                                 .append(Component.text("The cauldron finished brewing! ", Colors.SUCCESS))
                                 .append(Component.text("RIGHT-CLICK", Colors.GOLD, TextDecoration.BOLD))
                                 .append(Component.text(" on it to gain ", Colors.SUCCESS))
                                 .append(Definition.ALCHEMICAL_MADNESS)
                                 .append(Component.text("!", Colors.SUCCESS))
                );
                
                player.playSound(Sound.BLOCK_BREWING_STAND_BREW, 2.0f);
                player.playSound(Sound.BLOCK_BREWING_STAND_BREW, 1.5f);
                player.playSound(Sound.BLOCK_BREWING_STAND_BREW, 1.25f);
            }
            
            // Apply toxic in radius
            this.collectNearbyEntities(talent.cauldronElementalApplicationRadius)
                .filter(player::canAffect)
                .forEach(entity -> {
                    entity.applyElement(elementSource);
                    entity.showWarning(WarningType.WARNING, 5);
                });
            
            spawnWorldParticle(getLocation(), Particle.DUST, 1, 1, 0.6, 1, 0.1f, DUST_OPTIONS);
            
            // Animate cauldron
            final float pitch = (float) (RAD_2 * Math.cos(Math.toRadians(tick) * 10));
            final float roll = (float) (RAD_2 * Math.sin(Math.toRadians(tick) * 10));
            
            displayEntity.editRotation(rotation -> {
                rotation.x = pitch;
                rotation.z = roll;
            }, "animate");
            
            // Animate stick
            final Location location = stickAnimation.getLocation();
            location.setYaw(location.getYaw() + 10);
            
            stickAnimation.teleport(location);
            
            // Play global sfx
            if (tick == duration() || tick % 100 == 0) {
                this.playWorldSound(Sound.BLOCK_LAVA_AMBIENT, 100, 2.0f);
            }
        }
        
        return true;
    }
    
    public void applyInfusion() {
        // Apply infusion
        final HeroAlchemist alchemist = HeroRegistry.ALCHEMIST;
        final HeroDataAlchemist heroData = player.getHeroData(alchemist, HeroDataAlchemist::new);
        
        heroData.setAlchemicalMadness(talent.infusionDuration.intValue());
        player.getAttributes().addModifier(new AlchemicalMadnessModifier(player));
        
        alchemist.giveWeapon(player);
        
        // Fx
        this.spawnWorldParticle(this.getLocation(), Particle.EXPLOSION_EMITTER, 1, 0.0f);
        
        this.playWorldSound(Sound.ENTITY_GENERIC_EXPLODE, 1.25f);
        this.playWorldSound(Sound.ENTITY_WITCH_CELEBRATE, 0.75f);
        
        // Cleanup
        heroData.setAlchemicalCauldron(null);
        
        // Achievement
        AchievementRegistry.ALCHEMIST_LOCAL_BREWERY.progress(player.getProfile());
    }
    
    @NotNull
    @Override
    public Component getName() {
        return talent.getName();
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        
        this.stickAnimation.remove();
        this.player.setCooldown(talent);
        
        // Fx
        final Location fxLocation = this.getLocation().add(0, 1, 0);
        
        player.playWorldSound(fxLocation, Sound.ENTITY_IRON_GOLEM_DAMAGE, 0.75f);
        
        player.spawnWorldParticle(fxLocation, Particle.BLOCK, 10, 0.3, 0.3, 0.3, 1f, DESTROY_BLOCK_DATA[0]);
        player.spawnWorldParticle(fxLocation, Particle.BLOCK, 10, 0.3, 0.3, 0.3, 1f, DESTROY_BLOCK_DATA[1]);
    }
    
    public void toggleBrewing() {
        isBrewing = !isBrewing;
        
        // If stared brewing, replace
        if (isBrewing) {
            stickAnimation.getEquipment().setItemInMainHand(ITEM_STACK_STICK);
        }
        else {
            stickAnimation.getEquipment().setItemInMainHand(ITEM_STACK_AIR);
        }
        
        // Update player's weapon either way
        player.getHero().giveWeapon(player);
        
        // Fx
        player.playSound(Sound.ENTITY_CHICKEN_EGG, 1.0f);
        player.playSound(Sound.ENTITY_CHICKEN_EGG, 0.75f);
    }
    
    public boolean isBrewing() {
        return isBrewing;
    }
    
    @Override
    public int currentTick() {
        return tick;
    }
    
    @Override
    public int duration() {
        return talent.getDuration();
    }
    
    @NotNull
    @Override
    public Component asComponent() {
        final TextComponent.Builder builder = Component.text();
        builder.append(CAULDRON_PREFIX);
        builder.appendSpace();
        
        // If brewing is over, show that
        if (this.isOver()) {
            builder.append(Component.text("BREWING FINISHED!", Colors.SUCCESS, TextDecoration.BOLD));
        }
        else {
            builder.append(
                    isBrewing
                    ? Component.text(Tick.format(tick), Colors.SUCCESS)
                    : Component.text("NOT BREWING!", Colors.ERROR, TextDecoration.BOLD)
            );
        }
        
        return builder.build();
    }
    
    private static @NotNull Color bukkitColorFromStyle(@NotNull Style style) {
        final TextColor color = style.color();
        
        return Color.fromRGB(color != null ? color.value() : 0);
    }
    
    private class AlchemicalMadnessModifier extends AttributeModifier {
        
        private static final Key MODIFIER_KEY = Key.ofString("alchemical_madness");
        private static final Particle.DustTransition DUST_COLOR_TRANSITION = new Particle.DustTransition(Color.fromRGB(63, 188, 54), Color.fromRGB(29, 105, 24), 1f);
        
        AlchemicalMadnessModifier(@NotNull HariantEntity applier) {
            super(MODIFIER_KEY, Component.text("Alchemical Madness"), applier, talent.infusionDuration.intValue());
            
            this.of(AttributeType.TOXIC_DAMAGE_BONUS, AttributeModifierType.FLAT, talent.toxicDamageIncrease.doubleValue());
        }
        
        @Override
        public void display(@NotNull Location location) {
            ComponentDisplay.ofAscend(Definition.ALCHEMICAL_MADNESS.asComponent(), location, 40, 1.0f);
        }
        
        @Override
        public void onTick(@NotNull HariantEntity entity, @NotNull HariantEntity applier, int tick, int duration) {
            entity.spawnWorldParticle(entity.getMidpointLocation(), Particle.DUST_COLOR_TRANSITION, 2, 0.3, 0.5, 0.3, 0.15f, DUST_COLOR_TRANSITION);
        }
        
    }
}