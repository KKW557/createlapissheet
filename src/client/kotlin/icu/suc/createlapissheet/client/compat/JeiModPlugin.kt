package icu.suc.createlapissheet.client.compat

import com.zurrtum.create.client.compat.jei.JeiClientPlugin
import com.zurrtum.create.client.compat.jei.display.MysteriousItemConversionDisplay
import icu.suc.createlapissheet.Items
import icu.suc.createlapissheet.identifier
import mezz.jei.api.IModPlugin
import mezz.jei.api.registration.IRecipeRegistration
import net.minecraft.resources.Identifier

class JeiModPlugin : IModPlugin {
    companion object {
        @JvmStatic
        val ID = identifier("jei_plugin")
    }

    override fun getPluginUid(): Identifier = ID

    override fun registerRecipes(registration: IRecipeRegistration) {
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
    }
}