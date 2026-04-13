package me.hapyl.hariant.hero.alchemist;

import me.hapyl.eterna.module.component.Keybind;
import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.eterna.module.inventory.menu.ChestSize;
import me.hapyl.eterna.module.inventory.menu.pattern.SlotPattern;
import me.hapyl.eterna.module.inventory.menu.pattern.SlotPatternApplier;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.MapMaker;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.hero.HeroRegistry;
import me.hapyl.hariant.menu.Menu;
import me.hapyl.hariant.menu.hero.MenuHeroTalents;
import me.hapyl.hariant.talent.Response;
import me.hapyl.hariant.talent.Talent;
import me.hapyl.hariant.talent.TalentContext;
import me.hapyl.hariant.talent.TalentType;
import me.hapyl.hariant.talent.field.DisplayField;
import me.hapyl.hariant.talent.target.TalentTarget;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public final class TalentAbyssalBottle extends Talent implements MenuHeroTalents.SubMenuHandler {
    
    @DisplayField public final Decimal drinkDelay = Decimal.ofSeconds(0.5f);
    
    private final Map<Integer, TalentAlchemistPotion> alchemistPotionsMapped;
    
    public TalentAbyssalBottle(@NotNull Key key) {
        super(key, Component.text("Abyssal Bottle"), Icon.ofMaterial(Material.OMINOUS_BOTTLE));
        
        this.setTalentType(TalentType.ENHANCE);
        this.setCooldownSeconds(10f);
        
        // Maps talents by slot
        this.alchemistPotionsMapped = MapMaker.<Integer, TalentAlchemistPotion>ofLinkedHashMap()
                                              .put(1, new TalentAlchemistPotionHealing(this))
                                              .put(2, new TalentAlchemistPotionSpeed(this))
                                              .put(3, new TalentAlchemistPotionAttack(this))
                                              .put(4, new TalentAlchemistPotionInvisibility(this))
                                              .put(5, new TalentAlchemistPotionDefense(this))
                                              .makeMap();
        
        this.setDescription(
                Component.empty()
                         .append(Component.text("Shake the "))
                         .append(Component.text("abyssal bottle", Colors.ABYSS))
                         .append(Component.text(" to conjure five potent potions, which will "))
                         .append(Component.text("replace your talents", NamedTextColor.GRAY, TextDecoration.UNDERLINED))
                         .append(Component.text("."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("Shake the bottle again to store the potions back."))
        );
    }
    
    @NotNull
    @Override
    public TalentTarget target(@NotNull HariantPlayer player) {
        return TalentTarget.none();
    }
    
    @NotNull
    @Override
    public Response execute(@NotNull HariantPlayer player, @NotNull TalentContext context) {
        final HeroDataAlchemist heroData = player.getHeroData(HeroRegistry.ALCHEMIST, HeroDataAlchemist::new);
        
        // No need to check for state because player can't be in SELECT_POTION and execute this talent
        heroData.setState(State.SELECT_POTION);
        
        // The talent cooldown is for when we use a potion, so we await and start a 10 tick cooldown to prevent miss-clicks
        player.setCooldown(this, 10);
        player.snapTo(7);
        
        // Fx
        player.playWorldSound(Sound.ITEM_ARMOR_EQUIP_CHAIN, 0.75f);
        
        return Response.await();
    }
    
    @Nullable
    public TalentAlchemistPotion getAlchemistPotion(int slot) {
        return alchemistPotionsMapped.get(slot);
    }
    
    public void giveAlchemistPotions(@NotNull HariantPlayer player) {
        player.clearHotBar();
        
        // Put abyssal bottle on the first slot
        player.setHotBarItem(0, this.createItem());
        
        alchemistPotionsMapped.forEach((slot, potion) -> {
            player.setHotBarItem(slot, potion.createItem());
        });
        
        // Return weapon as well
        player.getHero().giveWeapon(player);
    }
    
    @NotNull
    @Override
    public String subMenuAction() {
        return "see potions descriptions";
    }
    
    @Override
    public void openSubMenu(@NotNull Player player, @NotNull Menu menu) {
        new SubMenu(player, menu);
    }
    
    public class SubMenu extends MenuHeroTalents.SubMenu {
        
        SubMenu(@NotNull Player player, @NotNull Menu returnMenu) {
            super(player, Component.text("Potion Descriptions"), returnMenu);
            
            this.openMenu();
        }
        
        @Override
        public void updateMenu() {
            final SlotPatternApplier applier = newSlotPatternApplier(SlotPattern.DEFAULT, ChestSize.SIZE_3);
            
            alchemistPotionsMapped.forEach((slot, potion) -> {
                applier.add(potion.createItem());
            });
            
            applier.apply();
        }
        
    }
    
}
