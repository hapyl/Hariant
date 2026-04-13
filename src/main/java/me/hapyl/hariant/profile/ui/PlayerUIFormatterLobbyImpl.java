package me.hapyl.hariant.profile.ui;

import me.hapyl.eterna.module.component.ComponentList;
import me.hapyl.hariant.inventory.item.ResourceRegistry;
import me.hapyl.hariant.profile.PlayerProfile;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;

public class PlayerUIFormatterLobbyImpl implements PlayerUIFormatter {
    
    PlayerUIFormatterLobbyImpl() {
    }
    
    @Override
    public void formatScoreboard(@NotNull PlayerProfile profile, @NotNull ComponentList components) {
        components.append(
                Component.empty()
                         .append(profile.asHeadComponent())
                         .append(Component.text(" You, ", NamedTextColor.GREEN))
                         .append(profile.getName().color(NamedTextColor.GREEN))
        );
        
        components.append(
                Component.empty()
                         .append(Component.text(" ʀᴀɴᴋ: ", NamedTextColor.GRAY))
                         .append(profile.getRank().formatter().getPrefix())
        );
        
        components.append(
                Component.empty()
                         .append(Component.text(" ʜᴇʀᴏ: ", NamedTextColor.GRAY))
                         .append(profile.getSelectedHero().asComponent())
        );
        
        components.append(
                Component.empty()
                         .append(Component.text(" ᴄᴀᴛᴄᴏɪɴꜱ: ", NamedTextColor.GRAY))
                         .append(ResourceRegistry.CAT_COINS.format(profile.getDatabase()))
        );
        
        // TODO @Feb 20, 2026 (xanyjl) -> Add rubies
    }
    
}
