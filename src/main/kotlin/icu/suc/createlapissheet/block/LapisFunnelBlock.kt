package icu.suc.createlapissheet.block

import com.zurrtum.create.content.logistics.funnel.BeltFunnelBlock
import com.zurrtum.create.content.logistics.funnel.FunnelBlock
import icu.suc.createlapissheet.Blocks
import net.minecraft.core.BlockPos
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.state.BlockState

class LapisFunnelBlock(properties: Properties) : FunnelBlock(properties) {

    override fun getEquivalentBeltFunnel(
        world: BlockGetter?,
        pos: BlockPos?,
        state: BlockState?
    ): BlockState {
        val facing = getFunnelFacing(state)!!
        return Blocks.LAPIS_BELT_FUNNEL.defaultBlockState()
            .setValue(BeltFunnelBlock.HORIZONTAL_FACING, facing)
            .setValue(POWERED, state!!.getValue(POWERED))
    }
}