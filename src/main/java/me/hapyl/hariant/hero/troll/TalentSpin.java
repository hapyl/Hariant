package me.hapyl.hariant.hero.troll;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.entity.effect.Effect;
import me.hapyl.hariant.entity.effect.EffectType;
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
import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;

public final class TalentSpin extends Talent implements Effect {
    
    @DisplayField private final Decimal radius = Decimal.ofValue(30);
    @DisplayField private final Decimal rotationDegrees = Decimal.ofValue(180, value -> Component.text("%.0f°".formatted(value)));
    
    public TalentSpin(@NotNull Key key) {
        super(key, Component.text("Spin"), Icon.ofMaterial(Material.NAUTILUS_SHELL));
        
        setTalentType(TalentType.IMPAIR);
        setCooldownSeconds(15);
        
        setDescription(
                Component.empty()
                         .append(Component.text("Rotate the heads of nearby "))
                         .append(Component.text("enemies", Colors.RED))
                         .append(Component.text(" by "))
                         .append(rotationDegrees)
                         .append(Component.text("."))
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
                  if (entity.triggerEffect(player, this)) {
                      return;
                  }
                  
                  final Location location = entity.getLocation();
                  location.setYaw(location.getYaw() + rotationDegrees.floatValue());
                  
                  entity.teleport(location);
                  
                  // Fx
                  entity.playSound(Sound.ENTITY_BLAZE_HURT, 2.0f);
              });
        
        // Fx
        player.playSound(Sound.ENTITY_BLAZE_HURT, 0.75f);
        player.playSound(Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 2.0f);
        
        return Response.ok();
    }
    
    @Override
    public @NotNull EffectType getEffectType() {
        return EffectType.DEBUFF;
    }
    
}