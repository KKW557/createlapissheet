package icu.suc.createlapissheet.foundation.blockentity.behaviour.filtering

import com.zurrtum.create.AllItems
import com.zurrtum.create.content.logistics.filter.FilterItemStack
import com.zurrtum.create.foundation.blockEntity.SmartBlockEntity
import com.zurrtum.create.foundation.blockEntity.behaviour.filtering.ServerFilteringBehaviour
import net.minecraft.world.item.ItemStack

class ServerEnchantmentFilteringBehaviour(be: SmartBlockEntity) : ServerFilteringBehaviour(be) {
    override fun isActive() = true

    override fun test(stack: ItemStack) = true

    override fun setFilter(stack: ItemStack) =
        if (stack.`is`(AllItems.ATTRIBUTE_FILTER)) super.setFilter(stack) else false

    override fun getAmount() = 1

    fun toEnchantmentFilter() = (filter as? FilterItemStack.AttributeFilterItemStack)?.let { EnchantmentFilter(it) } ?: EnchantmentFilter.EMPTY
}