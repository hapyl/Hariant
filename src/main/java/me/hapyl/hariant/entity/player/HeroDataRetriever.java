package me.hapyl.hariant.entity.player;

import me.hapyl.hariant.hero.Hero;
import me.hapyl.hariant.hero.HeroData;
import me.hapyl.hariant.hero.HeroDataSupplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

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
     * @see #touchHeroData(Hero, Class, Consumer)
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
    
    /**
     * Touches an <b><u>existing</u></b> data for the given {@link Hero} and {@link HeroData} without computing the data.
     *
     * <p>
     * This result of the given consumer will be ignored if the player doesn't have the expected data.
     * </p>
     *
     * @param hero          - The hero of the data.
     * @param heroDataClass - The target data class.
     * @param consumer      - The consumer.
     * @param <H>           - The hero type.
     * @param <D>           - The hero data type.
     */
    <H extends Hero, D extends HeroData<H>> void touchHeroData(@NotNull H hero, @NotNull Class<D> heroDataClass, @NotNull Consumer<@NotNull D> consumer);
    
    /**
     * Touches an <b><u>existing</u></b> data for the given {@link Hero} and {@link HeroData} without computing the data and returns the value
     * as instructed in the given {@link Function} wrapped in a {@link Optional}.
     *
     * @param hero          - The hero of the data.
     * @param heroDataClass - The target data class.
     * @param function      - The function.
     * @param <H>           - The hero type.
     * @param <D>           - The hero data type.
     * @param <R>           - The return value type.
     * @return an optional with the value; or an empty optional if data or value doesn't exist.
     */
    <H extends Hero, D extends HeroData<H>, R> @NotNull Optional<R> touchHeroData(@NotNull H hero, @NotNull Class<D> heroDataClass, @NotNull Function<@NotNull D, @Nullable R> function);
    
    
}
