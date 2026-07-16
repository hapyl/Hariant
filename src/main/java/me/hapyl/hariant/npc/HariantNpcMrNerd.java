package me.hapyl.hariant.npc;

import me.hapyl.eterna.module.component.ButtonComponents;
import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.eterna.module.inventory.menu.ChestSize;
import me.hapyl.eterna.module.inventory.menu.action.PlayerMenuAction;
import me.hapyl.eterna.module.inventory.menu.pattern.SlotPattern;
import me.hapyl.eterna.module.inventory.menu.pattern.SlotPatternApplier;
import me.hapyl.eterna.module.location.LocationHelper;
import me.hapyl.eterna.module.npc.ClickType;
import me.hapyl.eterna.module.npc.NpcProperties;
import me.hapyl.eterna.module.npc.appearance.AppearanceBuilder;
import me.hapyl.eterna.module.player.dialog.entry.DialogEntry;
import me.hapyl.eterna.module.reflect.Skin;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.database.PlayerDatabase;
import me.hapyl.hariant.dialog.HariantDialog;
import me.hapyl.hariant.inventory.item.ItemRegistry;
import me.hapyl.hariant.inventory.item.artifact.ItemArtifact;
import me.hapyl.hariant.menu.Menu;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class HariantNpcMrNerd extends HariantNpc {
    
    private final HariantDialog dialogFirstMeeting;
    private final HariantDialog dialogCreating;
    
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
        
        dialogFirstMeeting = new DialogFirstMeeting();
        dialogCreating = new DialogArtifactCreation();
    }
    
    @Override
    public void onClick(@NotNull Player player, @NotNull ClickType clickType) {
        if (!dialogFirstMeeting.hasCompleted(player)) {
            dialogFirstMeeting.start(player);
        }
        else {
            new ArtifactCreatingMenu(player);
        }
    }
    
    private class DialogFirstMeeting extends HariantDialog {
        DialogFirstMeeting() {
            super(Key.ofString("mr_nerd_first_meeting"), Component.text("Mr. Nerd's Dialog"), false);
            
            addEntry(
                    DialogEntry.ofNpc(
                            HariantNpcMrNerd.this,
                            Component.text("Greetings, {player}!"),
                            Component.text("I'm the temporary overseer of this place, {npc_name}!"),
                            Component.text("I posses the best kind of power - knowledge!"),
                            Component.empty()
                                     .append(Component.text("I can use the power of knowledge to create "))
                                     .append(Component.text("Artifacts", Colors.GOLD))
                                     .append(Component.text("!")),
                            Component.text("And since I'm so smart, so handsome, and so successful, I will do that for free!"),
                            Component.text("Talk to me again to open the Artifact Creating Menu!")
                    )
            );
        }
    }
    
    // TODO (xanyjl @ Friday, June 26) -> This needs to be a Page menu later
    private class ArtifactCreatingMenu extends Menu {
        public ArtifactCreatingMenu(@NotNull Player player) {
            super(player, () -> Component.text("Artifact Creating Menu"), ChestSize.SIZE_6);
            
            this.openMenu();
        }
        
        @Override
        public void updateMenu() {
            final SlotPatternApplier applier = newSlotPatternApplier(SlotPattern.INNER_LEFT_TO_RIGHT, ChestSize.SIZE_2);
            
            ItemRegistry.getRegistry().values()
                        .stream()
                        .filter(ItemArtifact.class::isInstance)
                        .map(ItemArtifact.class::cast)
                        .forEach(item -> {
                            final ItemBuilder builder = item.createBuilder();
                            
                            builder.addLore();
                            builder.addLore(Component.text("                                            ", Colors.DARK_GRAY, TextDecoration.STRIKETHROUGH));
                            builder.addLore();
                            builder.addLore(ButtonComponents.left("create one artifact"));
                            builder.addLore(ButtonComponents.right("create ten artifacts"));
                            builder.addLore(ButtonComponents.middle("create twenty artifacts"));
                            
                            applier.add(
                                    builder.asIcon(),
                                    PlayerMenuAction.builder()
                                                    .left(player -> createArtifacts(item, 1))
                                                    .right(player -> createArtifacts(item, 10))
                                                    .middle(player -> createArtifacts(item, 20))
                                                    .build()
                            );
                        });
            
            applier.apply();
        }
        
        private void createArtifacts(@NotNull ItemArtifact artifact, int amount) {
            final PlayerDatabase database = Hariant.getPlayerDatabase(player);
            
            for (int i = 0; i < amount; i++) {
                database.inventory.createItem(artifact);
            }
            
            dialogCreating.startForcefully(player);
            closeMenu();
        }
    }
    
    private class DialogArtifactCreation extends HariantDialog {
        DialogArtifactCreation() {
            super(Key.ofString("mr_nerd_artifact_creation"), Component.text("Artifact Creation"), true);
            
            addEntry(
                    DialogEntry.ofNpc(
                            HariantNpcMrNerd.this,
                            Component.text("ᔑリ↸ ℸ ̣ ⍑⚍ᓭ ╎ ⚍ᓭᒷ ℸ ̣ ⍑ᒷ !¡\uD835\uDE79∴ᒷ∷ \uD835\uDE79⎓ ꖌリ\uD835\uDE79∴ꖎᒷ↸⊣ᒷ", Colors.LIGHT_PURPLE),
                            Component.text("ℸ ̣ \uD835\uDE79 ᓵ∷ᒷᔑℸ ̣ ᒷ ∴⍑ᔑℸ ̣  ||\uD835\uDE79⚍ ↸ᒷᓭ╎∷ᒷ!", Colors.LIGHT_PURPLE),
                            Component.text("Done, enjoy!")
                    )
            );
        }
    }
}