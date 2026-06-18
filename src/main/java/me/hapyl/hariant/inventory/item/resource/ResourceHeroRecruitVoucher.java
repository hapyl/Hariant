package me.hapyl.hariant.inventory.item.resource;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.inventory.item.Rarity;
import me.hapyl.hariant.inventory.item.Resource;
import me.hapyl.hariant.util.Icon;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public final class ResourceHeroRecruitVoucher extends Resource implements ResourceUnlocksHero {
    
    public ResourceHeroRecruitVoucher(@NotNull Key key) {
        super(key, Component.text("Hero Recruit Voucher"), Icon.ofMaterial(Material.FILLED_MAP));
        
        setRarity(Rarity.FIVE_STAR);
        
        setDescription(Component.text("Can be used to recruit heroes."));
        setFlavorText(Component.text("An official looking document with ink writings on a expensive piece of paper."));
    }
    
    @Override
    public int maxStackSize() {
        return 100;
    }
    
    @Override
    public int unlockAmount() {
        return 1;
    }
    
}