package icu.suc.createlapissheet

import com.zurrtum.create.api.registry.CreateRegisterPlugin
import net.fabricmc.api.ModInitializer

class Main : ModInitializer, CreateRegisterPlugin {
    override fun onInitialize() {
        GameRules.register()
        Items.register()
        Tags.register()
        DataComponents.register()
        CreativeTabs.register()
        BlockEntityTypes.register()
        RecipeTypes.register()
        RecipeSerializers.register()
        RecipeSets.register()
        FluidItemInventory.register()
        OpenPipeEffectHandlers.register()
        FanProcessingTypes.register()
    }

    override fun onBlockRegister() {
        Blocks.register()
    }

    override fun onFluidRegister() {
        Fluids.register()
    }
}
