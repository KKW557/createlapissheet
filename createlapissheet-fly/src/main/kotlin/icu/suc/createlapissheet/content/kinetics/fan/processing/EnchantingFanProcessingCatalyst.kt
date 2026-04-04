package icu.suc.createlapissheet.content.kinetics.fan.processing

import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level

interface EnchantingFanProcessingCatalyst {
    fun tryEnchant(cost: Int, level: Level, pos: BlockPos): Boolean

    companion object {
        @JvmStatic
        fun enchant(cost: Int, level: Level, pos: BlockPos): Boolean {
            val block = level.getBlockState(pos).block
            if (block is EnchantingFanProcessingCatalyst) {
                return block.tryEnchant(cost, level, pos)
            }
            val fluid = level.getFluidState(pos).type
            if (fluid is EnchantingFanProcessingCatalyst) {
                return fluid.tryEnchant(cost, level, pos)
            }
            return true
        }
    }
}