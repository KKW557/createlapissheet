package icu.suc.createlapissheet.compat

import icu.suc.createlapissheet.MOD_ID
import icu.suc.createlapissheet.compat.rei.FanWololoDisplay
import icu.suc.createlapissheet.item.crafting.WololoRecipe
import me.shedaniel.rei.api.common.category.CategoryIdentifier
import me.shedaniel.rei.api.common.display.Display
import me.shedaniel.rei.api.common.display.DisplaySerializerRegistry
import me.shedaniel.rei.api.common.plugins.REICommonPlugin
import me.shedaniel.rei.api.common.registry.display.ServerDisplayRegistry
import net.minecraft.world.item.crafting.RecipeHolder

class ReiCommon : REICommonPlugin {
    override fun registerDisplays(registry: ServerDisplayRegistry) {
        registry.beginRecipeFiller<WololoRecipe, Display>(WololoRecipe::class.java)
            .fill { entry: RecipeHolder<WololoRecipe> -> FanWololoDisplay(entry) }
    }

    override fun registerDisplaySerializer(registry: DisplaySerializerRegistry) {
        registry.register(FAN_WOLOLO.identifier, FanWololoDisplay.SERIALIZER)
    }

    companion object {
        @JvmField
        val FAN_WOLOLO: CategoryIdentifier<FanWololoDisplay> = CategoryIdentifier.of(MOD_ID, "fan_wololo")
    }
}