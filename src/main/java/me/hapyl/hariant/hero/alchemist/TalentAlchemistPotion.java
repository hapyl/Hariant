package me.hapyl.hariant.hero.alchemist;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.PotionContents;
import me.hapyl.eterna.module.annotate.ForceLowercase;
import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.talent.Response;
import me.hapyl.hariant.talent.Talent;
import me.hapyl.hariant.talent.TalentContext;
import me.hapyl.hariant.talent.target.TalentTarget;
import me.hapyl.hariant.task.HariantTask;
import me.hapyl.hariant.util.Icon;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public abstract class TalentAlchemistPotion extends Talent {
    
    private final TalentAbyssalBottle talent;
    private final double abyssalCorrosion;
    
    TalentAlchemistPotion(@NotNull TalentAbyssalBottle talent, @NotNull @ForceLowercase String key, @NotNull Component name, @NotNull Color potionColor, double abyssalCorrosion) {
        super(Key.ofString("alchemist_potion_" + key.toLowerCase()), name, createPotionIcon(potionColor));
        
        this.talent = talent;
        this.abyssalCorrosion = abyssalCorrosion;
        
        this.setDurationSeconds(10);
    }
    
    public double getAbyssalCorrosion() {
        return abyssalCorrosion;
    }
    
    @NotNull
    public abstract AlchemistPotionInstance drink(@NotNull HariantPlayer player, @NotNull HeroDataAlchemist heroData);
    
    public final void drink0(@NotNull HariantPlayer player, @NotNull HeroDataAlchemist heroData) {
        // We add a little delay before drinking
        player.delegate(HariantTask.later(() -> {
            final AlchemistPotionInstance potionInstance = this.drink(player, heroData);
            
            heroData.setPotionInstance(potionInstance);
            heroData.incrementCorrosion(this.abyssalCorrosion);
            
            // Fx
            player.playWorldSound(Sound.ENTITY_WITCH_DRINK, 1.0f);
        }, talent.drinkDelay.intValue()));
        
        // Fx
        player.playWorldSound(Sound.BLOCK_DECORATED_POT_INSERT, 0.0f);
    }
    
    @NotNull
    @Override
    public final ItemStack createItem() {
        return this.createBuilder().asItemStack();
    }
    
    @NotNull
    @Override
    public TalentTarget target(@NotNull HariantPlayer player) {
        return TalentTarget.none();
    }
    
    @NotNull
    @Override
    public Response execute(@NotNull HariantPlayer player, @NotNull TalentContext context) {
        return Response.ok();
    }
    
    @SuppressWarnings("UnstableApiUsage")
    @NotNull
    protected static Icon createPotionIcon(@NotNull Color color) {
        // return Icon.ofMaterial(Material.POTION, PotionMeta.class, meta -> meta.setColor(color));
        return () -> new ItemBuilder(Material.POTION)
                .setComponent(
                        DataComponentTypes.POTION_CONTENTS,
                        PotionContents.potionContents()
                                      .customColor(color)
                                      .build()
                )
                .hideComponents();
    }
    
}
