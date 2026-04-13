package me.hapyl.hariant.hero;

import me.hapyl.eterna.module.util.Disposable;
import me.hapyl.eterna.module.util.Ticking;
import me.hapyl.hariant.entity.player.HariantPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Represents data storage for the given {@link H} hero.
 *
 * <h1>Important!</h1>
 *
 * <p>
 * Since hero data is internally stored in a {@link Map}, there must <u>never</u> exist multiple
 * {@link HeroData} types for any given {@link Hero}.
 * </p>
 *
 * <p><b>So remember, one {@link HeroData} per one {@link H} hero!</b></p>
 *
 * @param <H> - The hero type this data belongs to.
 */
public abstract class HeroData<H extends Hero> implements Disposable, Ticking {
    
    protected final H hero;
    protected final HariantPlayer player;
    
    public HeroData(@NotNull H hero, @NotNull HariantPlayer player) {
        this.hero = hero;
        this.player = player;
    }
    
    @NotNull
    public H getHero() {
        return hero;
    }
    
    @NotNull
    public HariantPlayer getPlayer() {
        return player;
    }
    
    @Override
    public abstract void dispose();
    
    @Override
    public void tick() {
    }
    
}