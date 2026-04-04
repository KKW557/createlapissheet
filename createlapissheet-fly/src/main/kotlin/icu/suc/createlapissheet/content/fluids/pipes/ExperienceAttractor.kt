package icu.suc.createlapissheet.content.fluids.pipes

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.RandomSource
import net.minecraft.world.entity.ExperienceOrb
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.IntegerProperty

class ExperienceAttractor(properties: Properties) : Block(properties) {
    init {
        registerDefaultState(defaultBlockState().setValue(EXPERIENCE, 0))
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        super.createBlockStateDefinition(builder)
        builder.add(EXPERIENCE)
    }

    override fun destroy(level: LevelAccessor, pos: BlockPos, state: BlockState) {
        super.destroy(level, pos, state)
        if (level is ServerLevel) {
            val xp = state.getValueOrElse(EXPERIENCE, 0)
            if (xp == 0) return
            ExperienceOrb.award(level, pos.center, xp)
        }
    }

    companion object {
        @JvmField
        val EXPERIENCE = IntegerProperty.create("experience", 0, 50)
    }
}