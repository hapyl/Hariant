package me.hapyl.hariant.hero.alchemist;

import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.heal.HealingSource;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.event.HariantDamageEvent;
import me.hapyl.hariant.hero.HeroRegistry;
import me.hapyl.hariant.talent.field.DisplayField;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Color;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import static org.bukkit.Sound.BLOCK_BREWING_STAND_BREW;

public final class TalentAlchemistPotionHealing extends TalentAlchemistPotion implements Listener {
    
    @DisplayField private final Decimal healing = Decimal.ofPercentage(30);
    @DisplayField private final Decimal extraHealing = Decimal.ofPercentage(10);
    
    TalentAlchemistPotionHealing(@NotNull TalentAbyssalBottle talent) {
        super(talent, "healing", Component.text("Potion of Healing"), Color.fromRGB(209, 13, 19), 20);
        
        // This is also used for extra healing delay
        this.setDurationSeconds(3);
        
        this.setDescription(
                Component.empty()
                         .append(Component.text("Heals your for "))
                         .append(healing)
                         .append(Component.text(" of your "))
                         .append(AttributeType.MAX_HEALTH)
                         .append(Component.text("."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("If you "))
                         .append(Component.text("don't take", NamedTextColor.GRAY, TextDecoration.UNDERLINED))
                         .append(Component.text(" damage in "))
                         .append(this.getDurationFormatted())
                         .append(Component.text(" after using this potion, heal for an additional "))
                         .append(extraHealing)
                         .append(Component.text(" of your "))
                         .append(AttributeType.MAX_HEALTH)
                         .append(Component.text("."))
        );
        
    }
    
    @NotNull
    @Override
    public AlchemistPotionInstance drink(@NotNull HariantPlayer player, @NotNull HeroDataAlchemist heroData) {
        final double maxHealth = player.getMaxHealth();
        
        // Heal the player
        player.heal(HealingSource.create(maxHealth * healing.doubleValue()));
        
        // Schedule extra healing
        return new ExtraHealingPotionInstance(player);
    }
    
    @EventHandler
    public void handleHariantDamageEvent(HariantDamageEvent ev) {
        final HariantEntity entity = ev.getEntity();
        
        if (!(entity instanceof HariantPlayer player)) {
            return;
        }
        
        if (!player.getHero().equals(HeroRegistry.ALCHEMIST)) {
            return;
        }
        
        final HeroDataAlchemist heroData = player.getHeroData(HeroRegistry.ALCHEMIST, HeroDataAlchemist::new);
        final AlchemistPotionInstance potionInstance = heroData.getPotionInstance();
        
        if (!(potionInstance instanceof ExtraHealingPotionInstance extraHealingPotionInstance)) {
            return;
        }
        
        extraHealingPotionInstance.cancel();
        heroData.setPotionInstance(null);
    }
    
    public class ExtraHealingPotionInstance extends AlchemistPotionInstance {
        
        private final double extraHealing;
        
        ExtraHealingPotionInstance(@NotNull HariantPlayer player) {
            super(player, TalentAlchemistPotionHealing.this);
            
            this.extraHealing = TalentAlchemistPotionHealing.this.extraHealing.doubleValue() * player.getMaxHealth();
        }
        
        @Override
        public boolean tick() {
            super.tick();
            
            // Heal the player
            if (this.currentTick() == 0) {
                player.heal(HealingSource.create(extraHealing));
                
                player.sendTitleSubtitle(
                        Component.text("\uD83D\uDC9E", NamedTextColor.DARK_GREEN),
                        Component.text("ʜᴇᴀʟᴇᴅ!", NamedTextColor.GREEN),
                        5, 10, 5
                );
                
                player.playWorldSound(BLOCK_BREWING_STAND_BREW, 1.25f);
            }
            
            return false;
        }
        
        public void cancel() {
            // Fx
            player.sendTitleSubtitle(
                    Component.text("\uD83D\uDC9E", NamedTextColor.DARK_RED),
                    Component.text("ʜᴇᴀʟɪɴɢ ᴄᴀɴᴄᴇʟʟᴇᴅ, ʏᴏᴜ ᴛᴏᴏᴋ ᴅᴀᴍᴀɢᴇ!", NamedTextColor.RED),
                    5, 10, 5
            );
            
            player.playWorldSound(Sound.ENTITY_WITCH_HURT, 1.25f);
        }
    }
    
}
