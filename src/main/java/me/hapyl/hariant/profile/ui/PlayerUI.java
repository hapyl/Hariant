package me.hapyl.hariant.profile.ui;

import me.hapyl.eterna.module.component.ComponentList;
import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.player.ScoreboardBuilder;
import me.hapyl.eterna.module.player.tablist.Tablist;
import me.hapyl.eterna.module.util.Ticking;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.instance.AttributesInstance;
import me.hapyl.hariant.attribute.modifier.AttributeModifier;
import me.hapyl.hariant.attribute.modifier.AttributeModifierType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.profile.PlayerProfile;
import me.hapyl.hariant.profile.VanillaTeamManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.List;

public final class PlayerUI implements Ticking {
    
    private final PlayerProfile profile;
    
    private final Scoreboard scoreboard;
    private final VanillaTeamManager vanillaTeamManager;
    private final ScoreboardBuilder scoreboardBuilder;
    
    private final Tablist tablist;
    
    public PlayerUI(@NotNull PlayerProfile profile) {
        this.profile = profile;
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.vanillaTeamManager = new VanillaTeamManager(scoreboard, profile);
        this.scoreboardBuilder = new ScoreboardBuilder(scoreboard, profile.getPlayer(), Hariant.GAME_NAME);
        
        this.tablist = null;
        // this.tablist = new Tablist(profile.getPlayer());
        // this.tablist.show();
    }
    
    @Override
    public void tick() {
        final HariantPlayer player = Hariant.getEntity(profile.getPlayer(), HariantPlayer.class).orElse(null);
        
        // TODO @Feb 12, 2026 (xanyjl) -> This needs to be automated
        if (player != null && player.shouldTick()) {
            player.tickActionbar();
            
            // Debug tablist for modifiers
            this.debugModifiersToTablistFooter(player);
        }
        else {
            profile.getPlayer().sendPlayerListFooter(Component.empty());
        }
        
        // Tick vanilla team manager
        this.vanillaTeamManager.tick();
        
        this.updateScoreboard();
        this.updateTablist();
    }
    
    @NotNull
    public VanillaTeamManager getVanillaTeamManager() {
        return vanillaTeamManager;
    }
    
    private void debugModifiersToTablistFooter(@NotNull HariantPlayer player) {
        final TextComponent.Builder builder = Component.text();
        final AttributesInstance attributes = player.getAttributes();
        final List<? extends AttributeModifier> modifiers = attributes.getModifiers();
        
        // Attributes
        builder.appendNewline();
        builder.append(Component.text("ATTRIBUTES", Colors.GOLD, TextDecoration.BOLD));
        builder.appendNewline();
        
        for (AttributeType attributeType : AttributeType.values()) {
            builder.append(
                    Component.empty()
                             .append(attributeType)
                             .appendSpace()
                             .append(attributeType.format(attributes.get(attributeType)))
            );
            builder.appendNewline();
        }
        
        builder.appendNewline();
        
        // Modifiers
        builder.append(Component.text("MODIFIERS", Colors.GOLD, TextDecoration.BOLD));
        builder.appendNewline();
        
        if (modifiers.isEmpty()) {
            builder.append(Component.text("None!", Colors.DARK_GRAY));
        }
        else {
            for (int i = 0; i < modifiers.size(); i++) {
                if (i != 0) {
                    builder.appendNewline();
                }
                
                final AttributeModifier modifier = modifiers.get(i);
                final HariantEntity applier = modifier.getApplier();
                
                builder.append(modifier.getName().color(Colors.YELLOW));
                builder.append(Component.text(" from %s".formatted(applier.equals(player) ? "self" : applier.toString()), Colors.DARK_GRAY));
                
                builder.appendNewline();
                
                // Append duration
                final int durationTimeLeft = modifier.currentTick();
                
                builder.append(Component.text(Tick.format(durationTimeLeft), Colors.GRAY));
                builder.appendNewline();
                
                // Append modifiers
                modifier.stream().forEach(entry -> {
                    final AttributeType attributeType = entry.attributeType();
                    final AttributeModifierType modifierType = entry.modifierType();
                    
                    final double value = entry.value();
                    final boolean isBuff = value > 0;
                    
                    final TextColor valueColor = isBuff ? Colors.GREEN : Colors.RED;
                    
                    builder.append(
                            Component.empty()
                                     .append(attributeType)
                                     .appendSpace()
                                     .append(isBuff ? Component.text("+", valueColor) : Component.text("-", valueColor))
                                     .append(modifierType.format(Math.abs(value)).color(valueColor))
                                     .appendSpace()
                                     .append(Component.text("(%s)".formatted(modifierType), Colors.DARK_GRAY))
                    );
                    builder.appendNewline();
                });
            }
        }
        
        player.getHandle().sendPlayerListFooter(builder);
    }
    
    @NotNull
    public PlayerUIFormatter formatter() {
        if (profile.isSpectator()) {
            return PlayerUIFormatter.SPECTATOR;
        }
        
        return Hariant.getCurrentGameInstance().map(PlayerUIFormatter.class::cast).orElse(PlayerUIFormatter.LOBBY);
    }
    
    private void updateScoreboard() {
        final ComponentList components = ComponentList.empty();
        components.append(Component.text(this.getTodayFormatted(), Colors.DARK_GRAY));
        components.append(Component.empty());
        
        this.formatter().formatScoreboard(profile, components);
        
        scoreboardBuilder.setLines(components);
    }
    
    private void updateTablist() {
    }
    
    @NotNull
    private String getTodayFormatted() {
        final LocalDate localDate = LocalDate.now();
        
        return "%s/%s/%s".formatted(localDate.getDayOfMonth(), localDate.getMonthValue(), localDate.getYear());
    }
    
}
