package me.hapyl.hariant.hero.ninja;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.talent.Response;
import me.hapyl.hariant.talent.Talent;
import me.hapyl.hariant.talent.TalentContext;
import me.hapyl.hariant.talent.TalentType;
import me.hapyl.hariant.talent.target.TalentTarget;
import me.hapyl.hariant.util.Icon;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class TalentShunshin extends Talent {

    private final double dashStrength = 1.3;

    public TalentShunshin(@NotNull Key key) {
        super(key,
                Component.text("Shunshin"),
                Icon.ofMaterial(Material.FEATHER));
        setTalentType(TalentType.MOVEMENT);
        setCooldownSeconds(12);

        setDescription(
                Component.empty().append(Component.text("A lightning-fast glide through space, slicing the air and closing the distance in a heartbeat."))
        );
    }

    @Override
    public @NotNull TalentTarget target(@NotNull HariantPlayer player) {
        return TalentTarget.none();
    }

    @Override
    public @NotNull Response execute(@NotNull HariantPlayer player, @NotNull TalentContext context) {
        Location eyeLocation = player.getEyeLocation();
        Vector direction = eyeLocation.getDirection().normalize();

        Vector impulse = direction.multiply(dashStrength);

        player.setVelocity(impulse);
        return Response.ok();
    }
}
