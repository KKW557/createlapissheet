package icu.suc.createlapissheet.content.processing.mansion

import com.zurrtum.create.api.behaviour.BlockEntityBehaviour
import com.zurrtum.create.catnip.animation.LerpedFloat
import com.zurrtum.create.catnip.math.AngleHelper
import com.zurrtum.create.foundation.blockEntity.SmartBlockEntity
import icu.suc.createlapissheet.BlockEntityTypes
import icu.suc.createlapissheet.Handle
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.block.HorizontalDirectionalBlock
import net.minecraft.world.level.block.state.BlockState

class EvokerMansionBlockEntity(pos: BlockPos, state: BlockState) :
    SmartBlockEntity(BlockEntityTypes.EVOKER, pos, state) {

    var headAnimation: LerpedFloat = LerpedFloat.linear()
    var headAngle: LerpedFloat = LerpedFloat.angular()

    init {
        headAngle.startWithValue(
            ((AngleHelper.horizontalAngle(
                state.getValueOrElse(
                    HorizontalDirectionalBlock.FACING,
                    Direction.SOUTH
                )
            ) + 180) % 360).toDouble()
        )
    }

    override fun tick() {
        super.tick()

        level?.let {
            if (!it.isClientSide) return

            Handle.INSTANCE.tickEvokerMansionAnimation(this)
        }
    }

    override fun addBehaviours(behaviours: List<BlockEntityBehaviour<*>?>?) {}

    fun getEvokerFromBlock() = EvokerMansionBlock.getEvokerOf(blockState)

    fun getNauseaFromBlock() = EvokerMansionBlock.getNauseaOf(blockState)
}