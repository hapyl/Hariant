package me.hapyl.hariant.hero.archer;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.achievement.AchievementRegistry;
import me.hapyl.hariant.achievement.UniqueId;
import me.hapyl.hariant.attribute.AttributeScaling;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.damage.DamageSource;
import me.hapyl.hariant.entity.damage.DamageSourceIdentity;
import me.hapyl.hariant.entity.damage.DeathMessage;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.handler.HariantDamageProjectile;
import me.hapyl.hariant.hero.HeroRegistry;
import me.hapyl.hariant.talent.Response;
import me.hapyl.hariant.talent.Talent;
import me.hapyl.hariant.talent.TalentContext;
import me.hapyl.hariant.talent.field.DisplayField;
import me.hapyl.hariant.talent.target.TalentTarget;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

public final class TalentTripleShot extends Talent {
    
    private final Color arrowColor = Color.fromRGB(Colors.ELEMENT_ELECTRIC.value());
    
    private final @DisplayField AttributeScaling damage = AttributeScaling.create(AttributeType.ATTACK, 121.5);
    
    private final @DisplayField Decimal additionalArrowDamageMultiplier = Decimal.ofPercentage(50);
    private final @DisplayField Decimal additionalArrowSpread = Decimal.ofAngle(5);
    private final @DisplayField Decimal elementalApplication = Decimal.ofElementalApplication(ElementType.ELECTRIC, 100);
    
    private final DamageSourceIdentity damageSourceIdentity = DamageSourceIdentity.createOfNamed(
            this,
            Key.ofString("triple_shot_damage_source"),
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
        
        final TripleShotProjectile projectile = launchArrow(localTick, player, damage, null);
        
        launchArrow(localTick, player, additionalArrowDamage, projectile.getVelocity().add(player.getVectorLeft(spread)));
        launchArrow(localTick, player, additionalArrowDamage, projectile.getVelocity().add(player.getVectorRight(spread)));
        
        // Fx
        player.playWorldSound(Sound.ITEM_CROSSBOW_SHOOT, 0.75f);
        player.playWorldSound(Sound.ITEM_CROSSBOW_SHOOT, 1.00f);
        player.playWorldSound(Sound.ITEM_CROSSBOW_SHOOT, 1.25f);
        
        return Response.ok();
    }
    
    private @NotNull TripleShotProjectile launchArrow(int localTick, @NotNull HariantPlayer player, double damage, @Nullable Vector velocity) {
        return player.launchProjectile(
                Arrow.class,
                velocity,
                (projectile, shooter) -> new TripleShotProjectile(projectile, shooter, new DamageSourceTripleShot(damageSourceIdentity, player, damage, elementalApplication.doubleValue()), localTick)
        );
    }
    
    public static class DamageSourceTripleShot extends DamageSourceArcherTalent {
        
        DamageSourceTripleShot(@NotNull DamageSourceIdentity identity, @Nullable HariantEntity attacker, double damage, double elementUnits) {
            super(identity, attacker, damage, elementUnits);
        }
        
    }
    
    private class TripleShotProjectile extends HariantDamageProjectile implements UniqueId {
        
        private final int uniqueId;
        
        TripleShotProjectile(@NotNull Arrow projectile, @NotNull HariantEntity shooter, @NotNull DamageSource damageSource, int uniqueId) {
            super(projectile, shooter, damageSource);
            
            this.uniqueId = uniqueId;
            
            projectile.setColor(arrowColor);
            projectile.setCritical(false);
        }
        
        @Override
        public @Range(from = 0, to = Integer.MAX_VALUE) int getUniqueId() {
            return uniqueId;
        }
        
        @Override
        public void onHit(@Nullable HariantEntity entity, @Nullable Block block) {
            super.onHit(entity, block);
            
            if (entity == null || !(this.getShooter() instanceof HariantPlayer player)) {
                return;
            }
            
            if (player.getProfile().getDatabase().achievements.hasCompleted(AchievementRegistry.ARCHER_TRIPLET)) {
                return;
            }
            
            player.getHeroData(HeroRegistry.ARCHER, HeroDataArcher::new).lastThreeHits.count(this);
        }
    }
    
}
