package icu.suc.createlapissheet.client.renderer.block.entity

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import com.zurrtum.create.catnip.animation.LerpedFloat.Chaser
import com.zurrtum.create.catnip.math.AngleHelper
import com.zurrtum.create.client.catnip.animation.AnimationTickHolder
import com.zurrtum.create.client.catnip.render.CachedBuffers
import com.zurrtum.create.client.catnip.render.SuperByteBuffer
import icu.suc.createlapissheet.block.EvokerMansionBlock.Evoker.*
import icu.suc.createlapissheet.block.entity.EvokerMansionBlockEntity
import icu.suc.createlapissheet.client.PartialModels
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.LightTexture
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState
import net.minecraft.client.renderer.feature.ModelFeatureRenderer
import net.minecraft.client.renderer.rendertype.RenderType
import net.minecraft.client.renderer.rendertype.RenderTypes
import net.minecraft.client.renderer.state.CameraRenderState
import net.minecraft.core.Direction
import net.minecraft.util.Mth
import net.minecraft.world.phys.Vec3

class EvokerMansionRenderer :
    BlockEntityRenderer<EvokerMansionBlockEntity, EvokerMansionRenderer.EvokerMansionRenderState> {

    override fun createRenderState() = EvokerMansionRenderState()

    override fun extractRenderState(
        be: EvokerMansionBlockEntity,
        state: EvokerMansionRenderState,
        tickProgress: Float,
        camera: Vec3,
        overlay: ModelFeatureRenderer.CrumblingOverlay?
    ) {
        val evoker = be.getEvokerFromBlock()
        if (evoker == NONE) return

        BlockEntityRenderState.extractBase(be, state, overlay)

        val animation = be.headAnimation.getValue(tickProgress) * .175f

        state.data = EvokerMansionRenderData().apply {
            this.evoker = CachedBuffers.partial(PartialModels.EVOKER, state.blockState)

            val time = AnimationTickHolder.getRenderTime(be.level)
            val hashCode = be.hashCode()

            when (evoker) {
                ANGRY -> {
                    val renderTick = time / 4f + (hashCode % 13)
                    this.headX = Mth.sin(renderTick.toDouble() * 1.2f) / 64 - (animation * .75f)
                    this.headY = Mth.sin(renderTick.toDouble() * 2.3f) / 64 - (animation * .65f)
                    this.headZ = Mth.sin(renderTick.toDouble() * 3.4f) / 64 - (animation * .55f)
                    this.horizontalAngle = AngleHelper.rad(be.headAngle.getValue(tickProgress).toDouble())
                }

                CASTING -> {
                    val renderTick = time + (hashCode % 360)
                    this.headY = 0.1f
                    this.horizontalAngle =
                        AngleHelper.rad(renderTick.toDouble() * if (be.getNauseaFromBlock()) 4 else -4)
                }
            }
        }
    }

    override fun submit(
        state: EvokerMansionRenderState,
        pose: PoseStack,
        collector: SubmitNodeCollector,
        camera: CameraRenderState
    ) {
        state.data?.submit(pose, collector)
    }

    companion object {
        @JvmStatic
        fun tickAnimation(be: EvokerMansionBlockEntity) {
            val angry = be.getEvokerFromBlock() == ANGRY

            if (angry) {
                var target = 0f
                val player = Minecraft.getInstance().player
                if (player != null && !player.isInvisible) {
                    val x: Double
                    val z: Double
                    if (be.isVirtual) {
                        x = -4.0
                        z = -10.0
                    } else {
                        x = player.x
                        z = player.z
                    }
                    val dx: Double = x - (be.blockPos.x + 0.5)
                    val dz: Double = z - (be.blockPos.z + 0.5)
                    target = AngleHelper.deg(-Mth.atan2(dz, dx)) - 90
                }
                target = be.headAngle.getValue() + AngleHelper.getShortestAngleDiff(
                    be.headAngle.getValue().toDouble(),
                    target.toDouble()
                )
                be.headAngle.chase(target.toDouble(), .25, Chaser.exp(5.0))
                be.headAngle.tickChaser()
            }

            be.headAnimation.chase(if (angry) 0.0 else 1.0, 0.25, Chaser.exp(0.25))
            be.headAnimation.tickChaser()
        }
    }

    class EvokerMansionRenderState : BlockEntityRenderState() {
        var data: EvokerMansionRenderData? = null
    }

    class EvokerMansionRenderData : SubmitNodeCollector.CustomGeometryRenderer {
        val renderType: RenderType = RenderTypes.solidMovingBlock()

        var evoker: SuperByteBuffer? = null
        var headX: Float = 0f
        var headY: Float = 0f
        var headZ: Float = 0f
        var horizontalAngle: Float = 0f

        fun submit(pose: PoseStack, collector: SubmitNodeCollector) {
            collector.submitCustomGeometry(pose, renderType, this)
        }

        override fun render(pose: PoseStack.Pose, vertexConsumer: VertexConsumer) {
            evoker!!
                .translate(0.5, 0.0, 0.5)
                .scale(0.85f)
                .translate(-0.5, 0.0, -0.5)
                .translate(headX.toDouble(), headY.toDouble(), headZ.toDouble())
                .rotateCentered(horizontalAngle, Direction.UP)
                .light<SuperByteBuffer>(LightTexture.FULL_BRIGHT)
                .renderInto(pose, vertexConsumer)
        }
    }
}