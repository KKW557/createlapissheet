package icu.suc.createlapissheet.content.kinetics.fan.processing;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.zurrtum.create.content.processing.recipe.ProcessingOutput;
import com.zurrtum.create.foundation.recipe.CreateSingleStackRollableRecipe;
import icu.suc.createlapissheet.RecipeSerializers;
import icu.suc.createlapissheet.RecipeTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.*;
import org.jspecify.annotations.NonNull;

import java.util.List;

public record WololoRecipe(Ingredient ingredient, List<ProcessingOutput> results,
                           boolean nausea) implements CreateSingleStackRollableRecipe {
    @Override
    public @NonNull RecipeSerializer<? extends Recipe<SingleRecipeInput>> getSerializer() {
        return RecipeSerializers.WOLOLO;
    }

    @Override
    public @NonNull RecipeType<? extends Recipe<SingleRecipeInput>> getType() {
        return RecipeTypes.WOLOLO;
    }

    public static class Serializer implements RecipeSerializer<WololoRecipe> {
        public static final MapCodec<WololoRecipe> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(Ingredient.CODEC.fieldOf("ingredient").forGetter(WololoRecipe::ingredient), ProcessingOutput.CODEC.listOf(1, 12).fieldOf("results").forGetter(WololoRecipe::results), Codec.BOOL.fieldOf("nausea").forGetter(WololoRecipe::nausea)).apply(instance, WololoRecipe::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, WololoRecipe> PACKET_CODEC = StreamCodec.composite(Ingredient.CONTENTS_STREAM_CODEC, WololoRecipe::ingredient, ProcessingOutput.STREAM_CODEC.apply(ByteBufCodecs.list()), WololoRecipe::results, ByteBufCodecs.BOOL, WololoRecipe::nausea, WololoRecipe::new);

        public @NonNull MapCodec<WololoRecipe> codec() {
            return CODEC;
        }

        public @NonNull StreamCodec<RegistryFriendlyByteBuf, WololoRecipe> streamCodec() {
            return PACKET_CODEC;
        }
    }
}
