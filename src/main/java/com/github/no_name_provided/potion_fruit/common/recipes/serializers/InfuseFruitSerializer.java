package com.github.no_name_provided.potion_fruit.common.recipes.serializers;

import com.github.no_name_provided.potion_fruit.common.recipes.InfuseFruit;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;

public class InfuseFruitSerializer {
    
    public static RecipeSerializer<InfuseFruit> getSerializer() {
        
        return new RecipeSerializer<>(CODEC.fieldOf("recipe"), STREAM_CODEC);
    }
    
    private static final MapCodec<InfuseFruit> CODEC = RecordCodecBuilder.mapCodec(
            builderInstance -> builderInstance.group(
                            Ingredient.CODEC.fieldOf("fruit").orElse(Ingredient.of(Items.APPLE)).forGetter(InfuseFruit::getFruit)
                    )
                    .apply(builderInstance, InfuseFruit::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, InfuseFruit> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC, InfuseFruit::getFruit,
            InfuseFruit::new
    );
}
