package me.hapyl.hariant.hero.orc;

import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.hero.HeroData;
import me.hapyl.hariant.hero.HeroRegistry;
import me.hapyl.hariant.talent.TalentRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class HeroDataOrc extends HeroData<HeroOrc> {
    
    public final TalentAngerIssues.AngerIssues angerIssues;
    
    private @Nullable WeaponGorkaMorkaEntity weaponEntity;
    
    public HeroDataOrc(@NotNull HeroOrc hero, @NotNull HariantPlayer player) {
        super(hero, player);
        
        this.angerIssues = TalentRegistry.ANGER_ISSUES.createAngerIssues(player);
    }
    
    @Override
    public void dispose() {
        if (weaponEntity != null) {
            weaponEntity.cancel();
            weaponEntity = null;
        }
    }
    
    public boolean hasWeaponEntity() {
        return weaponEntity != null;
    }
    
    public @Nullable WeaponGorkaMorkaEntity getWeaponEntity() {
        return weaponEntity;
    }
    
    public void setWeaponEntity(@NotNull WeaponGorkaMorkaEntity weaponEntity) {
        if (this.weaponEntity != null) {
            this.weaponEntity.cancel();
        }
        
        this.weaponEntity = weaponEntity;
        
        // Update weapon
        HeroRegistry.ORC.giveWeapon(player);
    }
    
    public void onWeaponRecall(@NotNull WeaponGorkaMorkaEntity weaponEntity) {
        if (this.weaponEntity != null) {
            // If somehow entities differ, cancel the other entity as well
            if (this.weaponEntity != weaponEntity) {
                weaponEntity.cancel();
            }
            
            this.weaponEntity.cancel();
            this.weaponEntity = null;
        }
        
        HeroRegistry.ORC.giveWeaponStartThrowCooldown(player);
    }
    
}