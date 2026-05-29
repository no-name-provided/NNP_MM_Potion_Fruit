package com.github.no_name_provided.potion_fruit.common;

import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;

@EventBusSubscriber
public class Events {
    
    @SubscribeEvent
    private static void onDataPackSync(OnDatapackSyncEvent event) {
        event.sendRecipes(RecipeType.CRAFTING);
    }
}
