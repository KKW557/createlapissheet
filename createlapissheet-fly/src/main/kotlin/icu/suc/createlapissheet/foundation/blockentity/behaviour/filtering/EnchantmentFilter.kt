package icu.suc.createlapissheet.foundation.blockentity.behaviour.filtering

import com.zurrtum.create.content.logistics.filter.FilterItemStack
import com.zurrtum.create.content.logistics.item.filter.attribute.attributes.EnchantAttribute
import com.zurrtum.create.infrastructure.component.AttributeFilterWhitelistMode
import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.enchantment.Enchantment
import net.minecraft.world.item.enchantment.ItemEnchantments

data class EnchantmentFilter(
    val others: List<EnchantmentFilter>,
    val tests: List<Pair<Enchantment, Boolean>>,
    val mode: AttributeFilterWhitelistMode
) : (List<Enchantment>) -> List<Enchantment> {
    constructor(stack: FilterItemStack.AttributeFilterItemStack) : this(
        emptyList(),
        stack.attributeTests.mapNotNull {
            val attribute = it.first
            if (attribute is EnchantAttribute) {
                val enchantment = attribute.enchantment?.value() ?: return@mapNotNull null
                enchantment to it.second
            } else null
        },
        stack.whitelistMode
    )

    constructor(data: ItemEnchantments) : this(
        emptyList(),
        data.entrySet().map { it.key.value() to false },
        AttributeFilterWhitelistMode.WHITELIST_CONJ
    )

    override fun invoke(enchantments: List<Enchantment>): List<Enchantment> {
        if (enchantments.isEmpty()) return emptyList()

        var enchantments = enchantments
        for (other in others) {
            enchantments = other(enchantments)
            if (enchantments.isEmpty()) return emptyList()
        }

        if (tests.isEmpty()) return enchantments

        return when (mode) {
            AttributeFilterWhitelistMode.WHITELIST_DISJ ->
                enchantments.filter { tests.any { (test, inverted) -> (it == test) != inverted } }

            AttributeFilterWhitelistMode.WHITELIST_CONJ -> {
                val result = tests.mapNotNull { (test, inverted) -> enchantments.find { (it == test) != inverted } }
                if (result.size == tests.size) result else emptyList()
            }

            AttributeFilterWhitelistMode.BLACKLIST ->
                enchantments.filter { tests.none { (test, inverted) -> (it == test) != inverted } }
        }
    }

    fun isEmpty() = this == EMPTY

    operator fun EnchantmentFilter.plus(other: EnchantmentFilter): EnchantmentFilter =
        if (other.isEmpty()) this else EnchantmentFilter(others + other, tests, mode)

    companion object {
        @JvmField
        val EMPTY = EnchantmentFilter(emptyList(), emptyList(), AttributeFilterWhitelistMode.WHITELIST_DISJ)

        @JvmStatic
        fun create(stack: FilterItemStack): EnchantmentFilter {
            if (stack is FilterItemStack.AttributeFilterItemStack) return EnchantmentFilter(stack)
            val item = stack.item()
            item.get(DataComponents.STORED_ENCHANTMENTS)?.let { return EnchantmentFilter(it) }
            item.get(DataComponents.ENCHANTMENTS)?.let { return EnchantmentFilter(it) }
            return EMPTY
        }
    }
}