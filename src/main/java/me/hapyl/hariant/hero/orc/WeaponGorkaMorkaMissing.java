package me.hapyl.hariant.hero.orc;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.NormalAttack;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.hero.HeroRegistry;
import me.hapyl.hariant.talent.Response;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.weapon.WeaponMelee;
import me.hapyl.hariant.weapon.ability.Ability;
import me.hapyl.hariant.weapon.ability.AbilityType;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public final class WeaponGorkaMorkaMissing extends WeaponMelee {
    
    private final Ability abilityRecall;
    
    WeaponGorkaMorkaMissing() {
        super(Key.ofString("gorka_morka_missing"), Icon.ofMaterial(Material.WOODEN_AXE), NormalAttack.melee(ElementType.PHYSICAL, AttributeType.ATTACK, 25, 10));
        
        setName(WeaponGorkaMorka.WEAPON_NAME.append(Component.text(" (Missing)")));
        
        setAbility(AbilityType.RIGHT_CLICK, abilityRecall = new AbilityRecall());
    }
    
    public void startRecallCooldown(@NotNull HariantPlayer player) {
        player.setCooldown(abilityRecall);
    }
    
    public static class AbilityRecall extends Ability {
        
        AbilityRecall() {
            super(Key.ofString("ability_recall"), Component.text("Recall"));
            
            setDescription(
                    Component.empty()
                             .append(Component.text("Recall your poleaxe."))
                             .appendNewline()
                             .appendNewline()
                             .append(Component.text("If the axe is currently in an "))
                             .append(Component.text("enemy", Colors.RED))
                             .append(Component.text(", deal additional "))
                             .append(ElementType.ICE.asComponentDamage())
                             .append(Component.text("."))
            );
            
            // This is more of an internal cooldown so you can't recall right away
            setCooldownSeconds(1);
        }
        
        @Override
        public @NotNull Response execute(@NotNull HariantPlayer player) {
            final HeroDataOrc heroData = player.getHeroData(HeroRegistry.ORC, HeroDataOrc::new);
            final WeaponGorkaMorkaEntity weaponEntity = heroData.getWeaponEntity();
            
            if (weaponEntity == null) {
                return Response.error("The axe isn't thrown!");
            }
            
            if (!weaponEntity.recall()) {
                return Response.error("Already recalled!");
            }
            
            return Response.hold();
        }
        
    }
    
    @Override
    public @NotNull Component getPrimaryAbilityCooldownComponent() {
        return WeaponGorkaMorka.COMPONENT_ABILITY_COOLDOWN_MISSING;
    }
    
}