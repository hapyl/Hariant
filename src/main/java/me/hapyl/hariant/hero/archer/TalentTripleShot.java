package me.hapyl.hariant.hero.archer;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.attribute.AttributeScaling;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.damage.DamageSourceIdentity;
import me.hapyl.hariant.entity.damage.DeathMessage;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.talent.Response;
import me.hapyl.hariant.talent.Talent;
import me.hapyl.hariant.talent.TalentContext;
import me.hapyl.hariant.talent.field.DisplayField;
import me.hapyl.hariant.talent.target.TalentTarget;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class TalentTripleShot extends Talent {
    
    private final Color arrowColor = Color.fromRGB(Colors.ELEMENT_ELECTRIC.value());
    
    @DisplayField private final AttributeScaling damage = AttributeScaling.create(AttributeType.ATTACK, 135);
    
    @DisplayField private final Decimal additionalArrowDamageMultiplier = Decimal.ofPercentage(50);
    @DisplayField private final Decimal additionalArrowSpread = Decimal.ofValue(5, v -> Component.text(v).append(Component.text("°")).color(TextColor.color(0xFFF854)));
    @DisplayField private final Decimal elementalApplication = Decimal.ofElementalApplication(ElementType.ELECTRIC, 100);
    
    private final DamageSourceIdentity damageSourceIdentity = DamageSourceIdentity.create(
            this,
            DeathMessage.create("{player} was triple shot [by {killer}]")
    );
    
    public TalentTripleShot(@NotNull Key key) {
        super(key, Component.text("Triple Shot"), Icon.ofMaterial(Material.ARROW));
        
        this.setCooldownSeconds(4.5f);
        
        this.setDescription(
                Component.empty()
                         .append(Component.text("Shoot three arrows in front of you that deal "))
                         .append(ElementType.ELECTRIC.asComponentDamage())
                         .append(Component.text("."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("The two additional arrows deal "))
                         .append(additionalArrowDamageMultiplier)
                         .append(Component.text(" of the original arrow damage."))
        );
        
    }
    
    @NotNull
    @Override
    public TalentTarget target(@NotNull HariantPlayer player) {
        return TalentTarget.none();
    }
    
    @NotNull
    @Override
    public Response execute(@NotNull HariantPlayer player, @NotNull TalentContext context) {
        final double damage = this.damage.getScaledValue(player);
        final double additionalArrowDamage = damage * this.additionalArrowDamageMultiplier.doubleValue();
        final double spread = Math.PI * Math.toRadians(this.additionalArrowSpread.doubleValue());
        
        final Arrow middleArrow = createArrow(player, damage, null);
        
        createArrow(player, additionalArrowDamage, middleArrow.getVelocity().add(player.getVectorLeft(spread)));
        createArrow(player, additionalArrowDamage, middleArrow.getVelocity().add(player.getVectorRight(spread)));
        
        // Fx
        player.playWorldSound(Sound.ITEM_CROSSBOW_SHOOT, 0.75f);
        player.playWorldSound(Sound.ITEM_CROSSBOW_SHOOT, 1.00f);
        player.playWorldSound(Sound.ITEM_CROSSBOW_SHOOT, 1.25f);
        
        return Response.ok();
    }
    
    @NotNull
    private Arrow createArrow(@NotNull HariantPlayer player, double damage, @Nullable Vector velocity) {
        return player.launchProjectile(
                Arrow.class,
                new DamageSourceArcherTalent(damageSourceIdentity, player, damage, elementalApplication.doubleValue()),
                self -> {
                    self.setColor(arrowColor);
                    self.setCritical(false);
                    
                    if (velocity != null) {
                        self.setVelocity(velocity);
                    }
                }
        );
    }
    
}
