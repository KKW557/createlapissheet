package icu.suc.createlapissheet.compat.rei.display

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import icu.suc.createlapissheet.compat.ReiCommon
import icu.suc.createlapissheet.content.kinetics.fan.processing.WololoRecipe
import me.shedaniel.rei.api.common.display.Display
import me.shedaniel.rei.api.common.display.DisplaySerializer
import me.shedaniel.rei.api.common.entry.EntryIngredient
import me.shedaniel.rei.api.common.util.EntryIngredients
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.Identifier
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.RecipeHolder
import java.util.*

class FanWololoDisplay(
    val input: EntryIngredient,
    val output: ItemStack,
    val nausea: Boolean,
    val location: Optional<Identifier>
) : Display {
    constructor(entry: RecipeHolder<WololoRecipe>) : this(entry.id.identifier(), entry.value)

    constructor(id: Identifier, recipe: WololoRecipe) : this(
        EntryIngredients.ofIngredient(recipe.ingredient),
        recipe.result,
        recipe.nausea,
        Optional.of(id)
    )

    override fun getInputEntries() = listOf(input)

    override fun getOutputEntries() = listOf(EntryIngredients.of(output))

    override fun getCategoryIdentifier() = ReiCommon.FAN_WOLOLO

    override fun getDisplayLocation() = location

    override fun getSerializer(): DisplaySerializer<out Display> = SERIALIZER

    companion object {
        @JvmField
        val SERIALIZER: DisplaySerializer<FanWololoDisplay> = DisplaySerializer.of(
            RecordCodecBuilder.mapCodec { instance ->
                instance.group(
                    EntryIngredient.codec().fieldOf("inout").forGetter(FanWololoDisplay::input),
                    ItemStack.CODEC.fieldOf("outputs").forGetter(FanWololoDisplay::output),
                    Codec.BOOL.fieldOf("nausea").forGetter(FanWololoDisplay::nausea),
                    Identifier.CODEC.optionalFieldOf("location").forGetter(FanWololoDisplay::location)
                )
                    .apply(instance, ::FanWololoDisplay)
            },
            StreamCodec.composite(
                EntryIngredient.streamCodec(),
                FanWololoDisplay::input,
                ItemStack.STREAM_CODEC,
                FanWololoDisplay::output,
                ByteBufCodecs.BOOL,
                FanWololoDisplay::nausea,
                ByteBufCodecs.optional(Identifier.STREAM_CODEC),
                FanWololoDisplay::location,
                ::FanWololoDisplay
            )
        )
    }
}