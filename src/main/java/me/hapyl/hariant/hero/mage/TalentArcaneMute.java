package me.hapyl.hariant.hero.mage;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.damage.AssistSource;
import me.hapyl.hariant.entity.effect.status.EnumStatusEffect;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.talent.Response;
import me.hapyl.hariant.talent.TalentContext;
import me.hapyl.hariant.talent.TalentType;
import me.hapyl.hariant.talent.field.DisplayField;
import me.hapyl.hariant.talent.target.TalentTarget;
import me.hapyl.hariant.term.Term;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public final class TalentArcaneMute extends TalentMage {
    
    @DisplayField private final Decimal maxDistance = Decimal.ofValue(20);
    @DisplayField private final Decimal lookupRadius = Decimal.ofValue(1.25);
    
    public TalentArcaneMute(@NotNull Key key) {
        super(key, Component.text("Arcane Mute"), Icon.ofMaterial(Material.FEATHER), 5);
        
        setDurationSeconds(4);
        setCooldownSeconds(20);
        
        setTalentType(TalentType.IMPAIR);
        
        setDescription(
                Component.empty()
                         .append(Component.text("Silence and "))
                         .append(Component.text("interrupt", Term.TERM_STYLE))
                         .append(Component.text(" the target "))
                         .append(Component.text("enemy player", NamedTextColor.RED))
                         .append(Component.text(", deafening them and disabling their "))
                         .append(Component.text("talents", NamedTextColor.YELLOW))
                         .append(Component.text("."))
        );
    }
    
    @NotNull
    @Override
    public TalentTarget target(@NotNull HariantPlayer player) {
        return TalentTarget.targetEntityRayCast(maxDistance.doubleValue(), lookupRadius.doubleValue(), entity -> {
            return entity instanceof HariantPlayer && player.canAffect(entity) && player.hasLineOfSight(entity);
        });
    }
    
    @NotNull
    @Override
    public Response execute1(@NotNull HariantPlayer player, @NotNull TalentContext context) {
        if (!(context.retrieve(HariantEntity.class) instanceof HariantPlayer targetPlayer)) {
            return Response.error("Must target a player!");
        }
        
        targetPlayer.interrupt(AssistSource.create(player, this));
        targetPlayer.addEffect(EnumStatusEffect.ARCANE_MUTE, getDuration(), player);
        
        // Fx
        player.messageSuccess(
                Component.empty()
                         .append(Component.text("Silenced "))
                         .append(targetPlayer.getName())
                         .append(Component.text("!"))
        );
        
        return Response.ok();
    }
}
