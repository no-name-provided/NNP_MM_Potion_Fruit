package com.github.no_name_provided.potion_fruit;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = NNPMMPotionFruit.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.BooleanValue ADD_LORE = BUILDER.comment("Add lore (tooltip) to potion fruit?").define("add_lore", true);
    private static final ModConfigSpec.BooleanValue ADD_GLINT = BUILDER.comment("Add glint effect to potion fruit?").define("add_glint", true);

    static final ModConfigSpec SPEC = BUILDER.build();

    public static boolean addLore;
    public static boolean addGlint;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent.Loading event) {
        addLore = ADD_LORE.get();
        addGlint = ADD_GLINT.get();
    }
}
