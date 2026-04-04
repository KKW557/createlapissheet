package icu.suc.createlapissheet.mixin;

import com.zurrtum.create.content.kinetics.deployer.ManualApplicationHelper;
import icu.suc.createlapissheet.Advancements;
import icu.suc.createlapissheet.Blocks;
import icu.suc.createlapissheet.Tags;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ManualApplicationHelper.class)
public abstract class MixinManualApplicationHelper {
    @Inject(method = "awardAdvancements", at = @At("RETURN"))
    private static void injectAwardAdvancements(ServerPlayer player, @NonNull BlockState placed, CallbackInfo ci) {
        if (placed.is(Blocks.LAPIS_CASING)) {
            Advancements.LAPIS_CASING.trigger(player);
        } else if (placed.is(Tags.Block.SEATS_OF_UNDYING)) {
            Advancements.SEAT_OF_UNDYING.trigger(player);
        }
    }
}
