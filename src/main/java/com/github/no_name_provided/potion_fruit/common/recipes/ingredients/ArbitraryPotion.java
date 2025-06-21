package com.github.no_name_provided.potion_fruit.common.recipes.ingredients;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

import static com.github.no_name_provided.potion_fruit.common.recipes.ingredients.types.Registry.ARBITRARY_POTION;

public class ArbitraryPotion implements ICustomIngredient {

    /**
     * Checks if a stack matches this ingredient.
     * The stack <b>must not</b> be modified in any way.
     *
     * @param stack the stack to test
     * @return {@code true} if the stack matches this ingredient, {@code false} otherwise
     */
    @Override
    public boolean test(ItemStack stack) {
        return stack.getItem() == Items.POTION;
    }

    @SuppressWarnings("unused")
    public static ArbitraryPotion of() {
        return new ArbitraryPotion();
    }

    /**
     * {@return the list of stacks that this ingredient accepts}
     *
     * <p>The following guidelines should be followed for good compatibility:
     * <ul>
     * <li>These stacks are generally used for display purposes, and need not be exhaustive or perfectly accurate.</li>
     * <li>An exception is ingredients that {@linkplain #isSimple() are simple},
     * for which it is important that the returned stacks correspond exactly to all the accepted {@link Item}s.</li>
     * <li>At least one stack must be returned for the ingredient not to be considered {@linkplain Ingredient#hasNoItems() accidentally empty}.</li>
     * <li>The ingredient should try to return at least one stack with each accepted {@link Item}.
     * This allows mods that inspect the ingredient to figure out which stacks it might accept.</li>
     * </ul>
     *
     * <p>Note: no caching needs to be done by the implementation, this is already handled by the ingredient itself.
     */
    @Override
    public @NotNull Stream<ItemStack> getItems() {
        return Stream.of(new ItemStack(Items.POTION));
    }

    /**
     * Returns whether this ingredient always requires {@linkplain #test direct stack testing}.
     *
     * @return {@code true} if this ingredient ignores NBT data when matching stacks, {@code false} otherwise
     * @see Ingredient#isSimple()
     */
    @Override
    public boolean isSimple() {
        return false;
    }

    /**
     * {@return the type of this ingredient}
     */
    @Override
    public @NotNull IngredientType<?> getType() {
        return ARBITRARY_POTION.get();
    }

    public static final MapCodec<ArbitraryPotion> CODEC = MapCodec.unit(ArbitraryPotion::new);
}
