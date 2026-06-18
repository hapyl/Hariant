package me.hapyl.hariant.hero.nyx;

import com.google.common.collect.Lists;
import me.hapyl.eterna.module.util.Removable;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.hero.HeroData;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;

public class HeroDataNyx extends HeroData<HeroNyx> {
    
    private final List<TalentDualVerdict.Droplet> droplets;
    
    public HeroDataNyx(@NotNull HeroNyx hero, @NotNull HariantPlayer player) {
        super(hero, player);
        
        this.droplets = Lists.newArrayList();
    }
    
    public void createDroplet(@NotNull TalentDualVerdict.Droplet droplet) {
        this.droplets.add(droplet);
    }
    
    @Override
    public void dispose() {
        this.removeDroplets();
    }
    
    @Override
    public void tick() {
        // Tick droplets
        final Iterator<TalentDualVerdict.Droplet> iterator = droplets.iterator();
        
        while (iterator.hasNext()) {
            final TalentDualVerdict.Droplet droplet = iterator.next();
            
            // Remove droplet if collision successful
            if (droplet.tick()) {
                droplet.remove();
                iterator.remove();
            }
        }
    }
    
    public void removeDroplets() {
        droplets.forEach(Removable::remove);
        droplets.clear();
    }
}