package me.hapyl.hariant.hero.pytaria;

import me.hapyl.hariant.Colors;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.hero.HeroData;
import me.hapyl.hariant.hero.pytaria.bee.BeeSwarm;
import me.hapyl.hariant.profile.ui.ActionbarSupplier;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HeroDataPytaria extends HeroData<HeroPytaria> implements ComponentLike, ActionbarSupplier {
    
    @Nullable
    public BeeSwarm swarm;
    
    private int excellency;
    
    public HeroDataPytaria(@NotNull HeroPytaria hero, @NotNull HariantPlayer player) {
        super(hero, player);
    }
    
    public void excellency(int excellency) {
        if (this.excellency == excellency) {
            return;
        }
        
        this.excellency = excellency;
        
        // Notify
        this.player.sendSubtitle(this.asComponent(), 5, 15, 5);
        this.player.playSound(Sound.ENTITY_BREEZE_LAND, excellency > 0 ? 1f + 0.25f * excellency : 0);
    }
    
    @Override
    public void dispose() {
        if (swarm != null) {
            swarm.cancel();
        }
    }
    
    @NotNull
    @Override
    public Component asComponent() {
        return Component.empty()
                        .append(Component.text("\uD83C\uDF3C", excellency > 1 ? Colors.FLOWER_TULIP : Colors.FLOWER_DEAD))
                        .append(Component.text("\uD83C\uDF38", excellency > 0 ? Colors.FLOWER_ROSE : Colors.FLOWER_DEAD))
                        .append(Component.text("\uD83C\uDF3C", excellency > 2 ? Colors.FLOWER_TULIP : Colors.FLOWER_DEAD));
    }
    
    @Override
    public @NotNull List<Component> supplyActionbar(@NotNull HariantPlayer player) {
        return List.of(this.asComponent());
    }
    
}