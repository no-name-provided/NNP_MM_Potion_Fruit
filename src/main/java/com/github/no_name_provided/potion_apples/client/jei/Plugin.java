package com.github.no_name_provided.potion_apples.client.jei;

import com.github.no_name_provided.potion_apples.client.jei.RecipeMakers.InfuseFruitMaker;
import com.github.no_name_provided.potion_apples.common.recipes.InfuseFruit;
import com.mojang.logging.LogUtils;
import io.netty.util.internal.logging.Slf4JLoggerFactory;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.github.no_name_provided.potion_apples.NNPMMPotionFruit.MOD_ID;

@JeiPlugin
public class Plugin implements IModPlugin {

    /**
     * The unique ID for this mod plugin.
     * The namespace should be your mod's modId.
     */
    @Override @ParametersAreNonnullByDefault
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, "jei_plugin");
    }

    /**Register modded recipes.*/
    @Override
    public void registerRecipes(IRecipeRegistration registration) {

        IJeiHelpers jeiHelpers = registration.getJeiHelpers();
        Level level = Minecraft.getInstance().level;
        if (null != level) {
            RecipeManager recipes = level.getRecipeManager();

            registration.addRecipes(RecipeTypes.CRAFTING, InfuseFruitMaker.createRecipes(jeiHelpers));
        } else {
            LogUtils.getLogger().debug("Level was null - " + MOD_ID + " noping out of JEI plugin.");
        }

    }

}
