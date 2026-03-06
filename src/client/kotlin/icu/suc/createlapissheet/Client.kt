package icu.suc.createlapissheet

import icu.suc.createlapissheet.client.BlockEntityRenders
import icu.suc.createlapissheet.client.BlockLayers
import icu.suc.createlapissheet.client.Handle
import icu.suc.createlapissheet.client.PartialModels
import icu.suc.createlapissheet.client.renderer.block.entity.EvokerMansionRenderer
import net.fabricmc.api.ClientModInitializer
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers

class Client : ClientModInitializer {

    override fun onInitializeClient() {
        BlockLayers.register()
        PartialModels.register()
        BlockEntityRenders.register()
        Handle.register()
    }
}