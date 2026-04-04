@file:JvmName("CreateLapisSheet")

package icu.suc.createlapissheet.client

import com.zurrtum.create.client.*
import com.zurrtum.create.client.catnip.render.SpriteShiftEntry
import com.zurrtum.create.client.catnip.render.SpriteShifter
import com.zurrtum.create.client.content.decoration.encasing.EncasedCTBehaviour
import com.zurrtum.create.client.flywheel.api.visualization.VisualizationManager
import com.zurrtum.create.client.flywheel.lib.model.baked.PartialModel
import com.zurrtum.create.client.foundation.block.connected.AllCTTypes
import com.zurrtum.create.client.foundation.block.connected.CTSpriteShiftEntry
import com.zurrtum.create.client.foundation.block.connected.CTSpriteShifter
import com.zurrtum.create.client.foundation.block.connected.CTType
import com.zurrtum.create.client.infrastructure.fluid.FluidConfig
import com.zurrtum.create.client.infrastructure.model.CTModel
import com.zurrtum.create.infrastructure.fluids.FlowableFluid
import icu.suc.createlapissheet.BlockEntityTypes
import icu.suc.createlapissheet.Blocks
import icu.suc.createlapissheet.Fluids
import icu.suc.createlapissheet.client.content.processing.mansion.EvokerMansionRenderer
import icu.suc.createlapissheet.client.content.processing.mansion.EvokerMansionVisual
import icu.suc.createlapissheet.content.processing.mansion.EvokerMansionBlockEntity
import icu.suc.createlapissheet.identifier
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.chunk.ChunkSectionLayer
import net.minecraft.client.renderer.texture.TextureAtlas
import net.minecraft.client.resources.model.Material
import net.minecraft.core.component.DataComponentPatch
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.Identifier
import net.minecraft.util.Mth

object FluidConfigs {
    @JvmStatic
    fun config(fluid: FlowableFluid) {
        config(fluid, -1) { 96.0f }
    }

    @JvmStatic
    fun config(fluid: FlowableFluid, color: Int, distance: () -> Float) {
        config(fluid, color, distance) { _: DataComponentPatch -> -1 }
    }

    @JvmStatic
    fun config(
        fluid: FlowableFluid,
        color: Int,
        distance: () -> Float,
        tint: (DataComponentPatch) -> Int
    ) {
        val id = BuiltInRegistries.FLUID.getKey(fluid).withPrefix("fluid/")
        println(id)
        val config = FluidConfig(
            { Minecraft.getInstance().atlasManager.get(Material(TextureAtlas.LOCATION_BLOCKS, id.withSuffix("_still"))) },
            { Minecraft.getInstance().atlasManager.get(Material(TextureAtlas.LOCATION_BLOCKS, id.withSuffix("_flow"))) },
            tint,
            distance,
            color
        )
        AllFluidConfigs.ALL[fluid] = config
        AllFluidConfigs.ALL[fluid.flowing] = config
    }

    @JvmStatic
    fun register() {
        config(Fluids.EXPERIENCE, -1, { 96.0f }) { _: DataComponentPatch ->
            val level = Minecraft.getInstance().level ?: return@config 0xFFB8FF2A.toInt()
            val o = level.gameTime / 8.0

            val red = ((Mth.sin(o) + 1.0) * 0.5f * 255.0f).toInt().coerceIn(0, 255)
            val green = 255
            val blue = ((Mth.sin(o + 4.1887903) + 1.0f) * 0.1f * 255.0f).toInt().coerceIn(0, 255)

            (128 shl 24) or (red shl 16) or (green shl 8) or blue
        }
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

object SpriteShifts {
    @JvmField
    val LAPIS_CASING = ct(AllCTTypes.OMNIDIRECTIONAL, "lapis_casing")

    @JvmField
    val LAPIS_CASING_BELT =
        get(Create.asResource("block/belt/brass_belt_casing"), identifier("block/belt/lapis_belt_casing"))

    @JvmStatic
    fun get(original: Identifier, target: Identifier): SpriteShiftEntry = SpriteShifter.get(original, target)

    @JvmStatic
    fun ct(type: CTType, block: String, connected: String = block) =
        ct(type, identifier("block/$block"), identifier("block/" + connected + "_connected"))

    @JvmStatic
    fun ct(type: CTType, block: Identifier, connected: Identifier): CTSpriteShiftEntry =
        CTSpriteShifter.getCT(type, block, connected)

    @JvmStatic
    fun register() {
    }
}

object Casings {
    @JvmStatic
    fun register() {
        AllCasings.make(Blocks.LAPIS_CASING, SpriteShifts.LAPIS_CASING)
    }
}

object CTBehaviours {
    @JvmField
    val LAPIS_CASING = EncasedCTBehaviour(SpriteShifts.LAPIS_CASING)

    @JvmStatic
    fun register() {
    }
}

object Models {
    @JvmStatic
    fun register() {
        AllModels.register(Blocks.LAPIS_CASING, CTModel.of(CTBehaviours.LAPIS_CASING))
    }
}

object PartialModels {
    @JvmField
    val LAPIS_BELT_COVER_X = block("belt_cover/lapis_belt_cover_x")

    @JvmField
    val LAPIS_BELT_COVER_Z = block("belt_cover/lapis_belt_cover_z")

    @JvmField
    val EVOKER: PartialModel = block("evoker_mansion/evoker")

    @JvmStatic
    fun block(path: String): PartialModel = PartialModel.of(identifier("block/$path"))

    @JvmStatic
    fun register() {
    }
}

object BlockEntityRenders {
    @JvmStatic
    fun register() {
        AllBlockEntityRenders.visual(
            BlockEntityTypes.EVOKER_MANSION,
            { EvokerMansionRenderer() },
            ::EvokerMansionVisual
        )
    }
}

object BlockLayers {
    @JvmStatic
    fun register() {
        AllBlockLayers.register(Blocks.LAPIS_FUNNEL, ChunkSectionLayer.CUTOUT)
        AllBlockLayers.register(Blocks.LAPIS_BELT_FUNNEL, ChunkSectionLayer.CUTOUT)
        AllBlockLayers.register(Blocks.EVOKER_MANSION, ChunkSectionLayer.CUTOUT)
    }
}
