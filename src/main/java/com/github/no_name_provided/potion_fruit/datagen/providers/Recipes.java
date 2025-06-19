package com.github.no_name_provided.potion_fruit.datagen.providers;

import com.github.no_name_provided.potion_fruit.common.recipes.InfuseFruit;
import com.github.no_name_provided.potion_fruit.datagen.builders.SpecialRecipeBuilderWithAdvancement;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.concurrent.CompletableFuture;

import static com.github.no_name_provided.potion_fruit.NNPMMPotionFruit.MOD_ID;
import static net.minecraft.world.item.Items.*;

public class Recipes extends RecipeProvider {

    private RecipeOutput recipeOutput;

    public Recipes(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override @ParametersAreNonnullByDefault
    protected void buildRecipes(RecipeOutput recipeOutput, HolderLookup.Provider holderLookup) {
        this.recipeOutput = recipeOutput;

        infuseFruit(APPLE);
        infuseFruit(MELON_SLICE);
        infuseFruit(BEETROOT);
        infuseFruit(CHORUS_FRUIT);
        infuseFruit(PUMPKIN_PIE);
        infuseFruit(GLOW_BERRIES);
        infuseFruit(SWEET_BERRIES);
        infuseFruit(CARROT);
    }

    void infuseFruit(Item fruit) {
        SpecialRecipeBuilderWithAdvancement.special((category) -> new InfuseFruit(category, Ingredient.of(fruit)))
                .unlockedBy("has_" + fruit.toString().replace(':','_'), has(fruit))
                .save(
                        recipeOutput,
                        ResourceLocation.fromNamespaceAndPath(
                                MOD_ID,
                                "potion_infusion/" + fruit.toString().replace(':','_')
                        )
                );
    }
}
