package com.github.no_name_provided.potion_fruit.common.recipes.property_sets;

import net.minecraft.world.item.crafting.RecipePropertySet;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.github.no_name_provided.potion_fruit.NNPMMPotionFruit.MOD_ID;

public class Registry {
    public static DeferredRegister<RecipePropertySet> PROPERTY_SETS = DeferredRegister.create(
        RecipePropertySet.TYPE_KEY,
            MOD_ID
    );
    
    public static void init(IEventBus modBus) {
        PROPERTY_SETS.register(modBus);
    }
}
