package icu.suc.createlapissheet.mixin;

import com.zurrtum.create.content.contraptions.actors.seat.SeatEntity;
import icu.suc.createlapissheet.Advancements;
import icu.suc.createlapissheet.Tags;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.component.DeathProtection;
import net.minecraft.world.level.gamerules.GameRules;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity {
    @Shadow
    public abstract boolean wasExperienceConsumed();

    @Shadow
    protected abstract boolean isAlwaysExperienceDropper();

    @Shadow
    protected int lastHurtByPlayerMemoryTime;

    @Shadow
    public abstract boolean shouldDropExperience();

    @Shadow
    public abstract int getExperienceReward(ServerLevel serverLevel, @Nullable Entity entity);

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

        if (entity instanceof ServerPlayer player) {
            Advancements.STAY_CALM.trigger(player);
        }

        entity.setHealth(1.0F);
        deathProtection.applyEffects(itemStack, entity);

        level.broadcastEntityEvent(entity, (byte) 35);
        level.destroyBlock(pos, false);

        cir.setReturnValue(true);
    }

    @Inject(method = "dropExperience", at = @At("HEAD"))
    private void injectDropExperience(@NonNull ServerLevel serverLevel, Entity entity, CallbackInfo ci) {
        boolean alwaysExperienceDropper = this.isAlwaysExperienceDropper();
        boolean hurtByPlayer = this.lastHurtByPlayerMemoryTime > 0;
        var gameRules = serverLevel.getGameRules();
        if (!this.wasExperienceConsumed() && (alwaysExperienceDropper || this.shouldDropExperience() && gameRules.get(GameRules.MOB_DROPS))) {
            int xp = this.getExperienceReward(serverLevel, entity);
            xp = alwaysExperienceDropper || hurtByPlayer ? xp : xp * gameRules.get(icu.suc.createlapissheet.GameRules.NATURAL_DEATH_XP_PERCENTAGE) / 100;
            if (xp == 0) return;
            ExperienceOrb.award(serverLevel, ((LivingEntity) (Object) this).position(), xp);
        }
    }
}
