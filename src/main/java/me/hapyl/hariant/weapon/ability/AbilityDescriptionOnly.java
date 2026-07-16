package me.hapyl.hariant.weapon.ability;

import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.talent.Response;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a special {@link Ability} implementation which is used purely to display the description.
 *
 * <p>
 * It has a finalized {@link #execute(HariantPlayer)} that is <b>never</b> called by the {@link AbilityHandler}, making it a tiny bit
 * more optimized to use, instead of the base {@link Ability} class.
 * </p>
 */
public class AbilityDescriptionOnly extends Ability {
    
    public AbilityDescriptionOnly(@NotNull Component name, @NotNull Component description) {
        super(name);
        
        this.setDescription(description);
    }
    
    @NotNull
    @Override
    public final Response execute(@NotNull HariantPlayer player) {
        return Response.error("Cannot execute this ability!");
    }
    
}
