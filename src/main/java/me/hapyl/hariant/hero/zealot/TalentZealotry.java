package me.hapyl.hariant.hero.zealot;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.modifier.AttributeModifier;
import me.hapyl.hariant.attribute.modifier.AttributeModifierType;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.event.HariantDamageCalculationsEvent;
import me.hapyl.hariant.hero.HeroRegistry;
import me.hapyl.hariant.talent.Response;
import me.hapyl.hariant.talent.TalentContext;
import me.hapyl.hariant.talent.TalentType;
import me.hapyl.hariant.talent.field.DisplayField;
import me.hapyl.hariant.talent.rechargeable.RechargeableTalentData;
import me.hapyl.hariant.talent.rechargeable.TalentRechargeable;
import me.hapyl.hariant.talent.target.TalentTarget;
import me.hapyl.hariant.term.Terminology;
import me.hapyl.hariant.util.Definition;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public final class TalentZealotry extends TalentRechargeable implements Listener {
    
    private final @DisplayField Decimal defenseIgnore = Decimal.ofPercentage(100);
    
    public TalentZealotry(@NotNull Key key) {
        super(key, Component.text("Zealotry"), Icon.ofMaterial(Material.ECHO_SHARD), 2);
        
        setTalentType(TalentType.ENHANCE);
        
        setCooldownSeconds(5);
        
        setDescription(
                Component.empty()
                         .append(Component.text("Apply "))
                         .append(Definition.ZEALOT_MARK)
                         .append(Component.text(" on yourself."))
                         .appendNewline()
                         .appendNewline()
                         .append(Definition.ZEALOT_MARK.getName().color(Colors.GOLD))
                         .appendNewline()
                         .append(Component.text("Your next "))
                         .append(Component.text("melee", Terminology.TERM_STYLE))
                         .append(Component.text(" attack will be infused with "))
                         .append(ElementType.AETHER)
                         .append(Component.text(" and ignores "))
                         .append(defenseIgnore)
                         .append(Component.text(" of the target's "))
                         .append(AttributeType.DEFENSE)
                         .append(Component.text("."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("This talent has %s initial uses.".formatted(getMaxCharges()), Colors.DARK_GRAY))
        );
    }
    
    @EventHandler
    public void handleHariantDamageCalculationsEvent(HariantDamageCalculationsEvent ev) {
        if (!(ev.getAttacker().entity().orElse(null) instanceof HariantPlayer player)) {
            return;
        }
        
        if (!player.getHero().equals(HeroRegistry.ZEALOT)) {
            return;
        }
        
        final HeroDataZealot heroData = player.getHeroData(HeroRegistry.ZEALOT, HeroDataZealot::new);
        
        if (!heroData.isZealotMarkActive()) {
            return;
        }
        
        ev.setDamageSource(builder -> {
            // Change element type to aether
            builder.elementType(ElementType.AETHER);
        });
        
        // Ignore N% of enemy DEF
        ev.getEntity().addModifier(
                player,
                AttributeModifier.entry(AttributeType.DEFENSE, AttributeModifierType.MULTIPLICATIVE, -defenseIgnore.doubleValue())
        );
        
        heroData.setZealotMarkActive(false);
        
        // Fx
        player.playSound(Sound.ENTITY_ENDER_DRAGON_FLAP, 0.75f);
    }
    
    @Override
    public @NotNull TalentTarget target(@NotNull HariantPlayer player) {
        return TalentTarget.none();
    }
    
    @Override
    public @NotNull Response execute(@NotNull HariantPlayer player, @NotNull TalentContext context, @NotNull RechargeableTalentData talentData) {
        final HeroDataZealot heroData = player.getHeroData(HeroRegistry.ZEALOT, HeroDataZealot::new);
        
        if (heroData.isZealotMarkActive()) {
            return Response.error("Already active!");
        }
        
        heroData.setZealotMarkActive(true);
        
        // Fx
        player.playSound(Sound.ENTITY_ENDER_DRAGON_FLAP, 1.25f);
        
        return Response.ok();
    }
    
}
