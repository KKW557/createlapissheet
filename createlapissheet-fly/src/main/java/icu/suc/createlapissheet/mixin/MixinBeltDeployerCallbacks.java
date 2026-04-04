package icu.suc.createlapissheet.mixin;

import com.zurrtum.create.content.kinetics.deployer.BeltDeployerCallbacks;
import com.zurrtum.create.content.kinetics.deployer.DeployerBlockEntity;
import icu.suc.createlapissheet.Advancements;
import icu.suc.createlapissheet.Items;
import icu.suc.createlapissheet.Tags;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BeltDeployerCallbacks.class)
public abstract class MixinBeltDeployerCallbacks {
    @Inject(method = "awardAdvancements", at = @At("RETURN"))
    private static void injectAwardAdvancements(DeployerBlockEntity blockEntity, @NonNull ItemStack created, CallbackInfo ci) {
        if (created.is(Items.LAPIS_CASING)) {
            blockEntity.award(Advancements.LAPIS_CASING);
        } else if (created.is(Tags.Item.SEATS_OF_UNDYING)) {
            blockEntity.award(Advancements.SEAT_OF_UNDYING);
        }
    }
}
