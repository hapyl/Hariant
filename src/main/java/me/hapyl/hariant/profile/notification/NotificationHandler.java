package me.hapyl.hariant.profile.notification;

import com.google.common.collect.Lists;
import me.hapyl.eterna.module.player.sequencer.Sequencer;
import me.hapyl.eterna.module.player.sequencer.Track;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.achievement.AchievementEntry;
import me.hapyl.hariant.experience.Level;
import me.hapyl.hariant.profile.PlayerProfile;
import me.hapyl.hariant.profile.setting.Settings;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;

public class NotificationHandler {
    
    // We use an immutable list that's instantiated here to keep all notifications loaded
    private static final List<NotificationListener> LISTENERS = List.of(
            Level.NOTIFICATION_LISTENER,
            AchievementEntry.NOTIFICATION_LISTENER
    );
    
    private static final Comparator<Notification> COMPARATOR = (o1, o2) -> o2.getNotificationType().compareTo(o1.getNotificationType());
    
    private static final Component COMPONENT_NOTIFICATIONS = Component.text("NOTIFICATIONS", Style.style(TextColor.color(0xB983FF), TextDecoration.BOLD));
    private static final Component COMPONENT_CLICK = Component.text(" ᴄʟɪᴄᴋ", Colors.GOLD, TextDecoration.BOLD);
    
    private static final HoverEvent<?> HOVER_EVENT_CLICK = HoverEvent.showText(Component.text("Click to clear the notification!", Colors.GRAY));
    
    private static final Sequencer SEQUENCER = Sequencer.singleTrack(
            Hariant.getPlugin(),
            Track.builder("abcdef")
                 .where('a', Sound.ENTITY_CHICKEN_EGG, 0.5f)
                 .where('b', Sound.ENTITY_CHICKEN_EGG, 0.6f)
                 .where('c', Sound.ENTITY_CHICKEN_EGG, 0.7f)
                 .where('d', Sound.ENTITY_CHICKEN_EGG, 0.8f)
                 .where('e', Sound.ENTITY_CHICKEN_EGG, 0.9f)
                 .where('f', Sound.ENTITY_CHICKEN_EGG, 1.0f)
    );
    
    private NotificationHandler() {
    }
    
    public static @NotNull NotificationResult getNotifications(@NotNull PlayerProfile profile) {
        final AllowedNotifications allowedNotifications = profile.getDatabase().settings.getValue(Settings.NOTIFICATIONS);
        final List<Notification> notifications = Lists.newArrayList();
        
        int totalSize = 0;
        
        // Call listeners
        for (NotificationListener listener : LISTENERS) {
            final Notification notification = listener.listen(profile);
            
            if (notification != null) {
                totalSize++;
                
                if (allowedNotifications.isAllowed(notification)) {
                    notifications.add(notification);
                }
            }
        }
        
        // Sort the notifications by the importance
        notifications.sort(COMPARATOR);
        
        return new NotificationResult(notifications, totalSize, notifications.size());
    }
    
    public static @NotNull List<? extends Notification> getNotificationsNotify(@NotNull PlayerProfile profile) {
        final NotificationResult notificationResult = getNotifications(profile);
        final List<? extends Notification> notifications = notificationResult.notifications;
        
        if (!notifications.isEmpty()) {
            profile.sendMessage(Component.empty());
            profile.sendMessage(
                    Component.empty()
                             .append(COMPONENT_NOTIFICATIONS)
                             .appendSpace()
                             .append(notificationResult.asComponent().color(Colors.GRAY))
            );
            
            for (Notification notification : notifications) {
                final TextComponent.Builder builder = Component.text();
                
                builder.appendSpace();
                builder.append(notification.getName().style(notification.getNotificationType().getStyle()));
                
                // If notification has a click event, append it
                if (notification.clickEvent() instanceof ClickEvent<?> clickEvent) {
                    builder.append(COMPONENT_CLICK);
                    builder.hoverEvent(HOVER_EVENT_CLICK);
                    builder.clickEvent(clickEvent);
                }
                
                profile.sendMessage(builder);
            }
            
            profile.sendMessage(Component.empty());
            
            // Sfx
            SEQUENCER.play(profile.getPlayer());
        }
        
        return notifications;
    }
    
    public record NotificationResult(@NotNull List<? extends Notification> notifications, int sizeTotal, int sizeFiltered) implements ComponentLike {
        
        @Override
        public @NotNull Component asComponent() {
            return sizeTotal == sizeFiltered
                   ? Component.text("(%s)".formatted(sizeTotal))
                   : Component.text("(%s/%s)".formatted(sizeFiltered, sizeTotal));
        }
        
    }
    
}