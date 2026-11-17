package me.hapyl.hariant.hero.shaman;

import com.google.common.collect.Lists;
import me.hapyl.eterna.module.util.Progressible;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.hero.HeroData;
import me.hapyl.hariant.profile.ui.ActionbarSupplier;
import net.kyori.adventure.text.Component;
import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Deque;
import java.util.List;

public class HeroDataShaman extends HeroData<HeroShaman> implements Progressible, ActionbarSupplier {
    
    private final Deque<Totem> totems;
    
    public @Nullable TalentShamanMark.ShamanMark shamanMark;
    
    private double totalOverheal;
    private int overheal;
    
    public HeroDataShaman(@NotNull HeroShaman hero, @NotNull HariantPlayer player) {
        super(hero, player);
        
        this.totems = Lists.newLinkedList();
    }
    
    public void increseOverheal(double overheal, int stacks) {
        final int overhealBeforeIncrement = this.overheal;
        
        this.overheal = Math.min(this.overheal + stacks, TalentOverheal.MAXIMUM_OVERHEAL);
        
        // If overheal changed values, play Sfx
        if (this.overheal > overhealBeforeIncrement) {
            this.player.playSound(Sound.ENTITY_ZOMBIE_INFECT, 1.25f);
        }
        
        // Increment total overheal
        this.totalOverheal += overheal;
        
        // TODO (xanyjl @ Tuesday, November 17) -> Achievement
    }
    
    public boolean consumeOverheal() {
        if (this.overheal <= 0) {
            return false;
        }
        
        this.overheal--;
        return true;
    }
    
    @Override
    public void dispose() {
        totems.forEach(Totem::remove);
        totems.clear();
        
        if (shamanMark != null) {
            shamanMark.dispose();
        }
    }
    
    @Override
    public void tick() {
        totems.removeIf(Totem::removeIfShould);
        totems.forEach(Totem::tick);
        
        // Tick shaman mark
        if (shamanMark != null) {
            shamanMark.tick();
            
            final TalentShamanMark.RemovalReason removalReason = shamanMark.getRemovalReason();
            
            if (removalReason != null) {
                shamanMark.dispose();
                shamanMark = null;
                
                // Notify shaman of removal
                player.sendMessage(
                        TalentShamanMark.COMPONENT_SHAMAN_MARK.appendSpace()
                                                              .append(Component.text("Shaman Mark removed because ", Colors.RED))
                                                              .append(removalReason.asComponent().color(Colors.DARK_RED))
                                                              .append(Component.text("!", Colors.RED))
                );
                
                // Sfx
                player.playSound(Sound.ENTITY_GOAT_HORN_BREAK, 0.0f);
            }
        }
    }
    
    public void createTotem(@NotNull HariantPlayer player, @NotNull TalentTotem talent) {
        if (totems.size() >= talent.maximumNumberOfTotems.intValue()) {
            final Totem totem = totems.pollFirst();
            
            if (totem != null) {
                totem.remove();
            }
        }
        
        totems.offer(new Totem(player, talent));
    }
    
    public void setShamanMark(@NotNull TalentShamanMark.ShamanMark shamanMark) {
        if (this.shamanMark != null) {
            this.shamanMark.dispose();
        }
        
        this.shamanMark = shamanMark;
    }
    
    @Override
    public double currentProgress() {
        return overheal;
    }
    
    @Override
    public double maxProgress() {
        return TalentOverheal.MAXIMUM_OVERHEAL;
    }
    
    @Override
    public @NotNull List<@NotNull Component> supplyActionbar(@NotNull HariantPlayer player) {
        return List.of(
                TalentOverheal.PROGRESS_BAR.build(this),
                shamanMark != null ? shamanMark.asComponent() : Component.empty()
        );
    }
}