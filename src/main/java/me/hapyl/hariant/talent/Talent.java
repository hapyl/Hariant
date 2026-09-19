package me.hapyl.hariant.talent;

import me.hapyl.eterna.module.component.Components;
import me.hapyl.eterna.module.component.Described;
import me.hapyl.eterna.module.component.Named;
import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.registry.Keyed;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.HariantConstants;
import me.hapyl.hariant.annotate.AutoRegisteredListener;
import me.hapyl.hariant.annotate.StrictNamingConvention;
import me.hapyl.hariant.entity.SmallCapsLike;
import me.hapyl.hariant.entity.cooldown.HariantCooldown;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.entity.player.LifecyclePlayer;
import me.hapyl.hariant.event.HariantTalentEvent;
import me.hapyl.hariant.event.HariantTalentPreconditionEvent;
import me.hapyl.hariant.inventory.item.ItemCreator;
import me.hapyl.hariant.inventory.item.ItemDetailsCreator;
import me.hapyl.hariant.profile.setting.Settings;
import me.hapyl.hariant.registry.Registrable;
import me.hapyl.hariant.talent.target.TalentTarget;
import me.hapyl.hariant.util.Duration;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.Identified;
import me.hapyl.hariant.util.field.DisplayFieldInstance;
import me.hapyl.hariant.util.field.DisplayFieldProvider;
import net.kyori.adventure.text.Component;
import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.List;
import java.util.Objects;
import java.util.function.IntFunction;
import java.util.stream.IntStream;

@AutoRegisteredListener
@StrictNamingConvention(startsWith = "Talent")
public abstract class Talent
        implements
        Named, Described, Keyed, ItemCreator,
        Registrable, HariantCooldown, Duration, Identified,
        LifecyclePlayer, SmallCapsLike, DisplayFieldProvider, ItemDetailsCreator {
    
    private final Key key;
    private final Component name;
    private final String identity;
    private final Icon icon;
    private final Component smallCaps;
    
    private @Unmodifiable List<DisplayFieldInstance> displayFields;
    
    private @NotNull Component description;
    private @NotNull TalentType talentType;
    
    private int cooldown;
    private int duration;
    
    public Talent(@NotNull Key key, @NotNull Component name, @NotNull Icon icon) {
        this.key = key;
        this.name = name;
        this.identity = Components.toString(name);
        this.icon = icon;
        this.description = Described.defaultValue();
        this.talentType = TalentType.DAMAGE;
        this.smallCaps = SmallCapsLike.asSmallCaps(name);
        this.displayFields = List.of();
        
        AutoRegisteredListener.Registry.register(this);
        StrictNamingConvention.Validator.validate(this);
    }
    
    @Override
    public @NotNull Component asSmallCaps() {
        return smallCaps;
    }
    
    @Override
    public @NotNull String identify() {
        return identity;
    }
    
    public @NotNull Icon getIcon() {
        return icon;
    }
    
    @Override
    public @NotNull Key getCooldownKey() {
        return key;
    }
    
    @Override
    public int getCooldown() {
        return cooldown;
    }
    
    @Override
    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }
    
    @Override
    public @NotNull Component asComponent() {
        return name.color(Colors.GOLD);
    }
    
    @Override
    public int getDuration() {
        return duration;
    }
    
    @Override
    public void setDuration(int duration) {
        this.duration = duration;
    }
    
    @Override
    public @NotNull ItemBuilder createBuilder() {
        final ItemBuilder builder = icon.createBuilder();
        
        // Set the cooldown key for display purposes
        builder.setCooldownKey(key);
        
        builder.setName(getName());
        
        // Append talent type
        builder.addLore(this.getTalentTypeWithClassName().color(Colors.DARK_GRAY));
        builder.addLore();
        
        // Add description
        builder.addWrappedLore(getDescription());
        
        return builder;
    }
    
    @Override
    public @NotNull ItemBuilder createDetailsBuilder() {
        final ItemBuilder builder = icon.createBuilder();
        
        builder.setName(getName());
        builder.addLore(Component.text("Details", Colors.DARK_GRAY));
        
        // Append talent type description
        builder.addLore();
        builder.addLore(this.talentType.getName().color(Colors.GOLD));
        builder.addWrappedLore(this.talentType.getDescription(), HariantConstants.COMPONENT_STYLER_DESCRIPTION_PADDING_2);
        
        // Append display fields
        if (!displayFields.isEmpty()) {
            builder.addLore();
            builder.addLore(Component.text("Attributes", Colors.GOLD));
            
            displayFields.forEach(instance -> builder.addLore(instance.asComponent()));
        }
        
        return builder;
    }
    
    @OverridingMethodsMustInvokeSuper
    @Override
    public void onRegister() {
        this.displayFields = DisplayFieldInstance.parse(this);
    }
    
    @Override
    public void onCreate(@NotNull HariantPlayer player) {
    }
    
    @Override
    public void onDestroy(@NotNull HariantPlayer player) {
    }
    
    @Override
    public @NotNull Component getName() {
        return name;
    }
    
    @Override
    public @NotNull Component getDescription() {
        return description;
    }
    
    @Override
    public void setDescription(@NotNull Component description) {
        this.description = description;
    }
    
    public @NotNull TalentType getTalentType() {
        return talentType;
    }
    
    public void setTalentType(@NotNull TalentType talentType) {
        this.talentType = talentType;
    }
    
    @Override
    public final @NotNull Key getKey() {
        return key;
    }
    
    @Override
    public final int hashCode() {
        return Objects.hashCode(this.key);
    }
    
    @Override
    public final boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        
        final Talent that = (Talent) object;
        return Objects.equals(this.key, that.key);
    }
    
    public abstract @NotNull TalentTarget target(@NotNull HariantPlayer player);
    
    public abstract @NotNull Response execute(@NotNull HariantPlayer player, @NotNull TalentContext context);
    
    public boolean respectCooldown() {
        return true;
    }
    
    public void execute0(@NotNull HariantPlayer player) {
        // Precondition checks
        final int cooldownTimeLeft = player.getCooldownTimeLeft(this);
        
        if (player.hasCooldown(this) && respectCooldown()) {
            if (player.getSetting(Settings.COOLDOWN_FEEDBACK)) {
                player.messageError(
                        Component.text("This talent is on cooldown for ")
                                 .append(Component.text(Tick.format(cooldownTimeLeft)))
                                 .append(Component.text("!"))
                );
                player.playSound(Sound.ENTITY_ENDERMAN_TELEPORT, 0.0f);
            }
            return;
        }
        
        // If a game is in progress, make sure it's IN_GAME
        if (Hariant.isGameInProgressButNotActive()) {
            player.messageError(Component.text("The game hasn't started or already ended!"));
            return;
        }
        
        // Call talent event execution
        final HariantTalentPreconditionEvent event = new HariantTalentPreconditionEvent(player, this);
        
        if (event.callEvent()) {
            player.messageError(
                    Component.empty()
                             .append(Component.text("Cannot use talent! "))
                             .append(Component.text("(", Colors.DARK_GRAY))
                             .append(event.getCancelReason().color(Colors.DARK_GRAY))
                             .append(Component.text(")", Colors.DARK_GRAY))
            );
            return;
        }
        
        final TalentTarget target = this.target(player);
        final TalentContext context = target.createContext(player);
        
        // We must handle `null` context, since it means that an error happened on target retrieval.
        if (context == null) {
            player.messageError(target.errorMessage());
            return;
        }
        
        final Response response = this.execute(player, context);
        
        // Handle error response, meaning something went wrong with the talent execution
        if (response.isError()) {
            player.messageError(Component.text(response.getReason()));
            return;
        }
        
        // Handle cooldown
        response.getStatus().setCooldown(player, this);
        
        // Call player callback
        player.onTalentExecuted(this, response);
        
        // Call the talent event AFTER the execution
        new HariantTalentEvent(player, this, response).callEvent();
    }
    
    public boolean incrementsStatistics() {
        return true;
    }
    
    public @NotNull String getTalentClassName() {
        return "Talent";
    }
    
    public final @NotNull Component getTalentTypeWithClassName() {
        return talentType.getName().appendSpace().append(Component.text(this.getTalentClassName()));
    }
    
    @OverridingMethodsMustInvokeSuper
    public void initDisplayFields(@NotNull List<? super DisplayFieldInstance> attributeFields) {
        if (cooldown > 0) {
            attributeFields.add(DisplayFieldInstance.create(Component.text("Cooldown"), this.getCooldownFormatted()));
        }
        
        if (duration > 0) {
            attributeFields.add(DisplayFieldInstance.create(Component.text("Duration"), this.getDurationFormatted()));
        }
    }
    
    public static @NotNull List<? extends Component> createSubloreComponent(@NotNull IntFunction<Component> prefixSupplier, @NotNull Component... components) {
        return IntStream.range(0, components.length)
                        .mapToObj(i -> prefixSupplier.apply(i).append(components[i]))
                        .toList();
    }
    
}