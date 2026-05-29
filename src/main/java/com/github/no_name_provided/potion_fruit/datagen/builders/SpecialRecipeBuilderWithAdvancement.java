package com.github.no_name_provided.potion_fruit.datagen.builders;

import net.minecraft.advancements.*;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.SpecialRecipeBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;
import org.jspecify.annotations.NonNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Creates proper recipe advancements, so they'll show up in the recipe book.
 * "Special" recipes aren't currently supported by the patched vanilla book, so we'll need probably need a mixin to make
 * these advancements actually work. Left in for now because there's no real downside.
 */
public class SpecialRecipeBuilderWithAdvancement extends SpecialRecipeBuilder {
    private final Supplier<Recipe<?>> factory;
    private final Map<String, Criterion<?>> criteria;

    public SpecialRecipeBuilderWithAdvancement(Supplier<Recipe<?>> factory) {
        super(factory);
        this.factory = factory;
        this.criteria = new LinkedHashMap<>();
    }

    @ParametersAreNonnullByDefault
    public static SpecialRecipeBuilderWithAdvancement special(Supplier<Recipe<?>> factory) {
        
        return new SpecialRecipeBuilderWithAdvancement(factory);
    }
    
        @Override @ParametersAreNonnullByDefault
    public void save(RecipeOutput recipeOutput, ResourceKey<Recipe<?>> recipeKey) {
        recipeOutput.accept(recipeKey, this.factory.get(), buildAdvancement(recipeOutput, recipeKey.identifier()));
    }

    @ParametersAreNonnullByDefault
    public @NonNull SpecialRecipeBuilderWithAdvancement unlockedBy(String name, Criterion<?> criterion) {
        this.criteria.put(name, criterion);
        
        return this;
    }

    private AdvancementHolder buildAdvancement(RecipeOutput output, Identifier id) {
        ResourceKey<Recipe<?>> key = ResourceKey.create(Registries.RECIPE, id);
        Advancement.Builder builder = output.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(key))
                .rewards(AdvancementRewards.Builder.recipe(key))
                .requirements(AdvancementRequirements.Strategy.OR);
        criteria.forEach(builder::addCriterion);
        
        return builder.build(id.withPrefix("recipes/" + "potion_infusion" + "/"));
    }
}
