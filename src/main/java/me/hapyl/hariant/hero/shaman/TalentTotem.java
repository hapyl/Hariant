package me.hapyl.hariant.hero.shaman;

import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.attribute.AttributeScaling;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.hero.HeroRegistry;
import me.hapyl.hariant.talent.Response;
import me.hapyl.hariant.talent.Talent;
import me.hapyl.hariant.talent.TalentContext;
import me.hapyl.hariant.talent.TalentType;
import me.hapyl.hariant.talent.target.TalentTarget;
import me.hapyl.hariant.term.EnumTerminology;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.decimal.Decimal;
import me.hapyl.hariant.util.field.DisplayField;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;

public final class TalentTotem extends Talent {
    
    public final @DisplayField Decimal maximumNumberOfTotems = Decimal.ofValue(3);
    public final @DisplayField Decimal tossY = Decimal.ofValue(0.25);
    public final @DisplayField Decimal tossVelocity = Decimal.ofValue(0.6);
    
    public final @DisplayField Decimal resonancePeriod = Decimal.ofSeconds(1.2f);
    public final @DisplayField Decimal resonanceRadius = Decimal.ofValue(2);
    
    public final @DisplayField AttributeScaling totemLandingDamage = AttributeScaling.create(AttributeType.MAX_HEALTH, 2);
    
    public final @DisplayField AttributeScaling totemResonanceHealing = AttributeScaling.create(AttributeType.MAX_HEALTH, 5, 10);
    public final @DisplayField AttributeScaling totemResonanceDamage = AttributeScaling.create(AttributeType.MAX_HEALTH, 8);
    
    public final @DisplayField Decimal totemResonanceElementalApplication = Decimal.ofElementalApplication(ElementType.TOXIC, 100);
    
    public TalentTotem(@NotNull Key key) {
        super(key, Component.text("Totem"), Icon.ofMaterial(Material.SPRUCE_FENCE));
        
        setTalentType(TalentType.SUPPORT);
        
        setDuration(Tick.ofSeconds(15));
        setCooldown(Tick.ofSeconds(3));
        
        setDescription(
                Component.empty()
                         .append(Component.text("Toss a makeshift totem forward."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("Upon landing, the totem deals "))
                         .append(ElementType.PHYSICAL.asComponentAreaOfEffectDamage())
                         .append(Component.text(" and begins to resonate."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("Resonance", Colors.GOLD))
                         .appendNewline()
                         .append(Component.text("Periodically releases pulses in small "))
                         .append(EnumTerminology.AREA_OF_EFFECT)
                         .append(Component.text(" with different effects based on the colliding entity."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("Teammate (or self)", Colors.GREEN))
                         .appendNewline()
                         .append(Component.text("Heals the teammate, based on "))
                         .append(HeroShaman.SHAMAN_S)
                         .appendSpace()
                         .append(AttributeType.MAX_HEALTH)
                         .append(Component.text("."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("Enemy", Colors.RED))
                         .appendNewline()
                         .append(Component.text("Deals "))
                         .append(ElementType.TOXIC.asComponentDamage())
                         .append(Component.text(" based on "))
                         .append(HeroShaman.SHAMAN_S)
                         .appendNewline()
                         .append(AttributeType.MAX_HEALTH)
                         .append(Component.text(" and builds up "))
                         .append(ElementType.TOXIC)
                         .append(Component.text(" anomaly."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("There can only exist %s totems at any given time.".formatted(maximumNumberOfTotems.wordValue(Decimal.WordValue.LOWERCASE)), Colors.DARK_GRAY))
        );
    }
    
    @Override
    public @NotNull TalentTarget target(@NotNull HariantPlayer player) {
        return TalentTarget.none();
    }
    
    @Override
    public @NotNull Response execute(@NotNull HariantPlayer player, @NotNull TalentContext context) {
        player.getHeroData(HeroRegistry.SHAMAN, HeroDataShaman::new).createTotem(player, this);
        
        // Fx
        player.playWorldSound(Sound.ENTITY_IRON_GOLEM_DEATH, 0.75f);
        
        return Response.ok();
    }
    
}