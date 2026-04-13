package me.hapyl.hariant.hero.alchemist;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.hero.Definition;
import me.hapyl.hariant.talent.TalentPassive;
import me.hapyl.hariant.talent.field.DisplayField;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public final class TalentAbyssalCorrosion extends TalentPassive {
    
    @DisplayField public final Decimal corrosionDecrementPerSecond = Decimal.ofValue(0.75);
    @DisplayField public final Decimal abyssalCurseInstabilityDecrementPerOneCorrosion = Decimal.ofPercentage(0.5);
    @DisplayField public final Decimal maximumCorrosion = Decimal.ofValue(100);
    
    @DisplayField public final Decimal corrosionThreshold1 = Decimal.ofValue(40);
    @DisplayField public final Decimal corrosionThreshold2 = Decimal.ofValue(60);
    @DisplayField public final Decimal corrosionThreshold3 = Decimal.ofValue(80);
    
    public final double corrosionDecrementPerTick = corrosionDecrementPerSecond.doubleValue() / 20;
    
    public TalentAbyssalCorrosion(@NotNull Key key) {
        super(key, Component.text("Abyssal Corrosion"), Icon.ofMaterial(Material.DRAGON_BREATH));
        
        this.setDescription(
                Component.empty()
                         .append(Component.text("Consuming potions created by the Abyss increases your "))
                         .append(Definition.ABYSSAL_CORROSION)
                         .append(Component.text(", that slowly dissipates over time."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("Having a high amount of "))
                         .append(Definition.ABYSSAL_CORROSION)
                         .append(Component.text(" puts a toll on your body and soul, while also "))
                         .append(Component.text("decreasing", Colors.FORMAT_TICK))
                         .append(Component.text(" the time it takes before "))
                         .append(Definition.ABYSSAL_CURSE)
                         .append(Component.text(" becomes "))
                         .append(Component.text("unstable", NamedTextColor.DARK_RED))
                         .append(Component.text("."))
        );
    }
}
