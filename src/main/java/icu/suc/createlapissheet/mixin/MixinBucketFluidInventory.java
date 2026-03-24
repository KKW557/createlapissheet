package icu.suc.createlapissheet.mixin;

import com.zurrtum.create.infrastructure.fluids.BucketFluidInventory;
import com.zurrtum.create.infrastructure.fluids.FluidStack;
import icu.suc.createlapissheet.Fluids;
import icu.suc.createlapissheet.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BucketFluidInventory.class)
public abstract class MixinBucketFluidInventory {
    @Inject(method = "toFillBucket", at = @At("HEAD"), cancellable = true)
    private void injectToFillBucket(@NonNull FluidStack stack, CallbackInfoReturnable<ItemStack> cir) {
        if (stack.isOf(Fluids.EXPERIENCE)) {
           cir.setReturnValue(Items.EXPERIENCE_BUCKET.getDefaultInstance());
        }
    }

    @Inject(method = "toFluid", at = @At("HEAD"), cancellable = true)
    private void injectToFluid(CallbackInfoReturnable<Fluid> cir) {
        if (((BucketFluidInventory) (Object) this).stack.is(Items.EXPERIENCE_BUCKET)) {
            cir.setReturnValue(Fluids.EXPERIENCE);
        }
    }
}
