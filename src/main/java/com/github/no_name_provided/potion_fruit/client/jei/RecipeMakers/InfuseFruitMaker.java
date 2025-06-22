package com.github.no_name_provided.potion_fruit.client.jei.RecipeMakers;

import com.github.no_name_provided.potion_fruit.common.annotations.OnlyIn;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.vanilla.IVanillaRecipeFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.*;
import net.neoforged.neoforge.common.crafting.CompoundIngredient;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@OnlyIn()
public class InfuseFruitMaker {

    public static List<RecipeHolder<CraftingRecipe>> createRecipes(IJeiHelpers jeiHelpers) {

        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (null == level) {
            throw new IllegalStateException("Could not get registry, registry access is unavailable because the level is currently null.");
        }
        RegistryAccess REGISTRY_ACCESS = level.registryAccess();

        List<Ingredient> potions = new ArrayList<>();

        Registry<Potion> potionRegistry = REGISTRY_ACCESS.registryOrThrow(BuiltInRegistries.POTION.key());
        potionRegistry.holders()
                .filter(
                        // Getting the potion name is wierd. The second parameter is prepended, and the suffix is parsed from a path.
                        // Filtering out any paths that can't be parsed (and any potions without effects) seems to remove uncraftables.
                        holder -> !holder.value().getEffects().isEmpty() &&
                                !Potion.getName(Optional.of(holder), "").isEmpty() &&
                                !Potion.getName(Optional.of(holder), "").startsWith("empty")
                ).forEach(
                        holder -> potions.add(
                                DataComponentIngredient.of(false, PotionContents.createItemStack(Items.POTION, holder))
                        )
                );
        

        RecipeManager manager = level.getRecipeManager();
        List<RecipeHolder<?>> iRecipes = manager.getRecipes().stream().filter(recipe -> recipe.id().getPath().startsWith("potion_infusion/")).toList();

        return generateRecipesForFruit(iRecipes, level, potions, jeiHelpers);

    }

    static List<RecipeHolder<CraftingRecipe>> generateRecipesForFruit(List<RecipeHolder<?>> iRecipes, ClientLevel level, List<Ingredient> potions, IJeiHelpers jeiHelpers) {
        IVanillaRecipeFactory vanillaRecipeFactory = jeiHelpers.getVanillaRecipeFactory();

        RegistryAccess REGISTRY_ACCESS = level.registryAccess();
        return iRecipes.stream().map(
                holder -> {
                    ItemStack fruit = holder.value().getResultItem(REGISTRY_ACCESS).copy();
                    ItemStack originalFruit = fruit.copy();
                    originalFruit.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, false);

                    CraftingRecipe recipe = vanillaRecipeFactory.createShapedRecipeBuilder(
                            CraftingBookCategory.MISC, List.of(fruit)
                            ).group("infuse_fruit")
                            .pattern("P")
                            .pattern("F")
                            .define('P', new CompoundIngredient(potions).toVanilla())
                            .define('F', Ingredient.of(originalFruit))
                            .build();
                    return new RecipeHolder<>(
                            ResourceLocation.fromNamespaceAndPath(
                                    holder.id().getNamespace(),
                                    holder.id().getPath().replace(':', '_')
                            ),
                            recipe
                    );
                }
        ).toList();
    }
}
