package me.hapyl.hariant.hero.blast_knight;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.Removable;
import me.hapyl.eterna.module.util.Ticking;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.attribute.AttributeScaling;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.HeadComponent;
import me.hapyl.hariant.entity.cooldown.HariantCooldown;
import me.hapyl.hariant.entity.damage.DamageSource;
import me.hapyl.hariant.entity.damage.DamageSourceImpl;
import me.hapyl.hariant.entity.damage.mutator.DamageMutator;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.event.HariantDamageEvent;
import me.hapyl.hariant.hero.HeroRegistry;
import me.hapyl.hariant.talent.Response;
import me.hapyl.hariant.talent.Talent;
import me.hapyl.hariant.talent.TalentContext;
import me.hapyl.hariant.talent.TalentType;
import me.hapyl.hariant.talent.field.DisplayField;
import me.hapyl.hariant.talent.target.TalentTarget;
import me.hapyl.hariant.team.EnumTeam;
import me.hapyl.hariant.util.Definition;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class TalentQuantumWard extends Talent implements Listener {
    
    private final @DisplayField Decimal maxDistance = Decimal.ofValue(20);
    private final @DisplayField Decimal damageSplit = Decimal.ofPercentage(50);
    
    private final @DisplayField Decimal quantumEnergyGeneration = Decimal.ofValue(1);
    private final @DisplayField HariantCooldown quantumEnergyGenerationCooldown = HariantCooldown.ofSeconds(Key.ofString("ward_quantum_generation_cooldown"), 1.0f);
    
    private final @DisplayField AttributeScaling wardStrength = AttributeScaling.create(AttributeType.DEFENSE, 180);
    
    private final @DisplayField Decimal splitDamageReductionDefenseMinimum = Decimal.ofValue(300);
    private final @DisplayField Decimal splitDamageReductionPerDefense = Decimal.ofValue(50);
    
    private final @DisplayField Decimal splitDamageReductionPercentage = Decimal.ofPercentage(10);
    private final @DisplayField Decimal splitDamageReductionPercentageLimit = Decimal.ofPercentage(50);
    
    public TalentQuantumWard(@NotNull Key key) {
        super(key, Component.text("Quantum Ward"), Icon.ofMaterial(Material.POPPED_CHORUS_FRUIT));
        
        setCooldownSeconds(20);
        setTalentType(TalentType.DEFENSE);
        
        setDescription(
                Component.empty()
                         .append(Component.text("Apply a "))
                         .append(this.getName().style(Definition.QUANTUM_ENERGY.getStyle()))
                         .append(Component.text(" on the target "))
                         .append(Component.text("teammate", Colors.GREEN))
                         .append(Component.text("."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("When the "))
                         .append(Component.text("teammate", Colors.GREEN))
                         .append(Component.text(" takes damage, the damage is "))
                         .append(Component.text("split", Colors.WHITE, TextDecoration.UNDERLINED))
                         .append(Component.text(" between you two, and you generate "))
                         .append(quantumEnergyGeneration)
                         .appendSpace()
                         .append(Definition.QUANTUM_ENERGY)
                         .append(Component.text(" once every "))
                         .append(quantumEnergyGenerationCooldown)
                         .append(Component.text("."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("For every "))
                         .append(splitDamageReductionPerDefense)
                         .appendSpace()
                         .append(AttributeType.DEFENSE)
                         .append(Component.text(" above "))
                         .append(splitDamageReductionDefenseMinimum)
                         .append(Component.text(", the split damage is reduced by "))
                         .append(splitDamageReductionPercentage)
                         .append(Component.text(", up to "))
                         .append(splitDamageReductionPercentageLimit)
                         .append(Component.text("."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("Cooldown of this talent starts after the ward is broken.", Colors.DARK_GRAY))
        );
    }
    
    @Override
    public @NotNull TalentTarget target(@NotNull HariantPlayer player) {
        return TalentTarget.targetEntityRayCast(maxDistance.doubleValue(), 1.25, player::isTeammate);
    }
    
    @Override
    public @NotNull Response execute(@NotNull HariantPlayer player, @NotNull TalentContext context) {
        final HeroDataBlastKnight heroData = player.getHeroData(HeroRegistry.BLAST_KNIGHT, HeroDataBlastKnight::new);
        final HariantEntity target = context.retrieve(HariantEntity.class);
        final double wardStrength = this.wardStrength.getScaledValue(player);
        
        heroData.createStoneCastle(new StoneCastle(player, target, wardStrength));
        
        // Fx
        player.playSound(Sound.BLOCK_BELL_RESONATE, 2.0f);
        target.playSound(Sound.BLOCK_BELL_RESONATE, 2.0f);
        
        target.sendMessage(
                Component.empty()
                         .append(player.getName())
                         .append(Component.text(" protected you with a "))
                         .append(this.getName())
                         .append(Component.text("!"))
        );
        
        return Response.await();
    }
    
    @EventHandler
    public void handleHariantDamageEvent(HariantDamageEvent ev) {
        final HariantEntity entity = ev.getEntity();
        final EnumTeam team = entity.getTeam().orElse(null);
        
        if (team == null) {
            return;
        }
        
        // Find a teammate with a castle
        final @Nullable StoneCastle stoneCastle = team.getPlayers()
                                                      .flatMap(player -> player.touchHeroData(HeroRegistry.BLAST_KNIGHT, HeroDataBlastKnight.class, HeroDataBlastKnight::getStoneCastle).stream())
                                                      .filter(castle -> castle.getEntity().equals(entity))
                                                      .findAny()
                                                      .orElse(null);
        
        if (stoneCastle == null) {
            return;
        }
        
        final HariantPlayer player = stoneCastle.getPlayer();
        
        // Calculate the damage and split it
        final double damage = ev.getDamage();
        final double damageReductionMultiplier = calculateDamageReductionMultiplier(player);
        final double damageToDealToBlastKnight = Math.max(0, damage * damageSplit.doubleValue() * damageReductionMultiplier);
        
        final double wardStrengthAfterTakingDamage = stoneCastle.damage(damageToDealToBlastKnight);
        final double damageReduction = damage - (damage * (1 - damageSplit.doubleValue()) + Math.max(0, -wardStrengthAfterTakingDamage));
        
        // Mutate damage by reducing the damage based on the mitigated damage
        ev.mutateDamage(() -> TalentQuantumWard.this + " (Split to %s)".formatted(player.getEntityName()), DamageMutator.subtract(), damageReduction);
        
        // Deal damage to blast knight
        player.damage(new DamageSourceStoneCastle(ev.getDamageSource(), damageToDealToBlastKnight));
        
        final HeroDataBlastKnight heroData = player.getHeroData(HeroRegistry.BLAST_KNIGHT, HeroDataBlastKnight::new);
        
        // If not on cooldown, regenerate energy
        heroData.incrementQuantumEnergyIfNotOnCooldown(quantumEnergyGeneration, quantumEnergyGenerationCooldown);
        
        // If ward broke, reset it
        if (wardStrengthAfterTakingDamage < 0) {
            heroData.removeStoneCastle();
            player.setCooldown(this);
        }
    }
    
    public double calculateDamageReductionMultiplier(@NotNull HariantPlayer player) {
        final double defense = player.getAttributes().get(AttributeType.DEFENSE);
        
        return Math.min(
                (int) (Math.max(0, defense - splitDamageReductionDefenseMinimum.intValue()) / splitDamageReductionPerDefense.intValue()) * splitDamageReductionPercentage.doubleValue(),
                splitDamageReductionPercentageLimit.doubleValue()
        );
        
    }
    
    public static class DamageSourceStoneCastle extends DamageSourceImpl {
        DamageSourceStoneCastle(@NotNull DamageSource damageSource, double damage) {
            super(
                    damageSource.getIdentity(),
                    damageSource.getSource(),
                    damageSource.getDamageType(),
                    damageSource.getElementType(),
                    damageSource.getDamageComponents(),
                    damageSource.getDamageFlags(),
                    damage, // Override the damage with the split damage
                    0       // Force not to apply any element
            );
        }
    }
    
    public static class StoneCastle implements Ticking, Removable, ComponentLike {
        
        private static final Component CASTLE_CHAR = Component.text("\uD83C\uDFF0", Colors.ARCHETYPE_DEFENSE);
        
        private final HariantPlayer player;
        private final HariantEntity entity;
        
        private final Component componentEntity;
        
        private double health;
        
        public StoneCastle(@NotNull HariantPlayer player, @NotNull HariantEntity entity, double health) {
            this.player = player;
            this.entity = entity;
            this.health = health;
            this.componentEntity = asComponent0(player); // Show just whoever applied the ward
        }
        
        public @NotNull HariantPlayer getPlayer() {
            return player;
        }
        
        public @NotNull HariantEntity getEntity() {
            return entity;
        }
        
        public double getHealth() {
            return health;
        }
        
        public double damage(double damage) {
            return this.health -= damage;
        }
        
        @Override
        public void remove() {
        }
        
        @Override
        public boolean shouldRemove() {
            // Remove ward if either the entity has died or health is negative
            return entity.isDead() || health <= 0;
        }
        
        @Override
        public void tick() {
            // Actionbar to entity
            if (entity instanceof HariantPlayer playerEntity) {
                playerEntity.actionbar(TalentQuantumWard.class, componentEntity);
            }
            
            // Fx
        }
        
        @Override
        public @NotNull Component asComponent() {
            return asComponent0(entity).append(Component.text(" %,.0f".formatted(health)));
        }
        
        private static @NotNull Component asComponent0(@NotNull HeadComponent headComponent) {
            return CASTLE_CHAR.appendSpace()
                              .append(Component.text("[", Colors.GRAY))
                              .append(headComponent.asHeadComponent())
                              .append(Component.text("]", Colors.GRAY));
        }
        
    }
    
}