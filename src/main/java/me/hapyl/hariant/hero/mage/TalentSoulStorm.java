package me.hapyl.hariant.hero.mage;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.attribute.AttributeScaling;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.talent.TalentContext;
import me.hapyl.hariant.talent.TalentType;
import me.hapyl.hariant.talent.field.DisplayField;
import me.hapyl.hariant.talent.target.TalentTarget;
import me.hapyl.hariant.talent.ultimate.TalentUltimate;
import me.hapyl.hariant.talent.ultimate.UltimateResourceType;
import me.hapyl.hariant.task.executor.Executable;
import me.hapyl.hariant.task.executor.ExecutorService;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public final class TalentSoulStorm extends TalentUltimate {
    
    public final @DisplayField Decimal castingDuration = Decimal.ofSeconds(0.75f);
    
    public final @DisplayField AttributeScaling damage = AttributeScaling.create(AttributeType.ATTACK, 90);
    
    public final @DisplayField Decimal distance = Decimal.ofValue(30);
    public final @DisplayField Decimal radius = Decimal.ofValue(2f);
    public final @DisplayField Decimal elementalApplication = Decimal.ofElementalApplication(ElementType.AETHER, 100);
    public final @DisplayField Decimal damagePeriod = Decimal.ofSeconds(0.5f);
    
    public final Key cooldownKey = Key.ofString("soul_storm_icd");
    
    public TalentSoulStorm(@NotNull Key key) {
        super(key, Component.text("Soul Storm"), Icon.ofMaterial(Material.WARDEN_SPAWN_EGG), UltimateResourceType.ENERGY, 60);
        
        setTalentType(TalentType.DAMAGE);
        
        setDurationSeconds(5);
        setCooldownSeconds(20);
        
        setDescription(
                Component.empty()
                         .append(Component.text("Start charging a powerful Soul Storm."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("After "))
                         .append(castingDuration)
                         .append(Component.text(", release the "))
                         .append(Component.text("souls", Colors.SOUL))
                         .append(Component.text(" that rush forward, dealing "))
                         .append(ElementType.AETHER.asComponentAreaOfEffectDamage())
                         .append(Component.text(" and apply "))
                         .append(ElementType.AETHER)
                         .append(Component.text(" anomaly."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("The souls can pass through solid blocks.", Colors.DARK_GRAY))
        );
    }
    
    @Override
    public @NotNull Executable execute(@NotNull HariantPlayer player, @NotNull TalentContext context, double consumedResource) {
        final Location location = player.getEyeLocation();
        final Vector vector = location.getDirection().normalize();
        
        return new ExecutorService()
                .then(Executable.execute(() -> {
                    // Fx
                    player.playWorldSound(Sound.ENTITY_WARDEN_EMERGE, 5, 2.0f);
                    
                    player.spawnWorldParticle(location, Particle.SOUL, 50, 0.2, 0.2, 0.2, 0.15f);
                    player.spawnWorldParticle(location, Particle.SONIC_BOOM, 1, 0);
                }))
                .then(Executable.later(() -> {
                    // Don't delegate
                    new SoulStorm(player, location, vector, TalentSoulStorm.this);
                }, castingDuration.intValue()));
    }
    
    @Override
    public @NotNull TalentTarget target(@NotNull HariantPlayer player) {
        return TalentTarget.none();
    }
    
}