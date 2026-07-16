package me.hapyl.hariant.npc;

import me.hapyl.eterna.module.location.LocationHelper;
import me.hapyl.eterna.module.npc.ClickType;
import me.hapyl.eterna.module.npc.NpcProperties;
import me.hapyl.eterna.module.npc.appearance.AppearanceBuilder;
import me.hapyl.eterna.module.player.dialog.entry.DialogEntry;
import me.hapyl.eterna.module.player.dialog.entry.DialogEntryOptions;
import me.hapyl.eterna.module.player.dialog.entry.OptionIndex;
import me.hapyl.eterna.module.reflect.Skin;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.dialog.HariantDialog;
import me.hapyl.hariant.inventory.drop.Amount;
import me.hapyl.hariant.inventory.drop.DropTable;
import me.hapyl.hariant.inventory.drop.Droppable;
import me.hapyl.hariant.inventory.item.ResourceRegistry;
import me.hapyl.hariant.menu.artifact.MenuArtifactCreation;
import net.kyori.adventure.text.Component;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class HariantNpcMrNerd extends HariantNpc {
    
    private final DropTable dropTable;
    private final HariantDialog dialogFirstMeeting;
    
    
    public HariantNpcMrNerd(@NotNull Key key) {
        super(
                key,
                LocationHelper.defaultLocation(4.5, 63.0, 6.5),
                Component.text("Mr. Nerd", Colors.GOLD),
                AppearanceBuilder.ofMannequin(
                        Skin.of(
                                "ewogICJ0aW1lc3RhbXAiIDogMTc1MTg5NDIzNTM3NCwKICAicHJvZmlsZUlkIiA6ICI0NmNhODkyZTY4ODA0YThmYjFkYzkwYjg0ZTY5ZjVmZSIsCiAgInByb2ZpbGVOYW1lIiA6ICJPbG8xNjA2IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzdlZmU0YmMzNDU5NmRhNzAzMDU0ZjZiODJlZTU4ODk1ZjlhMDVmODRlODEwYmFhNmRmMjQ1NDliZmM0ZWMyZjIiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ==",
                                "CMW+NtGUFC9Phe1KSqhD47ZwmwGPKtFakgUk9eZIIOXGNvnqvo+b4FeOOTz62j5GaIEnMNR9viUsw+62tQvddDFoJkj8bsRFLwBkVjPwYFHh4DoPqsVwfXmrw1DfUMIW8NClIrDC4E9MkSj11NxYj8fkcEysQtQq83IBkOfUk0dW0jTAudYQukIkNMI7I+laF15wDvla5GCmBdfJQtCCsU3UI8ARno44MNEvTyWVbFf3E6pe16Y31Ko7xWZJkC18+wfT2Pfu9bUmhHLig8pr3AwJz/vEDqi2V28DMloSzce2UYxRi+ZKPfQBU/5drRoLqr/QqMxPsgbXHs5zlxENfamDXjT8XZqVjyPXhlcnJeKPJSme9a/BjTtWKsWAopa3iJQHLmtktBIblE4nD30z65vMSpkIHRTQ0/rDtBP+3xOW1PqixwK+0CxacKbsqFJnYIlZzZN9d+DGP+N9WEliIBGVXl+8r3IEp38Zw2H0SlunA2VIIIIPyk+nropVNc9viFI2imL8yAjrf7yvyVA1MmV5ZcRMu6Tvpt5/MptPhSVSFRgvjf24hnIecphyGpXxtVs1aZgw592CRyyGDZC1GiE4vVUzmM8J3TzLHS0bI+ogRRpXnA1AY2QXXEfO6KkdyRkL2e0lvsN8GKoKhSR5o1+JFlOmCgw5vXtbXh/F6oU="
                        )
                )
        );
        
        final NpcProperties properties = getProperties();
        
        properties.setLookAtClosePlayerDistance(8);
        
        dropTable = DropTable.create(
                List.of(Droppable.ofResource(ResourceRegistry.ARTIFACT_ARTIFICER, 1, Amount.fixed(50))),
                Amount.fixed(1)
        );
        dialogFirstMeeting = new DialogFirstMeeting();
    }
    
    @Override
    public void onClick(@NotNull Player player, @NotNull ClickType clickType) {
        if (!dialogFirstMeeting.hasCompleted(player)) {
            dialogFirstMeeting.start(player);
        }
        else {
            new MenuArtifactCreation(player);
        }
    }
    
    @Override
    public void sendMessage(@NotNull Player player, @NotNull Component message) {
        super.sendMessage(player, message);
        
        player.playSound(player, Sound.ENTITY_VILLAGER_TRADE, 3, 1.25f);
    }
    
    private class DialogFirstMeeting extends HariantDialog {
        
        DialogFirstMeeting() {
            super(Key.ofString("mr_nerd_first_meeting_new"), Component.text("Mr. Nerd's Dialog"), false);
            
            addEntry(
                    DialogEntry.ofNpc(
                            HariantNpcMrNerd.this,
                            Component.text("Greetings, {player}!"),
                            Component.text("I'm the temporary overseer of this place, {npc_name}!"),
                            Component.text("I posses the best kind of power - knowledge!"),
                            Component.empty()
                                     .append(Component.text("I can use the power of knowledge to artifice "))
                                     .append(Component.text("Artifacts", Colors.GOLD))
                                     .append(Component.text("!")),
                            Component.empty()
                                     .append(Component.text("Artificing is a complicated process that requires a rare material - "))
                                     .append(ResourceRegistry.ARTIFACT_ARTIFICER.getName())
                                     .append(Component.text("!"))
                    )
            );
            
            addEntry(
                    DialogEntry.ofSelectableOptions(DialogEntry.ofNpc(HariantNpcMrNerd.this, Component.text("As long as you got the materials, I can always artifice artifacts for you!")))
                               .setOption(
                                       OptionIndex.OPTION_1,
                                       DialogEntryOptions.builder(Component.text("I don't have any."))
                                                         .append(DialogEntry.ofNpc(HariantNpcMrNerd.this, Component.text("That's a shame...")))
                                                         .advanceDialog(true)
                               )
                               .setOption(
                                       OptionIndex.OPTION_2,
                                       DialogEntryOptions.builder(Component.text("Aren't you so smart, so handsome and so successful? Can't you do that for free?"))
                                                         .append(DialogEntry.ofNpc(
                                                                 HariantNpcMrNerd.this,
                                                                 Component.text("I am indeed so smart, so handsome and so successful!"),
                                                                 Component.text("But even a person as me cannot do the impossible...")
                                                         ))
                                                         .advanceDialog(true)
                               )
            );
            
            addEntry(
                    DialogEntry.ofNpc(
                            HariantNpcMrNerd.this,
                            Component.text("Anyways, today is your lucky day!"),
                            Component.text("Because I have some spare for you, make sure to use the wisely!"),
                            Component.text("Talk to me again to open the Artifact Artificing Menu!")
                    )
            );
        }
        
        @Override
        public void complete(@NotNull Player player) {
            super.complete(player);
            
            dropTable.generateLootShowSummary(Hariant.getPlayerProfile(player));
        }
    }
    
}