package me.hapyl.hariant.hero.nyx;

import com.google.common.collect.Lists;
import me.hapyl.eterna.module.util.Removable;
import me.hapyl.hariant.achievement.AchievementRegistry;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.hero.HeroData;
import me.hapyl.hariant.talent.TalentRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class HeroDataNyx extends HeroData<HeroNyx> {
    
    private final List<TalentDualVerdict.Droplet> droplets;
    private final Set<TalentDualVerdict.DropletTickResult> pickedUpDroplets;
    
    public HeroDataNyx(@NotNull HeroNyx hero, @NotNull HariantPlayer player) {
        super(hero, player);
        
        this.droplets = Lists.newArrayList();
        this.pickedUpDroplets = EnumSet.noneOf(TalentDualVerdict.DropletTickResult.class);
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
            final TalentDualVerdict.DropletTickResult tickResult = droplet.tick();
            
            if (tickResult == TalentDualVerdict.DropletTickResult.NONE) {
                continue;
            }
            
            droplet.remove();
            iterator.remove();
            
            // If after picking up an orb the count is 0, call the onPickupAll
            if (droplets.isEmpty()) {
                TalentRegistry.DUAL_VERDICT.onPickupAll(player);
            }
            
            // Achievement
            pickedUpDroplets.add(tickResult);
            
            if (pickedUpDroplets.contains(TalentDualVerdict.DropletTickResult.HARMONY) && pickedUpDroplets.contains(TalentDualVerdict.DropletTickResult.DISCORD)) {
                AchievementRegistry.NYX_DOUBLE_DUTY.progress(player.getProfile());
            }
        }
    }
    
    public void removeDroplets() {
        droplets.forEach(Removable::remove);
        droplets.clear();
        pickedUpDroplets.clear();
    }
    
}