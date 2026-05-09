package me.hapyl.hariant.profile;

import me.hapyl.eterna.module.player.dialog.Dialog;
import me.hapyl.eterna.module.player.dialog.DialogInstance;
import me.hapyl.eterna.module.reflect.glowing.Glowing;
import me.hapyl.eterna.module.util.Ticking;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.database.PlayerDatabase;
import me.hapyl.hariant.database.rank.PlayerRank;
import me.hapyl.hariant.entity.HeadComponent;
import me.hapyl.hariant.entity.Lifecycle;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.game.GameInstance;
import me.hapyl.hariant.game.GameInstanceHandler;
import me.hapyl.hariant.game.battleground.EnumBattleground;
import me.hapyl.hariant.hero.Hero;
import me.hapyl.hariant.hero.HeroInstance;
import me.hapyl.hariant.inventory.drop.DropTable;
import me.hapyl.hariant.lobby.EnumLobbyItem;
import me.hapyl.hariant.profile.ui.PlayerUI;
import me.hapyl.hariant.team.EnumTeam;
import me.hapyl.hariant.team.TeamEntry;
import me.hapyl.hariant.team.TeamEntryProvider;
import me.hapyl.hariant.util.UniquelyIdentified;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.object.ObjectContents;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public final class PlayerProfile
        implements
        Lifecycle, Ticking, UniquelyIdentified, TeamEntryProvider,
        ForwardingAudience.Single, GameInstanceHandler, HeadComponent {
    
    private final Player player;
    private final PlayerDatabase database;
    
    private final PlayerUI playerUI;
    private int tick;
    private boolean isSpectator;
    
    public PlayerProfile(@NotNull Player player) {
        this.player = player;
        this.database = new PlayerDatabase(Hariant.getPlugin().getDatabase(), player.getUniqueId());
        this.playerUI = new PlayerUI(this);
    }
    
    @NotNull
    public PlayerRank getRank() {
        return database.getRank();
    }
    
    @NotNull
    public Component getName() {
        return player.name();
    }
    
    @NotNull
    public Component getNameFormatted() {
        return getRank().formatter().format(this);
    }
    
    @NotNull
    public Player getPlayer() {
        return player;
    }
    
    @NotNull
    @Override
    public Component asHeadComponent() {
        return Component.object(ObjectContents.playerHead(player)).color(NamedTextColor.WHITE);
    }
    
    @NotNull
    public Optional<HariantPlayer> getHariantPlayer() {
        return Hariant.getPlayer(this.getPlayer());
    }
    
    @NotNull
    public PlayerDatabase getDatabase() {
        return database;
    }
    
    @NotNull
    public PlayerUI getPlayerUI() {
        return playerUI;
    }
    
    @NotNull
    public Hero getSelectedHero() {
        return database.hero.getSelectedHero();
    }
    
    @NotNull
    public HeroInstance getSelectedHeroInstance() {
        return database.hero.getSelectedHeroInstance();
    }
    
    @Override
    public void onCreate() {
        // Join the smallest team
        EnumTeam.getSmallestTeam().addPlayer(this);
        
        final boolean gameInProgress = Hariant.isGameInProgress();
        
        // Create vanilla teams
        playerUI.getVanillaTeamManager().onCreate();
        
        VanillaTeamManager.bumpOtherProfilesToCreateTeamForMe(this);
        
        // If the game is currently in progress, put the player in spectator
        if (gameInProgress) {
            this.setSpectator(true);
        }
        else {
            // Give lobby items and teleport to origin if not in creative
            if (player.getGameMode() != GameMode.CREATIVE) {
                teleportToSpawnAndGiveLobbyItems();
            }
        }
    }
    
    @Override
    public void onDestroy() {
        // Save database
        this.database.save();
        
        VanillaTeamManager.bumpOtherProfilesToDeleteTeamForMe(this);
        
        // Leave team
        this.getTeam().removeEntry(this);
    }
    
    public boolean isSpectator() {
        return isSpectator;
    }
    
    public void setSpectator(boolean spectator) {
        if (spectator) {
            this.isSpectator = true;
            this.player.setGameMode(GameMode.SPECTATOR);
            
            // Update vanilla teams to SPECTATEE, unless self
            this.playerUI.getVanillaTeamManager().setStateForAllProfilesExceptSelf(VanillaTeamManager.State.IN_GAME_SPECTATEE);
            
            // Teleport to a random in-game player
            Hariant.getPlayers().findAny().ifPresent(player -> {
                this.player.teleport(player.getLocation().add(0, 1.5, 0));
            });
        }
        else {
            // Realistically this should never be called manually, but...
            this.isSpectator = false;
            this.playerUI.getVanillaTeamManager().setStateForAllProfilesExceptSelf(VanillaTeamManager.State.LOBBY);
        }
    }
    
    @Override
    public void tick() {
        tick++;
        
        // Reduce ui updates
        if (tick % 10 == 0) {
            playerUI.tick();
        }
    }
    
    @NotNull
    @Override
    public UUID getUuid() {
        return player.getUniqueId();
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(this.player);
    }
    
    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        
        final PlayerProfile that = (PlayerProfile) object;
        return Objects.equals(this.player.getUniqueId(), that.player.getUniqueId());
    }
    
    @NotNull
    @Override
    public TeamEntry teamEntry() {
        return TeamEntry.create(this.getUuid(), true);
    }
    
    @NotNull
    @Override
    public Audience audience() {
        return player;
    }
    
    @NotNull
    public EnumTeam getTeam() {
        return Objects.requireNonNull(EnumTeam.getEntryTeam(this), "Player cannot not be in a team!");
    }
    
    @Override
    public void handleInstanceCreated(@NotNull GameInstance gameInstance) {
        this.playerUI.getVanillaTeamManager().setStateForAllProfiles(false);
    }
    
    @Override
    public void handlerInstanceDestroyed(@NotNull GameInstance gameInstance) {
        this.playerUI.getVanillaTeamManager().setStateForAllProfiles(true);
        this.isSpectator = false;
        
        // Stop glowing for all players
        Glowing.stopGlowing(player);
        
        // Destroy player
        Hariant.destroyEntity(this.getUuid());
        
        this.teleportToSpawnAndGiveLobbyItems();
        this.player.setGameMode(GameMode.SURVIVAL);
     
        // Generate loot
        final DropTable dropTable = gameInstance.getBattleground().getDropTable();
        
        dropTable.generateLootAnDrop(this);
    }
    
    public void teleportToSpawnAndGiveLobbyItems() {
        // Teleport to the spawn
        player.teleport(EnumBattleground.SPAWN.getSpawnLocations().getFirst().getLocation());
        
        // Give lobby items
        EnumLobbyItem.giveAll(player);
    }
    
    @NotNull
    public Optional<DialogInstance> getCurrentDialog() {
        return Dialog.getCurrentDialog(player);
    }
    
    @Override
    public String toString() {
        return "PlayerProfile(%s)".formatted(player.getName());
    }
    
    public void saveDatabaseSync() {
        database.save(false);
    }
}
