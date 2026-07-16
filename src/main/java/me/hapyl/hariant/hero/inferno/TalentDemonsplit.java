package me.hapyl.hariant.hero.inferno;

import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.entity.StreamRules;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.hero.HeroRegistry;
import me.hapyl.hariant.talent.Response;
import me.hapyl.hariant.talent.Talent;
import me.hapyl.hariant.talent.TalentContext;
import me.hapyl.hariant.talent.target.TalentTarget;
import net.kyori.adventure.text.Component;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;

public abstract class TalentDemonsplit extends Talent {
    
    private static final Key SHARED_COOLDOWN_KEY = Key.ofString("demonsplit");
    
    private final InfernoDemonType demonType;
    
    public TalentDemonsplit(@NotNull Key key, @NotNull InfernoDemonType demonType) {
        super(key, Component.text("Demonsplit: ").append(demonType.getName()), demonType);
        
        this.demonType = demonType;
        
        setDurationSeconds(10);
        setCooldownSeconds(20);
    }
    
    @Override
    public void onRegister() {
        super.onRegister();
        
        setDescription(
                Component.empty()
                         .append(Component.text("Transform into a powerful "))
                         .append(Component.text("demon", Colors.HELL))
                         .append(Component.text(" — "))
                         .append(demonType.getName())
                         .append(Component.text(" — for "))
                         .append(getDurationFormatted())
                         .append(Component.text("."))
                         .appendNewline()
                         .append(Component.text("The demon inherits your health and attributes.", Colors.DARK_GRAY))
                         .appendNewline()
                         .appendNewline()
                         // Describe ability
                         .append(Component.text("Ability", Colors.ORANGE))
                         .appendNewline()
                         .append(describeAbility())
                         .appendNewline()
                         .appendNewline()
                         // Describe reform
                         .append(Component.text("On Reform", Colors.ORANGE))
                         .appendNewline()
                         .append(describeReform())
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("Demonsplit talents share the cooldown!", Colors.DARK_GRAY))
        );
    }
    
    @NotNull
    public abstract InfernoDemonEntity newInstance(@NotNull HariantPlayer player, InfernoDemonType infernoDemonType);
    
    @NotNull
    public abstract Component describeAbility();
    
    @NotNull
    public abstract Component describeReform();
    
    @NotNull
    @Override
    public final Key getCooldownKey() {
        return SHARED_COOLDOWN_KEY;
    }
    
    @NotNull
    @Override
    public final ItemBuilder createBuilder() {
        return super.createBuilder().setCooldownKey(SHARED_COOLDOWN_KEY);
    }
    
    @Override
    public @NotNull TalentTarget target(@NotNull HariantPlayer player) {
        return TalentTarget.none();
    }
    
    @Override
    public @NotNull Response execute(@NotNull HariantPlayer player, @NotNull TalentContext context) {
        final HeroDataInferno data = player.getHeroData(HeroRegistry.INFERNO, HeroDataInferno::new);
        
        if (data.currentDemon != null) {
            return Response.error("Already in demon form!");
        }
        
        final InfernoDemonEntity infernoDemonEntity = Hariant.createEntity(() -> newInstance(player, demonType));
        
        data.currentDemon = infernoDemonEntity;
        infernoDemonEntity.onForm(player, data);
        
        // Make the player invisible
        player.hide(StreamRules.ALL);
        
        // Fx
        player.getWorld().strikeLightningEffect(player.getEyeLocation().add(0d, 0.5d, 0d));
        
        player.spawnWorldParticle(player.getLocation(), Particle.EXPLOSION_EMITTER, 1, 0);
        
        player.playWorldSound(Sound.ENTITY_MOOSHROOM_CONVERT, 0.75f);
        player.playWorldSound(Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 1.75f);
        
        return Response.ok();
    }
    
    @NotNull
    @Override
    public String getTalentClassName() {
        return "Demonsplit Talent";
    }
    
}