package icu.suc.createlapissheet.mixin;

import com.zurrtum.create.content.fluids.pipes.VanillaFluidTargets;
import com.zurrtum.create.infrastructure.fluids.FluidStack;
import icu.suc.createlapissheet.Fluids;
import icu.suc.createlapissheet.content.fluids.pipes.ExperienceAttractor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(VanillaFluidTargets.class)
public abstract class MixinVanillaFluidTargets {
    @Inject(method = "drainBlock", at = @At("HEAD"), cancellable = true)
    private static void injectDrainBlock(Level level, BlockPos pos, @NonNull BlockState state, boolean simulate, CallbackInfoReturnable<FluidStack> cir) {
        int xp = state.getValueOrElse(ExperienceAttractor.EXPERIENCE, -1);
        if (xp > 0) {
            if (!simulate) {
                level.setBlockAndUpdate(pos, state.setValue(ExperienceAttractor.EXPERIENCE, 0));
            }
            cir.setReturnValue(new FluidStack(Fluids.EXPERIENCE, xp * 1620));
        }
    }
}
