package com.github.no_name_provided.potion_apples.datagen.providers;

import com.github.no_name_provided.potion_apples.common.recipes.InfuseApple;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SpecialRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.concurrent.CompletableFuture;

import static com.github.no_name_provided.potion_apples.NNPMMPotionApples.MOD_ID;
import static net.minecraft.world.item.Items.*;

public class Recipes extends RecipeProvider {
    public Recipes(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override @ParametersAreNonnullByDefault
    protected void buildRecipes(RecipeOutput recipeOutput, HolderLookup.Provider holderLookup) {
        SpecialRecipeBuilder.special((category) -> new InfuseApple(category, Ingredient.of(APPLE)))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(MOD_ID, "infuse_apple"));
    }
}
