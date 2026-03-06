package icu.suc.createlapissheet.client.compat

import com.zurrtum.create.AllItems
import com.zurrtum.create.client.compat.rei.display.MysteriousItemConversionDisplay
import icu.suc.createlapissheet.Items
import icu.suc.createlapissheet.client.compat.rei.FanWololoCategory
import me.shedaniel.rei.api.client.plugins.REIClientPlugin
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry
import me.shedaniel.rei.api.common.util.EntryIngredients

class ReiClient : REIClientPlugin {
    override fun registerDisplays(registry: DisplayRegistry) {
        registry.add(MysteriousItemConversionDisplay(Items.EMPTY_EVOKER_MANSION, Items.EVOKER_MANSION))
        registry.add(MysteriousItemConversionDisplay(Items.EVOKER_MANSION, Items.SAFE_EVOKER_MANSION))
    }

    override fun registerCategories(registry: CategoryRegistry) {
        registry.add(FanWololoCategory()) { configuration -> configuration.addWorkstations(EntryIngredients.of(AllItems.ENCASED_FAN)) }
    }
}