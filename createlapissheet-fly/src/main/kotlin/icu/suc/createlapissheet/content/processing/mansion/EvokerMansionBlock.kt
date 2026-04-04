package icu.suc.createlapissheet.content.processing.mansion

import com.zurrtum.create.content.kinetics.fan.processing.FanProcessingType
import com.zurrtum.create.foundation.block.IBE
import icu.suc.createlapissheet.*
import icu.suc.createlapissheet.content.kinetics.fan.processing.EnchantingFanProcessingCatalyst
import icu.suc.createlapissheet.content.kinetics.fan.processing.IFanProcessingBlock
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.component.DataComponents
import net.minecraft.core.particles.ColorParticleOption
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.core.registries.Registries
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.util.RandomSource
import net.minecraft.util.StringRepresentable
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.monster.illager.SpellcasterIllager
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.raid.Raid
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect
import net.minecraft.world.item.consume_effects.ClearAllStatusEffectsConsumeEffect
import net.minecraft.world.item.consume_effects.RemoveStatusEffectsConsumeEffect
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EnchantingTableBlock
import net.minecraft.world.level.block.HorizontalDirectionalBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.block.state.properties.EnumProperty
import net.minecraft.world.level.pathfinder.PathComputationType
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext
import kotlin.math.max

class EvokerMansionBlock(properties: Properties) : HorizontalDirectionalBlock(properties),
    IBE<EvokerMansionBlockEntity>, IFanProcessingBlock, EnchantingFanProcessingCatalyst {

    init {
        registerDefaultState(defaultBlockState().setValue(EVOKER, Evoker.NONE).setValue(NAUSEA, false))
    }

    override fun codec() = CODEC

    override fun newBlockEntity(pos: BlockPos, state: BlockState) =
        if (getEvokerOf(state) == Evoker.NONE) null else super.newBlockEntity(pos, state)

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState {
        val stack: ItemStack = context.itemInHand
        val item = stack.item
        val defaultState = defaultBlockState()
        if (item !is EvokerMansionBlockItem) return defaultState
        return defaultState.setValue(EVOKER, item.evoker).setValue(FACING, context.horizontalDirection.opposite)
    }

    override fun getCloneItemStack(
        level: LevelReader,
        pos: BlockPos,
        state: BlockState,
        bl: Boolean
    ) = when (state.getValue(EVOKER)) {
        Evoker.NONE -> ItemStack(Items.EMPTY_EVOKER_MANSION)
        Evoker.ANGRY -> ItemStack(Items.EVOKER_MANSION)
        else -> ItemStack(Items.SAFE_EVOKER_MANSION)
    }

    override fun useItemOn(
        itemStack: ItemStack,
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        result: BlockHitResult
    ): InteractionResult {
        if (getEvokerOf(state) == Evoker.NONE) return super.useItemOn(
            itemStack,
            state,
            level,
            pos,
            player,
            hand,
            result
        )

        var r: InteractionResult? = null

        itemStack[DataComponents.CONSUMABLE]?.let { consumable ->
            var flagged = false
            var nausea = false
            for (effect in consumable.onConsumeEffects) {
                when (effect) {
                    is ClearAllStatusEffectsConsumeEffect -> {
                        flagged = true
                        nausea = false
                    }

                    is RemoveStatusEffectsConsumeEffect -> {
                        if (!effect.effects.any { it.`is`(Tags.MobEffect.NAUSEA) }) continue
                        flagged = true
                        nausea = false
                    }

                    is ApplyStatusEffectsConsumeEffect -> {
                        if (!effect.effects.any { it.effect.`is`(Tags.MobEffect.NAUSEA) }) continue
                        flagged = true
                        nausea = true
                    }
                }
            }
            if (!flagged) return@let
            if (isNauseaOf(state) == nausea) return@let
            if (level.isClientSide) {
                r = InteractionResult.SUCCESS
                return@let
            }
            level.setBlockAndUpdate(pos, state.setValue(NAUSEA, nausea))
            itemStack.copy().apply {
                itemStack.consume(1, player)
                player.setItemInHand(hand, itemStack.applyAfterUseComponentSideEffects(player, this))
            }
            if (nausea) Advancements.NAUSEA.trigger(player as ServerPlayer)
            r = InteractionResult.CONSUME
        }

        itemStack
            .takeIf { it.has(DataComponents.BANNER_PATTERNS) }
            ?.let {
                if (getEvokerOf(state) != Evoker.ANGRY) return@let
                if (!isOminousBanner(itemStack, level)) return@let
                playSound(level, pos)
                if (level.isClientSide) {
                    r = InteractionResult.SUCCESS
                    return@let
                }
                level.setBlockAndUpdate(pos, state.setValue(EVOKER, Evoker.WOLOLO))
                itemStack.consume(1, player)
                Advancements.SAFE_EVOKER_MANSION.trigger(player as ServerPlayer)
                r = InteractionResult.CONSUME
            }

        return r ?: super.useItemOn(itemStack, state, level, pos, player, hand, result)
    }

    override fun getShape(
        state: BlockState,
        getter: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ) = Shapes.EVOKER_MANSION_BLOCK_SHAPE

    override fun getCollisionShape(
        state: BlockState,
        getter: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ) =
        if (context == CollisionContext.empty()) Shapes.EVOKER_MANSION_BLOCK_SPECIAL_COLLISION_SHAPE else Shapes.EVOKER_MANSION_BLOCK_SHAPE

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        super.createBlockStateDefinition(builder)
        builder.add(EVOKER, NAUSEA, FACING)
    }

    override fun hasAnalogOutputSignal(blockState: BlockState) = true

    override fun getAnalogOutputSignal(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        direction: Direction
    ) = max(0, getEvokerOf(state).ordinal - 1)

    override fun isPathfindable(blockState: BlockState, pathComputationType: PathComputationType) = false

    override fun getBlockEntityClass() = EvokerMansionBlockEntity::class.java

    override fun getBlockEntityType() = BlockEntityTypes.EVOKER_MANSION

    override fun animateTick(state: BlockState, level: Level, pos: BlockPos, source: RandomSource) {
        super.animateTick(state, level, pos, source)

        val evoker = getEvokerOf(state)

        if (evoker == Evoker.ENCHANTING) {
            for (blockPos2 in EnchantingTableBlock.BOOKSHELF_OFFSETS) {
                if (source.nextInt(16) == 0 && EnchantingTableBlock.isValidBookShelf(level, pos, blockPos2)) {
                    level.addParticle(
                        ParticleTypes.ENCHANT,
                        pos.x.toDouble() + 0.5,
                        pos.y.toDouble() + 2.0,
                        pos.z.toDouble() + 0.5,
                        (blockPos2.x.toFloat() + source.nextFloat()).toDouble() - 0.5,
                        (blockPos2.y.toFloat() - source.nextFloat() - 1.0f).toDouble(),
                        (blockPos2.z.toFloat() + source.nextFloat()).toDouble() - 0.5
                    )
                }
            }
        } else if (evoker == Evoker.WOLOLO) {
            val color = SpellcasterIllager.IllagerSpell.WOLOLO.spellColor
            level.addParticle(
                ColorParticleOption.create(
                    ParticleTypes.ENTITY_EFFECT,
                    color[0].toFloat(),
                    color[1].toFloat(),
                    color[2].toFloat()
                ),
                pos.x + .5,
                pos.y + .5,
                pos.z + .5,
                source.nextDouble() - .5,
                source.nextDouble() - .5,
                source.nextDouble() - .5
            )
        }
    }

    override fun isValid(
        type: FanProcessingType,
        state: BlockState
    ) = when (type) {
        FanProcessingTypes.WOLOLO -> getEvokerOf(state) == Evoker.WOLOLO && !isNauseaOf(state)
        FanProcessingTypes.WOLOLO_NAUSEA -> getEvokerOf(state) == Evoker.WOLOLO && isNauseaOf(state)
        FanProcessingTypes.ENCHANTING -> getEvokerOf(state) == Evoker.ENCHANTING
        else -> false
    }

    override fun tryEnchant(cost: Int, level: Level, pos: BlockPos): Boolean {
        val b = (getBlockEntity(level, pos) ?: return false).tryEnchant(cost)
        if (b) level.playSound(
            null,
            pos,
            SoundEvents.ENCHANTMENT_TABLE_USE,
            SoundSource.BLOCKS,
            1.0f,
            level.random.nextFloat() * 0.1f + 0.9f
        )
        return b
    }

    companion object {
        @JvmField
        val CODEC = simpleCodec { properties: Properties -> EvokerMansionBlock(properties) }

        @JvmField
        val EVOKER = EnumProperty.create("evoker", Evoker::class.java)

        @JvmField
        val NAUSEA = BooleanProperty.create("nausea")

        @JvmStatic
        fun getLight(state: BlockState) = if (state.getValue(EVOKER) >= Evoker.WOLOLO) 7 else 0

        @JvmStatic
        fun getEvokerOf(state: BlockState) =
            if (state.hasProperty(EVOKER)) state.getValue(EVOKER) else Evoker.NONE

        @JvmStatic
        fun isNauseaOf(state: BlockState) =
            if (state.hasProperty(NAUSEA)) state.getValue(NAUSEA) else false

        @JvmStatic
        fun isOminousBanner(itemStack: ItemStack, level: Level): Boolean {
            val ominousBanner = Raid.getOminousBannerInstance(level.holderLookup(Registries.BANNER_PATTERN))
            if (itemStack.item != ominousBanner.item) return false
            if (itemStack[DataComponents.BANNER_PATTERNS]?.let { it == ominousBanner[DataComponents.BANNER_PATTERNS] } != true) return false
            return true
        }

        @JvmStatic
        fun playSound(level: Level, pos: BlockPos) =
            level.playSound(null, pos, SoundEvents.EVOKER_CELEBRATE, SoundSource.HOSTILE, .25f, 1f)
    }

    enum class Evoker(val id: String) : StringRepresentable {
        NONE("none"),
        ANGRY("angry"),
        WOLOLO("wololo"),
        ENCHANTING("enchanting");

        companion object {
            @Suppress("unused")
            @JvmField
            val CODEC = StringRepresentable.fromEnum { entries.toTypedArray() }

            @Suppress("unused")
            @JvmStatic
            fun byIndex(index: Int) = Evoker.entries[index]
        }

        override fun getSerializedName() = id
    }
}