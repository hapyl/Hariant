package me.hapyl.hariant.hero.alchemist;

import com.google.common.collect.Maps;
import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.element.ElementSource;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.EntityCollector;
import me.hapyl.hariant.entity.HariantRandom;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.talent.Response;
import me.hapyl.hariant.talent.Talent;
import me.hapyl.hariant.talent.TalentContext;
import me.hapyl.hariant.talent.field.DisplayField;
import me.hapyl.hariant.talent.target.TalentTarget;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.SplashPotion;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class TalentBundleOPotions extends Talent implements Listener {
    
    @DisplayField private final Decimal magnitude = Decimal.ofValue(0.9);
    @DisplayField private final Decimal maxY = Decimal.ofValue(0.5);
    
    @DisplayField private final Decimal potionYOffset = Decimal.ofValue(0.35);
    @DisplayField private final Decimal potionMaxXOffset = Decimal.ofValue(0.2);
    @DisplayField private final Decimal potionMaxZOffset = Decimal.ofValue(0.2);
    @DisplayField private final Decimal potionExplosionRadius = Decimal.ofValue(4);
    @DisplayField private final Decimal potionElementalApplication = Decimal.ofValue(500);
    
    private final Map<ThrownPotion, BundlePotion> alchemistPotionMap = Maps.newHashMap();
    
    public TalentBundleOPotions(@NotNull Key key) {
        super(key, Component.text("Bundle o' Potions"), Icon.ofMaterial(Material.BUNDLE));
        
        this.setCooldownSeconds(10);
        
        this.setDescription(
                Component.empty()
                         .append(
                                 Component.empty()
                                          .append(Component.text("Dash forward and throw a bundle of "))
                                          .append(Component.text("elemental", Colors.ATTRIBUTE_ELEMENTAL_MASTERY))
                                          .append(Component.text(" potions backwards."))
                         )
                         .appendNewline()
                         .appendNewline()
                         .append(
                                 Component.empty()
                                          .append(Component.text("Upon landing, each potion explodes and applies "))
                                          .append(Component.text("massive", Colors.WHITE, TextDecoration.UNDERLINED))
                                          .append(Component.text(" elemental build-up."))
                         )
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
        final Location location = player.getEyeLocation();
        final Vector vector = location.getDirection().normalize();
        
        vector.multiply(magnitude.doubleValue());
        vector.setY(maxY.doubleValue());
        
        player.setVelocity(vector);
        
        // Throw a potion for each element
        for (ElementType elementType : ElementType.values()) {
            final BundlePotion potion = BundlePotion.create(player, location, elementType);
            
            // Calculate direction
            final Vector direction = location.getDirection().normalize().multiply(-1);
            direction.setY(potionYOffset.doubleValue());
            
            // Randomize x and z slightly
            final HariantRandom random = player.getRandom();
            
            direction.setX(direction.getX() * (random.nextDouble() * potionMaxXOffset.doubleValue()));
            direction.setZ(direction.getZ() * (random.nextDouble() * potionMaxZOffset.doubleValue()));
            
            potion.thrownPotion.setVelocity(direction);
            
            alchemistPotionMap.put(potion.thrownPotion, potion);
        }
        
        // Fx
        player.playWorldSound(Sound.ENTITY_BREEZE_JUMP, 1.25f);
        player.playWorldSound(Sound.ENTITY_WITCH_THROW, 0.75f);
        
        return Response.ok();
    }
    
    @EventHandler
    public void handlePotionSplashEvent(PotionSplashEvent ev) {
        final BundlePotion potion = alchemistPotionMap.remove(ev.getPotion());
        
        if (potion == null) {
            return;
        }
        
        potion.collectNearbyEntities(potionExplosionRadius)
              .filter(potion.alchemist::canAffect)
              .forEach(entity -> {
                  entity.applyElement(ElementSource.create(potion.elementType, potion.alchemist, potionElementalApplication.doubleValue()));
              });
    }
    
    public record BundlePotion(@NotNull HariantPlayer alchemist, @NotNull ThrownPotion thrownPotion, @NotNull ElementType elementType) implements EntityCollector {
        
        private static final Map<ElementType, ItemStack> ELEMENT_TYPE_ITEM_STACK = Map.ofEntries(
                createPotionOfColor(ElementType.PHYSICAL),
                createPotionOfColor(ElementType.FIRE),
                createPotionOfColor(ElementType.WATER),
                createPotionOfColor(ElementType.ICE),
                createPotionOfColor(ElementType.TOXIC),
                createPotionOfColor(ElementType.ELECTRIC),
                createPotionOfColor(ElementType.AETHER)
        );
        
        @NotNull
        @Override
        public Location getLocation() {
            return thrownPotion.getLocation();
        }
        
        @NotNull
        public static BundlePotion create(@NotNull HariantPlayer player, @NotNull Location location, @NotNull ElementType elementType) {
            return new BundlePotion(
                    player,
                    player.getWorld().spawn(location, SplashPotion.class, self -> {
                        self.setItem(ELEMENT_TYPE_ITEM_STACK.get(elementType));
                    }),
                    elementType
            );
        }
        
        @NotNull
        private static Map.Entry<ElementType, ItemStack> createPotionOfColor(@NotNull ElementType elementType) {
            final TextColor color = elementType.getStyle().color();
            
            return Map.entry(
                    elementType,
                    new ItemBuilder(Material.SPLASH_POTION)
                            .setPotionColor(color != null ? Color.fromRGB(color.value()) : null)
                            .asItemStack()
            );
        }
        
    }
    
}
