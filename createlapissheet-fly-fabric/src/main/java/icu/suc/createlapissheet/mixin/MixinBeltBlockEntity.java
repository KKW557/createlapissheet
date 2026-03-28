package icu.suc.createlapissheet.mixin;

import com.zurrtum.create.content.kinetics.belt.BeltBlockEntity;
import icu.suc.createlapissheet.Blocks;
import icu.suc.createlapissheet.CasingTypes;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(BeltBlockEntity.class)
public abstract class MixinBeltBlockEntity {
    @Shadow
    public BeltBlockEntity.CasingType casing;

    @ModifyArg(method = "setCasingType", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;getId(Lnet/minecraft/world/level/block/state/BlockState;)I"))
    private BlockState modifyBlockState(BlockState state) {
        if (casing == CasingTypes.LAPIS) return Blocks.LAPIS_CASING.defaultBlockState();
        return state;
    }
}
