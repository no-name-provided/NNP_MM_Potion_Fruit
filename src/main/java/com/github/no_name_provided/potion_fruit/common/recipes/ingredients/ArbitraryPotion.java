package com.github.no_name_provided.potion_fruit.common.recipes.ingredients;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.neoforged.neoforge.common.crafting.CustomDisplayIngredient;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

import static com.github.no_name_provided.potion_fruit.common.recipes.ingredients.types.Registry.ARBITRARY_POTION;

/**An attempt to make JEI accept any potion as an ingredient. Unfortunately, it still insists on matching
 * something (likely a registry lookup of potion contents). For this reason, this ingredient is unused and will likely
 * be deprecated.*/
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
    
    /**
     * {@return the list of items that this ingredient accepts}
     *
     * <p>The following guidelines should be followed for good compatibility:
     * <ul>
     * <li>At least one item must be returned for the ingredient not to be considered empty. Empty ingredients invalidate the entire recipe.</li>
     * <li>The ingredient should return all {@link Item}s it might possible accept.
     * This allows mods that inspect the ingredient to figure out which stacks it might accept.</li>
     * <li>Returned items might not always be accepted by the ingredient, as an ingredient might still perform additional NBT-dependent tests.</li>
     * <li>An exception is ingredients that {@linkplain #isSimple() are simple},
     * for which {@link #test testing a stack} is equivalent to testing if the item is in the returned list.</li>
     * </ul>
     *
     * <p>Note: no caching needs to be done by the implementation, this is already handled by the ingredient itself.
     */
    @Override
    public Stream<Holder<Item>> items() {
        
        return Stream.of(Items.POTION.builtInRegistryHolder());
    }
    
    @SuppressWarnings("unused")
    public static ArbitraryPotion of() {
        
        return new ArbitraryPotion();
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
    
    /**
     * Returns the display for this ingredient.
     *
     * <p>The display is synced to the client, and is also used to retrieve the {@link ItemStack}s that are shown to
     * the
     * client.
     *
     * @implNote The default implementation just constructs a list of stacks from {@link #items()}. This is generally
     * suitable for {@link #isSimple() simple} ingredients. Non-simple ingredients can either override this method to
     * provide a more customized display, or let data pack writers use {@link CustomDisplayIngredient} to override the
     * display of an ingredient.
     */
    @Override
    public SlotDisplay display() {
        
        return ICustomIngredient.super.display();
    }
    
    /**
     * {@return a new {@link Ingredient} behaving as defined by this custom ingredient}
     */
    @Override
    public Ingredient toVanilla() {
        
        return ICustomIngredient.super.toVanilla();
    }
    
    public static final MapCodec<ArbitraryPotion> CODEC = MapCodec.unit(ArbitraryPotion::new);
}
