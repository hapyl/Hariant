package me.hapyl.hariant.profile.ui;

import me.hapyl.eterna.module.component.ComponentList;
import me.hapyl.eterna.module.player.tablist.EntryList;
import me.hapyl.eterna.module.player.tablist.EntryTexture;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.inventory.HariantInventory;
import me.hapyl.hariant.inventory.item.ResourceRegistry;
import me.hapyl.hariant.profile.PlayerProfile;
import me.hapyl.hariant.team.EnumTeam;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;

import java.util.List;

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
    
    @Override
    public void formatTablistSystem(@NotNull PlayerProfile profile, @NotNull EntryList entryList) {
        // Display lobby information
        entryList.append(Component.text("Lobby", Colors.AQUA), EntryTexture.AQUA);
        entryList.append(Component.text(" ʙᴀᴛᴛʟᴇɢʀᴏᴜɴᴅ: ", Colors.GRAY).append(Hariant.getSelectedBattleground().getName().color(Colors.WHITE)));
        entryList.append(Component.text(" ɢᴀᴍᴇ ᴛʏᴘᴇ: ", Colors.GRAY).append(Hariant.getSelectedGameType().getName().color(Colors.WHITE)));
        entryList.append();
        
        // FIXME (xanyjl @ Thursday, July 30) -> Game formatter uses three lines, so might want to add something here to make it consistent if you give a fuck
        
        // Display team information
        final EnumTeam team = profile.getTeam();
        
        entryList.append(
                Component.empty()
                         .append(Component.text("Team ", team.getStyle()))
                         .append(Component.text("(", Colors.GRAY))
                         .append(team.getName().color(Colors.GRAY))
                         .append(Component.text(")", Colors.GRAY)),
                team.getEntryTexture()
        );
        
        final List<PlayerProfile> members = team.getPlayerProfiles().toList();
        
        for (int i = 0; i < EnumTeam.MAX_PLAYERS; i++) {
            if (i < members.size()) {
                final PlayerProfile member = members.get(i);
                
                entryList.append(EnumTeam.createMemberPrefix(
                        member.getNameFormatted()
                              .appendSpace()
                              .append(Component.text("(", Colors.GRAY))
                              .append(member.getSelectedHero().getName().color(Colors.GOLD))
                              .append(Component.text(")", Colors.GRAY))
                ));
            }
            else {
                entryList.append(EnumTeam.createMemberPrefix(null));
            }
        }
        
        entryList.append();
        
        // Display store information
        entryList.append(Component.text("Store ", Colors.GOLD).append(Component.text("COMING SOON", Colors.RED, TextDecoration.BOLD)), EntryTexture.GOLD);
        entryList.append(Component.text(" ...", Colors.DARK_GRAY));
        entryList.append(Component.text(" ...", Colors.DARK_GRAY));
        entryList.append(Component.text(" ...", Colors.DARK_GRAY));
        entryList.append(Component.text(" ...", Colors.DARK_GRAY));
    }
    
    @Override
    public @NotNull Component createTablistFooter(@NotNull PlayerProfile profile, @NotNull PlayerUIFormatter formatter) {
        return Component.empty();
    }
    
}
