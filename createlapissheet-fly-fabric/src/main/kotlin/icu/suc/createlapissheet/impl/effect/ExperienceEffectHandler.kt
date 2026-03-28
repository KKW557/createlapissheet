package icu.suc.createlapissheet.impl.effect

import com.zurrtum.create.api.effect.OpenPipeEffectHandler
import com.zurrtum.create.infrastructure.fluids.FluidStack
import icu.suc.createlapissheet.GameRules
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.ExperienceOrb
import net.minecraft.world.level.Level
import net.minecraft.world.phys.AABB
import java.util.WeakHashMap

class ExperienceEffectHandler : OpenPipeEffectHandler {
    private val cache = WeakHashMap<AABB, Int>()

    override fun apply(
        level: Level,
        area: AABB,
        fluid: FluidStack
    ) {
        val level = level as ServerLevel
        if (!level.gameRules.get(GameRules.EXPERIENCE_FLUID_TO_ORB)) return

        val f = fluid.amount
        if (f != 81) return

        val amount = cache.getOrDefault(area, 0) + f

        val xp = amount / 1620

        if (xp >= level.gameRules.get(GameRules.MIN_AWARD_XP)) {
            ExperienceOrb.award(level, area.center, xp)
            cache[area] = amount % 1620
        } else {
            cache[area] = amount
        }
    }
}