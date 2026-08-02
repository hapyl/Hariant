package me.hapyl.hariant.command;

import com.google.common.collect.ImmutableList;
import me.hapyl.eterna.module.command.ArgumentList;
import me.hapyl.eterna.module.util.Enums;
import me.hapyl.hariant.HariantLogger;
import me.hapyl.hariant.database.rank.PlayerRank;
import me.hapyl.hariant.hero.Hero;
import me.hapyl.hariant.hero.HeroRegistry;
import me.hapyl.hariant.statistics.Statistic;
import me.hapyl.hariant.statistics.StatisticEntry;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class HariantCommandAggregateStatistic extends HariantCommand {
    
    private static List<String> COMPLETER_HEROES;
    private static List<String> COMPLETER_STATISTICS;
    
    public HariantCommandAggregateStatistic(@NotNull String name) {
        super(name, PlayerRank.ADMIN);
    }
    
    @Override
    public void execute(@NotNull CommandSender sender, @NotNull ArgumentList args, @NotNull PlayerRank playerRank) {
        final Hero hero = args.get(0).toRegistryItem(HeroRegistry.getRegistry()).orElse(null);
        final Statistic statistic = args.get(1).toEnum(Statistic.class);
        
        if (hero == null) {
            return;
        }
        
        if (statistic == null) {
            HariantLogger.error(sender, Component.text("Invalid statistic!"));
            return;
        }
        
        HariantLogger.info(sender, Component.text("Aggregating statistic..."));
        
        final CompletableFuture<@Nullable Double> future = StatisticEntry.aggregatesStatistic(hero, statistic);
        
        future.whenComplete((value, _) -> {
            if (value == null) {
                HariantLogger.error(
                        sender,
                        Component.empty()
                                 .append(Component.text("The value for "))
                                 .append(statistic.getName())
                                 .append(Component.text(" is unset."))
                );
            }
            else {
                HariantLogger.success(
                        sender,
                        Component.empty()
                                 .append(Component.text("The value for "))
                                 .append(statistic.getName())
                                 .append(Component.text(" is %s.".formatted(value)))
                );
            }
        });
    }
    
    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull ArgumentList args, @NotNull PlayerRank playerRank) {
        // Lazy init because deadlock
        if (COMPLETER_HEROES == null || COMPLETER_STATISTICS == null) {
            COMPLETER_HEROES = HeroRegistry.getRegistry().keysAsString();
            COMPLETER_STATISTICS = ImmutableList.<String>builder()
                                                .addAll(Enums.getValueLowercaseNamesAsList(Statistic.class))
                                                // TODO -> .addAll(TalentRegistry.getRegistry().keysAsString())
                                                .build();
        }
        
        return switch (args.length) {
            case 1 -> COMPLETER_HEROES;
            case 2 -> COMPLETER_STATISTICS;
            default -> List.of();
        };
    }
}