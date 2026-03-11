package icu.suc.createlapissheet.mixin;

import com.zurrtum.create.AllBlocks;
import com.zurrtum.create.content.kinetics.belt.BeltBlockEntity;
import icu.suc.createlapissheet.Blocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BeltBlockEntity.class)
public abstract class MixinBeltBlockEntity {
    @Shadow
    public BeltBlockEntity.CasingType casing;

    @Redirect(method = "setCasingType", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;getId(Lnet/minecraft/world/level/block/state/BlockState;)I"))
    private int redirectSetCasingType(BlockState blockState) {
        if (casing == BeltBlockEntity.CasingType.ANDESITE) blockState = AllBlocks.ANDESITE_CASING.defaultBlockState();
        else if (casing == BeltBlockEntity.CasingType.BRASS) blockState = AllBlocks.BRASS_CASING.defaultBlockState();
        else if ("LAPIS".equals(casing.name())) blockState = Blocks.LAPIS_CASING.defaultBlockState();
        return Block.getId(blockState);
    }
}
