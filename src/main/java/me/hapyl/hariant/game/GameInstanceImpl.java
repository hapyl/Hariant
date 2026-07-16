package me.hapyl.hariant.game;

import me.hapyl.eterna.module.component.ComponentList;
import me.hapyl.eterna.module.text.TimeFormat;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.event.HariantGameInstanceStateEvent;
import me.hapyl.hariant.game.battleground.EnumBattleground;
import me.hapyl.hariant.game.type.GameType;
import me.hapyl.hariant.profile.PlayerProfile;
import me.hapyl.hariant.team.EnumTeam;
import me.hapyl.hariant.team.TeamData;
import me.hapyl.hariant.team.TeamDataMap;
import me.hapyl.hariant.util.HexId;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public class GameInstanceImpl implements GameInstance {
    
    private final HexId hexId;
    
    private final GameType gameType;
    private final EnumBattleground battleground;
    private final TeamDataMap teamDataMap;
    
    private GameInstanceState state;
    private int timeLeft;
    
    public GameInstanceImpl(@NotNull GameType gameType, @NotNull EnumBattleground battleground) {
        this.battleground = battleground;
        this.hexId = HexId.ofRandom();
        this.gameType = gameType;
        this.teamDataMap = new TeamDataMap();
        this.state = GameInstanceState.PREPARING;
        this.timeLeft = gameType.getTimeLimit();
    }
    
    @NotNull
    @Override
    public GameInstanceState getState() {
        return state;
    }
    
    @Override
    public void setState(@NotNull GameInstanceState state) {
        this.state = state;
        
        // Call event
        new HariantGameInstanceStateEvent(this, state).callEvent();
    }
    
    @NotNull
    @Override
    public TeamDataMap getTeamData() {
        return teamDataMap;
    }
    
    @NotNull
    @Override
    public TeamData getTeamData(@NotNull EnumTeam team) {
        return teamDataMap.getData(team);
    }
    
    @NotNull
    @Override
    public GameType getType() {
        return gameType;
    }
    
    @NotNull
    public EnumBattleground getBattleground() {
        return battleground;
    }
    
    @NotNull
    @Override
    public HexId getId() {
        return hexId;
    }
    
    @Override
    public void onCreate() {
    }
    
    @Override
    public void onDestroy() {
    }
    
    @Override
    public void tick() {
        this.timeLeft--;
        
        // Tick battleground
        this.battleground.tick();
    }
    
    @Override
    public int getTimeLeft() {
        return timeLeft;
    }
    
    @Override
    public void onKill(@NotNull GameInstance gameInstance, @NotNull HariantPlayer player, @NotNull HariantPlayer victim) {
        teamDataMap.getData(player.getPlayerTeam()).kills++;
        
        gameType.onKill(gameInstance, player, victim);
    }
    
    @Override
    public void onDeath(@NotNull GameInstance gameInstance, @NotNull HariantPlayer player) {
        teamDataMap.getData(player.getPlayerTeam()).deaths++;
        
        gameType.onDeath(gameInstance, player);
    }
    
    @Override
    public void formatScoreboard(@NotNull PlayerProfile profile, @NotNull ComponentList components) {
        components.append(
                Component.empty()
                         .append(Component.text("ᴍᴏᴅᴇ: ", Colors.WHITE))
                         .append(gameType.getName().color(Colors.GOLD))
        );
        
        components.append(
                Component.empty()
                         .append(Component.text("ᴛɪᴍᴇ ʟᴇꜰᴛ: ", Colors.WHITE))
                         .append(Component.text(TimeFormat.format(this.getTimeLeft() * 50L, TimeFormat.Part.MINUTES, TimeFormat.Part.SECONDS), Colors.GOLD))
        );
        
        components.appendEmpty();
        
        this.gameType.formatScoreboard(profile, this, components);
    }
    
}
