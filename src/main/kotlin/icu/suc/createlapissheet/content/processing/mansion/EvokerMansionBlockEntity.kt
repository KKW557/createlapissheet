package icu.suc.createlapissheet.content.processing.mansion

import com.zurrtum.create.AllItems
import com.zurrtum.create.api.behaviour.BlockEntityBehaviour
import com.zurrtum.create.catnip.animation.LerpedFloat
import com.zurrtum.create.catnip.math.AngleHelper
import com.zurrtum.create.catnip.math.BlockFace
import com.zurrtum.create.foundation.blockEntity.SmartBlockEntity
import com.zurrtum.create.foundation.blockEntity.behaviour.inventory.InvManipulationBehaviour
import com.zurrtum.create.foundation.blockEntity.behaviour.inventory.VersionedInventoryTrackerBehaviour
import icu.suc.createlapissheet.BlockEntityTypes
import icu.suc.createlapissheet.Handle
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.HorizontalDirectionalBlock
import net.minecraft.world.level.block.state.BlockState

class EvokerMansionBlockEntity(pos: BlockPos, state: BlockState) :
    SmartBlockEntity(BlockEntityTypes.EVOKER, pos, state) {

    var headAnimation: LerpedFloat = LerpedFloat.linear()
    var headAngle: LerpedFloat = LerpedFloat.angular()

    init {
        headAngle.startWithValue(
            ((AngleHelper.horizontalAngle(
                state.getValueOrElse(
                    HorizontalDirectionalBlock.FACING,
                    Direction.SOUTH
                )
            ) + 180) % 360).toDouble()
        )
        setLazyTickRate(10);
    }

    override fun tick() {
        super.tick()

        val level = level ?: return
        if (!level.isClientSide) return
        Handle.INSTANCE.tickEvokerMansionAnimation(this)
    }

    lateinit var invVersionTracker: VersionedInventoryTrackerBehaviour
    lateinit var observedInventory: InvManipulationBehaviour

    override fun addBehaviours(behaviours: MutableList<BlockEntityBehaviour<*>>) {
        invVersionTracker = VersionedInventoryTrackerBehaviour(this)
        observedInventory =
            InvManipulationBehaviour(this) { _, pos, _ -> BlockFace(pos, Direction.DOWN) }.bypassSidedness()
        behaviours.add(invVersionTracker)
        behaviours.add(observedInventory)
    }

    override fun lazyTick() {
        super.lazyTick()
        val level = level ?: return
        if (level.isClientSide) return
        updateCurrentLevel(level)
    }

    fun updateCurrentLevel(level: Level) {
        if (getEvokerFromBlock() != EvokerMansionBlock.Evoker.CASTING) return

        val enchanting = isEnchantingFromBlock()
        val pos = blockPos
        val state = blockState

        if (isNauseaFromBlock()) {
            if (enchanting) {
                level.setBlock(pos, state.setValue(EvokerMansionBlock.ENCHANTING, false), Block.UPDATE_CLIENTS)
            }
            return
        }

        if (hasRequired()) {
            if (enchanting) return
            level.setBlock(pos, state.setValue(EvokerMansionBlock.ENCHANTING, true), Block.UPDATE_CLIENTS)
        } else if (enchanting) {
            level.setBlock(pos, state.setValue(EvokerMansionBlock.ENCHANTING, false), Block.UPDATE_CLIENTS)
        }
    }

    fun hasRequired(): Boolean {
        observedInventory.findNewCapability()

        if (!observedInventory.hasInventory()) return false

        val inventory = observedInventory.inventory ?: return false

        if (invVersionTracker.stillWaiting(inventory)) return false

        invVersionTracker.awaitNewVersion(inventory)

        var hasLapis = false
        var hasExp = false

        for (i in 0 until inventory.containerSize) {
            val stack = inventory.getItem(i)
            if (stack.isEmpty) continue

            when (stack.item) {
                Items.LAPIS_LAZULI -> hasLapis = true
                AllItems.EXP_NUGGET -> hasExp = true
            }

            if (hasLapis && hasExp) return true
        }

        return false
    }

    fun getEvokerFromBlock() = EvokerMansionBlock.getEvokerOf(blockState)

    fun isNauseaFromBlock() = EvokerMansionBlock.isNauseaOf(blockState)

    fun isEnchantingFromBlock() = EvokerMansionBlock.isEnchantingOf(blockState)
}