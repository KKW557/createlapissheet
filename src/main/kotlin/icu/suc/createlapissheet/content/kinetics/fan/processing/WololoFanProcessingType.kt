package icu.suc.createlapissheet.content.kinetics.fan.processing

import com.zurrtum.create.catnip.theme.Color
import com.zurrtum.create.content.kinetics.fan.processing.FanProcessingType
import com.zurrtum.create.foundation.recipe.RecipeApplier
import icu.suc.createlapissheet.RecipeTypes
import icu.suc.createlapissheet.Tags
import icu.suc.createlapissheet.content.processing.mansion.EvokerMansionBlock
import icu.suc.createlapissheet.item.crafting.WololoRecipe
import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ColorParticleOption
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.RandomSource
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.monster.illager.SpellcasterIllager
import net.minecraft.world.item.DyeColor
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.RecipeHolder
import net.minecraft.world.item.crafting.SingleRecipeInput
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import java.util.*
import java.util.function.Function

class WololoFanProcessingType : FanProcessingType {
    var nausea = false

    override fun isValidAt(level: Level, pos: BlockPos): Boolean {
        level.getFluidState(pos).let {
            if (it.`is`(Tags.Fluid.FAN_PROCESSING_CATALYSTS_WOLOLO)) {
                nausea = it.getValue(EvokerMansionBlock.NAUSEA)
                return true
            }
        }
        level.getBlockState(pos).let {
            if (it.`is`(Tags.Block.FAN_PROCESSING_CATALYSTS_WOLOLO)) {
                if (EvokerMansionBlock.getEvokerOf(it) == EvokerMansionBlock.Evoker.CASTING) {
                    nausea = EvokerMansionBlock.getNauseaOf(it)
                    return true
                }
            }
        }
        return false
    }

    override fun getPriority() = 557

    override fun canProcess(stack: ItemStack, level: Level): Boolean =
        (level as ServerLevel).recipeAccess()
            .getRecipeFor(RecipeTypes.WOLOLO, SingleRecipeInput(stack), level)
            .map { it.value().nausea == nausea }
            .orElse(false) ?: false

    override fun process(
        stack: ItemStack,
        level: Level
    ): List<ItemStack>? {
        val input = SingleRecipeInput(stack)
        val recipe: Optional<RecipeHolder<WololoRecipe>> = (level as ServerLevel).recipeAccess()
            .getRecipeFor<SingleRecipeInput, WololoRecipe>(RecipeTypes.WOLOLO, input, level)
        return recipe.map(Function { entry: RecipeHolder<WololoRecipe> ->
            RecipeApplier.applyRecipeOn(
                level.getRandom(),
                stack.count,
                input,
                entry.value()
            )
        }).orElse(null)
    }

    override fun spawnProcessingParticles(
        level: Level,
        pos: Vec3
    ) {
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
            0.0,
            0.0,
            0.0
        )
    }

    override fun morphAirFlow(
        particleAccess: FanProcessingType.AirFlowParticleAccess,
        random: RandomSource
    ) {
        val float = random.nextFloat()
        particleAccess.setColor(
            if (nausea) Color.mixColors(DyeColor.BLUE.fireworkColor, DyeColor.BLUE.textureDiffuseColor, float)
            else Color.mixColors(DyeColor.RED.fireworkColor, DyeColor.RED.textureDiffuseColor, float)
        )
        particleAccess.setAlpha(.8f)
    }

    override fun affectEntity(entity: Entity?, level: Level?) {
        if (!nausea) return
        if (entity !is LivingEntity) return
        entity.addEffect(MobEffectInstance(MobEffects.NAUSEA, 80, 0, false, false))
    }
}