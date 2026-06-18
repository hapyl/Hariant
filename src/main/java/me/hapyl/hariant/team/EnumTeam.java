package me.hapyl.hariant.team;

import com.google.common.collect.Sets;
import me.hapyl.eterna.module.component.Named;
import me.hapyl.eterna.module.component.Styled;
import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.eterna.module.reflect.team.PacketTeamColor;
import me.hapyl.eterna.module.text.Capitalizable;
import me.hapyl.eterna.module.util.CollectionUtils;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.HariantConstants;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.inventory.item.ItemCreator;
import me.hapyl.hariant.profile.PlayerProfile;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.MessageSender;
import me.hapyl.hariant.util.Prefixed;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum EnumTeam implements Prefixed, Named, Styled, Icon, ComponentLike, ItemCreator, MessageSender {
    
    RED(NamedTextColor.RED, Material.RED_BANNER),
    GREEN(NamedTextColor.GREEN, Material.GREEN_BANNER),
    BLUE(NamedTextColor.AQUA, Material.BLUE_BANNER),
    ORANGE(NamedTextColor.GOLD, Material.ORANGE_BANNER),
    PURPLE(NamedTextColor.DARK_PURPLE, Material.PURPLE_BANNER),
    WHITE(NamedTextColor.WHITE, Material.WHITE_BANNER),
    BLACK(NamedTextColor.DARK_GRAY /* Vanilla black is way too dark */, Material.BLACK_BANNER);
    
    public static final int MAX_PLAYERS = 4;
    
    private final Set<TeamEntry> entries;
    
    private final NamedTextColor color;
    private final Style style;
    private final Icon icon;
    
    private final Component prefix;
    private final Component name;
    private final Component firstLetter;
    
    EnumTeam(@NotNull NamedTextColor color, @NotNull Material material) {
        this.entries = Sets.newLinkedHashSet();
        
        this.color = color;
        this.style = Style.style(color);
        this.icon = Icon.ofMaterial(material);
        
        this.prefix = Component.text("\uD83C\uDFF4");
        this.name = Component.text("%s Team".formatted(Capitalizable.capitalize(name())));
        this.firstLetter = Component.text(name().charAt(0));
    }
    
    @NotNull
    public NamedTextColor getColor() {
        return color;
    }
    
    @NotNull
    public Component getFirstLetter() {
        return firstLetter;
    }
    
    public int getMaxPlayers() {
        return MAX_PLAYERS;
    }
    
    public int getPlayerCount() {
        return (int) entries.stream().filter(TeamEntry::isPlayer).count();
    }
    
    public boolean hasAnyPlayers() {
        return this.getPlayerCount() > 0;
    }
    
    public boolean isFull() {
        return this.getPlayerCount() >= this.getMaxPlayers();
    }
    
    public boolean isInTeam(@NotNull TeamEntry entry) {
        return entries.contains(entry);
    }
    
    public boolean isInTeam(@NotNull TeamEntryProvider provider) {
        return this.isInTeam(provider.teamEntry());
    }
    
    @NotNull
    @Override
    public Component getPrefix() {
        return prefix;
    }
    
    @NotNull
    @Override
    public Component getPrefixStyled() {
        return prefix.style(style);
    }
    
    @NotNull
    @Override
    public Component getName() {
        return name;
    }
    
    @NotNull
    @Override
    public Style getStyle() {
        return style;
    }
    
    
    @NotNull
    @Override
    public ItemBuilder createBuilder() {
        final ItemBuilder builder = icon.createBuilder();
        
        builder.setName(asComponent());
        builder.addLore();
        
        final List<PlayerProfile> profiles = getPlayerProfiles().toList();
        
        // Display members
        builder.addLore(Component.text("Members:"));
        
        for (int j = 0; j < EnumTeam.MAX_PLAYERS; j++) {
            if (j >= profiles.size()) {
                builder.addLore(Component.text(" - Empty!", Colors.DARK_GRAY));
            }
            else {
                builder.addLore(Component.text(" - ", Colors.DARK_GRAY).append(profiles.get(j).getNameFormatted()));
            }
        }
        
        return builder;
    }
    
    @NotNull
    @Override
    public Component asComponent() {
        return Component.empty()
                        .append(prefix)
                        .appendSpace()
                        .append(name)
                        .style(style);
    }
    
    @NotNull
    public TeamJoinResponse addEntry(@NotNull TeamEntry entry) {
        // Player entries need additional checks
        if (entry.isPlayer()) {
            if (this.isFull()) {
                return TeamJoinResponse.CANNOT_JOIN_TEAM_IS_FULL;
            }
            
            if (this.entries.contains(entry)) {
                return TeamJoinResponse.CANNOT_JOIN_ALREADY_IN_THIS_TEAM;
            }
        }
        
        @Nullable final EnumTeam entryTeam = getEntryTeam(entry);
        
        // Remove from the previous team
        if (entryTeam != null) {
            entryTeam.removeEntry(entry);
        }
        
        this.entries.add(entry);
        return TeamJoinResponse.JOINED;
    }
    
    @NotNull
    public TeamJoinResponse addEntry(@NotNull TeamEntryProvider provider) {
        return this.addEntry(provider.teamEntry());
    }
    
    public void addPlayer(@NotNull PlayerProfile profile) {
        final Player player = profile.getPlayer();
        final TeamJoinResponse response = addEntry(TeamEntry.create(profile.getUuid(), true));
        
        switch (response) {
            case JOINED -> sendMessage(
                    player,
                    Component.empty()
                             .append(Component.text("Joined "))
                             .append(name.style(style))
                             .append(Component.text("!"))
                             .color(Colors.SUCCESS)
            );
            
            case CANNOT_JOIN_TEAM_IS_FULL -> sendMessage(player, Component.text("This team is full!", Colors.ERROR));
            case CANNOT_JOIN_ALREADY_IN_THIS_TEAM -> sendMessage(player, Component.text("You are already in this team!", Colors.ERROR));
        }
    }
    
    public void removeEntry(@NotNull TeamEntry entry) {
        this.entries.remove(entry);
    }
    
    public void removeEntry(@NotNull TeamEntryProvider provider) {
        this.removeEntry(provider.teamEntry());
    }
    
    @Override
    public void sendMessage(@NotNull Audience audience, @NotNull Component message) {
        audience.sendMessage(
                Component.empty()
                         .append(this.getPrefix().style(style))
                         .appendSpace()
                         .append(message)
        );
    }
    
    @NotNull
    public TeamData createTeamData() {
        return new TeamData(this);
    }
    
    @NotNull
    public Component formatPlayerNames() {
        final TextComponent.Builder builder = Component.text();
        
        // Append player names trimmed to the average length of a minecraft nickname
        final List<TextComponent> playerNames = this.getPlayerProfiles()
                                                    .map(profile -> {
                                                        final String playerName = profile.getPlayer().getName();
                                                        
                                                        return Component.text(playerName.substring(0, Math.min(playerName.length(), HariantConstants.AVERAGE_NICKNAME_LENGTH)), Colors.WHITE);
                                                    })
                                                    .toList();
        
        for (int i = 0; i < playerNames.size(); i++) {
            if (i != 0) {
                builder.append(Component.text(", "));
            }
            
            builder.append(playerNames.get(i));
        }
        
        return builder.build();
    }
    
    @NotNull
    public Stream<PlayerProfile> getPlayerProfiles() {
        return this.entries.stream()
                           .map(entry -> Hariant.getPlayerProfile(entry.getUuid()))
                           .filter(Optional::isPresent)
                           .map(Optional::get);
    }
    
    @NotNull
    public List<HariantPlayer> getPlayers() {
        return entries.stream()
                      .map(entry -> Hariant.getEntity(entry.getUuid(), HariantPlayer.class))
                      .filter(Optional::isPresent)
                      .map(Optional::get)
                      .toList();
    }
    
    @NotNull
    public Component getFirstLetterFormatted() {
        return firstLetter.style(Style.style(style.color(), TextDecoration.BOLD));
    }
    
    @NotNull
    public PacketTeamColor getPacketTeamColor() {
        return switch (this) {
            case RED -> PacketTeamColor.RED;
            case GREEN -> PacketTeamColor.GREEN;
            case BLUE -> PacketTeamColor.BLUE;
            case ORANGE -> PacketTeamColor.GOLD;
            case PURPLE -> PacketTeamColor.DARK_PURPLE;
            case WHITE -> PacketTeamColor.WHITE;
            case BLACK -> PacketTeamColor.DARK_GRAY;
        };
    }
    
    @Nullable
    public static EnumTeam getEntryTeam(@NotNull TeamEntry entry) {
        for (EnumTeam team : values()) {
            if (team.isInTeam(entry)) {
                return team;
            }
        }
        
        return null;
    }
    
    @Nullable
    public static EnumTeam getEntryTeam(@NotNull TeamEntryProvider provider) {
        return getEntryTeam(provider.teamEntry());
    }
    
    @NotNull
    public static EnumTeam getSmallestTeam() {
        final int minPlayers = Arrays.stream(values())
                                     .mapToInt(EnumTeam::getPlayerCount)
                                     .min()
                                     .orElse(0);
        
        final List<EnumTeam> teamsWithMinPlayers = Arrays.stream(values())
                                                         .filter(team -> team.getPlayerCount() == minPlayers)
                                                         .toList();
        
        return CollectionUtils.randomElement(teamsWithMinPlayers, EnumTeam.BLACK);
    }
    
    @NotNull
    public static Set<EnumTeam> getPopulatedTeams() {
        return Arrays.stream(values())
                     .filter(EnumTeam::hasAnyPlayers)
                     .collect(Collectors.toSet());
    }
    
    @NotNull
    public static EnumTeam fallbackTeam() {
        return EnumTeam.WHITE;
    }
    
}
