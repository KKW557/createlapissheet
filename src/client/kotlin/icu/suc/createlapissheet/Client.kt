package icu.suc.createlapissheet

import icu.suc.createlapissheet.client.BlockEntityRenders
import icu.suc.createlapissheet.client.BlockLayers
import icu.suc.createlapissheet.client.Handle
import icu.suc.createlapissheet.client.PartialModels
import net.fabricmc.api.ClientModInitializer

class Client : ClientModInitializer {
    override fun onInitializeClient() {
        BlockLayers.register()
        PartialModels.register()
        BlockEntityRenders.register()
        Handle.register()
    }
}