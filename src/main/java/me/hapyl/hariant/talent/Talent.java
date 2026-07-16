package me.hapyl.hariant.talent;

import com.google.common.collect.Lists;
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
import me.hapyl.hariant.entity.cooldown.Cooldown;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.event.HariantTalentEvent;
import me.hapyl.hariant.event.HariantTalentPreconditionEvent;
import me.hapyl.hariant.inventory.item.ItemCreator;
import me.hapyl.hariant.profile.setting.Settings;
import me.hapyl.hariant.registry.Registrable;
import me.hapyl.hariant.talent.field.DisplayField;
import me.hapyl.hariant.talent.field.DisplayFieldInstance;
import me.hapyl.hariant.talent.target.TalentTarget;
import me.hapyl.hariant.util.ComponentFormatter;
import me.hapyl.hariant.util.Duration;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.Identified;
import net.kyori.adventure.text.Component;
import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import java.util.function.IntFunction;
import java.util.stream.IntStream;

@AutoRegisteredListener
@StrictNamingConvention(startsWith = "Talent")
public abstract class Talent
        implements
        Named, Described, Keyed, ItemCreator,
        Registrable, Cooldown, Duration, Identified {
    
    private final List<DisplayFieldInstance> attributeFields;
    
    private final Key key;
    private final Component name;
    private final String identity;
    private final Icon icon;
    
    @NotNull private Component description;
    @NotNull private TalentType talentType;
    
    private int cooldown;
    private int duration;
    
    public Talent(@NotNull Key key, @NotNull Component name, @NotNull Icon icon) {
        this.key = key;
        this.name = name;
        this.identity = Components.toString(name);
        this.icon = icon;
        this.description = Described.defaultValue();
        this.talentType = TalentType.DAMAGE;
        this.attributeFields = Lists.newArrayList();
        
        AutoRegisteredListener.Registry.register(this);
        StrictNamingConvention.Validator.validate(this);
    }
    
    @NotNull
    @Override
    public String identify() {
        return identity;
    }
    
    @NotNull
    public Icon getIcon() {
        return icon;
    }
    
    @NotNull
    @Override
    public Key getCooldownKey() {
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
    public int getDuration() {
        return duration;
    }
    
    @Override
    public void setDuration(int duration) {
        this.duration = duration;
    }
    
    @Override
    @NotNull
    public ItemBuilder createBuilder() {
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
    
    @NotNull
    public ItemBuilder createDetailsBuilder() {
        final ItemBuilder builder = icon.createBuilder();
        
        builder.setName(getName());
        
        builder.addLore(Component.text("Details", Colors.DARK_GRAY));
        builder.addLore();
        
        // Append talent type description
        builder.addLore(this.talentType.getName().color(Colors.GOLD));
        builder.addWrappedLore(this.talentType.getDescription(), HariantConstants.COMPONENT_STYLER_DESCRIPTION_PADDING_2);
        builder.addLore();
        
        // Append attribute fields
        if (!attributeFields.isEmpty()) {
            builder.addLore(Component.text("Attributes", Colors.GOLD));
            
            attributeFields.forEach(instance -> {
                builder.addLore(instance.asComponent());
            });
        }
        
        return builder;
    }
    
    @OverridingMethodsMustInvokeSuper
    @Override
    public void onRegister() {
        this.initAttributeFields0();
    }
    
    @Override
    public void onUnregister() {
    }
    
    @NotNull
    @Override
    public Component getName() {
        return name;
    }
    
    @NotNull
    @Override
    public Component getDescription() {
        return description;
    }
    
    @Override
    public void setDescription(@NotNull Component description) {
        this.description = description;
    }
    
    @NotNull
    public TalentType getTalentType() {
        return talentType;
    }
    
    public void setTalentType(@NotNull TalentType talentType) {
        this.talentType = talentType;
    }
    
    @Override
    @NotNull
    public final Key getKey() {
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
    
    @NotNull
    public abstract TalentTarget target(@NotNull HariantPlayer player);
    
    @NotNull
    public abstract Response execute(@NotNull HariantPlayer player, @NotNull TalentContext context);
    
    public void execute0(@NotNull HariantPlayer player) {
        // Precondition checks
        final int cooldownTimeLeft = player.getCooldownTimeLeft(this);
        
        if (player.hasCooldown(this)) {
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
            player.messageError(Component.text("The game hasn't started yet!"));
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
        
        // Else start cooldown unless the response is await
        if (response.isOk()) {
            player.setCooldown(this);
        }
        else if (response.isAwait()) {
            player.setIndefiniteCooldown(this);
        }
        
        // Call the talent event AFTER the execution
        new HariantTalentEvent(player, this, response).callEvent();
    }
    
    @NotNull
    public String getTalentClassName() {
        return "Talent";
    }
    
    @NotNull
    public final Component getTalentTypeWithClassName() {
        return talentType.getName().appendSpace().append(Component.text(this.getTalentClassName()));
    }
    
    @OverridingMethodsMustInvokeSuper
    protected void initAttributeFields(@NotNull List<? super DisplayFieldInstance> attributeFields) {
        if (cooldown > 0) {
            attributeFields.add(new DisplayFieldInstance(Component.text("Cooldown"), this.getCooldownFormatted()));
        }
        
        if (duration > 0) {
            attributeFields.add(new DisplayFieldInstance(Component.text("Duration"), this.getDurationFormatted()));
        }
    }
    
    private void initAttributeFields0() {
        if (!attributeFields.isEmpty()) {
            throw new IllegalStateException("Attribute fields already initiated!");
        }
        
        this.initAttributeFields(attributeFields);
        
        try {
            for (Class<?> clazz : getClassHierarchyReversed()) {
                for (Field field : clazz.getDeclaredFields()) {
                    final DisplayField displayField = field.getAnnotation(DisplayField.class);
                    
                    if (displayField == null) {
                        continue;
                    }
                    
                    field.setAccessible(true);
                    final Object fieldValue = field.get(this);
                    
                    if (!(fieldValue instanceof ComponentFormatter formatter)) {
                        throw new IllegalArgumentException(
                                "Field %s (%s) in %s must implement %s!".formatted(field.getName(), clazz.getSimpleName(), field.getType().getSimpleName(), ComponentFormatter.class.getSimpleName()));
                    }
                    
                    final String fieldName = !displayField.name().isEmpty()
                                             ? displayField.name()
                                             : formatFieldName(field);
                    
                    attributeFields.add(new DisplayFieldInstance(Component.text(fieldName), formatter.format()));
                }
            }
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException("Error parsing attribute fields in %s: %s".formatted(this.getClass().getSimpleName(), e.getMessage()), e);
        }
    }
    
    @NotNull
    private List<Class<?>> getClassHierarchyReversed() {
        final List<Class<?>> hierarchy = Lists.newArrayList();
        Class<?> clazz = this.getClass();
        
        while (clazz != Talent.class) {
            hierarchy.addFirst(clazz);
            clazz = clazz.getSuperclass();
        }
        
        return hierarchy;
    }
    
    @NotNull
    private static String formatFieldName(@NotNull Field field) {
        final String fieldName = field.getName();
        final StringBuilder builder = new StringBuilder();
        
        final int length = fieldName.length();
        
        for (int i = 0; i < length; i++) {
            final char ch = fieldName.charAt(i);
            
            // First char is always uppercase
            if (i == 0) {
                builder.append(Character.toUpperCase(ch));
            }
            else {
                builder.append(ch);
                
                // If there is a next char, and it's either uppercase or a digit, append a space
                if (i + 1 < length) {
                    final char nextCh = fieldName.charAt(i + 1);
                    
                    if (Character.isUpperCase(nextCh) || Character.isDigit(nextCh)) {
                        builder.append(" ");
                    }
                }
            }
        }
        
        return builder.toString();
    }
    
    public static @NotNull List<? extends Component> createSubloreComponent(@NotNull IntFunction<Component> prefixSupplier, @NotNull Component... components) {
        return IntStream.range(0, components.length)
                        .mapToObj(i -> prefixSupplier.apply(i).append(components[i]))
                        .toList();
    }
    
}