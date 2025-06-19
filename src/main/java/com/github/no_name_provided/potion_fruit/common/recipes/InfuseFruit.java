package com.github.no_name_provided.potion_fruit.common.recipes;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

public class InfuseFruit extends CustomRecipe {

    private final Ingredient fruit;

    public InfuseFruit(CraftingBookCategory category, Ingredient fruit) {
        super(category);
        this.fruit = fruit;
    }

    /**
     * Does the recipe input match our recipe? Should we handle this craft?
     * */
    @Override @ParametersAreNonnullByDefault
    public boolean matches(CraftingInput cInput, Level level) {

        if (cInput.size() != 2) {

            return false;
        }

        ArrayList<ItemStack> orderedInputs = new ArrayList<>(List.of(
                ItemStack.EMPTY,
                ItemStack.EMPTY
        ));

        for (ItemStack input : cInput.items()) {
            if (input.getItem() == fruit.getItems()[0].getItem()) {
                orderedInputs.set(0, input);
            } else if (input.getItem() instanceof PotionItem) {
                orderedInputs.set(1, input);
            }
        }

        return !(orderedInputs.getFirst().isEmpty() || orderedInputs.getLast().isEmpty());
    }

    /**
     * Used by recipe book/JEI. Output shouldn't be modified.
     */
    @Override @ParametersAreNonnullByDefault
    public @NotNull ItemStack getResultItem(HolderLookup.Provider registries) {

        return fruit.getItems()[0];
    }

    /**
     * Return actual output item - which may be a copy of the result.
     */
    @Override @ParametersAreNonnullByDefault
    public @NotNull ItemStack assemble(CraftingInput cInput, HolderLookup.Provider registries) {

        ItemStack potion = cInput.items().stream().filter(item -> item.getItem() != fruit.getItems()[0].getItem())
                .toList().getFirst().copy();
        ItemStack infusedFruit = cInput.items().stream().filter(item -> item.getItem() == fruit.getItems()[0].getItem())
                .toList().getFirst().copyWithCount(1);

        ArrayList<MobEffectInstance> effects = new ArrayList<>();
        potion.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY)
                .getAllEffects()
                .forEach(effects::add);
        infusedFruit.getOrDefault(DataComponents.FOOD, new FoodProperties.Builder().build())
                .effects()
                .forEach((pEffect) -> effects.add(pEffect.effect()));

        // Use a set here to prevent duplicates.
        HashSet<Component> enchantDescriptions = new HashSet<>();
        effects.forEach(effect -> enchantDescriptions.add(Component.translatable(effect.getDescriptionId())));

        FoodProperties oldProps = infusedFruit.getOrDefault(DataComponents.FOOD, new FoodProperties(
                0,
                0,
                true,
                1,
                Optional.empty(),
                List.of()
                )
        );

        FoodProperties.Builder newPropsBuilder = new FoodProperties.Builder()
                .nutrition(oldProps.nutrition())
                .saturationModifier(oldProps.saturation())
                .alwaysEdible()
                .fast();

        // It's this or an unchecked assignment - the class it's looking for is an abstract generic.
        //noinspection deprecation
        effects.forEach(
                effect -> newPropsBuilder.effect(effect, 1.0f)
        );

        infusedFruit.set(DataComponents.FOOD, newPropsBuilder.build());
        infusedFruit.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
        infusedFruit.set(DataComponents.LORE, new ItemLore(enchantDescriptions.stream().toList()));

        return infusedFruit;
    }

    /**
     * Used to determine if this recipe can fit in a grid of the given width/height
     */
    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height > 1;
    }

    /**
     * The codec set to use for saving, reading, & streaming.
     * */
    @Override public
    @NotNull RecipeSerializer<?> getSerializer() {
        return Registry.INFUSE_FRUIT_SERIALIZER.get();
    }

    /**
     * Not necessary, but makes the serializer less finicky.
     * */
    public Ingredient getFruit() {
        return fruit;
    }

}
