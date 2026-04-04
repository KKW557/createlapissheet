package icu.suc.createlapissheet.client.compat

import com.zurrtum.create.AllItems
import com.zurrtum.create.client.compat.jei.JeiClientPlugin
import com.zurrtum.create.client.compat.jei.display.MysteriousItemConversionDisplay
import icu.suc.createlapissheet.Items
import icu.suc.createlapissheet.client.compat.jei.category.FanWololoCategory
import icu.suc.createlapissheet.content.kinetics.fan.processing.WololoRecipe
import icu.suc.createlapissheet.identifier
import mezz.jei.api.IModPlugin
import mezz.jei.api.JeiPlugin
import mezz.jei.api.recipe.types.IRecipeType
import mezz.jei.api.registration.IRecipeCatalystRegistration
import mezz.jei.api.registration.IRecipeCategoryRegistration
import mezz.jei.api.registration.IRecipeRegistration
import mezz.jei.common.Internal
import net.minecraft.world.item.crafting.RecipeHolder

@JeiPlugin
class JeiModPlugin : IModPlugin {
    override fun getPluginUid() = ID

    override fun registerRecipes(registration: IRecipeRegistration) {
        val recipes = Internal.getClientSyncedRecipes()
        registration.addRecipes(
            JeiClientPlugin.MYSTERY_CONVERSION,
            listOf(
                MysteriousItemConversionDisplay(
                    identifier("evoker_mansion"),
                    Items.EMPTY_EVOKER_MANSION,
                    Items.EVOKER_MANSION
                ),
                MysteriousItemConversionDisplay(
                    identifier("safe_evoker_mansion"),
                    Items.EVOKER_MANSION,
                    Items.SAFE_EVOKER_MANSION
                )
            )
        )
        registration.addRecipes(FAN_WOLOLO, FanWololoCategory.getRecipes(recipes))
    }

    override fun registerCategories(registration: IRecipeCategoryRegistration) {
        registration.addRecipeCategories(FanWololoCategory())
    }

    override fun registerRecipeCatalysts(registration: IRecipeCatalystRegistration) {
        registration.addCraftingStation(FAN_WOLOLO, AllItems.ENCASED_FAN)
    }

    companion object {
        @JvmField
        val ID = identifier("jei_mod_plugin")

        @JvmField
        val FAN_WOLOLO: IRecipeType<RecipeHolder<WololoRecipe>> = createRecipeHolderType("fan_wololo")

        @Suppress("UNCHECKED_CAST")
        @JvmStatic
        fun <T : Any> createRecipeHolderType(id: String): IRecipeType<T> =
            IRecipeType.create(identifier(id), RecipeHolder::class.java) as IRecipeType<T>
    }
}