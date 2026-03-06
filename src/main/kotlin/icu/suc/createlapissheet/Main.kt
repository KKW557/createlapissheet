package icu.suc.createlapissheet

import com.zurrtum.create.api.registry.CreateRegisterPlugin
import net.fabricmc.api.ModInitializer

class Main : ModInitializer, CreateRegisterPlugin {
    override fun onInitialize() {
        Items.register()
        BlockEntityTypes.register()
        CreativeTabs.register()
        Tags.register()
        RecipeTypes.register()
        RecipeSerializers.register()
        FanProcessingTypes.register()
    }

    override fun onBlockRegister() {
        Blocks.register()
    }
}
