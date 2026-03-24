package icu.suc.createlapissheet.client.mixin;

import com.zurrtum.create.content.contraptions.actors.seat.SeatEntity;
import icu.suc.createlapissheet.Tags;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DeathProtection;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPacketListener.class)
public abstract class MixinClientPacketListener {
    @Inject(method = "findTotem", at = @At(value = "NEW", target = "(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/item/ItemStack;"), cancellable = true)
    private static void injectFindTotem(@NonNull Player player, CallbackInfoReturnable<ItemStack> cir) {
        if (!(player.getVehicle() instanceof SeatEntity seat)) return;

        var level = seat.level();
        var pos = seat.blockPosition();
        var state = level.getBlockState(pos);

        if (!state.is(Tags.Block.SEATS_OF_UNDYING)) return;

        var itemStack = state.getBlock().asItem().getDefaultInstance();
        itemStack.set(DataComponents.DEATH_PROTECTION, DeathProtection.TOTEM_OF_UNDYING);
        var deathProtection = itemStack.get(DataComponents.DEATH_PROTECTION);

        if (deathProtection == null) return;

        cir.setReturnValue(itemStack);
    }
}
