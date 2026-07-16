package me.hapyl.hariant.hero.shark;

import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.hero.HeroData;
import me.hapyl.hariant.profile.ui.ActionbarSupplier;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HeroDataShark extends HeroData<HeroShark> implements ActionbarSupplier {
    
    private @Nullable BloodScent bloodScent;
    
    public HeroDataShark(@NotNull HeroShark hero, @NotNull HariantPlayer player) {
        super(hero, player);
    }
    
    @Override
    public void dispose() {
        this.disposeBloodScent();
    }
    
    @Override
    public void tick() {
        if (bloodScent != null) {
            bloodScent.tick();
            
            if (bloodScent.isOver()) {
                this.disposeBloodScent();
            }
        }
    }
    
    public @Nullable BloodScent getBloodScent() {
        return bloodScent;
    }
    
    public void createBloodScent(@NotNull HariantEntity entity, int duration) {
        bloodScent = new BloodScent(player, entity, duration);
    }
    
    @Override
    public @NotNull List<Component> supplyActionbar(@NotNull HariantPlayer player) {
        if (bloodScent == null) {
            return List.of();
        }
        
        return List.of(bloodScent.asComponent());
    }
    
    private void disposeBloodScent() {
        if (bloodScent != null) {
            bloodScent.cancel();
            bloodScent = null;
        }
    }
    
}
