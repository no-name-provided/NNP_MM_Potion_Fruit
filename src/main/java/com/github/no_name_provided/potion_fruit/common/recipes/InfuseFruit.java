package com.github.no_name_provided.potion_fruit.common.recipes;

import com.github.no_name_provided.potion_fruit.Config;
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

import java.util.*;

public class InfuseFruit extends CustomRecipe {

    private final Ingredient fruit;

    public InfuseFruit(CraftingBookCategory category, Ingredient fruit) {
        super(category);
        this.fruit = fruit;
    }

    /**
     * Does the recipe input match our recipe? Should we handle this craft?
     */
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
        ItemStack result = fruit.getItems()[0].copy();
        if (Config.addGlint) {
            result.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
        }
        if (Config.addLore) {
            result.set(DataComponents.LORE,
                    new ItemLore(List.of(
                            Component.translatable("craft_result.lore.nnp_mm_potion_fruit.infuse_fruit")
                    )));
        }

        return result;
    }

    /**
     * Return actual output item - which may be a copy of the result.
     */
    @Override @ParametersAreNonnullByDefault
    public @NotNull ItemStack assemble(CraftingInput cInput, HolderLookup.Provider registries) {

        Map<MobEffectInstance, Integer> colorMap = new HashMap<>();

        ItemStack potion = cInput.items().stream().filter(item -> item.getItem() != fruit.getItems()[0].getItem())
                .toList().getFirst().copy();
        ItemStack infusedFruit = cInput.items().stream().filter(item -> item.getItem() == fruit.getItems()[0].getItem())
                .toList().getFirst().copyWithCount(1);

        PotionContents contents = potion.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
        ArrayList<MobEffectInstance> effects = new ArrayList<>();
        contents.getAllEffects()
                .forEach(effect ->
                        {
                            effects.add(effect);
                            colorMap.putIfAbsent(effect, contents.getColor());
                        }
                );
        infusedFruit.getOrDefault(DataComponents.FOOD, new FoodProperties.Builder().build())
                .effects()
                .forEach(pEffect ->
                        {
                            effects.add(pEffect.effect());
                            colorMap.putIfAbsent(pEffect.effect(), contents.getColor());
                        }
                );

        // Use a set here to prevent duplicates.
        HashSet<Component> enchantDescriptions = new HashSet<>();
        effects.forEach(
                effect -> enchantDescriptions
                        .add(Component.translatable(
                                effect.getDescriptionId()).withColor(colorMap.getOrDefault(effect, 0))
                        )
        );

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
        if (Config.addGlint) {
            infusedFruit.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
        }
        if (Config.addLore) {
            infusedFruit.set(DataComponents.LORE, new ItemLore(enchantDescriptions.stream().toList()));
        }

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
     */
    @Override public
    @NotNull RecipeSerializer<?> getSerializer() {
        return Registry.INFUSE_FRUIT_SERIALIZER.get();
    }

    /**
     * Not necessary, but makes the serializer less finicky.
     */
    public Ingredient getFruit() {
        return fruit;
    }

}
