package icu.suc.createlapissheet.content.kinetics.fan.processing

import icu.suc.createlapissheet.RecipeTypes
import icu.suc.createlapissheet.Tags
import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ColorParticleOption
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.animal.sheep.Sheep
import net.minecraft.world.entity.monster.illager.SpellcasterIllager
import net.minecraft.world.item.DyeColor
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.SingleRecipeInput
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3

class WololoFanProcessingType(val nausea: Boolean) :
    EvokerFanProcessingType(
        Tags.Fluid.FAN_PROCESSING_CATALYSTS_WOLOLO,
        Tags.Block.FAN_PROCESSING_CATALYSTS_WOLOLO,
        if (nausea) DyeColor.BLUE.textureDiffuseColor else DyeColor.RED.textureDiffuseColor
    ) {
    override fun isValidAt(level: Level, pos: BlockPos): Boolean {
        level.getFluidState(pos).let {
            if (!it.`is`(Tags.Fluid.FAN_PROCESSING_CATALYSTS_WOLOLO)) return@let
            val type = it.type
            if (type !is IFanProcessingFluid) return true
            if (type.isValid(this, it)) return true
        }
        level.getBlockState(pos).let {
            if (!it.`is`(Tags.Block.FAN_PROCESSING_CATALYSTS_WOLOLO)) return@let
            val type = it.block
            if (type !is IFanProcessingBlock) return true
            if (type.isValid(this, it)) return true
        }
        return false
    }

    override fun getPriority() = 557

    override fun canProcess(stack: ItemStack, level: Level) = (level as ServerLevel).recipeAccess()
        .getRecipeFor(RecipeTypes.WOLOLO, SingleRecipeInput(stack), level)
        .map { it.value().nausea == nausea }
        .orElse(false) ?: false

    override fun process(
        stack: ItemStack,
        level: Level
    ): MutableList<ItemStack>? {
        val input = SingleRecipeInput(stack)
        val recipe = (level as ServerLevel).recipeAccess()
            .getRecipeFor<SingleRecipeInput, WololoRecipe>(RecipeTypes.WOLOLO, input, level)
        return recipe.map { mutableListOf(it.value.result) }.orElse(null)
    }

    override fun spawnProcessingParticles(level: Level, pos: Vec3) {
        if (level.random.nextInt(8) != 0) return
        val color = SpellcasterIllager.IllagerSpell.WOLOLO.spellColor
        level.addParticle(
            ColorParticleOption.create(
                ParticleTypes.ENTITY_EFFECT,
                color[0].toFloat(),
                color[1].toFloat(),
                color[2].toFloat()
            ),
            pos.x + (level.random.nextFloat() - .5f) * .5f,
            pos.y + .5f,
            pos.z + (level.random.nextFloat() - .5f) * .5f,
            .0,
            .125,
            .0
        )
    }

    override fun affectEntity(entity: Entity?, level: Level?) {
        if (entity !is LivingEntity) return
        if (nausea) entity.addEffect(MobEffectInstance(MobEffects.NAUSEA, 80, 0, false, false))
        if (entity !is Sheep) return
        if (nausea && entity.color == DyeColor.RED) entity.color = DyeColor.BLUE
        else if (!nausea && entity.color == DyeColor.BLUE) entity.color = DyeColor.RED
    }
}