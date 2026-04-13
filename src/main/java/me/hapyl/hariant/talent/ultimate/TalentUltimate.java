package me.hapyl.hariant.talent.ultimate;

import me.hapyl.eterna.module.annotate.EventLike;
import me.hapyl.eterna.module.component.Keybind;
import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.HariantConstants;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.talent.Response;
import me.hapyl.hariant.talent.Talent;
import me.hapyl.hariant.talent.TalentContext;
import me.hapyl.hariant.talent.field.DisplayFieldInstance;
import me.hapyl.hariant.talent.target.TalentTarget;
import me.hapyl.hariant.task.executor.Executable;
import me.hapyl.hariant.task.executor.ExecutorService;
import me.hapyl.hariant.task.executor.Promise;
import me.hapyl.hariant.team.EnumTeam;
import me.hapyl.hariant.util.Duration;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.SoundFx;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;

public abstract class TalentUltimate extends Talent implements Duration {
    
    private final TalentUltimateResource resource;
    private final double cost;
    
    private SoundFx soundFx;
    
    public TalentUltimate(@NotNull Key key, @NotNull Component name, @NotNull Icon icon, @NotNull TalentUltimateResource resource, final double cost) {
        super(key, name, icon);
        
        this.resource = resource;
        this.cost = cost;
        this.soundFx = SoundFx.create(Sound.ENTITY_ENDER_DRAGON_GROWL, 2.0f);
    }
    
    @NotNull
    public SoundFx getSoundFx() {
        return soundFx;
    }
    
    public void setSoundFx(@NotNull SoundFx soundFx) {
        this.soundFx = soundFx;
    }
    
    @NotNull
    public TalentUltimateResource getResource() {
        return resource;
    }
    
    public double getResourceCost() {
        return cost;
    }
    
    @EventLike
    public void onResourceValue(@NotNull HariantPlayer player, final double previousValue, final double newValue) {
        final double minimumResourceCost = getResourceCost();
        
        if (previousValue < minimumResourceCost && newValue >= minimumResourceCost) {
            player.sendSubtitle(Component.text("ᴜʟᴛɪᴍᴀᴛᴇ ᴄʜᴀʀɢᴇᴅ", Colors.ULTIMATE_RESOURCE_ENERGY, TextDecoration.BOLD), 5, 10, 5);
            player.sendMessage(this.getUltimateUsageComponent());
            
            player.playSound(Sound.BLOCK_CONDUIT_ACTIVATE, 2.0f);
            
            // TODO @Feb 16, 2026 (xanyjl) -> Fx & chat message
        }
    }
    
    @NotNull
    public Component getComponent(@NotNull HariantPlayer player) {
        final TextComponent.Builder builder = Component.text();
        final TextColor resourceColor = resource.getStyle().color();
        
        builder.append(resource.getPrefix().style(resource.getStyle()));
        builder.appendSpace();
        
        if (player.isUsingUltimate()) {
            final int ticksAlive = player.getTicksAlive();
            final int usedUltimateAt = player.getUsedUltimateAt();
            final int durationTimeLeft = Math.max(0, getDuration() - (ticksAlive - usedUltimateAt));
            
            builder.append(Component.text("IN USE", resourceColor, TextDecoration.BOLD))
                   .append(Component.text(" (%s)".formatted(durationTimeLeft == 0 ? Tick.INFINITY_CHAR : Tick.format(durationTimeLeft)), resourceColor));
        }
        else {
            final double percentCharged = player.getUltimateResource() / cost;
            final Style resourceStyle = resource.getStyle().decorate(TextDecoration.BOLD);
            
            final Component ultimateResourceFormatted = Component.text(percentCharged >= 1.0 ? "CHARGED!" : "%,.0f%%".formatted(percentCharged * 100), resourceStyle);
            
            // If on cooldown, show the percentage grayed out with a cooldown in parentheses
            if (player.isOnCooldown(this)) {
                builder.append(ultimateResourceFormatted.color(NamedTextColor.DARK_GRAY).decorate(TextDecoration.STRIKETHROUGH))
                       .append(Component.text(" (%s)".formatted(Tick.format(player.getCooldownTimeLeft(this))), Colors.FORMAT_TICK));
            }
            // Otherwise show the percentage charged
            else {
                builder.append(ultimateResourceFormatted);
            }
        }
        
        return builder.build();
    }
    
    @NotNull
    public Component getUltimateUsageComponent() {
        return Component.empty()
                        .append(HariantConstants.GENERIC_ULTIMATE_PREFIX.color(Colors.ULTIMATE_RESOURCE_ENERGY))
                        .appendSpace()
                        .append(Component.text("Your ultimate has charged! Press ", Colors.DEFAULT_COLOR))
                        .append(Component.keybind(Keybind.SWAP_OFFHAND, NamedTextColor.GOLD, TextDecoration.BOLD))
                        .append(Component.text(" to use it!", Colors.DEFAULT_COLOR));
    }
    
    /**
     * Executes this {@link TalentUltimate} talent for the given {@link HariantPlayer}.
     *
     * @param player           - The player for whom to execute the ultimate.
     * @param context          - The talent context for target retrieval, if needed.
     * @param consumedResource - The amount of ultimate resource consumed.
     * @return an {@link Executable} to run and await the {@link Promise} to complete in order to end the ultimate the start the cooldown.
     * @see Executable
     * @see ExecutorService
     */
    @NotNull
    public abstract Executable execute(@NotNull HariantPlayer player, @NotNull TalentContext context, final double consumedResource);
    
    @NotNull
    @Override
    public abstract TalentTarget target(@NotNull HariantPlayer player);
    
    @NotNull
    @Override
    public final Response execute(@NotNull HariantPlayer player, @NotNull TalentContext context) {
        // Check for cost
        final double ultimateResource = player.getUltimateResource();
        final double resourceCost = this.getResourceCost();
        
        if (ultimateResource < resourceCost) {
            return Response.error("Your ultimate is not ready!");
        }
        
        if (player.isUsingUltimate()) {
            return Response.error("You are already using ultimate!");
        }
        
        // Decrement energy
        player.decrementUltimateResource(resourceCost);
        player.setUsingUltimate(true);
        
        final Executable executable = execute(player, context, ultimateResource);
        
        executable.execute().then(() -> {
            player.setUsingUltimate(false);
            player.setCooldown(this);
        });
        
        this.broadcast(player);
        
        return Response.ok();
    }
    
    @NotNull
    @Override
    public String getTalentClassName() {
        return "Ultimate Talent";
    }
    
    @Override
    protected void initLocalAttributeFields() {
        super.initLocalAttributeFields();
        
        this.attributeFields.add(new DisplayFieldInstance(
                Component.empty()
                         .append(this.resource.getName())
                         .append(Component.text(" Cost")),
                Component.text("%.0f".formatted(cost))
        ));
    }
    
    public void broadcast(@NotNull HariantPlayer player) {
        Hariant.getPlayers().forEach(other -> {
            final EnumTeam otherTeam = other.getPlayerTeam();
            
            final Component youOrPlayerName = other.equals(player)
                                              ? Component.text("You", Colors.DEFAULT_COLOR)
                                              : player.getName().color(otherTeam.getStyle().color());
            
            
            other.sendMessage(
                    Component.empty()
                             .append(HariantConstants.GENERIC_ULTIMATE_PREFIX.color(Colors.ULTIMATE_RESOURCE_ENERGY))
                             .appendSpace()
                             .append(youOrPlayerName)
                             .append(Component.text(" used ", Colors.DEFAULT_COLOR))
                             .append(this.getName().color(NamedTextColor.GOLD))
                             .append(Component.text("!", Colors.DEFAULT_COLOR))
            );
            
            soundFx.play(other);
        });
    }
    
}
