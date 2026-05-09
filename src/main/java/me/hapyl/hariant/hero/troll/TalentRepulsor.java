package me.hapyl.hariant.hero.troll;

import me.hapyl.eterna.module.registry.Key;
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
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class TalentRepulsor extends Talent {
    
    @DisplayField private final Decimal radius = Decimal.ofValue(10);
    @DisplayField private final Decimal strength = Decimal.ofValue(0.8);
    
    public TalentRepulsor(@NotNull Key key) {
        super(key, Component.text("Repulsor"), Icon.ofMaterial(Material.IRON_BOOTS));
        
        setTalentType(TalentType.IMPAIR);
        setCooldownSeconds(10);
        
        setDescription(
                Component.empty()
                         .append(Component.text("Propel nearby "))
                         .append(Component.text("enemies", NamedTextColor.RED))
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
                  entity.playSound(Sound.ENTITY_BREEZE_SHOOT, 1.75f);
                  entity.sendMessage(Component.text("Whooosh!", NamedTextColor.GREEN));
              });
        
        // Fx
        player.playSound(Sound.ENTITY_BREEZE_SHOOT, 1.25f);
        
        return Response.ok();
    }
}
