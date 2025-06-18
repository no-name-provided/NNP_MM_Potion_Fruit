package com.github.no_name_provided.potion_apples.common.recipes.serializers;

import com.github.no_name_provided.potion_apples.common.recipes.InfuseApple;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import org.jetbrains.annotations.NotNull;

public class InfuseAppleSerializer implements RecipeSerializer<InfuseApple> {

    private static final MapCodec<InfuseApple> CODEC = RecordCodecBuilder.mapCodec(
            builderInstance -> builderInstance.group(
                            CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(CraftingRecipe::category),
                            Ingredient.CODEC.fieldOf("fruit").orElse(Ingredient.of(Items.APPLE)).forGetter(InfuseApple::getFruit)
                    )
                    .apply(builderInstance, InfuseApple::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, InfuseApple> STREAM_CODEC = StreamCodec.composite(
            CraftingBookCategory.STREAM_CODEC, CraftingRecipe::category,
            Ingredient.CONTENTS_STREAM_CODEC, InfuseApple::getFruit,
            InfuseApple::new
    );

    @Override
    public @NotNull MapCodec<InfuseApple> codec() {
        return CODEC;
    }

    @Override
    public @NotNull StreamCodec<RegistryFriendlyByteBuf, InfuseApple> streamCodec() {
        return STREAM_CODEC;
    }
}
