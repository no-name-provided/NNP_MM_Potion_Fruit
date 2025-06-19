package com.github.no_name_provided.potion_fruit.common.recipes.ingredients.types;

import com.github.no_name_provided.potion_fruit.common.recipes.ingredients.ArbitraryPotion;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.crafting.CompoundIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;
import net.neoforged.neoforge.internal.versions.neoforge.NeoForgeVersion;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class Registry {

    public static final DeferredRegister<IngredientType<?>> INGREDIENT_TYPES = DeferredRegister.create(
            NeoForgeRegistries.Keys.INGREDIENT_TYPES, NeoForgeVersion.MOD_ID
    );

    public static final DeferredHolder<IngredientType<?>, IngredientType<ArbitraryPotion>> ARBITRARY_POTION =
            INGREDIENT_TYPES.register("arbitrary_potion", () -> new IngredientType<>(ArbitraryPotion.CODEC)
            );

    public static void init(IEventBus modEventBus) {
        INGREDIENT_TYPES.register(modEventBus);
    }
}
