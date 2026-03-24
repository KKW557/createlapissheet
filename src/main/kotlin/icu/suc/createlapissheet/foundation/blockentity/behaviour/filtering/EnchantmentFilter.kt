package icu.suc.createlapissheet.foundation.blockentity.behaviour.filtering

import com.zurrtum.create.content.logistics.filter.FilterItemStack
import com.zurrtum.create.content.logistics.item.filter.attribute.attributes.EnchantAttribute
import com.zurrtum.create.infrastructure.component.AttributeFilterWhitelistMode
import net.minecraft.core.Holder
import net.minecraft.world.item.enchantment.Enchantment

data class EnchantmentFilter(
    val others: List<EnchantmentFilter>,
    val tests: List<Pair<EnchantAttribute, Boolean>>,
    val mode: AttributeFilterWhitelistMode
) : (Holder<Enchantment>) -> Boolean {
    constructor(filter: FilterItemStack.AttributeFilterItemStack) : this(
        listOf(),
        filter.attributeTests.mapNotNull {
            val attribute = it.first
            if (attribute is EnchantAttribute) attribute to it.second else null
        },
        filter.whitelistMode
    )

    override fun invoke(enchantment: Holder<Enchantment>): Boolean {
        for (other in others) {
            if (!other(enchantment)) return false
        }

        if (tests.isEmpty()) return true

        for (test in tests) {
            val enchant = test.first
            val inverted = test.second

            val matches = enchant.enchantment == enchantment != inverted

            return if (matches) {
                when (mode) {
                    AttributeFilterWhitelistMode.WHITELIST_DISJ -> true
                    AttributeFilterWhitelistMode.WHITELIST_CONJ -> continue
                    AttributeFilterWhitelistMode.BLACKLIST -> false
                }
            } else {
                when (mode) {
                    AttributeFilterWhitelistMode.WHITELIST_DISJ, AttributeFilterWhitelistMode.BLACKLIST -> continue
                    AttributeFilterWhitelistMode.WHITELIST_CONJ -> false
                }
            }
        }

        return when (mode) {
            AttributeFilterWhitelistMode.WHITELIST_DISJ -> false
            AttributeFilterWhitelistMode.WHITELIST_CONJ, AttributeFilterWhitelistMode.BLACKLIST -> true
        }
    }

    fun isEmpty() = this == EMPTY

    operator fun EnchantmentFilter.plus(other: EnchantmentFilter): EnchantmentFilter =
        if (other.isEmpty()) this else EnchantmentFilter(others + other, tests, mode)

    companion object {
        @JvmField
        val EMPTY = EnchantmentFilter(listOf(), listOf(), AttributeFilterWhitelistMode.WHITELIST_DISJ)
    }
}