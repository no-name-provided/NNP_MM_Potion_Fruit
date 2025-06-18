package com.github.no_name_provided.potion_apples.common.recipes;

import com.github.no_name_provided.potion_apples.common.recipes.serializers.InfuseAppleSerializer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.github.no_name_provided.potion_apples.NNPMMPotionApples.MOD_ID;

public class registries {

    static DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(
            BuiltInRegistries.RECIPE_SERIALIZER,
            MOD_ID
            );

    public static DeferredHolder<RecipeSerializer<?>, InfuseAppleSerializer> INFUSE_APPLE_SERIALIZER =
            RECIPE_SERIALIZERS.register(
                    "infuse_apple",
                    InfuseAppleSerializer::new
            );


    public static void init(IEventBus modEventBus) {
        RECIPE_SERIALIZERS.register(modEventBus);
    }
}
