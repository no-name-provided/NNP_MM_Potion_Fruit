package com.github.no_name_provided.potion_fruit.datagen.providers;

import com.github.no_name_provided.potion_fruit.common.recipes.InfuseFruit;
import com.github.no_name_provided.potion_fruit.datagen.builders.SpecialRecipeBuilderWithAdvancement;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import org.jspecify.annotations.NonNull;

import javax.annotation.ParametersAreNonnullByDefault;

import java.util.concurrent.CompletableFuture;

import static com.github.no_name_provided.potion_fruit.NNPMMPotionFruit.MOD_ID;
import static net.minecraft.world.item.Items.*;

public class Recipes extends RecipeProvider {
    
    private final RecipeOutput recipeOutput;
    
    public Recipes(RecipeOutput output, HolderLookup.Provider registries) {
        super(registries, output);
        this.recipeOutput = output;
    }
    
    @Override @ParametersAreNonnullByDefault
    protected void buildRecipes() {
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
        SpecialRecipeBuilderWithAdvancement.special(() -> new InfuseFruit(Ingredient.of(fruit)))
                .unlockedBy("has_" + fruit.toString().replace(':', '_'), has(fruit))
                .save(
                        recipeOutput,
                        ResourceKey.create(
                                Registries.RECIPE,
                                Identifier.fromNamespaceAndPath(
                                        MOD_ID,
                                        "potion_infusion/" + fruit.toString().replace(':', '_')
                                )
                        )
                );
    }
    
    public static class Runner extends RecipeProvider.Runner {
        public Runner(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
            super(packOutput, registries);
        }
        
        @Override @ParametersAreNonnullByDefault
        protected @NonNull RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
            return new Recipes(output, registries);
        }
        
        @Override
        public @NonNull String getName() {
            return "Potion Fruit Recipes";
        }
    }
}
