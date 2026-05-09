package me.hapyl.hariant.hero.troll;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.entity.HariantRandom;
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
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class TalentPanicRoll extends Talent {
    
    @DisplayField private final Decimal invulnerabilityDuration = Decimal.ofValue(10);
    @DisplayField private final Decimal rollStrength = Decimal.ofValue(0.345);
    
    public TalentPanicRoll(@NotNull Key key) {
        super(key, Component.text("Panic Roll"), Icon.ofMaterial(Material.IRON_NAUTILUS_ARMOR));
        
        setTalentType(TalentType.MOVEMENT);
        
        setDescription(
                Component.empty()
                         .append(Component.text("Panic and roll in a random direction."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("You are invulnerable during the roll.", NamedTextColor.DARK_GRAY))
        );
    }
    
    @Override
    public @NotNull TalentTarget target(@NotNull HariantPlayer player) {
        return TalentTarget.none();
    }
    
    @Override
    public @NotNull Response execute(@NotNull HariantPlayer player, @NotNull TalentContext context) {
        final HariantRandom random = player.getRandom();
        
        // Set invulnerability
        player.setInvulnerability(10);
        
        // Roll
        final Vector direction = new Vector(
                random.nextSignedDouble(1.0),
                0.0,
                random.nextSignedDouble(1.0)
        ).multiply(rollStrength.doubleValue());
        
        player.setVelocity(direction);
        
        // Fx
        final Location location = player.getLocation();
        
        player.playWorldSound(Sound.ENTITY_CAMEL_DASH, 1.25f);
        player.spawnWorldParticle(location, Particle.POOF, 5, 0.2, 0.2, 0.2, 0.05f);
        
        return Response.ok();
    }
}
