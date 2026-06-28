package me.hapyl.hariant.hero.mage;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.hero.HeroRegistry;
import me.hapyl.hariant.talent.TalentContext;
import me.hapyl.hariant.talent.TalentType;
import me.hapyl.hariant.talent.field.DisplayField;
import me.hapyl.hariant.talent.target.TalentTarget;
import me.hapyl.hariant.talent.ultimate.TalentUltimate;
import me.hapyl.hariant.talent.ultimate.UltimateResourceType;
import me.hapyl.hariant.task.executor.Executable;
import me.hapyl.hariant.util.ComponentFormatter;
import me.hapyl.hariant.util.Definition;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class TalentSoulStorm extends TalentUltimate {
    
    @DisplayField private final Decimal maximumSoulsConsumed = Decimal.ofValue(10);
    @DisplayField private final Decimal minimumSoulsRequired = Decimal.ofValue(2);
    @DisplayField private final Decimal maximumSoulStormCharges = Decimal.ofValue(maximumSoulsConsumed.doubleValue() / minimumSoulsRequired.doubleValue());
    
    @DisplayField private final ComponentFormatter soulToRestlessSoulConversionRatio = ComponentFormatter.format(Component.text("2/1", Colors.SUCCESS));
    
    public TalentSoulStorm(@NotNull Key key) {
        super(key, Component.text("Soul Storm"), Icon.ofMaterial(Material.WARDEN_SPAWN_EGG), UltimateResourceType.ENERGY, 30);
        
        setTalentType(TalentType.ENHANCE);
        
        setCooldownSeconds(20);
        
        setDescription(
                Component.empty()
                         .append(Component.text("Consume up to "))
                         .append(maximumSoulsConsumed)
                         .appendSpace()
                         .append(Definition.SOUL_FRAGMENT)
                         .append(Component.text("s", Definition.SOUL_FRAGMENT.getStyle()))
                         .append(Component.text(", converting them into "))
                         .append(Component.text("Restless Souls", Colors.RESTLESS_SOUL))
                         .append(Component.text(" with a "))
                         .append(soulToRestlessSoulConversionRatio)
                         .append(Component.text(" ratio."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("Requires at least %s souls.".formatted(minimumSoulsRequired.intValue()), Colors.DARK_GRAY))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("Restless Soul", Colors.ORANGE))
                         .appendNewline()
                         .append(WeaponSoulEaterUltimate.WeaponRangeProjectileTypeRestlessSoul.DESCRIPTION)
                         .appendNewline()
                         .appendNewline()
                         .append(
                                 Component.empty()
                                          .append(Component.text("Restless Souls are used before ", Colors.DARK_GRAY))
                                          .append(Definition.SOUL_FRAGMENT.asComponent().color(Colors.DARK_GRAY))
                                          .append(Component.text(".", Colors.DARK_GRAY))
                         )
        );
    }
    
    @Override
    public @NotNull Executable execute(@NotNull HariantPlayer player, @NotNull TalentContext context, double consumedResource) {
        final HeroDataMage heroData = player.getHeroData(HeroRegistry.MAGE, HeroDataMage::new);
        
        final int soulsUsed = Math.min(
                (heroData.getSouls() / minimumSoulsRequired.intValue()) * minimumSoulsRequired.intValue(),
                maximumSoulsConsumed.intValue()
        );
        
        final int soulStormCharges = soulsUsed / minimumSoulsRequired.intValue();
        
        // Decrement souls
        heroData.decrementSouls(soulsUsed);
        
        // TODO (xanyjl @ Saturday, May 30) -> Animation
        
        return Executable.await(promise -> {
            heroData.createSoulStorm(soulStormCharges, maximumSoulStormCharges.intValue(), promise);
        });
    }
    
    @NotNull
    @Override
    public TalentTarget target(@NotNull HariantPlayer player) {
        return new TalentTarget() {
            @Override
            public @Nullable TalentContext createContext(@NotNull HariantPlayer player) {
                return player.getHeroData(HeroRegistry.MAGE, HeroDataMage::new).getSouls() < minimumSoulsRequired.intValue()
                       ? null
                       : TalentContext.empty();
            }
            
            @Override
            public @NotNull Component errorMessage() {
                return Component.text("Not enough souls!");
            }
        };
    }
    
}