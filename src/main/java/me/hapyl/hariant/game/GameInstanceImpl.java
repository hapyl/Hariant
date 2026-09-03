package me.hapyl.hariant.game;

import com.google.common.collect.Lists;
import me.hapyl.eterna.module.component.ComponentList;
import me.hapyl.eterna.module.component.Components;
import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.player.tablist.EntryList;
import me.hapyl.eterna.module.player.tablist.EntryTexture;
import me.hapyl.eterna.module.text.TimeFormat;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.daily.DailyType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.PlayerState;
import me.hapyl.hariant.entity.effect.status.StatusEffectInstance;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.entity.player.combat.CombatData;
import me.hapyl.hariant.event.HariantGameInstanceStateEvent;
import me.hapyl.hariant.experience.ExperienceConstants;
import me.hapyl.hariant.experience.ExperienceSource;
import me.hapyl.hariant.experience.LevelEntry;
import me.hapyl.hariant.game.battleground.EnumBattleground;
import me.hapyl.hariant.game.type.GameType;
import me.hapyl.hariant.inventory.drop.DropSummary;
import me.hapyl.hariant.inventory.item.ResourceRegistry;
import me.hapyl.hariant.profile.PlayerProfile;
import me.hapyl.hariant.profile.ui.PlayerUIFormatter;
import me.hapyl.hariant.statistics.PlayerStatistics;
import me.hapyl.hariant.statistics.Statistic;
import me.hapyl.hariant.task.InternalTasks;
import me.hapyl.hariant.team.EnumTeam;
import me.hapyl.hariant.team.TeamData;
import me.hapyl.hariant.team.TeamDataMap;
import me.hapyl.hariant.util.ComponentShine;
import me.hapyl.hariant.util.HexId;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GameInstanceImpl implements GameInstance {
    
    private static final Component COMPONENT_GAME_OVER = Components.centerText("GAME OVER", Colors.GOLD, TextDecoration.BOLD);
    private static final Component COMPONENT_HEADER_FOOTER = Component.text("▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀", Colors.GOLD, TextDecoration.BOLD);
    
    private static final Style STYLE_SHINE = Style.style(Colors.GOLD, TextDecoration.BOLD);
    private static final Style STYLE_SHINE_SHINE = Style.style(TextColor.color(0xEBC93A), TextDecoration.BOLD);
    private static final Style STYLE_SHINE_FADE = Style.style(TextColor.color(0xF6ED29), TextDecoration.BOLD);
    
    private static final ComponentShine TITLE_SHINE_FIRST = ComponentShine.builder("ᴠɪᴄᴛᴏʀʏ")
                                                                          .style(STYLE_SHINE)
                                                                          .styleShine(STYLE_SHINE_SHINE)
                                                                          .styleFade(STYLE_SHINE_FADE)
                                                                          .build();
    
    private static final ComponentShine TITLE_SHINE_OTHER = ComponentShine.builder("ɢᴀᴍᴇ ᴏᴠᴇʀ")
                                                                          .style(STYLE_SHINE)
                                                                          .styleShine(STYLE_SHINE_SHINE)
                                                                          .styleFade(STYLE_SHINE_FADE)
                                                                          .build();
    
    private static final int TICKS_IN_MINUTE = 1200;
    private static final int LEVEL_UP_NOTIFICATION_DELAY = Tick.fromSeconds(2.5f);
    
    private static final Component COMPONENT_NONE = Component.text("None!", Colors.DARK_GRAY);
    private static final Component COMPONENT_SUMMARY = Components.centerText("SUMMARY", Colors.WHITE, TextDecoration.BOLD);
    
    private final HexId hexId;
    private final GameType gameType;
    private final EnumBattleground battleground;
    private final TeamDataMap teamDataMap;
    
    private @NotNull GameInstanceState state;
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
    public void onCreate(@NotNull Iterable<? extends HariantPlayer> players) {
        // Pass to battleground & game type
        battleground.onCreate(players);
        gameType.onCreate(players);
    }
    
    @Override
    public void onDestroy(@NotNull Iterable<? extends HariantPlayer> players, @NotNull WinResult result) {
        // Pass to battleground & game type
        battleground.onDestroy(players, result);
        gameType.onDestroy(players, result);
        
        final Component componentDuration = Components.centerText(TimeFormat.format((gameType.getTimeLimit() - timeLeft) * 50L, TimeFormat.Part.MINUTES, TimeFormat.Part.SECONDS), Colors.GRAY);
        final List<? extends Component> winners = result.createWinnerCenterComponents();
        
        for (HariantPlayer player : players) {
            final Placement placement = result.getPlacement(player);
            
            // Keep winners in survival
            player.setGameMode(placement.isWinner() ? GameMode.SURVIVAL : GameMode.SPECTATOR);
            
            // Show game over screen
            player.sendMessage(COMPONENT_HEADER_FOOTER);
            player.sendMessage(COMPONENT_GAME_OVER);
            player.sendMessage(componentDuration);
            player.sendMessage(Component.empty());
            
            // Display winners
            winners.forEach(player::sendMessage);
            
            player.sendMessage(Component.empty());
            player.sendMessage(COMPONENT_HEADER_FOOTER);
            
            // Show title
            (placement.isWinner() ? TITLE_SHINE_FIRST : TITLE_SHINE_OTHER).display(player, Component.empty(), 1, 100);
        }
    }
    
    @Override
    public void onFinalize(@NotNull List<? extends HariantPlayer> players, @NotNull WinResult result) {
        // Pass to battleground & game type
        battleground.onFinalize(players, result);
        gameType.onFinalize(players, result);
        
        final int minutesPlayed = Math.max(1, (gameType.getTimeLimit() - timeLeft) / TICKS_IN_MINUTE);
        
        for (HariantPlayer player : players) {
            final PlayerProfile profile = player.getProfile();
            final Placement placement = result.getPlacement(player.teamEntry());
            
            // Calculate experience
            final List<? extends ExperienceSource> experienceSources = calculateExperience(player, placement, minutesPlayed);
            final long totalExperience = experienceSources.stream().mapToLong(ExperienceSource::getExperience).sum();
            
            // Increment experience
            final LevelEntry levelEntry = profile.getDatabase().level;
            final LevelEntry.Result levelResult = levelEntry.addExperience(profile, totalExperience, true);
            
            // Generate loot
            final DropSummary dropSummary = battleground.getDropTable().generateLoot(player.getProfile());
            
            // Calculate statistics at the end
            final PlayerStatistics statistics = player.getStatistics();
            statistics.incrementStatistic(Statistic.GAMES_PLAYED, 1);
            
            if (placement.isWinner()) {
                statistics.incrementStatistic(Statistic.GAMES_WON, 1);
            }
            
            // Calculate coins and experience earned
            final long coinsEarned = dropSummary.sumOfResource(ResourceRegistry.CAT_COINS);
            
            statistics.incrementStatistic(Statistic.COINS_EARNED, coinsEarned);
            statistics.incrementStatistic(Statistic.EXPERIENCE_GAINED, totalExperience);
            
            // Sync the statics to the database
            statistics.syncToDatabase();
            
            // Display summary
            player.sendMessage(Component.empty());
            player.sendMessage(COMPONENT_SUMMARY);
            
            // Statistics
            player.sendMessage(Component.text("   ꜱᴛᴀᴛɪꜱᴛɪᴄꜱ", Colors.WHITE, TextDecoration.BOLD));
            
            final int statisticKills = (int) statistics.getStatistic(Statistic.KILLS);
            final int statisticDeaths = (int) statistics.getStatistic(Statistic.DEATH);
            final int statisticAssists = (int) statistics.getStatistic(Statistic.ASSISTS);
            
            player.sendMessage(
                    Component.empty()
                             .append(Component.text("     ⚔ ", Colors.DARK_RED))
                             .append(Component.text(statisticKills, Colors.WHITE))
                             .append(Component.text(" ☠ ", Colors.RED))
                             .append(Component.text(statisticDeaths, Colors.WHITE))
                             .append(Component.text(" 🌿 ", Colors.GREEN))
                             .append(Component.text(statisticAssists, Colors.WHITE))
                             .append(Component.text("  Hover for more statistics", Colors.DARK_GRAY))
                             .hoverEvent(statistics.createHoverEvent())
            );
            
            // Resources
            player.sendMessage(Component.empty());
            player.sendMessage(Component.text("   ʀᴇꜱᴏᴜʀᴄᴇꜱ", Colors.WHITE, TextDecoration.BOLD));
            
            player.sendMessage(
                    Component.empty()
                             .append(Component.text("     Catcoins Earned: ", Colors.GRAY))
                             .append(coinsEarned > 0 ? Component.text(coinsEarned, Colors.GOLD) : COMPONENT_NONE)
            );
            
            player.sendMessage(
                    Component.empty()
                             .append(Component.text("     Experience Gained: ", Colors.GRAY))
                             .append(Component.text(totalExperience, Colors.EXPERIENCE))
                             .append(Component.text("  Hover for details", Colors.DARK_GRAY))
                             .hoverEvent(HoverEvent.showText(
                                     Component.empty()
                                              .append(Component.text("Experience Sources", Colors.WHITE, TextDecoration.BOLD))
                                              .appendNewline()
                                              .append(
                                                      experienceSources.stream()
                                                                       .map(ExperienceSource::asComponent)
                                                                       .collect(Component.toComponent(Component.newline()))
                                              )
                             ))
            );
            
            // Drops
            player.sendMessage(Component.empty());
            player.sendMessage(Component.text("   ᴅʀᴏᴘꜱ", Colors.WHITE, TextDecoration.BOLD));
            
            dropSummary.createSummary()
                       // We only care about unique drops here, so only keep `totalAmount == 1`
                       .filter(entry -> entry.totalAmount() == 1)
                       .forEach(entry -> player.sendMessage(Component.text("     ").append(entry.asComponent())));
            
            // Delay level up message so you can read the summary
            if (levelResult.hasLevelChanged()) {
                InternalTasks.later(() -> {
                    final Player bukkitPlayer = profile.getPlayer();
                    
                    if (bukkitPlayer.isOnline()) {
                        levelEntry.notifyLevelUp(bukkitPlayer, levelResult.levelBeforeAdd(), levelResult.levelAfterAdd());
                    }
                }, LEVEL_UP_NOTIFICATION_DELAY);
            }
            
            // Progress daily
            DailyType.progressArchetypeDaily(profile, player.getHero().getProfile().getArchetype());
        }
        
        // TODO (xanyjl @ Saturday, August 1) -> Save instance result to database
    }
    
    @Override
    public void onKill(@NotNull GameInstance gameInstance, @NotNull HariantPlayer player, @NotNull HariantPlayer victim) {
        // Pass to battleground & game type
        battleground.onKill(gameInstance, player, victim);
        gameType.onKill(gameInstance, player, victim);
        
        teamDataMap.getData(player.getPlayerTeam()).kills++;
        
        // Progress first blood
        if (totalKills() == 1) {
            DailyType.FIRST_BLOOD.progress(player.getProfile());
        }
    }
    
    @Override
    public void onDeath(@NotNull GameInstance gameInstance, @NotNull HariantPlayer player, @Nullable HariantEntity killer) {
        // Pass to battleground & game type
        battleground.onDeath(gameInstance, player, killer);
        gameType.onDeath(gameInstance, player, killer);
        
        teamDataMap.getData(player.getPlayerTeam()).deaths++;
    }
    
    @Override
    public void tick() {
        // Only tick when the state is IN_PROGRESS
        if (state != GameInstanceState.IN_PROGRESS) {
            return;
        }
        
        battleground.tick();
        
        timeLeft--;
    }
    
    @Override
    public int getTimeLeft() {
        return timeLeft;
    }
    
    @Override
    public void formatScoreboard(@NotNull PlayerProfile profile, @NotNull ComponentList components) {
        gameType.formatScoreboard(profile, this, components);
    }
    
    @Override
    public void formatTablistSystem(@NotNull PlayerProfile profile, @NotNull EntryList entryList) {
        // Display game information
        final Component playerState = profile.getHariantPlayer().map(HariantPlayer::getState)
                                             .orElse(PlayerState.ALIVE)
                                             .asComponent();
        
        entryList.append(Component.text("Game", Colors.GOLD).append(Component.text(" " + hexId, Colors.DARK_GRAY)), EntryTexture.GOLD);
        entryList.append(Component.text(" ᴛɪᴍᴇ ʟᴇғᴛ: ", Colors.GRAY).append(this.getTimeLeftFormatted().color(Colors.WHITE)));
        entryList.append(Component.text(" sᴛᴀᴛᴜs: ", Colors.GRAY).append(playerState));
        entryList.append();
        
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
        
        final List<HariantPlayer> players = team.getPlayers().toList();
        
        for (int i = 0; i < EnumTeam.MAX_PLAYERS; i++) {
            if (i < players.size()) {
                final HariantPlayer player = players.get(i);
                
                entryList.append(EnumTeam.createMemberPrefix(
                        Component.empty()
                                 .append(player.asHeadComponent())
                                 .appendSpace()
                                 .append(player.getName().color(Colors.GREEN))
                                 .append(player.createSuffix())
                ));
            }
            else {
                entryList.append(EnumTeam.createMemberPrefix(null));
            }
        }
    }
    
    @Override
    public @NotNull Component createTablistFooter(@NotNull PlayerProfile profile, @NotNull PlayerUIFormatter formatter) {
        final HariantPlayer player = profile.getHariantPlayer().orElse(null);
        
        if (player == null) {
            return Component.empty();
        }
        
        // Show active effects
        final TextComponent.Builder builder = Component.text();
        
        builder.appendNewline();
        builder.append(Component.text("ᴀᴄᴛɪᴠᴇ ᴇғғᴇᴄᴛs:", Colors.YELLOW, TextDecoration.BOLD));
        builder.append(Component.newline());
        
        final List<@NotNull Component> list = player.getEffects()
                                                    .map(StatusEffectInstance::asComponent)
                                                    .toList();
        
        if (list.isEmpty()) {
            builder.append(Component.text("None!", Colors.DARK_GRAY));
        }
        else {
            for (int i = 0; i < list.size(); i++) {
                if (i > 0) {
                    builder.append(Component.text("  "));
                }
                
                builder.append(list.get(i));
                
                if (i % 2 == 1) {
                    builder.append(Component.newline());
                }
            }
        }
        
        return builder.appendNewline().build();
    }
    
    public @NotNull Component getTimeLeftFormatted() {
        return Component.text(TimeFormat.format(Math.max(0, timeLeft) * 50L, TimeFormat.Part.MINUTES, TimeFormat.Part.SECONDS));
    }
    
    public int totalKills() {
        return teamDataMap.stream().mapToInt(TeamData::getKills).sum();
    }
    
    private @NotNull List<? extends ExperienceSource> calculateExperience(@NotNull HariantPlayer player, @NotNull Placement placement, int minutesPlayed) {
        final List<ExperienceSource> sources = Lists.newArrayList();
        
        // Calculate placement experience
        sources.add(ExperienceConstants.PLACEMENT.createSource(placement));
        
        // Calculate minutes played
        sources.add(ExperienceConstants.MINUTE_PLAYED.createSource(minutesPlayed));
        
        // Calculate stats
        final PlayerStatistics statistics = player.getStatistics();
        
        final double kills = statistics.getStatistic(Statistic.KILLS);
        final double assists = statistics.getStatistic(Statistic.ASSISTS);
        
        if (kills > 0) {
            sources.add(ExperienceConstants.ELIMINATION.createSource(kills));
        }
        
        if (assists > 0) {
            sources.add(ExperienceConstants.ASSIST.createSource(assists));
        }
        
        return sources;
    }
    
    public static void calculateCombatData(@NotNull HariantPlayer player, @NotNull CombatData.Type type) {
        // FIXME (xanyjl @ Sunday, August 2) ->
    }
    
}