package me.hapyl.hariant.hero.alchemist;

import com.google.common.collect.Sets;
import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.CollectionUtils;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.HariantRandom;
import me.hapyl.hariant.entity.damage.DamageSource;
import me.hapyl.hariant.entity.damage.DamageSourceIdentity;
import me.hapyl.hariant.entity.damage.DeathMessage;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.event.HariantAttackEvent;
import me.hapyl.hariant.hero.HeroRegistry;
import me.hapyl.hariant.talent.TalentContext;
import me.hapyl.hariant.talent.field.DisplayField;
import me.hapyl.hariant.talent.target.TalentTarget;
import me.hapyl.hariant.talent.ultimate.TalentUltimate;
import me.hapyl.hariant.talent.ultimate.UltimateResourceType;
import me.hapyl.hariant.task.HariantTask;
import me.hapyl.hariant.task.HariantTickingTask;
import me.hapyl.hariant.task.Scheduler;
import me.hapyl.hariant.task.executor.Executable;
import me.hapyl.hariant.task.executor.ExecutorService;
import me.hapyl.hariant.util.Definition;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

import static org.bukkit.Sound.ENTITY_BLAZE_HURT;
import static org.bukkit.Sound.ENTITY_EVOKER_PREPARE_ATTACK;

public final class TalentAbyssalCurse extends TalentUltimate implements Listener {
    
    private final @DisplayField Decimal castingDuration = Decimal.ofSeconds(1.5f);
    private final @DisplayField Decimal curseTransferCooldown = Decimal.ofSeconds(0.25f);
    private final @DisplayField Decimal curseDamage = Decimal.ofPercentage(150);
    
    private final Set<AbyssalCurse> globalCurses = Sets.newHashSet();
    
    private final Style curseStyle = Style.style(Colors.ABYSSAL_CURSE, TextDecoration.BOLD);
    private final Style curseStyleObfuscated = Style.style(Colors.ABYSSAL_CURSE, TextDecoration.BOLD, TextDecoration.OBFUSCATED);
    
    private final DeathMessage deathMessage = DeathMessage.create("{player} was consumed by the Abyss [created by {killer}]");
    
    private final int fallingBlocksCount = 50;
    
    private final BlockData[] fallingBlocksData = {
            Material.PURPLE_STAINED_GLASS.createBlockData(),
            Material.MAGENTA_STAINED_GLASS.createBlockData(),
            Material.PURPLE_WOOL.createBlockData(),
            Material.MAGENTA_WOOL.createBlockData(),
            Material.PURPLE_TERRACOTTA.createBlockData(),
            Material.MAGENTA_TERRACOTTA.createBlockData()
    };
    
    public TalentAbyssalCurse(@NotNull Key key) {
        super(key, Component.text("Abyssal Curse"), Icon.ofMaterial(Material.FERMENTED_SPIDER_EYE), UltimateResourceType.ENERGY, 60);
        
        this.setDurationSeconds(10);
        this.setCooldownSeconds(30);
        
        this.setDescription(
                Component.empty()
                         .append(Component.text("Apply "))
                         .append(Definition.ABYSSAL_CURSE)
                         .append(Component.text(" to yourself that gradually becomes "))
                         .append(obfuscate("unstable", 4, curseStyle, curseStyleObfuscated))
                         .append(Component.text("."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("Hit a "))
                         .append(Component.text("player", Colors.WHITE, TextDecoration.UNDERLINED))
                         .append(Component.text(" to transfer the curse to that player."))
                         .appendNewline()
                         .append(Component.text("Other players can also transfer the curse.", Colors.DARK_GRAY))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("After te curse becomes "))
                         .append(Component.text("unstable", Colors.DARK_RED))
                         .append(Component.text(", it explodes, dealing "))
                         .append(ElementType.AETHER.asComponentDamage())
                         .append(Component.text(" equal to "))
                         .append(curseDamage)
                         .append(Component.text(" of the bearer's "))
                         .append(AttributeType.MAX_HEALTH)
                         .append(Component.text("."))
        );
    }
    
    @NotNull
    @Override
    public Executable execute(@NotNull HariantPlayer player, @NotNull TalentContext context, double consumedResource) {
        return new ExecutorService()
                .then(Executable.execute(() -> {
                    // Fx
                    player.spawnWorldParticle(player.getMidpointLocation(), Particle.ENCHANT, 100, 0, 0, 0, 1.0f);
                    player.playWorldSound(ENTITY_EVOKER_PREPARE_ATTACK, 2.0f);
                }))
                .then(Executable.later(() -> {
                    final int curseDuration = HeroRegistry.ALCHEMIST.getCurseDuration(player, this.getDuration());
                    
                    // Store in the global hash set for faster lookup
                    globalCurses.add(new AbyssalCurse(player, curseDuration));
                }, castingDuration.intValue()));
    }
    
    @NotNull
    @Override
    public TalentTarget target(@NotNull HariantPlayer player) {
        return TalentTarget.none();
    }
    
    @EventHandler(ignoreCancelled = true)
    public void handleHariantAttackEvent(HariantAttackEvent ev) {
        final HariantEntity entity = ev.getEntity();
        final HariantEntity attacker = ev.getAttacker();
        
        if (!(entity instanceof HariantPlayer playerEntity) || !(attacker instanceof HariantPlayer)) {
            return;
        }
        
        // Check whether there is a curse whose bearer is the entity
        globalCurses.stream()
                    .filter(_curse -> _curse.bearer.equals(attacker))
                    .findAny()
                    .ifPresent(curse -> curse.transfer(playerEntity));
        
    }
    
    @NotNull
    public static Component obfuscate(@NotNull String string, int numberOfCharsToObfuscate, @NotNull Style style, @NotNull Style styleObfuscated) {
        final int length = string.length();
        
        if (length < numberOfCharsToObfuscate) {
            throw new IllegalArgumentException("String length cannot be lower than numbers of chars to obfuscate!");
        }
        
        final Set<Integer> obfuscatedIndexes = Sets.newHashSet();
        
        while (obfuscatedIndexes.size() < numberOfCharsToObfuscate) {
            obfuscatedIndexes.add(Hariant.getRandom().nextInt(length));
        }
        
        final TextComponent.Builder builder = Component.text();
        
        for (int i = 0; i < length; i++) {
            final char ch = string.charAt(i);
            
            builder.append(Component.text(ch, obfuscatedIndexes.contains(i) ? styleObfuscated : style));
        }
        
        return builder.build();
    }
    
    private class AbyssalCurse extends HariantTickingTask {
        
        private final HariantPlayer player;
        private final int duration;
        
        private @NotNull HariantPlayer bearer;
        private @Nullable HariantEntity lastTransferer;
        
        private long lastTransfer;
        
        AbyssalCurse(@NotNull HariantPlayer player, int duration) {
            super(Scheduler.ofTimer());
            
            this.player = player;
            this.bearer = player;
            this.duration = duration;
        }
        
        public void transfer(@NotNull HariantPlayer player) {
            if (bearer.equals(player)) {
                player.messageError(Component.text("Cannot transfer curse to yourself!"));
                return;
            }
            
            if (bearer.isTeammate(player)) {
                player.messageError(Component.text("Cannot transfer curse to a teammate!"));
                return;
            }
            
            // Transfer cooldown check
            final long currentTimeMillis = System.currentTimeMillis();
            final long transferCooldownInMillis = curseTransferCooldown.intValue() * 50L;
            final long timePassedSinceLastTransfer = currentTimeMillis - lastTransfer;
            
            if (timePassedSinceLastTransfer < transferCooldownInMillis) {
                player.messageError(
                        Component.empty()
                                 .append(Component.text("Cannot transfer curse for another "))
                                 .append(Component.text(Tick.format((int) ((transferCooldownInMillis - timePassedSinceLastTransfer) / 50))))
                                 .append(Component.text("!"))
                );
                return;
            }
            
            final HariantPlayer previousBearer = bearer;
            
            this.bearer = player;
            this.lastTransferer = previousBearer;
            this.lastTransfer = currentTimeMillis;
            
            // Fx
            previousBearer.sendMessage(
                    Component.empty()
                             .append(Definition.ABYSSAL_CURSE.getPrefixStyled())
                             .appendSpace()
                             .append(Component.text("You transferred the curse to ", Colors.LIGHT_PURPLE))
                             .append(player.getName().color(Colors.DARK_PURPLE))
                             .append(Component.text("!", Colors.LIGHT_PURPLE))
            );
            
            previousBearer.playSound(Sound.ENTITY_EVOKER_CAST_SPELL, 0.75f);
            previousBearer.playSound(Sound.ENTITY_EVOKER_PREPARE_SUMMON, 0.75f);
            
            // Fx for new bearer
            player.sendMessage(
                    Component.empty()
                             .append(Definition.ABYSSAL_CORROSION.getPrefixStyled())
                             .appendSpace()
                             .append(Component.text("YOU HAVE BEEN CURSED!", Colors.ABYSSAL_CURSE, TextDecoration.BOLD))
            );
            
            player.sendMessage(
                    Component.empty()
                             .append(Definition.ABYSSAL_CORROSION.getPrefixStyled())
                             .appendSpace()
                             .append(Component.text("Hit another player to transfer the curse or ", Colors.LIGHT_PURPLE))
                             .append(Component.text("DIE", Colors.ERROR, TextDecoration.BOLD))
                             .append(Component.text("!", Colors.LIGHT_PURPLE))
            );
            
            player.playSound(Sound.ENTITY_GHAST_HURT, 1.5f);
            player.playSound(Sound.ENTITY_GHAST_HURT, 1.75f);
            player.playSound(Sound.ENTITY_GHAST_HURT, 2.0f);
        }
        
        @Override
        public void run(int tick) {
            // Boom
            if (tick >= duration) {
                this.boom();
                this.cancel();
                return;
            }
            
            // If current bearer is dead, extinguish
            if (bearer.isDead()) {
                final Location location = bearer.getLocation();
                
                bearer.playWorldSound(location, Sound.BLOCK_FIRE_EXTINGUISH, 0.0f);
                bearer.spawnWorldParticle(location, Particle.SMOKE, 20, 0.2, 0.2, 0.2, 0.05f);
                
                this.cancel();
                return;
            }
            
            // Fx
            final Location location = bearer.getMidpointLocation();
            final double unstablePercent = (double) tick / duration;
            
            bearer.spawnWorldParticle(location, Particle.WITCH, 5, 0.2d, 0.33d, 0.2d, 0.01f);
            
            // Sfx
            if (modulo(2)) {
                bearer.playWorldSound(location, Sound.BLOCK_LAVA_POP, (float) (0.5f + (1.5f * unstablePercent)));
            }
            
            // Display
            if (modulo(5)) {
                bearer.sendSubtitle(
                        obfuscate("ʏᴏᴜ ᴀʀᴇ ᴄᴜʀꜱᴇᴅ", 3, curseStyle, curseStyleObfuscated),
                        0, 10, 5
                );
            }
        }
        
        public void boom() {
            bearer.damage(
                    DamageSource.builder(DamageSourceIdentity.create(TalentAbyssalCurse.this, deathMessage), bearer.getMaxHealth() * curseDamage.doubleValue())
                                // If the last bearer is the one who applied the curse, source the kill to whoever last transferred the curse
                                .source(bearer.equals(player) ? lastTransferer : player)
                                .elementType(ElementType.AETHER)
                                .build()
            );
            
            // Fx
            final Location location = bearer.getLocation();
            
            bearer.playWorldSound(location, Sound.ENTITY_BLAZE_DEATH, 0.75f);
            bearer.playWorldSound(location, Sound.ENTITY_WARDEN_DEATH, 0.0f);
            
            bearer.spawnWorldParticle(location, Particle.WITCH, 50, 1, 1, 1, 1f);
            bearer.spawnWorldParticle(location, Particle.SCULK_SOUL, 1, 0f);
            
            final World world = location.getWorld();
            final Set<FallingBlock> fallingBlocks = Sets.newHashSet();
            
            for (int i = 0; i < fallingBlocksCount; i++) {
                final FallingBlock fallingBlock = world.spawn(location, FallingBlock.class, self -> {
                    self.setGravity(false);
                    self.setHurtEntities(false);
                    self.setDropItem(false);
                    self.setCancelDrop(true);
                    
                    self.setBlockData(CollectionUtils.randomElementOrFirst(fallingBlocksData));
                });
                
                // Throw randomly
                final HariantRandom random = player.getRandom();
                
                fallingBlock.setVelocity(new Vector(random.nextSignedDouble(0.5), random.nextDouble(-0.3, 0.3), random.nextSignedDouble(0.5)));
                fallingBlocks.add(fallingBlock);
            }
            
            new HariantTickingTask(Scheduler.ofTimer()) {
                @Override
                public void run(int tick) {
                    if (tick > 4) {
                        this.cancel();
                    }
                    else if (tick % 2 == 0) {
                        final float pitch = 0.75f + (1.5f * tick / 4);
                        
                        bearer.playWorldSound(ENTITY_BLAZE_HURT, pitch);
                    }
                }
            };
            
            // Cleanup
            new HariantTask(Scheduler.ofDelayed(40)) {
                @Override
                public void run() {
                    this.removeFallingBlocks();
                }
                
                @Override
                public void onCancel() {
                    this.removeFallingBlocks();
                }
                
                private void removeFallingBlocks() {
                    fallingBlocks.forEach(block -> {
                        final Location location = block.getLocation();
                        
                        bearer.spawnWorldParticle(location, Particle.WITCH, 5, 0.5, 0.5, 0.5, 0.1f);
                        bearer.spawnWorldParticle(location, Particle.POOF, 5, 0.5, 0.5, 0.5, 0.1f);
                        
                        block.remove();
                    });
                    
                    fallingBlocks.clear();
                }
            };
        }
        
        @Override
        public void onCancel() {
            globalCurses.remove(this);
        }
    }
    
}
