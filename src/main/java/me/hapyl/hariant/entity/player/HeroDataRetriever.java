package me.hapyl.hariant.entity.player;

import me.hapyl.hariant.hero.Hero;
import me.hapyl.hariant.hero.HeroData;
import me.hapyl.hariant.hero.HeroDataSupplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface HeroDataRetriever {
    
    /**
     * Retrieves (or computes) {@link HeroData} {@link D} data for the given {@link Hero} {@link H}.
     *
     * <p>
     * This method <b>always</b> returns an exiting {@link HeroData} {@link D}, by either retrieving an existing object,
     * or applying the given supplier.
     * </p>
     *
     * <p>
     * This method is proven to have a safe type cast, but since the data is stored in a hash map, the compiler cannot
     * guarantee that the returned value is a capture of {@link D}, so is screams about the unchecked cast.
     * <p>
     * There is one exception when the compiler is correct, which is where multiply {@link HeroData} classes exist for
     * any one {@link Hero}, which is illegal behaviour and is not supported.
     * </p>
     *
     * @param hero     - The hero for which to retrieve or compute the data.
     * @param supplier - The hero data supplier for computing.
     * @param <H>      - The hero type.
     * @param <D>      - The data type.
     * @return an existing or newly computed hero data.
     */
    @NotNull
    <H extends Hero, D extends HeroData<H>> D getHeroData(@NotNull H hero, @NotNull HeroDataSupplier<H, D> supplier);
    
    /**
     * Checks whether this {@link HariantPlayer} has an existing {@link HeroData} for the given {@link Hero}.
     *
     * @param hero - The hero to check.
     * @return {@code true} if this player has existing data for the given hero; {@code false} otherwise.
     */
    <H extends Hero> boolean hasHeroData(@NotNull H hero);
    
}
