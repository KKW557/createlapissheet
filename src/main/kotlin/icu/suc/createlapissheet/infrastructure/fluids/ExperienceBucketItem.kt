package icu.suc.createlapissheet.infrastructure.fluids

import net.minecraft.server.level.ServerLevel
import net.minecraft.stats.Stats
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.ExperienceOrb
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.BucketItem
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemUtils
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.Level
import net.minecraft.world.phys.HitResult

class ExperienceBucketItem(properties: Properties) : Item(properties) {
    override fun use(level: Level, player: Player, hand: InteractionHand): InteractionResult {
        val stack = player.getItemInHand(hand)
        val result = getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE)
        val type = result.type
        if (type == HitResult.Type.MISS) {
            return InteractionResult.PASS
        } else if (type != HitResult.Type.BLOCK) {
            return InteractionResult.PASS
        } else {
            val pos = result.blockPos
            val direction = result.direction
            if (level.mayInteract(player, pos) && player.mayUseItemAt(pos.relative(direction), direction, stack)) {
                if (level is ServerLevel) ExperienceOrb.awardWithDirection(level, result.location, result.direction.unitVec3, 50)
                player.awardStat(Stats.ITEM_USED.get(this))
                return InteractionResult.SUCCESS.heldItemTransformedTo(
                    ItemUtils.createFilledResult(
                        stack,
                        player,
                        BucketItem.getEmptySuccessItem(stack, player)
                    )
                )
            }
        }
        return InteractionResult.FAIL
    }
}