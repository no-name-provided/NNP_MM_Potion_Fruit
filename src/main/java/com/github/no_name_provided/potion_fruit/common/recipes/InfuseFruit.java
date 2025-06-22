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

import static net.minecraft.SharedConstants.TICKS_PER_SECOND;

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

        ItemStack potion = cInput.items().stream().filter(item -> item.getItem() != fruit.getItems()[0].getItem())
                .toList().getFirst().copyWithCount(1);
        ItemStack infusedFruit = cInput.items().stream().filter(item -> item.getItem() == fruit.getItems()[0].getItem())
                .toList().getFirst().copyWithCount(1);

        PotionContents contents = potion.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
        Effects effects = new Effects();
        contents.getAllEffects()
                .forEach(effects::add
                );
        infusedFruit.getOrDefault(DataComponents.FOOD, new FoodProperties.Builder().build())
                .effects()
                .forEach(pEffect ->
                        effects.add(pEffect.effect())
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
        effects.get().forEach(
                effect -> newPropsBuilder.effect(effect, 1.0f)
        );

        infusedFruit.set(DataComponents.FOOD, newPropsBuilder.build());
        if (Config.addGlint) {
            infusedFruit.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
        }
        if (Config.addLore) {
            List<Component> lore = new ArrayList<>();
            // In vanilla, ticks per second appears to target a hardcoded value in an interface. This happens to be
            // the same as the shared TICKS_PER_SECOND constant, so I'm using that. It does not seem to reference
            // the actual tick rate, which can be changed from this default. Doing this without a level reference would
            // require siloed code.
            PotionContents.addPotionTooltip(effects.get(), lore::add, 1.0f, TICKS_PER_SECOND);
            infusedFruit.set(DataComponents.LORE, new ItemLore(lore));
        }

        return infusedFruit;
    }

    /**
     * This class exists to wrap the #add method in ArrayList and filter out redundant effects.
     */
    private static class Effects {
        private final ArrayList<MobEffectInstance> effects = new ArrayList<>();

        /**
         * Checks for duplicates and ensures only the dominant effect is added.
         * Makes no difference mechanically, but filters bad information out of tooltips.
         */
        public void add(MobEffectInstance newEffect) {
            // It's either this or dealing with a possible null value in the tagkey. That feels less clean.
            @SuppressWarnings("deprecation")
            List<MobEffectInstance> oldEffects = effects.stream().filter(
                    effect -> effect.getEffect().is(newEffect.getEffect())
            ).toList();

            if (oldEffects.isEmpty()) {
                effects.add(newEffect);
            } else {
                // There should never be more than one effect of the same type...
                MobEffectInstance oldEffect = oldEffects.getFirst();

                if (oldEffect.getAmplifier() < newEffect.getAmplifier()) {
                    effects.replaceAll((effect) -> effect.equals(oldEffect) ? newEffect : effect);
                } else if (oldEffect.getAmplifier() == newEffect.getAmplifier() && oldEffect.getDuration() < newEffect.getDuration()) {
                    effects.replaceAll((effect) -> effect.equals(oldEffect) ? newEffect : effect);
                }
            }
        }

        public ArrayList<MobEffectInstance> get() {
            return effects;
        }
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
