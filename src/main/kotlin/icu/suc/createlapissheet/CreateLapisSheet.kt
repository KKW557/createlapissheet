@file:JvmName("CreateLapisSheet")

package icu.suc.createlapissheet

import com.mojang.logging.LogUtils
import com.zurrtum.create.AllBlockEntityTypes
import com.zurrtum.create.AllBlocks
import com.zurrtum.create.AllRecipeSets
import com.zurrtum.create.AllShapes
import com.zurrtum.create.api.registry.CreateRegistries
import com.zurrtum.create.content.contraptions.actors.seat.SeatBlock
import com.zurrtum.create.content.decoration.encasing.CasingBlock
import com.zurrtum.create.content.kinetics.fan.processing.FanProcessingType
import com.zurrtum.create.content.logistics.funnel.BeltFunnelBlock
import com.zurrtum.create.content.logistics.funnel.FunnelItem
import icu.suc.createlapissheet.content.kinetics.fan.processing.WololoFanProcessingType
import icu.suc.createlapissheet.content.kinetics.fan.processing.WololoRecipe
import icu.suc.createlapissheet.content.logistics.funnel.LapisFunnelBlock
import icu.suc.createlapissheet.content.processing.mansion.EvokerMansionBlock
import icu.suc.createlapissheet.content.processing.mansion.EvokerMansionBlockEntity
import icu.suc.createlapissheet.content.processing.mansion.EvokerMansionBlockItem
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.tags.TagKey
import net.minecraft.world.item.*
import net.minecraft.world.item.crafting.*
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.material.MapColor
import net.minecraft.world.phys.shapes.VoxelShape
import org.slf4j.Logger
import java.util.*
import java.util.function.Function

typealias MinecraftBlocks = net.minecraft.world.level.block.Blocks

const val MOD_ID = "createlapissheet"
val LOGGER: Logger = LogUtils.getLogger()

fun identifier(path: String) = Identifier.fromNamespaceAndPath(MOD_ID, path)

object Blocks {
    @JvmField
    val LAPIS_CASING = register(
        "lapis_casing",
        ::CasingBlock,
        BlockBehaviour.Properties.ofFullCopy(MinecraftBlocks.LAPIS_BLOCK).mapColor(MapColor.TERRACOTTA_BLUE)
            .sound(SoundType.NETHERITE_BLOCK)
    )

    @JvmField
    val LAPIS_FUNNEL = register(
        "lapis_funnel",
        ::LapisFunnelBlock,
        BlockBehaviour.Properties.ofFullCopy(MinecraftBlocks.LAPIS_BLOCK).mapColor(MapColor.TERRACOTTA_BLUE)
    )

    @JvmField
    val LAPIS_BELT_FUNNEL = register(
        "lapis_belt_funnel",
        { properties -> BeltFunnelBlock(LAPIS_FUNNEL, properties) },
        BlockBehaviour.Properties.ofFullCopy(MinecraftBlocks.LAPIS_BLOCK).mapColor(MapColor.TERRACOTTA_BLUE)
    )

    @JvmField
    val EVOKER_MANSION = register(
        "evoker_mansion",
        ::EvokerMansionBlock,
        BlockBehaviour.Properties.ofFullCopy(MinecraftBlocks.LAPIS_BLOCK).mapColor(MapColor.COLOR_GRAY)
            .lightLevel(EvokerMansionBlock::getLight)
    )

    @JvmField
    val WHITE_SEAT_OF_UNDYING = register(
        "white_seat_of_undying",
        { properties -> SeatBlock(properties, DyeColor.WHITE) },
        BlockBehaviour.Properties.ofFullCopy(AllBlocks.WHITE_SEAT)
    )

    @JvmField
    val ORANGE_SEAT_OF_UNDYING = register(
        "orange_seat_of_undying",
        { properties -> SeatBlock(properties, DyeColor.ORANGE) },
        BlockBehaviour.Properties.ofFullCopy(AllBlocks.ORANGE_SEAT)
    )

    @JvmField
    val MAGENTA_SEAT_OF_UNDYING = register(
        "magenta_seat_of_undying",
        { properties -> SeatBlock(properties, DyeColor.MAGENTA) },
        BlockBehaviour.Properties.ofFullCopy(AllBlocks.MAGENTA_SEAT)
    )

    @JvmField
    val LIGHT_BLUE_SEAT_OF_UNDYING = register(
        "light_blue_seat_of_undying",
        { properties -> SeatBlock(properties, DyeColor.LIGHT_BLUE) },
        BlockBehaviour.Properties.ofFullCopy(AllBlocks.LIGHT_BLUE_SEAT)
    )

    @JvmField
    val YELLOW_SEAT_OF_UNDYING = register(
        "yellow_seat_of_undying",
        { properties -> SeatBlock(properties, DyeColor.YELLOW) },
        BlockBehaviour.Properties.ofFullCopy(AllBlocks.YELLOW_SEAT)
    )

    @JvmField
    val LIME_SEAT_OF_UNDYING = register(
        "lime_seat_of_undying",
        { properties -> SeatBlock(properties, DyeColor.LIME) },
        BlockBehaviour.Properties.ofFullCopy(AllBlocks.LIME_SEAT)
    )

    @JvmField
    val PINK_SEAT_OF_UNDYING = register(
        "pink_seat_of_undying",
        { properties -> SeatBlock(properties, DyeColor.PINK) },
        BlockBehaviour.Properties.ofFullCopy(AllBlocks.PINK_SEAT)
    )

    @JvmField
    val GRAY_SEAT_OF_UNDYING = register(
        "gray_seat_of_undying",
        { properties -> SeatBlock(properties, DyeColor.GRAY) },
        BlockBehaviour.Properties.ofFullCopy(AllBlocks.GRAY_SEAT)
    )

    @JvmField
    val LIGHT_GRAY_SEAT_OF_UNDYING = register(
        "light_gray_seat_of_undying",
        { properties -> SeatBlock(properties, DyeColor.LIGHT_GRAY) },
        BlockBehaviour.Properties.ofFullCopy(AllBlocks.LIGHT_GRAY_SEAT)
    )

    @JvmField
    val CYAN_SEAT_OF_UNDYING = register(
        "cyan_seat_of_undying",
        { properties -> SeatBlock(properties, DyeColor.CYAN) },
        BlockBehaviour.Properties.ofFullCopy(AllBlocks.CYAN_SEAT)
    )

    @JvmField
    val PURPLE_SEAT_OF_UNDYING = register(
        "purple_seat_of_undying",
        { properties -> SeatBlock(properties, DyeColor.PURPLE) },
        BlockBehaviour.Properties.ofFullCopy(AllBlocks.PURPLE_SEAT)
    )

    @JvmField
    val BLUE_SEAT_OF_UNDYING = register(
        "blue_seat_of_undying",
        { properties -> SeatBlock(properties, DyeColor.BLUE) },
        BlockBehaviour.Properties.ofFullCopy(AllBlocks.BLUE_SEAT)
    )

    @JvmField
    val BROWN_SEAT_OF_UNDYING = register(
        "brown_seat_of_undying",
        { properties -> SeatBlock(properties, DyeColor.BROWN) },
        BlockBehaviour.Properties.ofFullCopy(AllBlocks.BROWN_SEAT)
    )

    @JvmField
    val GREEN_SEAT_OF_UNDYING = register(
        "green_seat_of_undying",
        { properties -> SeatBlock(properties, DyeColor.GREEN) },
        BlockBehaviour.Properties.ofFullCopy(AllBlocks.GREEN_SEAT)
    )

    @JvmField
    val RED_SEAT_OF_UNDYING = register(
        "red_seat_of_undying",
        { properties -> SeatBlock(properties, DyeColor.RED) },
        BlockBehaviour.Properties.ofFullCopy(AllBlocks.RED_SEAT)
    )

    @JvmField
    val BLACK_SEAT_OF_UNDYING = register(
        "black_seat_of_undying",
        { properties -> SeatBlock(properties, DyeColor.BLACK) },
        BlockBehaviour.Properties.ofFullCopy(AllBlocks.BLACK_SEAT)
    )

    @JvmStatic
    fun <T : Block> register(
        id: String,
        function: (BlockBehaviour.Properties) -> T,
        properties: BlockBehaviour.Properties
    ) = register(identifier(id), function, properties)

    @JvmStatic
    fun <T : Block> register(
        id: Identifier,
        function: (BlockBehaviour.Properties) -> T,
        properties: BlockBehaviour.Properties
    ): T {
        val key = ResourceKey.create(Registries.BLOCK, id)
        return Registry.register(
            BuiltInRegistries.BLOCK,
            key,
            function(properties.setId(key))
        )
    }

    @JvmStatic
    fun register() {
    }
}

object Items {
    @JvmField
    val LAPIS_CASING = register(Blocks.LAPIS_CASING)

    @JvmField
    val LAPIS_FUNNEL = register(Blocks.LAPIS_FUNNEL, ::FunnelItem)

    @JvmField
    val LAPIS_SHEET = register("lapis_sheet")

    @JvmField
    val INTEGRATED_CIRCUIT = register("integrated_circuit")

    @JvmField
    val SAFE_EVOKER_MANSION = register("safe_evoker_mansion", EvokerMansionBlockItem::safe)

    @JvmField
    val EVOKER_MANSION = register(Blocks.EVOKER_MANSION, EvokerMansionBlockItem::withEvoker)

    @JvmField
    val EMPTY_EVOKER_MANSION = register("empty_evoker_mansion", EvokerMansionBlockItem::empty)

    @JvmField
    val WHITE_SEAT_OF_UNDYING = register(Blocks.WHITE_SEAT_OF_UNDYING)

    @JvmField
    val ORANGE_SEAT_OF_UNDYING = register(Blocks.ORANGE_SEAT_OF_UNDYING)

    @JvmField
    val MAGENTA_SEAT_OF_UNDYING = register(Blocks.MAGENTA_SEAT_OF_UNDYING)

    @JvmField
    val LIGHT_BLUE_SEAT_OF_UNDYING = register(Blocks.LIGHT_BLUE_SEAT_OF_UNDYING)

    @JvmField
    val YELLOW_SEAT_OF_UNDYING = register(Blocks.YELLOW_SEAT_OF_UNDYING)

    @JvmField
    val LIME_SEAT_OF_UNDYING = register(Blocks.LIME_SEAT_OF_UNDYING)

    @JvmField
    val PINK_SEAT_OF_UNDYING = register(Blocks.PINK_SEAT_OF_UNDYING)

    @JvmField
    val GRAY_SEAT_OF_UNDYING = register(Blocks.GRAY_SEAT_OF_UNDYING)

    @JvmField
    val LIGHT_GRAY_SEAT_OF_UNDYING = register(Blocks.LIGHT_GRAY_SEAT_OF_UNDYING)

    @JvmField
    val CYAN_SEAT_OF_UNDYING = register(Blocks.CYAN_SEAT_OF_UNDYING)

    @JvmField
    val PURPLE_SEAT_OF_UNDYING = register(Blocks.PURPLE_SEAT_OF_UNDYING)

    @JvmField
    val BLUE_SEAT_OF_UNDYING = register(Blocks.BLUE_SEAT_OF_UNDYING)

    @JvmField
    val BROWN_SEAT_OF_UNDYING = register(Blocks.BROWN_SEAT_OF_UNDYING)

    @JvmField
    val GREEN_SEAT_OF_UNDYING = register(Blocks.GREEN_SEAT_OF_UNDYING)

    @JvmField
    val RED_SEAT_OF_UNDYING = register(Blocks.RED_SEAT_OF_UNDYING)

    @JvmField
    val BLACK_SEAT_OF_UNDYING = register(Blocks.BLACK_SEAT_OF_UNDYING)

    @JvmStatic
    fun <T : Block> register(block: T) = register(block, ::BlockItem)

    @JvmStatic
    fun <T : Block, U : Item> register(
        block: T,
        function: (T, Item.Properties) -> U
    ) = register(block, function, Item.Properties())

    @Suppress("DEPRECATION")
    @JvmStatic
    fun <T : Block, U : Item> register(
        block: T,
        function: (T, Item.Properties) -> U,
        properties: Item.Properties
    ) = register(
        block.builtInRegistryHolder().key().identifier(),
        { properties ->
            function(block, properties)
        },
        properties.useBlockDescriptionPrefix()
    )

    @JvmStatic
    fun register(id: String) = register(id, ::Item, Item.Properties())

    @JvmStatic
    fun <T : Item> register(id: String, function: (Item.Properties) -> T) = register(id, function, Item.Properties())

    @JvmStatic
    fun <T : Item> register(
        id: String,
        function: (Item.Properties) -> T,
        properties: Item.Properties
    ) = register(identifier(id), function, properties)

    @JvmStatic
    fun <T : Item> register(
        id: Identifier,
        function: (Item.Properties) -> T,
        properties: Item.Properties
    ): T {
        val key = ResourceKey.create(Registries.ITEM, id)
        return Registry.register(
            BuiltInRegistries.ITEM,
            key,
            function(properties.setId(key))
        )
    }

    @JvmStatic
    fun register() {
    }
}

object Tags {
    object Block {
        @JvmField
        val FAN_TRANSPARENT_REQUIRES_UNPOWERED = block("fan_transparent_requires_unpowered")

        @JvmField
        val SEATS_OF_UNDYING = block("seats_of_undying")

        @JvmField
        val FAN_PROCESSING_CATALYSTS_WOLOLO = block("fan_processing_catalysts/wololo")

        @JvmStatic
        fun block(id: String) = block(identifier(id))

        @JvmStatic
        fun block(id: Identifier) = register(Registries.BLOCK, id)

        @JvmStatic
        fun register() {
        }
    }

    object Fluid {
        @JvmField
        val FAN_PROCESSING_CATALYSTS_WOLOLO = fluid("fan_processing_catalysts/wololo")

        @JvmStatic
        fun fluid(id: String) = fluid(identifier(id))

        @JvmStatic
        fun fluid(id: Identifier) = register(Registries.FLUID, id)

        @JvmStatic
        fun register() {
        }
    }

    object EntityType {
        @JvmField
        val EVOKER_MANSION_CAPTURABLE = entityType("evoker_mansion_capturable")

        @JvmStatic
        fun entityType(id: String) = entityType(identifier(id))

        @JvmStatic
        fun entityType(id: Identifier) = register(Registries.ENTITY_TYPE, id)

        @JvmStatic
        fun register() {
        }
    }

    object MobEffect {
        @JvmField
        val NAUSEA = mobEffect("nausea")

        @JvmStatic
        fun mobEffect(id: String) = mobEffect(identifier(id))

        @JvmStatic
        fun mobEffect(id: Identifier) = register(Registries.MOB_EFFECT, id)

        @JvmStatic
        fun register() {
        }
    }

    @JvmStatic
    fun <T : Any> register(registry: ResourceKey<out Registry<T>>, id: Identifier) = TagKey.create(registry, id)

    @JvmStatic
    fun register() {
        Tags.Block.register()
        EntityType.register()
        MobEffect.register()
        Fluid.register()
    }
}

object CreativeTabs {
    @Suppress("unused")
    @JvmField
    val MAIN = register(
        "main", FabricItemGroup.builder()
            .icon { ItemStack(Items.LAPIS_SHEET) }
            .title(Component.translatable("itemGroup.$MOD_ID"))
            .displayItems { _, output ->
                output.accept { Items.LAPIS_CASING }
                output.accept { Items.LAPIS_FUNNEL }
                output.accept { Items.LAPIS_SHEET }
                output.accept { Items.INTEGRATED_CIRCUIT }
                output.accept { Items.EMPTY_EVOKER_MANSION }
                output.accept { Items.EVOKER_MANSION }
                output.accept { Items.SAFE_EVOKER_MANSION }
                output.accept { Items.WHITE_SEAT_OF_UNDYING }
                output.accept { Items.ORANGE_SEAT_OF_UNDYING }
                output.accept { Items.MAGENTA_SEAT_OF_UNDYING }
                output.accept { Items.LIGHT_BLUE_SEAT_OF_UNDYING }
                output.accept { Items.YELLOW_SEAT_OF_UNDYING }
                output.accept { Items.LIME_SEAT_OF_UNDYING }
                output.accept { Items.PINK_SEAT_OF_UNDYING }
                output.accept { Items.GRAY_SEAT_OF_UNDYING }
                output.accept { Items.LIGHT_GRAY_SEAT_OF_UNDYING }
                output.accept { Items.CYAN_SEAT_OF_UNDYING }
                output.accept { Items.PURPLE_SEAT_OF_UNDYING }
                output.accept { Items.BLUE_SEAT_OF_UNDYING }
                output.accept { Items.BROWN_SEAT_OF_UNDYING }
                output.accept { Items.GREEN_SEAT_OF_UNDYING }
                output.accept { Items.RED_SEAT_OF_UNDYING }
                output.accept { Items.BLACK_SEAT_OF_UNDYING }
            }
            .build())

    @JvmStatic
    fun register(id: String, tab: CreativeModeTab) =
        register(identifier(id), tab)

    @JvmStatic
    fun register(id: Identifier, tab: CreativeModeTab) =
        Registry.register(
            BuiltInRegistries.CREATIVE_MODE_TAB,
            ResourceKey.create(Registries.CREATIVE_MODE_TAB, id),
            tab
        )

    @JvmStatic
    fun register() {
    }
}

object BlockEntityTypes {
    @JvmField
    val EVOKER = register("evoker", ::EvokerMansionBlockEntity, Blocks.EVOKER_MANSION)

    @JvmStatic
    fun <T : BlockEntity> register(
        id: String,
        function: BlockEntityType.BlockEntitySupplier<T>,
        vararg blocks: Block
    ) = register(Identifier.fromNamespaceAndPath(id, id), function, *blocks)

    @JvmStatic
    fun <T : BlockEntity> register(
        id: Identifier,
        function: BlockEntityType.BlockEntitySupplier<T>,
        vararg blocks: Block
    ) = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id, BlockEntityType(function, blocks.toSet()))

    @JvmStatic
    fun register() {
        AllBlockEntityTypes.FUNNEL.addSupportedBlock(Blocks.LAPIS_FUNNEL)
        AllBlockEntityTypes.FUNNEL.addSupportedBlock(Blocks.LAPIS_BELT_FUNNEL)
    }
}

object RecipeTypes {
    @JvmField
    val WOLOLO = register<WololoRecipe>("wololo")

    @JvmStatic
    fun <T : Recipe<*>> register(id: String) = register<T>(identifier(id))

    @JvmStatic
    fun <T : Recipe<*>> register(id: Identifier) =
        Registry.register(BuiltInRegistries.RECIPE_TYPE, id, object : RecipeType<T> {
            override fun toString(): String {
                return id.toString()
            }
        })

    @JvmStatic
    fun register() {
    }
}

object RecipeSerializers {
    @JvmField
    val WOLOLO = register("wololo", WololoRecipe.Serializer())

    @JvmStatic
    fun <S : RecipeSerializer<T>, T : Recipe<*>> register(id: String, serializer: S) =
        register(identifier(id), serializer)

    @JvmStatic
    fun <S : RecipeSerializer<T>, T : Recipe<*>> register(id: Identifier, serializer: S) =
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, id, serializer)

    @JvmStatic
    fun register() {
    }
}

object RecipeSets {
    @JvmField
    val WOLOLO = register("wololo")

    @JvmStatic
    fun register(id: String) = register(identifier(id))

    @JvmStatic
    fun register(id: Identifier) = ResourceKey.create(RecipePropertySet.TYPE_KEY, id)

    @Suppress("UNCHECKED_CAST")
    private fun <T : Recipe<*>> register(
        key: ResourceKey<RecipePropertySet>,
        type: Class<T>,
        getter: Function<T, Ingredient>
    ) {
        AllRecipeSets.ALL[key] = RecipeManager.IngredientExtractor { recipe: Recipe<*> ->
            if (type.isInstance(recipe)) {
                return@IngredientExtractor Optional.of<Ingredient>(getter.apply(recipe as T))
            } else {
                return@IngredientExtractor Optional.empty<Ingredient>()
            }
        }
    }

    @JvmStatic
    fun register() {
        register(WOLOLO, WololoRecipe::class.java, WololoRecipe::ingredient)
    }
}

object FanProcessingTypes {
    @Suppress("unused")
    @JvmField
    val WOLOLO = register("wololo", WololoFanProcessingType())

    @JvmStatic
    fun <T : FanProcessingType> register(id: String, type: T) = register(identifier(id), type)

    @JvmStatic
    fun <T : FanProcessingType> register(id: Identifier, type: T) =
        Registry.register(CreateRegistries.FAN_PROCESSING_TYPE, id, type)

    @JvmStatic
    fun register() {
    }
}

object Shapes {
    @JvmField
    val EVOKER_MANSION_BLOCK_SHAPE: VoxelShape = shape(1.0, 0.0, 1.0, 15.0, 14.0, 15.0).build()

    @JvmField
    val EVOKER_MANSION_BLOCK_SPECIAL_COLLISION_SHAPE: VoxelShape = shape(0.0, 0.0, 0.0, 16.0, 4.0, 16.0).build()

    @JvmStatic
    fun shape(shape: VoxelShape): AllShapes.Builder {
        return AllShapes.Builder(shape)
    }

    @JvmStatic
    fun shape(x1: Double, y1: Double, z1: Double, x2: Double, y2: Double, z2: Double): AllShapes.Builder {
        return shape(cuboid(x1, y1, z1, x2, y2, z2))
    }

    @JvmStatic
    fun cuboid(x1: Double, y1: Double, z1: Double, x2: Double, y2: Double, z2: Double): VoxelShape {
        return Block.box(x1, y1, z1, x2, y2, z2)
    }
}

abstract class Handle {
    abstract fun isClient(): Boolean

    abstract fun tickEvokerMansionAnimation(be: EvokerMansionBlockEntity)

    companion object {
        @JvmStatic
        lateinit var INSTANCE: Handle
            private set

        @JvmStatic
        fun init(handle: Handle) {
            INSTANCE = handle
        }
    }
}
