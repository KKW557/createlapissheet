package icu.suc.createlapissheet.content.kinetics.fan.processing

import com.zurrtum.create.content.kinetics.fan.processing.FanProcessingType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.FluidState

interface IFanProcessing<T> {
    fun isValid(type: FanProcessingType, state: T): Boolean
}

interface IFanProcessingBlock : IFanProcessing<BlockState>

interface IFanProcessingFluid : IFanProcessing<FluidState>