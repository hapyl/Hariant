package me.hapyl.hariant.hero.alchemist;

import me.hapyl.eterna.module.location.LocationHelper;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.hero.HeroRegistry;
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
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;

public final class TalentAlchemicalCauldron extends Talent {
    
    @DisplayField public final Decimal cauldronHealth = Decimal.ofValue(500);
    
    @DisplayField public final Decimal infusionDuration = Decimal.ofSeconds(15);
    @DisplayField public final Decimal toxicDamageIncrease = Decimal.ofAttribute(AttributeType.TOXIC_DAMAGE_BONUS, 40);
    
    public TalentAlchemicalCauldron(@NotNull Key key) {
        super(key, Component.text("Alchemical Cauldron"), Icon.ofMaterial(Material.CAULDRON));
        
        this.setDurationSeconds(15); // Brewing duration
        this.setCooldownSeconds(30);
        
        this.setTalentType(TalentType.ENHANCE);
        
        this.setDescription(
                Component.empty()
                         .append(Component.text("Place an alchemical cauldron in front of you."))
                         .appendNewline()
                         .append(Component.text("Put your stick inside to start brewing a toxic concoction!", NamedTextColor.DARK_GRAY, TextDecoration.ITALIC))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("After the cauldron finishes brewing, you can pull out the stick infused with "))
                         .append(ElementType.TOXIC.asComponentDamage())
                         .append(Component.text(" for "))
                         .append(infusionDuration)
                         .append(Component.text("."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("The cooldown of this talent starts after the brewing is done.", NamedTextColor.DARK_GRAY))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("The cauldron can be destroyed!", Colors.ERROR))
        );
    }
    
    @NotNull
    @Override
    public TalentTarget target(@NotNull HariantPlayer player) {
        return TalentTarget.none();
    }
    
    @NotNull
    @Override
    public Response execute(@NotNull HariantPlayer player, @NotNull TalentContext context) {
        final HeroDataAlchemist heroData = player.getHeroData(HeroRegistry.ALCHEMIST, HeroDataAlchemist::new);
        final Location location = player.getLocationInFrontFromEyes(2);
        
        location.setYaw(0.0f);
        location.setPitch(0.0f);
        
        heroData.setAlchemicalCauldron(Hariant.createEntity(() -> new HariantEntityAlchemicalCauldron(player, LocationHelper.anchor(location), this)));
        
        // Fx
        player.spawnWorldParticle(location, Particle.EXPLOSION_EMITTER, 1, 0.0f);
        
        player.playWorldSound(location, Sound.ENTITY_WITCH_HURT, 0.75f);
        player.playWorldSound(location, Sound.ENTITY_BREEZE_SHOOT, 0.5f);
        
        return Response.await();
    }
}
