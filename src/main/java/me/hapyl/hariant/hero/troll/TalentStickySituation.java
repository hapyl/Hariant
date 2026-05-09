package me.hapyl.hariant.hero.troll;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.game.GameInstance;
import me.hapyl.hariant.object.HariantObject;
import me.hapyl.hariant.talent.TalentContext;
import me.hapyl.hariant.talent.target.TalentTarget;
import me.hapyl.hariant.talent.ultimate.TalentUltimate;
import me.hapyl.hariant.talent.ultimate.TalentUltimateResource;
import me.hapyl.hariant.task.executor.Executable;
import me.hapyl.hariant.util.Icon;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public class TalentStickySituation extends TalentUltimate implements Listener {
    
    public TalentStickySituation(@NotNull Key key) {
        super(key, Component.text("Sticky Situation"), Icon.ofMaterial(Material.COBWEB), TalentUltimateResource.ENERGY, 40);
    }
    
    @Override
    public @NotNull Executable execute(@NotNull HariantPlayer player, @NotNull TalentContext context, double consumedResource) {
        final GameInstance gameInstance = context.retrieve(GameInstance.class);
        
        return Executable.execute(() -> {
            gameInstance.getObjectManager().createObject(new StickySituation(player));
        });
    }
    
    @Override
    public @NotNull TalentTarget target(@NotNull HariantPlayer player) {
        return TalentTarget.requireGameInstance();
    }
    
    // TODO @Apr 28, 2026 (xanyjl) -> interface -> class
    public static class StickySituation implements HariantObject {
        
        private final HariantPlayer player;
        
        StickySituation(@NotNull HariantPlayer player) {
            this.player = player;
        }
        
        @Override
        public void tick() {
        }
        
        @Override
        public void remove() {
        }
    }
    
}
