package me.hapyl.hariant.profile.ui;

import me.hapyl.eterna.module.component.Styled;
import me.hapyl.eterna.module.player.tablist.*;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.database.rank.FormatRules;
import me.hapyl.hariant.hero.Hero;
import me.hapyl.hariant.profile.PlayerProfile;
import me.hapyl.hariant.statistics.Statistic;
import me.hapyl.hariant.statistics.StatisticMap;
import me.hapyl.hariant.talent.Talent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

public class PlayerTablist extends Tablist {
    
    private static final FormatRules FORMAT_RULES = FormatRules.create(false, true, true, true, true);
    
    private static final Comparator<PlayerProfile> PROFILE_COMPARATOR = (o1, o2) -> {
        // First compare the rank
        final int compareRank = o2.getRank().compareTo(o1.getRank());
        
        if (compareRank != 0) {
            return compareRank;
        }
        
        // Then compare level
        return Integer.compare(o2.getLevel(), o1.getLevel());
    };
    
    private static final int CAN_FIT_PLAYERS = Tablist.MAX_ENTRIES_PER_COLUMN - 3;
    
    private static final List<Statistic> STATISTICS_TO_SHOW = List.of(
            Statistic.KILLS,
            Statistic.DEATH,
            Statistic.ASSISTS,
            Statistic.ANOMALY_TRIGGERED,
            Statistic.DAMAGE_DEALT,
            Statistic.DAMAGE_TAKEN,
            Statistic.TALENT_USAGE,
            Statistic.ULTIMATE_USAGE,
            Statistic.GAMES_PLAYED,
            Statistic.GAMES_WON
    );
    
    private static final List<Function<Hero, Talent>> STATISTICS_TALENT_SUPPLIERS = List.of(
            Hero::getFirstTalent,
            Hero::getSecondTalent,
            Hero::getThirdTalent,
            Hero::getUltimateTalent
    );
    
    private final PlayerProfile profile;
    
    PlayerTablist(@NotNull PlayerProfile profile) {
        super(profile.getPlayer());
        
        this.profile = profile;
    }
    
    public void update(@NotNull PlayerUIFormatter formatter) {
        this.updateColumnPlayers();
        this.updateColumnSystem(formatter);
        this.updateColumnTheEye();
        this.updateColumnStatistics();
        
        player.sendPlayerListHeaderAndFooter(this.createHeader(), this.createFooter(formatter));
    }
    
    private void updateColumnPlayers() {
        final EntryList entryList = EntryList.ofEmpty();
        final List<PlayerProfile> profiles = Hariant.getPlayerProfiles()
                                                    .filter(profile -> profile.isVisibleTo(this.profile))
                                                    .sorted(PROFILE_COMPARATOR)
                                                    .toList();
        
        final int numberOfProfiles = profiles.size();
        
        // Append header
        entryList.append(
                Component.empty()
                         .append(Component.text("    ᴘʟᴀʏᴇʀs", Colors.GREEN, TextDecoration.BOLD))
                         .append(Component.text(" (%s)".formatted(numberOfProfiles), Colors.GRAY)),
                EntryTexture.GREEN
        );
        
        entryList.append();
        
        // Append players
        profiles.forEach(profile -> {
            entryList.append(profile.getNameFormatted(FORMAT_RULES), profile.asEntryTexture(), PingBars.byPing(player.getPing()));
        });
        
        // If profiles number is higher than what we can fit, append `... and N more!`
        if (numberOfProfiles > CAN_FIT_PLAYERS) {
            final int numberOfProfilesThatCouldNotFit = numberOfProfiles - CAN_FIT_PLAYERS;
            
            entryList.append(Component.text("...and %s more!".formatted(numberOfProfilesThatCouldNotFit), Colors.DARK_GRAY));
        }
        
        this.setColumn(TablistColumn.FIRST, entryList);
    }
    
    private void updateColumnSystem(@NotNull PlayerUIFormatter formatter) {
        final EntryList entryList = EntryList.ofEmpty();
        
        // Append header
        entryList.append(
                Component.empty()
                         .append(Component.text("    sʏsᴛᴇᴍ", Colors.YELLOW, TextDecoration.BOLD))
                         .appendSpace()
                         .append(Component.text("(", Colors.GRAY))
                         .append(getServerTicksPerSecondFormatted())
                         .append(Component.text(")", Colors.GRAY)),
                EntryTexture.YELLOW
        );
        
        entryList.append();
        
        // Pass to the formatter
        formatter.formatTablistSystem(profile, entryList);
        
        this.setColumn(TablistColumn.SECOND, entryList);
    }
    
    private void updateColumnTheEye() {
        final EntryList entryList = EntryList.ofEmpty();
        
        entryList.append(Component.text("    ᴛʜᴇ ᴇʏᴇ", Colors.DARK_GREEN, TextDecoration.BOLD), EntryTexture.DARK_GREEN);
        entryList.append();
        
        entryList.append(Component.text(" You have yet to meet", Colors.GRAY));
        entryList.append(Component.text(" the overseer of this ", Colors.GRAY));
        entryList.append(Component.text(" place, but I really need ", Colors.GRAY));
        entryList.append(Component.text(" a placeholder for this", Colors.GRAY));
        entryList.append(Component.text(" column, so here is a wall", Colors.GRAY));
        entryList.append(Component.text(" of text for you to read,", Colors.GRAY));
        entryList.append(Component.text(" in case there isn't anything", Colors.GRAY));
        entryList.append(Component.text(" to do in the lobby.", Colors.GRAY));
        
        this.setColumn(TablistColumn.THIRD, entryList);
    }
    
    private void updateColumnStatistics() {
        final EntryList entryList = EntryList.ofEmpty();
        final Hero selectedHero = profile.getSelectedHero();
        
        entryList.append(Component.text("    sᴛᴀᴛɪsᴛɪᴄs", Colors.AQUA, TextDecoration.BOLD), EntryTexture.AQUA);
        
        entryList.append();
        entryList.append(selectedHero.getName().color(Colors.DARK_AQUA), EntryTexture.DARK_AQUA);
        
        final StatisticMap statisticMap = profile.getDatabase().statistics.getStatisticMap(selectedHero);
        
        for (final Statistic statistic : STATISTICS_TO_SHOW) {
            final double value = statisticMap.getStatistic(statistic);
            
            entryList.append(
                    Component.empty()
                             .append(statistic.asSmallCaps().color(Colors.GRAY))
                             .appendSpace()
                             .append(Component.text("%,.0f".formatted(value), Colors.WHITE))
            );
        }
        
        // Append talent usage
        entryList.append();
        entryList.append(Component.text("Talent Usage", Colors.DARK_AQUA), EntryTexture.DARK_AQUA);
        
        for (Function<Hero, Talent> supplier : STATISTICS_TALENT_SUPPLIERS) {
            final Talent talent = supplier.apply(selectedHero);
            final int usage = statisticMap.getTalentUsage(talent);
            
            entryList.append(
                    Component.empty()
                             .append(talent.asSmallCaps().color(Colors.GRAY))
                             .appendSpace()
                             .append(Component.text("%,1d".formatted(usage), Colors.WHITE))
            );
        }
        
        this.setColumn(TablistColumn.FOURTH, entryList);
    }
    
    private @NotNull Component createHeader() {
        return Component.empty()
                        .appendNewline()
                        .append(Hariant.GAME_NAME)
                        .appendNewline()
                        .append(Component.text(Hariant.getVersion(), Colors.DARK_GRAY))
                        .appendNewline();
    }
    
    private @NotNull Component createFooter(@NotNull PlayerUIFormatter formatter) {
        final Component footer = formatter.createTablistFooter(profile, formatter);
        
        return footer.appendNewline()
                     .append(Component.text("www.totallyrealwebsite.net", Colors.YELLOW));
    }
    
    public static @NotNull Component getServerTicksPerSecondFormatted() {
        final double tps = Bukkit.getTPS()[0];
        final TpsStyle tpsStyle = TpsStyle.tpsStyle(tps);
        
        return Component.text("%.1f tps".formatted(tps), tpsStyle.getStyle());
    }
    
    public enum TpsStyle implements Styled {
        
        TPS_20(20, Style.style(TextColor.color(0x00FF00))),
        TPS_19(19, Style.style(TextColor.color(0x33FF00))),
        TPS_18(18, Style.style(TextColor.color(0x66FF00))),
        TPS_17(17, Style.style(TextColor.color(0x99FF00))),
        TPS_16(16, Style.style(TextColor.color(0xCCFF00))),
        TPS_15(15, Style.style(TextColor.color(0xFFFF00))),
        TPS_14(14, Style.style(TextColor.color(0xFFEE00))),
        TPS_13(13, Style.style(TextColor.color(0xFFDD00))),
        TPS_12(12, Style.style(TextColor.color(0xFFCC00))),
        TPS_11(11, Style.style(TextColor.color(0xFFBB00))),
        TPS_10(10, Style.style(TextColor.color(0xFFAA00))),
        TPS_9(9, Style.style(TextColor.color(0xFF9900))),
        TPS_8(8, Style.style(TextColor.color(0xFF8800))),
        TPS_7(7, Style.style(TextColor.color(0xFF7700))),
        TPS_6(6, Style.style(TextColor.color(0xFF6600))),
        TPS_5(5, Style.style(TextColor.color(0xFF5500))),
        TPS_4(4, Style.style(TextColor.color(0xFF4400))),
        TPS_3(3, Style.style(TextColor.color(0xFF3300))),
        TPS_2(2, Style.style(TextColor.color(0xFF2200))),
        TPS_1(1, Style.style(TextColor.color(0xFF1100))),
        TPS_0(0, Style.style(TextColor.color(0xFF0000)));
        
        private final double tps;
        private final Style style;
        
        TpsStyle(double tps, @NotNull Style style) {
            this.tps = tps;
            this.style = style;
        }
        
        public double getTps() {
            return tps;
        }
        
        @Override
        public @NotNull Style getStyle() {
            return style;
        }
        
        public static @NotNull TpsStyle tpsStyle(double tps) {
            for (TpsStyle tpsStyle : values()) {
                if (tps >= tpsStyle.tps) {
                    return tpsStyle;
                }
            }
            
            return TPS_0;
        }
    }
    
}