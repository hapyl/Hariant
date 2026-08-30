package me.hapyl.hariant.daily;

import me.hapyl.eterna.module.component.Components;
import me.hapyl.eterna.module.component.Described;
import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.database.PlayerDatabase;
import me.hapyl.hariant.database.problem.ProblemReporter;
import me.hapyl.hariant.database.serialize.MongoSerializable;
import me.hapyl.hariant.database.serialize.codec.MongoCodec;
import me.hapyl.hariant.database.serialize.codec.MongoCodecs;
import me.hapyl.hariant.inventory.HariantInventory;
import me.hapyl.hariant.inventory.item.ItemCreator;
import me.hapyl.hariant.inventory.item.ResourceRegistry;
import me.hapyl.hariant.profile.PlayerProfile;
import me.hapyl.hariant.util.Hoverable;
import me.hapyl.hariant.util.ShowTextBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.bson.Document;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public final class DailyInstance implements Described, MongoSerializable, Hoverable, ItemCreator {
    
    private static final MongoCodec<DailyType, String> CODEC_DAILY_TYPE = MongoCodecs.ofEnum(DailyType.class);
    private static final Style LORE_STYLE = Style.style(Colors.GRAY).decoration(TextDecoration.ITALIC, false);
    
    private final DailyType daily;
    private final int goal;
    
    private int progress;
    private boolean hasClaimedRewards;
    
    DailyInstance(@NotNull DailyType daily, int goal) {
        this.daily = daily;
        this.goal = goal;
    }
    
    public @NotNull DailyType getDaily() {
        return daily;
    }
    
    public int getGoal() {
        return goal;
    }
    
    public int getProgress() {
        return progress;
    }
    
    public boolean isCompleted() {
        return progress >= goal;
    }
    
    public boolean isClaimedRewards() {
        return hasClaimedRewards;
    }
    
    public boolean progress() {
        this.progress = Math.min(progress + 1, goal);
        return progress >= goal;
    }
    
    public boolean isCompleteNotClaimed() {
        return this.isCompleted() && !hasClaimedRewards;
    }
    
    @Override
    public void write(@NotNull PlayerDatabase database, @NotNull Document document, @NotNull ProblemReporter problemReporter) {
        CODEC_DAILY_TYPE.write(document, "type", daily);
        
        document.put("goal", goal);
        document.put("progress", progress);
        document.put("has_claimed_rewards", hasClaimedRewards);
    }
    
    @Override
    public void read(@NotNull PlayerDatabase database, @NotNull Document document, @NotNull ProblemReporter problemReporter) {
        // `type` and `goal` is instantiated in the constructor
        this.progress = document.get("progress", 0);
        this.hasClaimedRewards = document.get("has_claimed_rewards", false);
    }
    
    @Override
    public @NotNull HoverEvent<?> createHoverEvent() {
        final ShowTextBuilder builder = ShowTextBuilder.builder();
        
        builder.append(daily.getName().color(Colors.GREEN));
        builder.append(daily.getTier().getName().color(Colors.DARK_GRAY));
        builder.appendNewline();
        
        builder.append(
                Components.wrap(this.getDescription(), 40)
                          .stream()
                          .map(c -> Components.normalizeStyle(c, LORE_STYLE))
                          .toList()
        );
        
        return builder.createHoverEvent();
    }
    
    @Override
    public @NotNull ItemBuilder createBuilder() {
        final boolean hasCompleted = this.isCompleted();
        final ItemBuilder builder = new ItemBuilder(hasCompleted ? Material.WRITTEN_BOOK : Material.WRITABLE_BOOK);
        
        builder.setName(daily.getName().color(hasCompleted ? hasClaimedRewards ? Colors.GREEN : Colors.YELLOW : Colors.RED));
        
        // Append description
        builder.addLore();
        builder.addWrappedLore(this.getDescription());
        
        // Append progress
        builder.addLore();
        builder.addLore(
                Component.empty()
                         .append(Component.text("Progress ", Colors.YELLOW))
                         .append(Components.makeComponentFractional(progress, goal))
        );
        
        // Append expiration
        builder.addLore();
        builder.addLore(
                Component.empty()
                         .append(Component.text("Refreshes in ", Colors.RED))
                         .append(Hariant.getTimeUntilResetFormatted().color(Colors.RED))
        );
        
        return builder;
    }
    
    @Override
    public @NotNull Component getDescription() {
        return daily.getDescription().getDescription(goal);
    }
    
    public boolean claimRewards(@NotNull PlayerProfile profile) {
        if (hasClaimedRewards) {
            return false;
        }
        
        final DailyTier tier = daily.getTier();
        final PlayerDatabase database = profile.getDatabase();
        final HariantInventory inventory = database.inventory;
        
        inventory.addResource(ResourceRegistry.CAT_COINS, tier.getCoinsReward());
        inventory.addResource(ResourceRegistry.RUBY, tier.getRubyReward());
        
        database.level.addExperience(profile, tier.getExperienceReward(), true);
        
        hasClaimedRewards = true;
        return true;
    }
    
    static @NotNull DailyInstance read0(@NotNull PlayerDatabase database, @NotNull Document document, @NotNull ProblemReporter problemReporter) {
        final DailyType dailyType = CODEC_DAILY_TYPE.read(document, "type").orElse(DailyType.KILL_PLAYERS);
        final int goal = document.get("goal", 1);
        
        final DailyInstance dailyInstance = new DailyInstance(dailyType, goal);
        dailyInstance.read(database, document, problemReporter);
        
        return dailyInstance;
    }
    
}