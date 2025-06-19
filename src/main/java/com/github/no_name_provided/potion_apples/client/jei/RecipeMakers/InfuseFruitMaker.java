package com.github.no_name_provided.potion_apples.client.jei.RecipeMakers;

import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.vanilla.IVanillaRecipeFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;

import java.util.List;
import java.util.stream.Stream;

import static com.github.no_name_provided.potion_apples.NNPMMPotionFruit.MOD_ID;

public class InfuseFruitMaker {
    public static List<RecipeHolder<CraftingRecipe>> createRecipes(IJeiHelpers jeiHelpers) {
        IVanillaRecipeFactory vanillaRecipeFactory = jeiHelpers.getVanillaRecipeFactory();

        String group = MOD_ID + ".infuse.fruit";
        ItemStack potionStack = new ItemStack(Items.POTION);
        Ingredient potionIngredient = Ingredient.of(potionStack);

        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (null == level) {
            throw new IllegalStateException("Could not get registry, registry access is unavailable because the level is currently null.");
        }
        RegistryAccess REGISTRY_ACCESS = level.registryAccess();
        Registry<Item> itemRegistry = REGISTRY_ACCESS.registryOrThrow(BuiltInRegistries.ITEM.key());

        Registry<Potion> potionRegistry = REGISTRY_ACCESS.registryOrThrow(BuiltInRegistries.POTION.key());

        RecipeManager manager = level.getRecipeManager();

        List<RecipeHolder<?>> iRecipes = manager.getRecipes().stream().filter(recipe -> recipe.id().getPath().startsWith("potion_infusion/")).toList();

        return potionRegistry.stream().map( potion -> generateRecipesForFruit(iRecipes, level, potion, jeiHelpers)
        ).flatMap(List::stream).toList();


    }

    static List<RecipeHolder<CraftingRecipe>> generateRecipesForFruit(List<RecipeHolder<?>> iRecipes, ClientLevel level, Potion potion, IJeiHelpers jeiHelpers) {
        IVanillaRecipeFactory vanillaRecipeFactory = jeiHelpers.getVanillaRecipeFactory();
        ItemStack potionStack = PotionContents.createItemStack(Items.POTION, new Holder.Direct<>(potion));

        RegistryAccess REGISTRY_ACCESS = level.registryAccess();
        return iRecipes.stream().map(
                holder -> {
                    ItemStack fruit = holder.value().getResultItem(REGISTRY_ACCESS);
                    CraftingRecipe recipe = vanillaRecipeFactory.createShapedRecipeBuilder(CraftingBookCategory.MISC, List.of(fruit))
                            .group("infuse_fruit")
                            .pattern("P")
                            .pattern("F")
                            .define('P', DataComponentIngredient.of(false, potionStack))
                            .define('F', Ingredient.of(fruit))
                            .build();
                    return new RecipeHolder<>(
                            ResourceLocation.fromNamespaceAndPath(
                                    holder.id().getNamespace(),
                                    holder.id().getPath()
                                            .replace(':','_') +
                                            "-" +
                                            potion.toString()
                                                    .toLowerCase()
                                                    .replace('@','-')
                            ),
                            recipe
                    );
                }
        ).toList();
    }


    //

//        return manager.getRecipes()
//                            .stream()
////                            .filter(recipe -> recipe.value()
////                                            .matches(CraftingInput.of(1,2, List.of(
////                                    new ItemStack(Items.POTION),
////                                    new ItemStack(Items.APPLE)
////                            )
////                            ),
////                                    level
////                            )
////                            )
//        .map(
//                        recipe -> {
//                            return new RecipeHolder(ResourceLocation.fromNamespaceAndPath(MOD_ID, recipe.toString()), vanillaRecipeFactory.createShapedRecipeBuilder(CraftingBookCategory.MISC, List.of(new ItemStack(Items.GOLD_BLOCK))).build());
//                        }
//                ).toList();

//        return itemRegistry.holders()
//                // Only applies to fruit.
//                .filter((item) -> null != item.value().getDefaultInstance().get(DataComponents.FOOD))
//                .map(item -> {
//                    ItemStack input =  new ItemStack(item);
//                    ItemStack output = input.copyWithCount(1);
//
//                    input.set(NoNameProvidedDataComponents.SALTED, new Salted(false)); // Does nothing, because JEI ignores data components?
//                    output.set(DataComponents.CUSTOM_NAME, MutableComponent.create(Component.literal("Salted ").getContents()).append(input.getHoverName().getString()));
//
//                    Ingredient foodIngredient = DataComponentIngredient.of(true, input);
//                    ResourceLocation id = ResourceLocation.fromNamespaceAndPath(MODID, "nnp_easy_farming.salted.food." + output.getDescriptionId());
//                    CraftingRecipe recipe = vanillaRecipeFactory.createShapedRecipeBuilder(CraftingBookCategory.MISC, List.of(output))
//                            .group(group)
//                            .define('a', potionIngredient)
//                            .define('f', foodIngredient)
//                            .pattern("a")
//                            .pattern("f")
//                            .build();
//                    return new RecipeHolder<>(id, recipe);
//                })
//                .toList();
//    }

//    private InfuseFruitMaker() {}
}
