package com.github.no_name_provided.potion_fruit.datagen;

import com.github.no_name_provided.potion_fruit.datagen.providers.Recipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

import static com.github.no_name_provided.potion_fruit.NNPMMPotionFruit.MOD_ID;

@EventBusSubscriber(modid = MOD_ID)
public class DataGen{
    @SubscribeEvent
    public static void onGatherData(GatherDataEvent.Client event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        generator.addProvider(true, new Recipes.Runner(packOutput, lookupProvider));

    }
}
