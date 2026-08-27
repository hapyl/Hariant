package me.hapyl.hariant.hero.archer;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.achievement.AchievementRegistry;
import me.hapyl.hariant.achievement.UniqueId;
import me.hapyl.hariant.attribute.AttributeScaling;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.damage.DamageSourceIdentity;
import me.hapyl.hariant.entity.damage.DeathMessage;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.event.HariantProjectileHitEvent;
import me.hapyl.hariant.handler.HariantProjectile;
import me.hapyl.hariant.hero.HeroRegistry;
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
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class TalentTripleShot extends Talent implements Listener {
    
    private final Color arrowColor = Color.fromRGB(Colors.ELEMENT_ELECTRIC.value());
    
    private final @DisplayField AttributeScaling damage = AttributeScaling.create(AttributeType.ATTACK, 135);
    
    private final @DisplayField Decimal additionalArrowDamageMultiplier = Decimal.ofPercentage(50);
    private final @DisplayField Decimal additionalArrowSpread = Decimal.ofValue(5, v -> Component.text(v).append(Component.text("°")).color(TextColor.color(0xFFF854)));
    private final @DisplayField Decimal elementalApplication = Decimal.ofElementalApplication(ElementType.ELECTRIC, 100);
    
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
        
        // Technically the tick cannot increment unless the execution finishes, but to be 1000% sure,
        // we're caching the tick the arrows were shot at ¯\_(ツ)_/¯
        final int localTick = player.localTicks();
        
        final Arrow middleArrow = createArrow(localTick, player, damage, null);
        
        createArrow(localTick, player, additionalArrowDamage, middleArrow.getVelocity().add(player.getVectorLeft(spread)));
        createArrow(localTick, player, additionalArrowDamage, middleArrow.getVelocity().add(player.getVectorRight(spread)));
        
        // Fx
        player.playWorldSound(Sound.ITEM_CROSSBOW_SHOOT, 0.75f);
        player.playWorldSound(Sound.ITEM_CROSSBOW_SHOOT, 1.00f);
        player.playWorldSound(Sound.ITEM_CROSSBOW_SHOOT, 1.25f);
        
        return Response.ok();
    }
    
    private @NotNull Arrow createArrow(int localTick, @NotNull HariantPlayer player, double damage, @Nullable Vector velocity) {
        return player.launchProjectile(
                Arrow.class,
                new DamageSourceTripleShot(localTick, damageSourceIdentity, player, damage, elementalApplication.doubleValue()),
                self -> {
                    self.setColor(arrowColor);
                    self.setCritical(false);
                    
                    if (velocity != null) {
                        self.setVelocity(velocity);
                    }
                }
        );
    }
    
    @EventHandler
    public void handleHariantProjectileHitEvent(HariantProjectileHitEvent ev) {
        final HariantProjectile projectile = ev.getProjectile();
        
        if (!(projectile.getShooter() instanceof HariantPlayer player)) {
            return;
        }
        
        if (!(projectile.getDamageSource() instanceof DamageSourceTripleShot damageSource)) {
            return;
        }
        
        if (ev.getEntity() == null) {
            return;
        }
        
        if (player.getProfile().getDatabase().achievements.hasCompleted(AchievementRegistry.ARCHER_TRIPLET)) {
            return;
        }
        
        player.getHeroData(HeroRegistry.ARCHER, HeroDataArcher::new).lastThreeHits.count(damageSource);
    }
    
    public static class DamageSourceTripleShot extends DamageSourceArcherTalent implements UniqueId {
        
        private final int shotAtTick;
        
        DamageSourceTripleShot(int shotAtTick, @NotNull DamageSourceIdentity identity, @Nullable HariantEntity attacker, double damage, double elementUnits) {
            super(identity, attacker, damage, elementUnits);
            
            this.shotAtTick = shotAtTick;
        }
        
        @Override
        public int getUniqueId() {
            return shotAtTick;
        }
        
    }
    
}
