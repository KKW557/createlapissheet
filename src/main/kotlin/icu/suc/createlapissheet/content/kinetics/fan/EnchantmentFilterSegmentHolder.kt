package icu.suc.createlapissheet.content.kinetics.fan

interface EnchantmentFilterSegmentHolder {
    val enchantmentFilterSegments: MutableList<EnchantmentFilterSegment>

    fun getSegmentAt(offset: Float): EnchantmentFilterSegment?
}