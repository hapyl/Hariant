package me.hapyl.hariant.hero.archer;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.HariantConstants;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.instance.AttributesInstance;
import me.hapyl.hariant.attribute.modifier.AttributeModifier;
import me.hapyl.hariant.attribute.modifier.AttributeModifierType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.event.HariantHealthChangeEvent;
import me.hapyl.hariant.hero.HeroRegistry;
import me.hapyl.hariant.hero.pytaria.HeroDataPytaria;
import me.hapyl.hariant.talent.TalentPassive;
import me.hapyl.hariant.talent.field.DisplayField;
import me.hapyl.hariant.term.EnumTerminology;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public final class TalentExcellency extends TalentPassive implements Listener {
    
    @DisplayField private final Decimal attackIncreaseThreshold = Decimal.ofPercentage(75);
    @DisplayField private final Decimal attackIncrease = Decimal.ofPercentage(25);
    
    @DisplayField private final Decimal allTypeResistanceThreshold = Decimal.ofPercentage(50);
    @DisplayField private final Decimal allTypeResistanceIncrease = Decimal.ofAttribute(AttributeType.PHYSICAL_RESISTANCE, 10);
    
    @DisplayField private final Decimal allTypeDamageThreshold = Decimal.ofPercentage(25);
    @DisplayField private final Decimal allTypeDamageIncrease = Decimal.ofAttribute(AttributeType.PHYSICAL_RESISTANCE, 20);
    
    private final Key modifierKey = Key.ofString("excellency");
    
    public TalentExcellency(@NotNull Key key) {
        super(key, Component.text("Excellency"), Icon.ofMaterial(Material.ROSE_BUSH));
        
        this.setDescription(
                Component.empty()
                         .append(Component.text("Whenever "))
                         .append(Component.text("Pytaria", Colors.LIGHT_PURPLE))
                         .append(Component.text("'s health falls below certain thresholds, she gains a buff:"))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("%.0f%% of less:".formatted(attackIncreaseThreshold.value()), Colors.YELLOW))
                         .appendNewline()
                         .append(Component.text("  Increases ").append(AttributeType.ATTACK).append(Component.text(" by ")).append(attackIncrease).append(Component.text(".")))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("%.0f%% of less:".formatted(allTypeResistanceThreshold.value()), Colors.GOLD))
                         .appendNewline()
                         .append(Component.text("  Increases ").append(EnumTerminology.ALL_TYPE_RESISTANCE).append(Component.text(" by ")).append(allTypeResistanceIncrease).append(Component.text(".")))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("%.0f%% of less:".formatted(allTypeDamageThreshold.value()), Colors.RED))
                         .appendNewline()
                         .append(Component.text("  Increases ").append(EnumTerminology.ALL_TYPE_DAMAGE).append(Component.text(" by ")).append(allTypeDamageIncrease).append(Component.text(".")))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("Each buff lasts as long as your health is at or below its threshold.", Colors.DARK_GRAY))
        );
    }
    
    @EventHandler
    public void handleHariantHealthChangeEvent(HariantHealthChangeEvent ev) {
        final HariantEntity entity = ev.getEntity();
        
        if (!(entity instanceof HariantPlayer player)) {
            return;
        }
        
        if (!player.getHero().equals(HeroRegistry.PYTARIA)) {
            return;
        }
        
        final double maxHealth = player.getMaxHealth();
        final double newHealth = ev.getNewHealth();
        
        final double threshold = newHealth / maxHealth;
        
        final AttributesInstance attributes = player.getAttributes();
        final HeroDataPytaria data = player.getHeroData(HeroRegistry.PYTARIA, HeroDataPytaria::new);
        
        int excellency = 0;
        
        if (threshold <= attackIncreaseThreshold.doubleValue()) {
            final ExcellencyAttributeModifier modifier = new ExcellencyAttributeModifier(player);
            excellency++;
            
            modifier.of(AttributeType.ATTACK, AttributeModifierType.MULTIPLICATIVE, attackIncrease.doubleValue());
            
            if (threshold < allTypeResistanceThreshold.doubleValue()) {
                modifier.ofElementalResistance(AttributeModifierType.FLAT, allTypeResistanceIncrease.doubleValue());
                excellency++;
            }
            
            if (threshold < allTypeDamageThreshold.doubleValue()) {
                modifier.ofElementalDamageBonus(AttributeModifierType.FLAT, allTypeDamageIncrease.doubleValue());
                excellency++;
            }
            
            attributes.addModifier(modifier);
        }
        else {
            // Otherwise remove the modifier
            attributes.removeModifier(modifierKey);
        }
        
        data.excellency(excellency);
    }
    
    public class ExcellencyAttributeModifier extends AttributeModifier {
        ExcellencyAttributeModifier(@NotNull HariantEntity applier) {
            super(modifierKey, TalentExcellency.this.getName(), applier, HariantConstants.INDEFINITE_DURATION);
        }
        
        @Override
        public void display(@NotNull Location location) {
        }
    }
    
}
