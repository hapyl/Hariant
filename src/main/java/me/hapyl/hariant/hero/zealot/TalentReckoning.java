package me.hapyl.hariant.hero.zealot;

import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.text.Strings;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.attribute.AttributeScaling;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.damage.DamageSourceIdentity;
import me.hapyl.hariant.entity.damage.DamageSourceImpl;
import me.hapyl.hariant.entity.damage.DamageType;
import me.hapyl.hariant.entity.damage.DeathMessage;
import me.hapyl.hariant.entity.damage.component.DamageComponent;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.event.HariantAttackEvent;
import me.hapyl.hariant.hero.HeroRegistry;
import me.hapyl.hariant.talent.TalentPassive;
import me.hapyl.hariant.talent.TalentType;
import me.hapyl.hariant.talent.field.DisplayField;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public final class TalentReckoning extends TalentPassive implements Listener {
    
    private final @DisplayField AttributeScaling damage = AttributeScaling.create(AttributeType.ATTACK, 36);
    
    private final @DisplayField Decimal hitThreshold = Decimal.ofValue(5);
    private final @DisplayField Decimal resetThreshold = Decimal.ofSeconds(5);
    private final @DisplayField Decimal ferocityHits = Decimal.ofValue(1);
    
    private final DamageSourceIdentity damageSourceIdentity = DamageSourceIdentity.create(
            this,
            DeathMessage.create("{player} was reckoned [by {killer}]")
    );
    
    public TalentReckoning(@NotNull Key key) {
        super(key, Component.text("Reckoning"), Icon.ofMaterial(Material.NETHER_WART));
        
        setTalentType(TalentType.DAMAGE);
        
        setDescription(
                Component.empty()
                         .append(Component.text("Every "))
                         // Technically it's every 6th hit because threshold is 5, so do + 1
                         .append(Component.text(Strings.stNdTh(hitThreshold.intValue() + 1), Colors.NUMBER))
                         .append(Component.text(" attack on the "))
                         .append(Component.text("same enemy", Style.style(TextDecoration.UNDERLINED)))
                         .append(Component.text(" triggers an additional attack that deals "))
                         .append(ElementType.AETHER.asComponentDamage())
                         .append(Component.text(", which is considered to be a "))
                         .append(AttributeType.FEROCITY.getPrefixStyled())
                         .append(Component.text(" ferocity", AttributeType.FEROCITY.getStyle()))
                         .append(Component.text(" attack."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("Resets after %s of not attacking or after attacking another enemy.".formatted(Tick.round(resetThreshold.intValue())), Colors.DARK_GRAY))
        );
        
    }
    
    @EventHandler
    public void handleHariantDamageEvent(HariantAttackEvent ev) {
        if (!(ev.getAttacker() instanceof HariantPlayer player)) {
            return;
        }
        
        if (!player.getHero().equals(HeroRegistry.ZEALOT)) {
            return;
        }
        
        if (player.isSelfOrTeammate(ev.getEntity())) {
            return;
        }
        
        player.getHeroData(HeroRegistry.ZEALOT, HeroDataZealot::new).attack(ev.getEntity());
    }
    
    public @NotNull Decimal getResetThreshold() {
        return resetThreshold;
    }
    
    public @NotNull Decimal getHitThreshold() {
        return hitThreshold;
    }
    
    public void trigger(@NotNull HariantPlayer player, @NotNull HariantEntity entity) {
        entity.damageFerocity(entity.createDamageInstance(new ReckoningDamageSource(player)), ferocityHits.intValue(), true);
    }
    
    public class ReckoningDamageSource extends DamageSourceImpl {
        ReckoningDamageSource(@NotNull HariantEntity source) {
            super(
                    damageSourceIdentity,
                    source,
                    DamageType.FEROCITY,
                    ElementType.AETHER,
                    DamageComponent.ofCommon(),
                    Set.of(),
                    damage.getScaledValue(source),
                    0
            );
        }
    }
    
}