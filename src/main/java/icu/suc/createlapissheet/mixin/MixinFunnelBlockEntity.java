package icu.suc.createlapissheet.mixin;

import com.zurrtum.create.content.logistics.funnel.FunnelBlockEntity;
import com.zurrtum.create.foundation.blockEntity.SmartBlockEntity;
import com.zurrtum.create.foundation.blockEntity.behaviour.filtering.ServerFilteringBehaviour;
import icu.suc.createlapissheet.Blocks;
import icu.suc.createlapissheet.foundation.blockentity.behaviour.filtering.EnchantmentFilteringBehaviour;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FunnelBlockEntity.class)
public abstract class MixinFunnelBlockEntity {

    @Redirect(method = "addBehaviours", at = @At(value = "NEW", target = "com/zurrtum/create/foundation/blockEntity/behaviour/filtering/ServerFilteringBehaviour"))
    private @NonNull ServerFilteringBehaviour redirectFiltering(@NonNull SmartBlockEntity be) {
        if (be.getBlockState().is(Blocks.LAPIS_FUNNEL) || be.getBlockState().is(Blocks.LAPIS_BELT_FUNNEL)) {
            return new EnchantmentFilteringBehaviour(be);
        }
        return new ServerFilteringBehaviour(be);
    }
}
