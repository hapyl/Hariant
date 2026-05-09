package me.hapyl.hariant.hero.mage;

import me.hapyl.eterna.module.location.LocationHelper;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.attribute.AttributeScaling;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.damage.DamageSource;
import me.hapyl.hariant.entity.damage.DamageSourceIdentity;
import me.hapyl.hariant.entity.damage.DamageType;
import me.hapyl.hariant.entity.damage.DeathMessage;
import me.hapyl.hariant.entity.effect.status.EnumStatusEffect;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.hero.Definition;
import me.hapyl.hariant.hero.HeroRegistry;
import me.hapyl.hariant.talent.Response;
import me.hapyl.hariant.talent.Talent;
import me.hapyl.hariant.talent.TalentContext;
import me.hapyl.hariant.talent.field.DisplayField;
import me.hapyl.hariant.talent.target.TalentTarget;
import me.hapyl.hariant.task.HariantTickingTask;
import me.hapyl.hariant.task.Scheduler;
import me.hapyl.hariant.term.EnumTerm;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public final class TalentSoulStorm extends Talent {
    
    @DisplayField private final Decimal minimumSoulCost = Decimal.ofValue(10);
    @DisplayField private final Decimal damageBonusPerExtraSoulConsumed = Decimal.ofPercentage(10);
    
    @DisplayField private final Decimal numberOfSoulSpirits = Decimal.ofValue(4);
    
    @DisplayField private final AttributeScaling soulSpiritDamage = AttributeScaling.of(AttributeType.ATTACK, 432);
    
    @DisplayField private final Decimal soulSpiritHealth = Decimal.ofValue(200);
    @DisplayField private final Decimal soulSpiritLookupRadius = Decimal.ofValue(10);
    @DisplayField private final Decimal soulSpiritExplosionRadius = Decimal.ofValue(2);
    @DisplayField private final Decimal soulSpiritDuration = Decimal.ofSeconds(10);
    
    private final DeathMessage deathMessage = DeathMessage.create("{player}'s soul was stormed [by {killer}]");
    
    public TalentSoulStorm(@NotNull Key key) {
        super(key, Component.text("Soul Storm"), Icon.ofMaterial(Material.HEART_OF_THE_SEA));
        
        setDurationSeconds(2.5f);
        setCooldownSeconds(20);
        
        setDescription(
                Component.empty()
                         .append(Component.text("Consume all "))
                         .append(Definition.SOUL_FRAGMENT)
                         .append(Component.text("s", Definition.SOUL_FRAGMENT.getStyle()))
                         .append(Component.text(" to channel a "))
                         .append(Component.text("Soul Storm", Colors.SOUL))
                         .append(Component.text(" around yourself."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("After a short delay, the storm splits into "))
                         .append(Component.text("Soul Spirits", Colors.SOUL))
                         .append(Component.text(" that chase nearby "))
                         .append(Component.text("enemies", NamedTextColor.RED))
                         .append(Component.text(" and explode in small "))
                         .append(EnumTerm.AREA_OF_EFFECT)
                         .append(Component.text(", dealing "))
                         .append(ElementType.AETHER.asComponentDamage())
                         .append(Component.text("."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("Each ", NamedTextColor.DARK_GRAY))
                         .append(Definition.SOUL_FRAGMENT.getName().color(NamedTextColor.DARK_GRAY))
                         .append(Component.text(" consumed above ", NamedTextColor.DARK_GRAY))
                         .append(minimumSoulCost.asComponent().color(NamedTextColor.DARK_GRAY))
                         .append(Component.text(" increases the damage dealt by Soul Spirits by ", NamedTextColor.DARK_GRAY))
                         .append(damageBonusPerExtraSoulConsumed.asComponent().color(NamedTextColor.DARK_GRAY))
        );
    }
    
    public double calculateDamage(@NotNull HariantPlayer player, int soulsConsumed) {
        final double baseDamage = soulSpiritDamage.getScaledValue(player);
        
        return baseDamage * (1 + (soulsConsumed - minimumSoulCost.intValue()) * damageBonusPerExtraSoulConsumed.doubleValue());
    }
    
    @NotNull
    @Override
    public TalentTarget target(@NotNull HariantPlayer player) {
        return TalentTarget.none();
    }
    
    @NotNull
    @Override
    public Response execute(@NotNull HariantPlayer player, @NotNull TalentContext context) {
        final HeroDataMage heroData = player.getHeroData(HeroRegistry.MAGE, HeroDataMage::new);
        final int souls = heroData.getSouls();
        
        if (souls < minimumSoulCost.intValue()) {
            return Response.error("Not enough souls!");
        }
        
        heroData.decrementSouls(souls);
        
        final double damage = calculateDamage(player, souls);
        final DamageSource damageSource = DamageSource.common(DamageSourceIdentity.create(this, deathMessage), damage)
                                                      .source(player)
                                                      .elementType(ElementType.AETHER)
                                                      .damageType(DamageType.TALENT)
                                                      .build();
        
        final int duration = getDuration();
        
        player.addVanillaEffect(PotionEffectType.LEVITATION, 0, duration);
        player.addEffect(EnumStatusEffect.TALENT_LOCK, duration, player);
        
        player.delegate(
                new HariantTickingTask(Scheduler.ofTimer()) {
                    @Override
                    public void run(int tick) {
                        if (tick > duration) {
                            explode(player, damageSource);
                            this.cancel();
                            return;
                        }
                        
                        // Fx
                        final double radians = Math.toRadians(tick * 10);
                        final double radius = 2 * (1 - (double) tick / duration);
                        
                        final double x = Math.sin(radians) * radius;
                        final double y = Math.sin(radians * 0.5);
                        final double z = Math.cos(radians) * radius;
                        
                        final Location location = player.getMidpointLocation();
                        
                        LocationHelper.offset(location, x, y, z, () -> player.spawnWorldParticle(location, Particle.SCULK_SOUL, 1, 0, 0, 0, 0.0f));
                        LocationHelper.offset(location, -x, y, -z, () -> player.spawnWorldParticle(location, Particle.SCULK_SOUL, 1, 0, 0, 0, 0.0f));
                        LocationHelper.offset(location, x, -y, z, () -> player.spawnWorldParticle(location, Particle.SCULK_SOUL, 1, 0, 0, 0, 0.0f));
                        LocationHelper.offset(location, -x, -y, -z, () -> player.spawnWorldParticle(location, Particle.SCULK_SOUL, 1, 0, 0, 0, 0.0f));
                        
                        // TODO @Apr 27, 2026 (xanyjl) -> Adds Sfx
                    }
                    
                    @Override
                    public void onCancel() {
                        player.removeVanillaEffect(PotionEffectType.LEVITATION);
                        player.removeEffect(EnumStatusEffect.TALENT_LOCK);
                    }
                }
        );
        
        return Response.ok();
    }
    
    public void explode(@NotNull HariantPlayer player, @NotNull DamageSource damageSource) {
        for (int i = 0; i < numberOfSoulSpirits.intValue(); i++) {
            Hariant.createEntity(() -> new HariantEntitySoulSpirit(player, new HariantEntitySoulSpirit.Properties(
                    soulSpiritHealth,
                    soulSpiritLookupRadius,
                    soulSpiritExplosionRadius,
                    soulSpiritDuration,
                    damageSource
            )));
        }
    }
    
}