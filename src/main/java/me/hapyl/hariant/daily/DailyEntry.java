package me.hapyl.hariant.daily;

import io.papermc.paper.registry.keys.SoundEventKeys;
import me.hapyl.eterna.module.util.Enums;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.achievement.ComponentUtils;
import me.hapyl.hariant.database.PlayerDatabase;
import me.hapyl.hariant.database.PlayerDatabaseEntry;
import me.hapyl.hariant.database.problem.ProblemReporter;
import me.hapyl.hariant.database.serialize.MongoSerializableConstructor;
import me.hapyl.hariant.menu.Menus;
import me.hapyl.hariant.profile.PlayerProfile;
import me.hapyl.hariant.profile.notification.DeclaresNotifaction;
import me.hapyl.hariant.profile.notification.Notification;
import me.hapyl.hariant.profile.notification.NotificationListener;
import me.hapyl.hariant.profile.notification.NotificationType;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class DailyEntry extends PlayerDatabaseEntry implements DeclaresNotifaction {
    
    public static final NotificationListener NOTIFICATION_LISTENER = new NotificationListener() {
        final Notification notification = Notification.create(
                DailyEntry.class,
                Component.text("Unclaimed Daily Rewards"),
                NotificationType.IMPORTANT,
                Menus.DAILY.createClickEvent()
        );
        
        @Override
        public @Nullable Notification listen(@NotNull PlayerProfile profile) {
            return profile.getDatabase().daily.hasUnclaimedRewards() ? notification : null;
        }
    };
    
    public static final int NUMBER_OF_DAILIES = 3;
    
    private static final Component COMPONENT_NEW_DAILY_BONDS = ComponentUtils.sparkly(Component.text("ɴᴇᴡ ᴅᴀɪʟʏ ʙᴏɴᴅꜱ", Colors.DARK_GREEN, TextDecoration.BOLD), Style.style(Colors.GREEN));
    private static final Component COMPONENT_DAILY_BOND_COMPLETE = Component.text("ᴅᴀɪʟʏ ʙᴏɴᴅ ᴄᴏᴍᴘʟᴇᴛᴇ", Colors.GREEN, TextDecoration.BOLD);
    
    private final DailyInstance[] dailyInstances;
    private int currentDay;
    
    @MongoSerializableConstructor
    private DailyEntry(@NotNull PlayerDatabase database, @NotNull Document document, @NotNull String parent) {
        super(database, document, parent);
        
        this.dailyInstances = new DailyInstance[NUMBER_OF_DAILIES];
        this.currentDay = 0;
    }
    
    public @NotNull DailyInstance[] getDailyInstances() {
        return dailyInstances;
    }
    
    public boolean hasUnclaimedRewards() {
        for (DailyInstance dailyInstance : dailyInstances) {
            if (dailyInstance.isCompleteNotClaimed()) {
                return true;
            }
        }
        
        return false;
    }
    
    public int countUnclaimedRewards() {
        int count = 0;
        
        for (DailyInstance dailyInstance : dailyInstances) {
            if (dailyInstance.isCompleteNotClaimed()) {
                count++;
            }
        }
        
        return count;
    }
    
    public int countCompleteDailies() {
        int count = 0;
        
        for (DailyInstance dailyInstance : dailyInstances) {
            if (dailyInstance.isCompleted()) {
                count++;
            }
        }
        
        return count;
    }
    
    public int getCurrentDay() {
        return currentDay;
    }
    
    @Override
    public void write(@NotNull PlayerDatabase database, @NotNull Document document, @NotNull ProblemReporter problemReporter) {
        // Write current day
        document.put("current_day", this.currentDay);
        
        // Write dailies
        document.put(
                "dailies",
                Arrays.stream(this.dailyInstances)
                      .map(dailyInstance -> dailyInstance.writeToNewDocument(database, problemReporter))
                      .toList()
        );
    }
    
    @Override
    public void read(@NotNull PlayerDatabase database, @NotNull Document document, @NotNull ProblemReporter problemReporter) {
        // Read current day
        this.currentDay = document.get("current_day", 0);
        
        // If current day does not match the current actual day, reset the dailies
        if (this.currentDay != Hariant.getCurrentDaySinceEpoch()) {
            this.resetDailies();
        }
        // Otherwise load dailies
        else {
            int index = 0;
            
            for (Document dailyDocument : document.getList("dailies", Document.class, List.of())) {
                this.dailyInstances[Math.min(NUMBER_OF_DAILIES, index++)] = DailyInstance.read0(database, dailyDocument, problemReporter);
            }
        }
    }
    
    public boolean contains(@NotNull DailyType dailyType) {
        for (DailyInstance dailyInstance : this.dailyInstances) {
            if (dailyInstance != null && dailyInstance.getDaily() == dailyType) {
                return true;
            }
        }
        
        return false;
    }
    
    public void resetDailies() {
        this.currentDay = Hariant.getCurrentDaySinceEpoch();
        
        // Generate dailies
        for (int i = 0; i < this.dailyInstances.length; i++) {
            this.dailyInstances[i] = generateDailyInstance(1 << 4);
        }
        
        // Notify
        final PlayerProfile profile = getProfile();
        
        profile.sendMessage(Component.empty());
        profile.sendMessage(COMPONENT_NEW_DAILY_BONDS);
        profile.sendMessage(Component.empty());
        
        for (DailyInstance dailyInstance : this.dailyInstances) {
            final DailyType daily = dailyInstance.getDaily();
            
            profile.sendMessage(
                    Component.empty()
                             .hoverEvent(dailyInstance.createHoverEvent())
                             .append(Component.text("  "))
                             .append(daily.getName().color(Colors.GREEN))
            );
        }
        
        profile.sendMessage(Component.empty());
        profile.sendMessage(Component.text("Hover for details.", Colors.DARK_GRAY, TextDecoration.ITALIC));
        profile.sendMessage(Component.empty());
        
        // Sfx
        profile.playSound(Sound.sound(SoundEventKeys.ENTITY_ENDERMAN_HURT, Sound.Source.UI, 3, 0.25f));
        profile.playSound(Sound.sound(SoundEventKeys.ENTITY_ILLUSIONER_MIRROR_MOVE, Sound.Source.UI, 3, 0.75f));
    }
    
    public void progress(@NotNull DailyType dailyType) {
        final PlayerProfile profile = getProfile();
        
        for (DailyInstance dailyInstance : dailyInstances) {
            if (dailyInstance.getDaily() != dailyType || dailyInstance.isCompleted()) {
                continue;
            }
            
            // If daily is complete after progress, notify the player
            if (dailyInstance.progress()) {
                profile.sendMessage(Component.empty());
                profile.sendMessage(COMPONENT_DAILY_BOND_COMPLETE);
                profile.sendMessage(Component.space().append(dailyType.getName().color(Colors.GRAY)));
                profile.sendMessage(Component.empty());
                
                profile.playSound(Sound.sound(SoundEventKeys.ENTITY_PLAYER_LEVELUP, Sound.Source.UI, 3, 2.0f));
            }
        }
    }
    
    private @NotNull DailyInstance generateDailyInstance(int bound) {
        if (bound == 0) {
            return DailyType.KILL_PLAYERS.newInstance();
        }
        
        final DailyType dailyType = Enums.getRandomValueOrFirst(DailyType.class);
        
        // Make sure the daily can be generated for the player
        if (!dailyType.canGenerate(database)) {
            return this.generateDailyInstance(bound - 1);
        }
        
        // Make sure the same daily doesn't already exist
        if (this.contains(dailyType)) {
            return this.generateDailyInstance(bound - 1);
        }
        
        return dailyType.newInstance();
    }
    
}