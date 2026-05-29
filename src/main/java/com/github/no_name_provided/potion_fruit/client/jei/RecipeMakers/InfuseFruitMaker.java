package com.github.no_name_provided.potion_fruit.client.jei.RecipeMakers;

import com.github.no_name_provided.potion_fruit.Config;
import com.github.no_name_provided.potion_fruit.common.annotations.OnlyIn;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.vanilla.IVanillaRecipeFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.neoforged.neoforge.common.crafting.CompoundIngredient;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;

import java.util.ArrayList;
import java.util.List;

import static com.github.no_name_provided.potion_fruit.client.ClientEvents.INFUSION_RECIPES;

@OnlyIn()
public class InfuseFruitMaker {
    
    public static List<RecipeHolder<CraftingRecipe>> createRecipes(IJeiHelpers jeiHelpers) {
        
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (null == level) {
            throw new IllegalStateException("Could not get registry, registry access is unavailable because the level is currently null.");
        }
        RegistryAccess REGISTRY_ACCESS = level.registryAccess();
        
        List<Ingredient> potions = new ArrayList<>();
        
        Registry<Potion> potionRegistry = REGISTRY_ACCESS.lookupOrThrow(BuiltInRegistries.POTION.key());
        potionRegistry.listElements()
                .filter(
                        // Getting the potion name is weird. The second parameter is prepended, and the suffix is parsed from a path.
                        // Filtering out any potions without effects seems to remove uncraftables.
                        holder -> !holder.value().getEffects().isEmpty() &&
                                !holder.value().name().isEmpty() &&
                                !holder.value().name().startsWith("empty")
                ).forEach(
                        holder -> potions.add(
                                DataComponentIngredient.of(false, PotionContents.createItemStack(Items.POTION, holder))
                        )
                );
        
        return generateRecipesForFruit(potions, jeiHelpers);
    }
    
    static List<RecipeHolder<CraftingRecipe>> generateRecipesForFruit(List<Ingredient> potions, IJeiHelpers jeiHelpers) {
        IVanillaRecipeFactory vanillaRecipeFactory = jeiHelpers.getVanillaRecipeFactory();
        
        return INFUSION_RECIPES.stream().map(
                holder -> {
                    ItemStack fruit = holder.value().getFruit().getValues().get(0).value().getDefaultInstance();
                    ItemStack originalFruit = fruit.copy();
                    originalFruit.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, false);
                    fruit.set(DataComponents.LORE, new ItemLore(List.of(Component.literal("Infused with potion effect!"))));
                    if (Config.addGlint) {
                        fruit.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
                    }
                    
                    CraftingRecipe recipe = vanillaRecipeFactory.createShapedRecipeBuilder(
                                    CraftingBookCategory.MISC, new SlotDisplay.ItemStackSlotDisplay(ItemStackTemplate.fromNonEmptyStack(fruit))
                            ).group("infuse_fruit")
                            .pattern("P")
                            .pattern("F")
                            .define('P', new CompoundIngredient(potions).toVanilla())
                            .define('F', Ingredient.of(originalFruit.getItem()))
                            .build();
                    return new RecipeHolder<>(
                            ResourceKey.create(
                                    Registries.RECIPE,
                                    Identifier.fromNamespaceAndPath(
                                            holder.id().identifier().getNamespace(),
                                            holder.id().identifier().getPath().replace(':', '_')
                                    )
                            ),
                            recipe
                    );
                }
        ).toList();
    }
}
