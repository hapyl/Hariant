package me.hapyl.hariant.hero.pytaria;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.attribute.AttributeScaling;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.hero.HeroRegistry;
import me.hapyl.hariant.hero.pytaria.bee.BeeSwarm;
import me.hapyl.hariant.talent.TalentContext;
import me.hapyl.hariant.talent.TalentRegistry;
import me.hapyl.hariant.talent.field.DisplayField;
import me.hapyl.hariant.talent.target.TalentTarget;
import me.hapyl.hariant.talent.ultimate.TalentUltimate;
import me.hapyl.hariant.talent.ultimate.TalentUltimateResource;
import me.hapyl.hariant.task.executor.Executable;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;

public final class TalentFeelTheBreeze extends TalentUltimate {
    
    @DisplayField public final Decimal numberOfBees = Decimal.ofValue(10);
    
    @DisplayField public final Decimal maxStrayDistance = Decimal.ofValue(16);
    @DisplayField public final Decimal stingDistance = Decimal.ofValue(1.5);
    @DisplayField public final Decimal enemyLookupRadius = Decimal.ofValue(5);
    
    @DisplayField public final AttributeScaling beeDamage = AttributeScaling.of(AttributeType.ATTACK, 124);
    @DisplayField public final AttributeScaling beeDamageIvy = AttributeScaling.of(AttributeType.ATTACK, 166);
    
    @DisplayField public final Decimal elementalApplication = Decimal.ofElementalApplication(ElementType.PHYSICAL, 25);
    
    public TalentFeelTheBreeze(@NotNull Key key) {
        super(key, Component.text("Feel the Breeze"), Icon.ofTexture("d4579f1ea3864269c2148d827c0887b0c5ed43a975b102a01afb644efb85ccfd"), TalentUltimateResource.ENERGY, 60);
        
        this.setCooldownSeconds(20);
        
        this.setDescription(
                Component.empty()
                         .append(Component.text("Summon a swarm of "))
                         .append(Component.text("bees", NamedTextColor.GOLD))
                         .append(Component.text(" that float around "))
                         .append(Component.text("Pytaria", NamedTextColor.LIGHT_PURPLE))
                         .append(Component.text("."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("The bees get "))
                         .append(Component.text("angry", Colors.ERROR))
                         .append(Component.text(" whenever a nearby enemy disturbs their peace and fly towards that enemy, dealing "))
                         .append(ElementType.PHYSICAL.asComponentDamage())
                         .append(Component.text(", but "))
                         .append(Component.text("dying ", Colors.ERROR))
                         .append(Component.text("in the process."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("The damage is increased if the enemy is standing in a "))
                         .append(TalentRegistry.ROSE_IVY.getName().color(Colors.SUCCESS))
                         .append(Component.text("."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("The swarm lasts until all the bees die and Pytaria cannot gain Energy while the swarm is alive.", NamedTextColor.DARK_GRAY))
        );
    }
    
    @NotNull
    @Override
    public TalentTarget target(@NotNull HariantPlayer player) {
        return TalentTarget.none();
    }
    
    @NotNull
    @Override
    public Executable execute(@NotNull HariantPlayer player, @NotNull TalentContext context, double consumedResource) {
        return Executable.await(promise -> {
            final HeroDataPytaria data = player.getHeroData(HeroRegistry.PYTARIA, HeroDataPytaria::new);
            
            if (data.swarm != null) {
                data.swarm.cancel();
            }
            
            // No need to delegate because we store the swarm in hero data
            data.swarm = new BeeSwarm(player, this, promise);
            
            // Fx
            // player.playWorldSound();
        });
    }
    
}
