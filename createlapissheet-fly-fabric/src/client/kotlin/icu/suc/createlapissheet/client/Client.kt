package icu.suc.createlapissheet.client

import net.fabricmc.api.ClientModInitializer

class Client : ClientModInitializer {
    override fun onInitializeClient() {
        FluidConfigs.register()
        Handle.register()
        SpriteShifts.register()
        Casings.register()
        CTBehaviours.register()
        Models.register()
        PartialModels.register()
        BlockEntityRenders.register()
        BlockLayers.register()
    }
}