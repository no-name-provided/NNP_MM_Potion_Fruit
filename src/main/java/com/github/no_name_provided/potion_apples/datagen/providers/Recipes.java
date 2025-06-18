package com.github.no_name_provided.potion_apples.datagen.providers;

import com.github.no_name_provided.potion_apples.common.recipes.InfuseFruit;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SpecialRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.concurrent.CompletableFuture;

import static com.github.no_name_provided.potion_apples.NNPMMPotionFruit.MOD_ID;
import static net.minecraft.world.item.Items.*;

public class Recipes extends RecipeProvider {

    private RecipeOutput recipeOutput;

    public Recipes(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override @ParametersAreNonnullByDefault
    protected void buildRecipes(RecipeOutput recipeOutput, HolderLookup.Provider holderLookup) {
        this.recipeOutput = recipeOutput;

        infuseFruit(APPLE, "infuse_apple");
        infuseFruit(MELON_SLICE, "infuse_melon");
        infuseFruit(BEETROOT, "infuse_beetroot");
        infuseFruit(CHORUS_FRUIT, "infuse_chorus_fruit");
        infuseFruit(PUMPKIN_PIE, "infuse_pumpkin_pie");
        infuseFruit(GLOW_BERRIES, "infuse_glow_berries");
        infuseFruit(SWEET_BERRIES, "infuse_sweet_berries");
        infuseFruit(CARROT, "infuse_carrot");

    }

    void infuseFruit(Item fruit, String id) {
        SpecialRecipeBuilder.special((category) -> new InfuseFruit(category, Ingredient.of(fruit)))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(MOD_ID, id));
    }
}
