package icu.suc.createlapissheet.mixin;

import com.zurrtum.create.content.kinetics.belt.BeltBlock;
import com.zurrtum.create.content.kinetics.belt.BeltBlockEntity;
import com.zurrtum.create.content.kinetics.belt.BeltHelper;
import icu.suc.createlapissheet.Blocks;
import icu.suc.createlapissheet.Items;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BeltBlock.class)
public abstract class MixinBeltBlock {
    @Shadow
    public abstract void updateCoverProperty(LevelReader world, BlockPos pos, BlockState state);

    @Inject(method = "useItemOn", at = @At("RETURN"), cancellable = true)
    private void injectUseItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult, @NonNull CallbackInfoReturnable<InteractionResult> cir) {
        if (cir.getReturnValue() != InteractionResult.TRY_WITH_EMPTY_HAND) return;

        if (stack.is(Items.LAPIS_CASING)) {
            var belt = BeltHelper.getSegmentBE(level, pos);
            if (belt == null) return;

            if (!level.isClientSide()) {
                belt.setCasingType(BeltBlockEntity.CasingType.valueOf("LAPIS"));
                updateCoverProperty(level, pos, level.getBlockState(pos));
            }

            var sound = Blocks.LAPIS_CASING.defaultBlockState().getSoundType();

            level.playSound(null, pos, sound.getPlaceSound(), SoundSource.BLOCKS, (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F);

            cir.setReturnValue(InteractionResult.SUCCESS);
        }
    }
}
