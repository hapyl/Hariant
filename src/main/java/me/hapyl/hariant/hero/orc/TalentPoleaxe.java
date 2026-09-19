package me.hapyl.hariant.hero.orc;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.hero.HeroRegistry;
import me.hapyl.hariant.talent.Response;
import me.hapyl.hariant.talent.Talent;
import me.hapyl.hariant.talent.TalentContext;
import me.hapyl.hariant.talent.target.TalentTarget;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.decimal.Decimal;
import me.hapyl.hariant.util.field.DisplayField;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public abstract class TalentPoleaxe extends Talent {
    
    private final @DisplayField Decimal otherTalentCooldownThreshold = Decimal.ofPercentage(50);
    
    public TalentPoleaxe(@NotNull Key key, @NotNull Component name, @NotNull Icon icon) {
        super(key, name, icon);
    }
    
    @Override
    public @NotNull TalentTarget target(@NotNull HariantPlayer player) {
        return TalentTarget.none();
    }
    
    @Override
    public final @NotNull Response execute(@NotNull HariantPlayer player, @NotNull TalentContext context) {
        // If player doesn't current have the axe, don't allow execution
        final HeroDataOrc heroData = player.getHeroData(HeroRegistry.ORC, HeroDataOrc::new);
        
        if (heroData.hasWeaponEntity()) {
            return Response.error("Poleaxe is missing!");
        }
        
        // Execute the talent
        this.execute(player);
        
        // Start the 'other talent' cooldown
        final TalentPoleaxe otherTalent = this.otherTalent();
        
        // If player cooldown is already higher than other talent cooldown %, don't restart it
        final int cooldown = (int) (otherTalent.getCooldown() * otherTalentCooldownThreshold.doubleValue());
        final int playerCooldown = player.getCooldownTimeLeft(otherTalent);
        
        if (playerCooldown == 0 || playerCooldown < cooldown) {
            player.setCooldown(otherTalent, cooldown);
        }
        
        return Response.ok();
    }
    
    @Override
    public @NotNull String getTalentClassName() {
        return "Poleaxe Talent";
    }
    
    public abstract @NotNull TalentPoleaxe otherTalent();
    
    public abstract void execute(@NotNull HariantPlayer player);
    
    protected @NotNull Component createCooldownComponent(@NotNull Component otherTalentName) {
        return Component.empty()
                        .append(Component.text("This talent cannot be used without your Poleaxe!", Colors.YELLOW))
                        .appendNewline()
                        .appendNewline()
                        .append(Component.text("Additionally, using this talent starts ", Colors.DARK_GRAY))
                        .append(otherTalentCooldownThreshold.asComponent().color(Colors.DARK_GRAY))
                        .append(Component.text(" of ", Colors.DARK_GRAY))
                        .append(otherTalentName.color(Colors.DARK_GRAY))
                        .append(Component.text("'s cooldown.", Colors.DARK_GRAY));
    }
    
}