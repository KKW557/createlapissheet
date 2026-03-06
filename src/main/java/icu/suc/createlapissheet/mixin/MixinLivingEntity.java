package icu.suc.createlapissheet.mixin;

import com.zurrtum.create.content.contraptions.actors.seat.SeatEntity;
import icu.suc.createlapissheet.Tags;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.component.DeathProtection;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity {

    @Inject(method = "checkTotemDeathProtection", at = @At("RETURN"), cancellable = true)
    private void injectCheckTotemDeathProtection(DamageSource damageSource, @NonNull CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) return;
        if (damageSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) return;

        var entity = (LivingEntity) (Object) this;

        if (!(entity.getVehicle() instanceof SeatEntity seat)) return;

        var level = seat.level();
        var pos = seat.blockPosition();
        var state = level.getBlockState(pos);

        if (!state.is(Tags.Block.SEATS_OF_UNDYING)) return;

        // for compat, idk reealllly usefuulll??
        var itemStack = state.getBlock().asItem().getDefaultInstance();
        itemStack.set(DataComponents.DEATH_PROTECTION, DeathProtection.TOTEM_OF_UNDYING);
        var deathProtection = itemStack.get(DataComponents.DEATH_PROTECTION);

        if (deathProtection == null) return;

        entity.setHealth(1.0F);
        deathProtection.applyEffects(itemStack, entity);

        level.broadcastEntityEvent(entity, (byte) 35);
        level.destroyBlock(pos, false);

        cir.setReturnValue(true);
    }
}
