package me.hapyl.hariant.inventory.drop;

import me.hapyl.hariant.HariantConstants;
import me.hapyl.hariant.inventory.item.ResourceRegistry;

public final class CommonDroppable {
    
    public static final Droppable CAT_COINS;
    public static final Droppable ARTIFACT_ARTIFICER;
    public static final Droppable HERO_RECRUIT_VOUCHER;
    
    static {
        CAT_COINS = Droppable.ofResource(ResourceRegistry.CAT_COINS, HariantConstants.GUARANTEED_DROP_CHANCE, Amount.range(100, 200));
        ARTIFACT_ARTIFICER = Droppable.ofResource(ResourceRegistry.ARTIFACT_ARTIFICER, 10, Amount.range(1, 2));
        HERO_RECRUIT_VOUCHER = Droppable.ofResource(ResourceRegistry.HERO_RECRUIT_VOUCHER, 1, Amount.fixed(1));
    }
    
    private CommonDroppable() {
    }
    
}