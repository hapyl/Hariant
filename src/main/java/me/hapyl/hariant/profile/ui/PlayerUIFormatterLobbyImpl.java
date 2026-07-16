package me.hapyl.hariant.profile.ui;

import me.hapyl.eterna.module.component.ComponentList;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.inventory.HariantInventory;
import me.hapyl.hariant.inventory.item.ResourceRegistry;
import me.hapyl.hariant.profile.PlayerProfile;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public class PlayerUIFormatterLobbyImpl implements PlayerUIFormatter {
    
    PlayerUIFormatterLobbyImpl() {
    }
    
    @Override
    public void formatScoreboard(@NotNull PlayerProfile profile, @NotNull ComponentList components) {
        components.append(
                Component.empty()
                         .append(profile.asHeadComponent())
                         .append(Component.text(" You, ", Colors.GOLD))
                         .append(profile.getName().color(Colors.GOLD))
        );
        
        components.append(
                Component.empty()
                         .append(Component.text(" ʀᴀɴᴋ: ", Colors.GRAY))
                         .append(profile.getRank().formatter().getPrefix())
        );
        
        components.append(
                Component.empty()
                         .append(Component.text(" ʜᴇʀᴏ: ", Colors.GRAY))
                         .append(profile.getSelectedHero().asComponent())
        );
        
        components.append(
                Component.empty()
                         .append(Component.text(" ᴄᴀᴛᴄᴏɪɴꜱ: ", Colors.GRAY))
                         .append(ResourceRegistry.CAT_COINS.format(profile.getDatabase()))
        );
        
        final HariantInventory inventory = profile.getDatabase().inventory;
        final int rubies = inventory.getResource(ResourceRegistry.RUBY);
        
        if (rubies > 0) {
            components.append(
                    Component.empty()
                             .append(Component.text(rubies == 1 ? " ʀᴜʙʏ: " : " ʀᴜʙɪᴇꜱ: ", Colors.GRAY))
                             .append(ResourceRegistry.RUBY.format(profile.getDatabase()))
            );
        }
    }
    
}
