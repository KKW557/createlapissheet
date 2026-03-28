package icu.suc.createlapissheet.content.kinetics.fan.processing

import com.zurrtum.create.content.kinetics.fan.processing.FanProcessingType
import net.minecraft.core.BlockPos
import net.minecraft.tags.TagKey
import net.minecraft.util.RandomSource
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.material.Fluid

abstract class EvokerFanProcessingType(val fluid: TagKey<Fluid>, val block: TagKey<Block>, val color: Int) :
    FanProcessingType {
    override fun isValidAt(level: Level, pos: BlockPos): Boolean {
        level.getFluidState(pos).let {
            if (!it.`is`(fluid)) return@let
            val type = it.type
            if (type !is IFanProcessingFluid) return true
            if (type.isValid(this, it)) return true
        }
        level.getBlockState(pos).let {
            if (!it.`is`(block)) return@let
            val type = it.block
            if (type !is IFanProcessingBlock) return true
            if (type.isValid(this, it)) return true
        }
        return false
    }

    override fun morphAirFlow(access: FanProcessingType.AirFlowParticleAccess, random: RandomSource) {
        access.setColor(color)
        access.setAlpha(.5f)
    }
}