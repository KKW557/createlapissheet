@file:JvmName("CreateLapisSheet")

package icu.suc.createlapissheet.client

import com.zurrtum.create.client.AllBlockEntityRenders
import com.zurrtum.create.client.AllBlockLayers
import com.zurrtum.create.client.flywheel.api.visualization.VisualizationManager
import com.zurrtum.create.client.flywheel.lib.model.baked.PartialModel
import icu.suc.createlapissheet.BlockEntityTypes
import icu.suc.createlapissheet.Blocks
import icu.suc.createlapissheet.content.processing.mansion.EvokerMansionBlockEntity
import icu.suc.createlapissheet.client.content.processing.mansion.EvokerMansionRenderer
import icu.suc.createlapissheet.client.content.processing.mansion.EvokerMansionVisual
import icu.suc.createlapissheet.identifier
import net.minecraft.client.renderer.chunk.ChunkSectionLayer

object BlockLayers {
    @JvmStatic
    fun register() {
        AllBlockLayers.register(Blocks.LAPIS_FUNNEL, ChunkSectionLayer.CUTOUT)
        AllBlockLayers.register(Blocks.LAPIS_BELT_FUNNEL, ChunkSectionLayer.CUTOUT)
        AllBlockLayers.register(Blocks.EVOKER_MANSION, ChunkSectionLayer.CUTOUT)
    }
}

object PartialModels {
    @JvmField
    val EVOKER: PartialModel = block("evoker_mansion/evoker")

    @JvmStatic
    fun block(path: String): PartialModel = PartialModel.of(identifier("block/$path"))

    @JvmStatic
    fun register() {}
}

object BlockEntityRenders {
    @JvmStatic
    fun register() {
        AllBlockEntityRenders.visual(BlockEntityTypes.EVOKER, { EvokerMansionRenderer() }, ::EvokerMansionVisual)
    }
}

class Handle : icu.suc.createlapissheet.Handle() {
    override fun isClient() = true

    override fun tickEvokerMansionAnimation(be: EvokerMansionBlockEntity) {
        if (VisualizationManager.supportsVisualization(be.getLevel())) return
        EvokerMansionRenderer.tickAnimation(be)
    }

    companion object {
        @JvmStatic
        fun register() {
            init(Handle())
        }
    }
}
