package com.github.no_name_provided.potion_fruit.common.recipes.serializers;

import com.github.no_name_provided.potion_fruit.common.recipes.InfuseFruit;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import org.jetbrains.annotations.NotNull;

public class InfuseFruitSerializer implements RecipeSerializer<InfuseFruit> {

    private static final MapCodec<InfuseFruit> CODEC = RecordCodecBuilder.mapCodec(
            builderInstance -> builderInstance.group(
                            CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(CraftingRecipe::category),
                            Ingredient.CODEC.fieldOf("fruit").orElse(Ingredient.of(Items.APPLE)).forGetter(InfuseFruit::getFruit)
                    )
                    .apply(builderInstance, InfuseFruit::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, InfuseFruit> STREAM_CODEC = StreamCodec.composite(
            CraftingBookCategory.STREAM_CODEC, CraftingRecipe::category,
            Ingredient.CONTENTS_STREAM_CODEC, InfuseFruit::getFruit,
            InfuseFruit::new
    );

    @Override
    public @NotNull MapCodec<InfuseFruit> codec() {
        return CODEC;
    }

    @Override
    public @NotNull StreamCodec<RegistryFriendlyByteBuf, InfuseFruit> streamCodec() {
        return STREAM_CODEC;
    }
}
