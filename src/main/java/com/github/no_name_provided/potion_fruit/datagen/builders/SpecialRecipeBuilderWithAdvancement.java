package com.github.no_name_provided.potion_fruit.datagen.builders;

import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.SpecialRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Recipe;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Creates proper recipe advancements, so they'll show up in the recipe book.
 * "Special" recipes aren't currently supported by the patched vanilla book, so we'll need probably need a mixin to make
 * these advancements actually work. Left in for now because there's no real downside.
 */
public class SpecialRecipeBuilderWithAdvancement extends SpecialRecipeBuilder {
    private final Function<CraftingBookCategory, Recipe<?>> factory;
    private final Map<String, Criterion<?>> criteria;

    public SpecialRecipeBuilderWithAdvancement(Function<CraftingBookCategory, Recipe<?>> factory) {
        super(factory);
        this.factory = factory;
        this.criteria = new LinkedHashMap<>();
    }

    @ParametersAreNonnullByDefault
    public static SpecialRecipeBuilderWithAdvancement special(Function<CraftingBookCategory, Recipe<?>> factory) {
        return new SpecialRecipeBuilderWithAdvancement(factory);
    }

    @Override @ParametersAreNonnullByDefault
    public void save(RecipeOutput recipeOutput, ResourceLocation recipeId) {
        recipeOutput.accept(recipeId, this.factory.apply(CraftingBookCategory.MISC), buildAdvancement(recipeOutput, recipeId));
    }

    public SpecialRecipeBuilderWithAdvancement unlockedBy(String name, Criterion<?> criterion) {
        this.criteria.put(name, criterion);
        return this;
    }

    private AdvancementHolder buildAdvancement(RecipeOutput output, ResourceLocation id) {
        Advancement.Builder builder = output.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                .rewards(AdvancementRewards.Builder.recipe(id))
                .requirements(AdvancementRequirements.Strategy.OR);
        criteria.forEach(builder::addCriterion);
        return builder.build(id.withPrefix("recipes/" + "potion_infusion" + "/"));
    }
}
