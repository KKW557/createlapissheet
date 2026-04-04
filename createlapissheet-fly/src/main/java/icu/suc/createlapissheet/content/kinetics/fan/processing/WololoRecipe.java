package icu.suc.createlapissheet.content.kinetics.fan.processing;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.zurrtum.create.foundation.recipe.CreateSingleStackRecipe;
import icu.suc.createlapissheet.RecipeSerializers;
import icu.suc.createlapissheet.RecipeTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import org.jspecify.annotations.NonNull;

public record WololoRecipe(Ingredient ingredient, ItemStack result, boolean nausea) implements CreateSingleStackRecipe {
    @Override
    public @NonNull RecipeSerializer<? extends Recipe<SingleRecipeInput>> getSerializer() {
        return RecipeSerializers.WOLOLO;
    }

    @Override
    public @NonNull RecipeType<? extends Recipe<SingleRecipeInput>> getType() {
        return RecipeTypes.WOLOLO;
    }

    public static class Serializer implements RecipeSerializer<WololoRecipe> {
        public static final MapCodec<WololoRecipe> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(Ingredient.CODEC.fieldOf("ingredient").forGetter(WololoRecipe::ingredient), ItemStack.CODEC.fieldOf("result").forGetter(WololoRecipe::result), Codec.BOOL.fieldOf("nausea").forGetter(WololoRecipe::nausea)).apply(instance, WololoRecipe::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, WololoRecipe> PACKET_CODEC = StreamCodec.composite(Ingredient.CONTENTS_STREAM_CODEC, WololoRecipe::ingredient, ItemStack.STREAM_CODEC, WololoRecipe::result, ByteBufCodecs.BOOL, WololoRecipe::nausea, WololoRecipe::new);

        public @NonNull MapCodec<WololoRecipe> codec() {
            return CODEC;
        }

        public @NonNull StreamCodec<RegistryFriendlyByteBuf, WololoRecipe> streamCodec() {
            return PACKET_CODEC;
        }
    }
}
