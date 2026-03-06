package icu.suc.createlapissheet.block.entity.behaviour.filtering

import com.zurrtum.create.content.logistics.filter.AttributeFilterItem
import com.zurrtum.create.foundation.blockEntity.SmartBlockEntity
import com.zurrtum.create.foundation.blockEntity.behaviour.filtering.ServerFilteringBehaviour
import net.minecraft.world.item.ItemStack

class EnchantmentFilteringBehaviour(be: SmartBlockEntity) : ServerFilteringBehaviour(be) {
    override fun isActive() = true

    override fun test(stack: ItemStack?) = true

    override fun setFilter(stack: ItemStack?): Boolean {
        if (stack == null) return false
        if (stack.item !is AttributeFilterItem) return false

        return super.setFilter(stack)
    }

    override fun getAmount() = 1
}