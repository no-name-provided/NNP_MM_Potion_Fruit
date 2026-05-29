package com.github.no_name_provided.potion_fruit.client;

import com.github.no_name_provided.potion_fruit.common.annotations.OnlyIn;
import com.github.no_name_provided.potion_fruit.common.recipes.InfuseFruit;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RecipesReceivedEvent;

import java.util.ArrayList;
import java.util.List;

import static com.github.no_name_provided.potion_fruit.NNPMMPotionFruit.MOD_ID;

@OnlyIn(Dist.CLIENT) @EventBusSubscriber(modid = MOD_ID)
public class ClientEvents {
    public static final List<RecipeHolder<InfuseFruit>> INFUSION_RECIPES = new ArrayList<>();
    
    @SubscribeEvent // on the game event bus only on the physical client
    public static void onRecipesReceived(RecipesReceivedEvent event) {
        // First remove the previous recipes
        INFUSION_RECIPES.clear();
        
        // Then store the recipes you want
        INFUSION_RECIPES.addAll(event.getRecipeMap().values().stream().filter(holder ->
                        holder.id().identifier().getPath().startsWith("potion_infusion/")
                ).map(holder -> (RecipeHolder<InfuseFruit>) holder).toList()
        );
    }
    
    @SubscribeEvent // on the game event bus only on the physical client
    public static void onClientLogOut(ClientPlayerNetworkEvent.LoggingOut event) {
        // Clear the stored recipes on world log out
        INFUSION_RECIPES.clear();
    }
}
