package icu.suc.createlapissheet.client.content.processing.mansion

import com.zurrtum.create.catnip.math.AngleHelper
import com.zurrtum.create.client.catnip.animation.AnimationTickHolder
import com.zurrtum.create.client.flywheel.api.instance.Instance
import com.zurrtum.create.client.flywheel.api.visual.DynamicVisual
import com.zurrtum.create.client.flywheel.api.visual.TickableVisual
import com.zurrtum.create.client.flywheel.api.visualization.VisualizationContext
import com.zurrtum.create.client.flywheel.lib.instance.InstanceTypes
import com.zurrtum.create.client.flywheel.lib.model.Models
import com.zurrtum.create.client.flywheel.lib.visual.AbstractBlockEntityVisual
import com.zurrtum.create.client.flywheel.lib.visual.SimpleDynamicVisual
import com.zurrtum.create.client.flywheel.lib.visual.SimpleTickableVisual
import icu.suc.createlapissheet.client.PartialModels
import icu.suc.createlapissheet.content.processing.mansion.EvokerMansionBlock
import icu.suc.createlapissheet.content.processing.mansion.EvokerMansionBlockEntity
import net.minecraft.client.renderer.LightTexture
import net.minecraft.core.Direction
import net.minecraft.util.Mth
import java.util.function.Consumer

class EvokerMansionVisual(
    ctx: VisualizationContext,
    blockEntity: EvokerMansionBlockEntity,
    partialTick: Float
) : AbstractBlockEntityVisual<EvokerMansionBlockEntity>(ctx, blockEntity, partialTick), SimpleDynamicVisual,
    SimpleTickableVisual {
    private val head =
        instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial(PartialModels.EVOKER)).createInstance()

    init {
        head.light(LightTexture.FULL_BRIGHT)

        animate(partialTick)
    }

    override fun tick(context: TickableVisual.Context?) {
        EvokerMansionRenderer.tickAnimation(blockEntity)
    }

    override fun beginFrame(ctx: DynamicVisual.Context?) {
        if (!isVisible(ctx!!.frustum()) || doDistanceLimitThisFrame(ctx)) {
            return
        }

        animate(ctx.partialTick())
    }

    fun animate(partialTicks: Float) {
        val evoker = blockEntity.getEvokerFromBlock()
        val animation = blockEntity.headAnimation.getValue(partialTicks) * .175f

        var headX = 0f
        var headY = 0f
        var headZ = 0f
        var horizontalAngle = 0f

        val time = AnimationTickHolder.getRenderTime(level)
        val hashCode = blockEntity.hashCode()

        when (evoker) {
            EvokerMansionBlock.Evoker.NONE -> {}

            EvokerMansionBlock.Evoker.ANGRY -> {
                val renderTick = time / 4f + (hashCode % 13)
                headX = Mth.sin(renderTick.toDouble() * 1.2f) / 64 - (animation * .75f)
                headY = Mth.sin(renderTick.toDouble() * 2.3f) / 64 - (animation * .65f)
                headZ = Mth.sin(renderTick.toDouble() * 3.4f) / 64 - (animation * .55f)
                horizontalAngle = AngleHelper.rad(blockEntity.headAngle.getValue(partialTicks).toDouble())
            }

            else -> {
                val renderTick = time + (hashCode % 360)
                headY = 0.1f
                horizontalAngle =
                    AngleHelper.rad(renderTick.toDouble() * if (blockEntity.isNauseaFromBlock()) 4 else -4)
            }
        }

        head.setIdentityTransform()
            .translate(visualPosition)
            .translate(0.5, 0.0, 0.5)
            .scale(0.85f)
            .translate(-0.5, 0.0, -0.5)
            .translate(headX, headY, headZ)
            .rotateCentered(horizontalAngle, Direction.UP)
            .setChanged()
    }

    override fun _delete() {
        head.delete()
    }

    override fun collectCrumblingInstances(consumer: Consumer<Instance?>?) {
    }

    override fun updateLight(partialTick: Float) {
    }
}