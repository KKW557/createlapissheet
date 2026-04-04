package icu.suc.createlapissheet.mixin;

import com.zurrtum.create.content.logistics.funnel.FunnelBlockEntity;
import com.zurrtum.create.foundation.blockEntity.SmartBlockEntity;
import com.zurrtum.create.foundation.blockEntity.behaviour.filtering.ServerFilteringBehaviour;
import icu.suc.createlapissheet.Advancements;
import icu.suc.createlapissheet.Blocks;
import icu.suc.createlapissheet.foundation.blockentity.behaviour.filtering.EnchantmentFilter;
import icu.suc.createlapissheet.foundation.blockentity.behaviour.filtering.EnchantmentFiltering;
import icu.suc.createlapissheet.foundation.blockentity.behaviour.filtering.ServerEnchantmentFilteringBehaviour;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(FunnelBlockEntity.class)
public abstract class MixinFunnelBlockEntity implements EnchantmentFiltering {
    @Shadow
    private ServerFilteringBehaviour filtering;

    @SuppressWarnings("AddedMixinMembersNamePattern")
    @Override
    public @NonNull EnchantmentFilter getEnchantmentFilter(@NotNull Direction side) {
        return getEnchantmentFilter();
    }

    @SuppressWarnings("AddedMixinMembersNamePattern")
    @Override
    public @NonNull EnchantmentFilter getEnchantmentFilter() {
        if (filtering instanceof ServerEnchantmentFilteringBehaviour serverEnchantmentFilteringBehaviour) {
            return serverEnchantmentFilteringBehaviour.toEnchantmentFilter();
        }
        return EnchantmentFilter.EMPTY;
    }

    @Redirect(method = "addBehaviours", at = @At(value = "NEW", target = "com/zurrtum/create/foundation/blockEntity/behaviour/filtering/ServerFilteringBehaviour"))
    private @NonNull ServerFilteringBehaviour redirectFiltering(@NonNull SmartBlockEntity be) {
        if (be.getBlockState().is(Blocks.LAPIS_FUNNEL) || be.getBlockState().is(Blocks.LAPIS_BELT_FUNNEL)) {
            return new ServerEnchantmentFilteringBehaviour(be);
        }
        return new ServerFilteringBehaviour(be);
    }

    @Redirect(method = "getAwardables", at = @At(value = "INVOKE", target = "Ljava/util/List;of(Ljava/lang/Object;)Ljava/util/List;"))
    private @NonNull @Unmodifiable List<Object> redirectGetAwardables(Object e1) {
        return List.of(e1, Advancements.LAPIS_FUNNEL);
    }
}
