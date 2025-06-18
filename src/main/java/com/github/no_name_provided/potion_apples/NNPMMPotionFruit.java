package com.github.no_name_provided.potion_apples;

import com.github.no_name_provided.potion_apples.common.recipes.Registry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(NNPMMPotionFruit.MOD_ID) public class NNPMMPotionFruit {
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "nnp_mm_potion_fruit";

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    public NNPMMPotionFruit(IEventBus modEventBus, ModContainer modContainer) {

        // Register deferred registers
        Registry.init(modEventBus);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.SERVER, Config.SPEC);
    }

}
