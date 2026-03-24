package icu.suc.createlapissheet.foundation.blockentity.behaviour.filtering

import net.minecraft.core.Direction

interface EnchantmentFiltering {
    fun getEnchantmentFilter(side: Direction): EnchantmentFilter

    fun getEnchantmentFilter(): EnchantmentFilter
}