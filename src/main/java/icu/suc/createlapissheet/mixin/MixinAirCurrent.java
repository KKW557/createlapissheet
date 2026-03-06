package icu.suc.createlapissheet.mixin;

import com.zurrtum.create.content.kinetics.fan.AirCurrent;
import icu.suc.createlapissheet.Tags;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AirCurrent.class)
public abstract class MixinAirCurrent {

    @Inject(method = "shouldAlwaysPass", at = @At("RETURN"), cancellable = true)
    private static void injectShouldAlwaysPass(BlockState state, @NonNull CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue()) return;
        if (!state.is(Tags.Block.FAN_TRANSPARENT_REQUIRES_UNPOWERED)) return;
        if (state.hasProperty(BlockStateProperties.POWERED) && state.getValue(BlockStateProperties.POWERED)) cir.setReturnValue(false);
    }
}
