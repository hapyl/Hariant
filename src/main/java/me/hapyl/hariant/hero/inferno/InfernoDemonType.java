package me.hapyl.hariant.hero.inferno;

import me.hapyl.eterna.module.component.Named;
import me.hapyl.eterna.module.inventory.Equipment;
import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.util.Icon;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.WitherSkeleton;
import org.jetbrains.annotations.NotNull;

public enum InfernoDemonType implements Named, Icon {
    
    QUAZII(
            Component.text("Quazii", Colors.AQUA),
            Component.text("ⓆⓊⒶⓏⒾⒾ"),
            WitherSkeleton.class,
            Material.WITHER_SKELETON_SPAWN_EGG,
            Equipment.builder()
                     .mainHand(new ItemBuilder(Material.DIAMOND_AXE).addEnchant(Enchantment.LUCK_OF_THE_SEA, 1).asIcon())
                     .helmet(ItemBuilder.playerHead("16ca145ba435b375f763ff53b4ce04b2a0c873e8ff547e8b14b392fde6fbfd94").asIcon())
                     .chestPlate(Material.CHAINMAIL_CHESTPLATE)
                     .boots(Material.DIAMOND_BOOTS)
                     .build()
    ),
    
    TYPHOEUS(
            Component.text("Typhoeus", Colors.RED),
            Component.text("ⓉⓎⓅⒽⓄⒺⓊⓈ"),
            PigZombie.class,
            Material.ZOMBIFIED_PIGLIN_SPAWN_EGG,
            Equipment.builder()
                     .mainHand(new ItemBuilder(Material.BLAZE_ROD).addEnchant(Enchantment.LUCK_OF_THE_SEA, 1).asIcon())
                     .helmet(ItemBuilder.playerHead("e2f29945aa53cd95a0978a62ef1a8c1978803395a8ad5c0921d9cbe5e196bb8b").asIcon())
                     .chestPlate(Material.CHAINMAIL_CHESTPLATE)
                     .leggings(ItemBuilder.leatherPants(Color.fromRGB(96, 0, 0)).asIcon())
                     .boots(Material.IRON_BOOTS)
                     .build()
    );
    
    private final Component name;
    private final Component demonName;
    private final Class<? extends LivingEntity> entityClass;
    private final Material material;
    private final Equipment equipment;
    
    InfernoDemonType(@NotNull Component name, @NotNull Component demonName, @NotNull Class<? extends LivingEntity> entityClass, @NotNull Material material, @NotNull Equipment equipment) {
        this.name = name;
        this.demonName = demonName;
        this.entityClass = entityClass;
        this.material = material;
        this.equipment = equipment;
    }
    
    @NotNull
    @Override
    public Component getName() {
        return name;
    }
    
    @NotNull
    public Component getDemonName() {
        return demonName;
    }
    
    @NotNull
    public Equipment getEquipment() {
        return equipment;
    }
    
    public @NotNull LivingEntity createEntity(@NotNull Location location) {
        return location.getWorld().spawn(location, entityClass, self -> {
            self.setAI(false);
            self.setVisibleByDefault(false);
            self.setPersistent(true);
            
            if (self instanceof Ageable ageable) {
                ageable.setAdult();
            }
            
            // Set equipment
            equipment.equip(self);
        });
    }
    
    @NotNull
    @Override
    public ItemBuilder createBuilder() {
        return new ItemBuilder(material);
    }
    
}