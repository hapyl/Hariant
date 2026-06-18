package me.hapyl.hariant.hero.troll;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.entity.damage.AssistSource;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.talent.Response;
import me.hapyl.hariant.talent.Talent;
import me.hapyl.hariant.talent.TalentContext;
import me.hapyl.hariant.talent.TalentType;
import me.hapyl.hariant.talent.field.DisplayField;
import me.hapyl.hariant.talent.target.TalentTarget;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public final class TalentRepulsor extends Talent {
    
    @DisplayField private final Decimal radius = Decimal.ofValue(10);
    @DisplayField private final Decimal strength = Decimal.ofValue(1.2);
    
    public TalentRepulsor(@NotNull Key key) {
        super(key, Component.text("Repulsor"), Icon.ofMaterial(Material.IRON_BOOTS));
        
        setTalentType(TalentType.IMPAIR);
        setCooldownSeconds(10);
        
        setDescription(
                Component.empty()
                         .append(Component.text("Propel nearby "))
                         .append(Component.text("enemies", Colors.RED))
                         .append(Component.text(" high up into the sky."))
        );
    }
    
    @Override
    public @NotNull TalentTarget target(@NotNull HariantPlayer player) {
        return TalentTarget.none();
    }
    
    @Override
    public @NotNull Response execute(@NotNull HariantPlayer player, @NotNull TalentContext context) {
        player.collectNearbyEntities(radius)
              .filter(player::canAffect)
              .forEach(entity -> {
                  if (entity.hasEffectResistance(AssistSource.create(player, this))) {
                      return;
                  }
                  
                  entity.setVelocity(new Vector(0, strength.doubleValue(), 0));
                  entity.triggerDebuff(player);
                  
                  // Fx
                  final Location location = entity.getLocation();
                  
                  entity.playSound(location, Sound.ENTITY_BREEZE_SHOOT, 1.75f);
                  entity.spawnWorldParticle(location, Particle.GUST, 1, 0.0f);
                  entity.spawnWorldParticle(location, Particle.POOF, 5, 0.1, 0.4, 0.4, 0.1f);
              });
        
        // Fx
        player.playSound(Sound.ENTITY_BREEZE_SHOOT, 1.25f);
        
        return Response.ok();
    }
}
