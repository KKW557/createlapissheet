package icu.suc.createlapissheet.foundation.blockentity.behaviour.filtering

import com.zurrtum.create.AllItems
import com.zurrtum.create.foundation.blockEntity.SmartBlockEntity
import com.zurrtum.create.foundation.blockEntity.behaviour.filtering.ServerFilteringBehaviour
import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.ItemStack

class ServerEnchantmentFilteringBehaviour(be: SmartBlockEntity) : ServerFilteringBehaviour(be) {
    override fun isActive() = true

    override fun test(stack: ItemStack) = true

    override fun setFilter(stack: ItemStack) =
        if (stack.isEmpty ||
            stack.`is`(AllItems.ATTRIBUTE_FILTER) ||
            stack.has(DataComponents.STORED_ENCHANTMENTS) ||
            stack.has(DataComponents.ENCHANTMENTS)
        ) super.setFilter(stack) else false

    override fun getAmount() = 1

    fun toEnchantmentFilter() = EnchantmentFilter.create(filter)
}