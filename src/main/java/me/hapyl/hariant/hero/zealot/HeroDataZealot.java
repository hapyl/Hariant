package me.hapyl.hariant.hero.zealot;

import me.hapyl.hariant.Colors;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.TickSupplier;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.hero.HeroData;
import me.hapyl.hariant.profile.ui.ActionbarSupplier;
import me.hapyl.hariant.talent.TalentRegistry;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class HeroDataZealot extends HeroData<HeroZealot> implements ActionbarSupplier {
    
    private static final Component ZEALOT_MARK_ACTIVE = Component.text("✦", Colors.ELEMENT_AETHER);
    private static final Component ZEALOT_MARK_INACTIVE = Component.text("✧", Colors.DARK_GRAY);
    
    private static final Component[] RECKONING_COMPONENTS = {
            Component.text("丿"),
            Component.text("乀"),
            Component.text("ウ"),
            Component.text("く"),
            Component.text("ア")
    };
    
    private static final Style RECKONING_STYLE_FULL = Style.style(Colors.LIGHT_PURPLE, TextDecoration.BOLD);
    private static final Style RECKONING_STYLE_FILLED = Style.style(Colors.DARK_PURPLE, TextDecoration.BOLD);
    private static final Style RECKONING_STYLE_EMPTY = Style.style(Colors.DARK_GRAY, TextDecoration.BOLD);
    
    private static final Component RECKONING_COMPONENT_EMPTY = Component.join(
            JoinConfiguration.builder()
                             .separator(Component.space())
                             .convertor(component -> component.asComponent().style(RECKONING_STYLE_EMPTY))
                             .build(),
            RECKONING_COMPONENTS
    );
    
    private boolean zealotMarkActive;
    private @Nullable Target target;
    
    public HeroDataZealot(@NotNull HeroZealot hero, @NotNull HariantPlayer player) {
        super(hero, player);
    }
    
    public boolean isZealotMarkActive() {
        return zealotMarkActive;
    }
    
    public void setZealotMarkActive(boolean zealotMarkActive) {
        this.zealotMarkActive = zealotMarkActive;
    }
    
    public void attack(@NotNull HariantEntity entity) {
        // Assist or reassign target
        if (target == null || !target.entity.equals(entity)) {
            target = new Target(player, entity);
        }
        else {
            target.numberOfHits++;
            target.lastAttackAt = player.localTicks();
            
            // If number of hits is greater than the threshold, execute the talent
            if (target.numberOfHits > TalentRegistry.RECKONING.getHitThreshold().intValue()) {
                target.numberOfHits = 0;
                TalentRegistry.RECKONING.trigger(player, entity);
            }
        }
    }
    
    @Override
    public void dispose() {
    }
    
    @Override
    public void tick() {
        // Tick mark on subtitle because that's clearer
        if (zealotMarkActive) {
            player.showTitle(Title.title(Component.empty(), ZEALOT_MARK_ACTIVE, 0, 2, 0));
        }
        
        if (target != null) {
            // If last damage dealt was too long ago or target died, reset
            if (player.localTicks() - target.lastAttackAt > TalentRegistry.RECKONING.getResetThreshold().intValue() || target.entity.isDead()) {
                target = null;
                
                // Fx
                player.playSound(Sound.ENTITY_DONKEY_EAT, 0.5f);
            }
        }
    }
    
    @Override
    public @NotNull List<Component> supplyActionbar(@NotNull HariantPlayer player) {
        return List.of(
                this.createTargetComponent()
        );
    }
    
    private @NotNull Component createTargetComponent() {
        if (target == null) {
            return RECKONING_COMPONENT_EMPTY;
        }
        
        final TextComponent.Builder builder = Component.text();
        
        for (int i = 0; i < RECKONING_COMPONENTS.length; i++) {
            final Component component = RECKONING_COMPONENTS[i];
            
            builder.append(component.style(
                    target.numberOfHits == TalentRegistry.RECKONING.getHitThreshold().intValue()
                    ? RECKONING_STYLE_FULL
                    : target.numberOfHits > i
                      ? RECKONING_STYLE_FILLED
                      : RECKONING_STYLE_EMPTY
            ));
        }
        
        return builder.appendSpace().append(target.entity.asHeadComponent()).build();
    }
    
    public static class Target {
        private final HariantEntity entity;
        
        private int numberOfHits;
        private int lastAttackAt;
        
        Target(@NotNull TickSupplier tickSupplier, @NotNull HariantEntity entity) {
            this.entity = entity;
            this.numberOfHits = 1;
            this.lastAttackAt = tickSupplier.localTicks();
        }
    }
    
}