package icu.suc.createlapissheet.item

import com.zurrtum.create.catnip.math.VecHelper
import icu.suc.createlapissheet.Blocks
import icu.suc.createlapissheet.Items
import icu.suc.createlapissheet.LOGGER
import icu.suc.createlapissheet.Tags
import icu.suc.createlapissheet.block.EvokerMansionBlock
import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.util.ProblemReporter
import net.minecraft.util.random.Weighted
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.SpawnData
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BannerBlockEntity
import net.minecraft.world.level.block.entity.SpawnerBlockEntity
import net.minecraft.world.level.storage.TagValueInput
import net.minecraft.world.phys.Vec3

class EvokerMansionBlockItem(block: Block, properties: Properties, val evoker: EvokerMansionBlock.Evoker) :
    BlockItem(block, properties) {
    override fun registerBlocks(map: Map<Block, Item>, item: Item) {
        if (isEmpty()) return
        super.registerBlocks(map, item)
    }

    override fun useOn(context: UseOnContext): InteractionResult {
        val level = context.level
        val pos = context.clickedPos
        val be = level.getBlockEntity(pos)

        val player = context.player

        return when (evoker) {
            EvokerMansionBlock.Evoker.NONE -> {
                if (be !is SpawnerBlockEntity) return super.useOn(context)

                val spawner = be.spawner
                var list = spawner.spawnPotentials.unwrap().map(Weighted<SpawnData>::value)

                if (list.isEmpty()) {
                    list = mutableListOf()
                    spawner.nextSpawnData?.let { list.add(it) }
                }

                ProblemReporter.ScopedCollector(be.problemPath(), LOGGER).use { logging ->
                    for (data in list) {
                        val readView = TagValueInput.create(logging, level.registryAccess(), data.entityToSpawn())

                        val entityType = EntityType.by(readView).orElse(null) ?: continue
                        if (!entityType.`is`(Tags.EntityType.EVOKER_MANSION_CAPTURABLE)) continue

                        spawnCaptureEffects(level, VecHelper.getCenterOf(pos))
                        if (level.isClientSide || player == null) return@use InteractionResult.SUCCESS

                        giveItemTo(player, Items.EVOKER_MANSION.defaultInstance, context.itemInHand, context.hand)
                        return@use InteractionResult.CONSUME
                    }

                    return@use super.useOn(context)
                }
            }

            EvokerMansionBlock.Evoker.ANGRY -> {
                if (be !is BannerBlockEntity) return super.useOn(context)

                if (!EvokerMansionBlock.isOminousBanner(be.item, level)) return super.useOn(context)

                EvokerMansionBlock.playSound(level, pos)
                if (level.isClientSide || player == null) return InteractionResult.SUCCESS

                level.destroyBlock(pos, false)
                giveItemTo(player, Items.SAFE_EVOKER_MANSION.defaultInstance, context.itemInHand, context.hand)
                InteractionResult.CONSUME
            }

            EvokerMansionBlock.Evoker.CASTING -> super.useOn(context)
        }
    }

    override fun interactLivingEntity(
        held: ItemStack,
        player: Player,
        entity: LivingEntity,
        hand: InteractionHand
    ): InteractionResult {
        if (!isEmpty()) return super.interactLivingEntity(held, player, entity, hand)

        if (!entity.type.`is`(Tags.EntityType.EVOKER_MANSION_CAPTURABLE)) return super.interactLivingEntity(held, player, entity, hand)

        val level = player.level()
        spawnCaptureEffects(level, entity.position())

        if (level.isClientSide) return InteractionResult.FAIL

        giveItemTo(player, Items.EVOKER_MANSION.defaultInstance, held, hand)
        entity.discard()
        return InteractionResult.FAIL
    }

    fun isEmpty() = evoker == EvokerMansionBlock.Evoker.NONE

    companion object {
        @JvmStatic
        fun empty(properties: Properties) =
            EvokerMansionBlockItem(Blocks.EVOKER_MANSION, properties, EvokerMansionBlock.Evoker.NONE)

        @JvmStatic
        fun withEvoker(block: Block, properties: Properties) =
            EvokerMansionBlockItem(block, properties, EvokerMansionBlock.Evoker.ANGRY)

        @JvmStatic
        fun safe(properties: Properties) =
            EvokerMansionBlockItem(Blocks.EVOKER_MANSION, properties, EvokerMansionBlock.Evoker.CASTING)

        @JvmStatic
        fun giveItemTo(player: Player, filled: ItemStack, held: ItemStack, hand: InteractionHand) {
            held.consume(1, player)

            if (held.isEmpty) {
                player.setItemInHand(hand, filled)
                return
            }

            player.inventory.placeItemBackInInventory(filled)
        }

        @JvmStatic
        fun spawnCaptureEffects(level: Level, pos: Vec3) {
            if (level.isClientSide) {
                repeat(16) {
                    val motion = VecHelper.offsetRandomly(Vec3.ZERO, level.random, .625f)
                    level.addParticle(ParticleTypes.TOTEM_OF_UNDYING, pos.x, pos.y, pos.z, motion.x, motion.y, motion.z)
                }
            }

            level.playSound(null, BlockPos.containing(pos), SoundEvents.TOTEM_USE, SoundSource.HOSTILE, .25f, .75f)
        }
    }
}