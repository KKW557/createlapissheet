package icu.suc.createlapissheet.content.kinetics.fan

import icu.suc.createlapissheet.foundation.blockentity.behaviour.filtering.EnchantmentFilter
import net.minecraft.core.BlockPos

data class EnchantmentFilterSegment(
    var filter: EnchantmentFilter = EnchantmentFilter.EMPTY,
    var startOffset: Int = 0,
    var endOffset: Int = 0,
    var source: BlockPos? = null
)