package me.hapyl.hariant.hero.inferno;

import me.hapyl.eterna.module.reflect.team.PacketTeamColor;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.attribute.instance.Attributes;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.StreamRules;
import me.hapyl.hariant.entity.VanillaAttributeModifier;
import me.hapyl.hariant.entity.damage.DamageInstance;
import me.hapyl.hariant.entity.damage.DamageResult;
import me.hapyl.hariant.entity.damage.DamageType;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.team.EnumTeam;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.jetbrains.annotations.NotNull;

import javax.annotation.OverridingMethodsMustInvokeSuper;

public abstract class InfernoDemonEntity extends HariantEntity implements InfernoDemon {
    
    private static final VanillaAttributeModifier REACH_MODIFIER = VanillaAttributeModifier.create(
            Key.ofString("demonsplit_reach"),
            Attribute.ENTITY_INTERACTION_RANGE,
            VanillaAttributeModifier.Operation.ADDITIVE,
            TalentDemonsplit.REACH_INCREASE
    );
    
    protected final HariantPlayer player;
    protected final InfernoDemonType demonType;
    
    private final int duration;
    private int tick;
    
    public InfernoDemonEntity(@NotNull HariantPlayer player, @NotNull InfernoDemonType demonType, @NotNull TalentDemonsplit talentDemonsplit) {
        super(demonType.createEntity(player.getLocation()), Attributes.copyOf(player.getAttributes()));
        
        this.player = player;
        this.demonType = demonType;
        this.duration = talentDemonsplit.getDuration();
        this.tick = talentDemonsplit.getDuration();
        
        final EnumTeam playerTeam = player.getPlayerTeam();
        playerTeam.addEntry(this);
        
        // Glow demon for teammates
        playerTeam.getPlayers().forEach(teammate -> {
            if (!player.equals(teammate)) {
                setGlowing(teammate, PacketTeamColor.GREEN);
            }
        });
        
        // Show demon for everyone except the player via bukkit entities because spectators
        Bukkit.getOnlinePlayers().forEach(other -> {
            if (!player.compareEntity(other)) {
                Hariant.showBukkitEntity(other, getHandle());
            }
        });
        
        // Add modifier
        player.addVanillaAttributeModifier(REACH_MODIFIER);
    }
    
    @Override
    public @NotNull HariantEntity owner() {
        return player;
    }
    
    @Override
    public boolean isImmuneTo(@NotNull DamageInstance damageInstance) {
        return damageInstance.getElementType() == ElementType.FIRE || damageInstance.getDamageType() == DamageType.ENVIRONMENT;
    }
    
    @Override
    public @NotNull DamageResult damage0(@NotNull DamageInstance damageInstance) {
        final DamageType damageType = damageInstance.getDamageType();
        
        // Only redirect melee and ranged damage
        if (damageType != DamageType.MELEE && damageType != DamageType.RANGED) {
            return DamageResult.IMMUNE;
        }
        
        final DamageResult damageResult = player.damage(damageInstance);
        
        // If damage is success, play hurt fx
        if (damageResult == DamageResult.OK) {
            playDamageFx(this::getHurtSound);
        }
        
        return damageResult;
    }
    
    @OverridingMethodsMustInvokeSuper
    @Override
    public boolean tick() {
        // Don't tick super, we don't give a fuck about it
        tick--;
        
        // Always sync the entity to player
        teleport(player.getLocation());
        
        return true;
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        
        // Always show the player
        player.show(StreamRules.ALL);
        
        // Remove reach modifier
        player.removeVanillaAttributeModifier(REACH_MODIFIER);
    }
    
    @Override
    public @NotNull InfernoDemonType getDemonType() {
        return demonType;
    }
    
    @Override
    public void onReform(@NotNull HariantPlayer player, @NotNull HeroDataInferno data) {
        player.strikeLightningEffect();
        
        player.spawnWorldParticle(player.getLocation(), Particle.EXPLOSION_EMITTER, 1, 0);
        
        player.playWorldSound(Sound.ENTITY_BLAZE_DEATH, 0.75f);
        player.playWorldSound(Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 0.75f);
        
        InfernoDemon.drawParticleBox(player, location -> player.spawnWorldParticle(location, Particle.FALLING_LAVA, 1, 0), 2.0d);
    }
    
    @Override
    public int currentTick() {
        return tick;
    }
    
    @Override
    public int duration() {
        return duration;
    }
    
    @Override
    public void swingArm() {
        entity.swingMainHand();
    }
    
    @Override
    public @NotNull Component getName() {
        return demonType.getName();
    }
    
}