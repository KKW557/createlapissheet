package icu.suc.createlapissheet

import com.zurrtum.create.api.registry.CreateRegisterPlugin
import net.fabricmc.api.ModInitializer

class Main : ModInitializer, CreateRegisterPlugin {
    override fun onInitialize() {
        Items.register()
        Tags.register()
        CreativeTabs.register()
        BlockEntityTypes.register()
        RecipeTypes.register()
        RecipeSerializers.register()
        RecipeSets.register()
        FanProcessingTypes.register()
    }

    override fun onBlockRegister() {
        Blocks.register()
    }
}
