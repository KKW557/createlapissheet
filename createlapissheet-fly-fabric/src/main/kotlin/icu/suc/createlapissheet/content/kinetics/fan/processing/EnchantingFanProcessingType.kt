package icu.suc.createlapissheet.content.kinetics.fan.processing

import icu.suc.createlapissheet.Tags
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.DyeColor
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3

class EnchantingFanProcessingType : EvokerFanProcessingType(
    Tags.Fluid.FAN_PROCESSING_CATALYSTS_ENCHANTING,
    Tags.Block.FAN_PROCESSING_CATALYSTS_ENCHANTING,
    DyeColor.PURPLE.textureDiffuseColor
) {
    override fun getPriority() = 558

    override fun canProcess(
        stack: ItemStack,
        level: Level
    ) = stack.isEnchantable

    override fun process(
        stack: ItemStack,
        level: Level
    ) = mutableListOf(stack.copy())

    override fun spawnProcessingParticles(level: Level, pos: Vec3) {
        if (level.random.nextInt(8) != 0) return
        level.addParticle(
            ParticleTypes.ENCHANT,
            pos.x + (level.random.nextFloat() - .5f) * .5f,
            pos.y + .5f,
            pos.z + (level.random.nextFloat() - .5f) * .5f,
            .0,
            .125,
            .0
        )
    }

    override fun affectEntity(entity: Entity?, level: Level?) {}
}