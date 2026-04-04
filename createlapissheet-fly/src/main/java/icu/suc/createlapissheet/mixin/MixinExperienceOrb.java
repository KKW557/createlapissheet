package icu.suc.createlapissheet.mixin;

import icu.suc.createlapissheet.Blocks;
import icu.suc.createlapissheet.content.fluids.pipes.ExperienceAttractor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ExperienceOrb.class)
public abstract class MixinExperienceOrb {
    @Shadow
    @Nullable
    private Player followingPlayer;

    @Shadow
    public abstract int getValue();

    @Shadow
    protected abstract void setValue(int i);

    @Unique
    @Nullable
    public Vec3 followingAttractor;

    @Inject(method = "followNearbyPlayer", at = @At("HEAD"), cancellable = true)
    private void injectFollowNearbyAttractorFirst(CallbackInfo ci) {
        var self = (ExperienceOrb) (Object) this;

        var level = self.level();

        if (this.followingAttractor != null) {
            var pos = this.followingAttractor;
            move(ci, self, pos, level);
            return;
        }

        var pos = findNearbyAttractor(level, self.blockPosition());
        if (pos != null) {
            this.followingAttractor = pos;
            this.followingPlayer = null;

            move(ci, self, pos, level);
        }

        this.followingAttractor = null;
    }

    @Unique
    private void move(CallbackInfo ci, @NotNull ExperienceOrb self, @NotNull Vec3 pos, @NotNull Level level) {
        var delta = new Vec3(pos.x - self.getX(), pos.y - self.getY(), pos.z - self.getZ());
        double distance = delta.length();

        if (distance < 0.8) {
            var blockPos = BlockPos.containing(pos);
            var state = level.getBlockState(blockPos);
            if (state.hasProperty(ExperienceAttractor.EXPERIENCE)) {
                int current = state.getValue(ExperienceAttractor.EXPERIENCE);
                int space = 50 - current;

                int value = getValue();
                if (space > 0 && value > 0) {
                    int added = Math.min(value, space);
                    level.setBlockAndUpdate(blockPos, state.setValue(ExperienceAttractor.EXPERIENCE, current + added));
                    setValue(value - added);
                    if (getValue() <= 0) {
                        self.discard();
                    }
                }
            }
            ci.cancel();
            return;
        }

        self.setDeltaMovement(delta.scale(0.16 / distance));
        ci.cancel();
    }

    @Unique
    @Nullable
    private Vec3 findNearbyAttractor(@NotNull Level level, @NotNull BlockPos center) {
        var pos = new BlockPos.MutableBlockPos();
        int r = 8 * 8;

        for (int dx = -8; dx <= 8; dx++) {
            for (int dy = -8; dy <= 8; dy++) {
                for (int dz = -8; dz <= 8; dz++) {
                    if (dx * dx + dy * dy + dz * dz > r) continue;

                    pos.set(center.getX() + dx, center.getY() + dy, center.getZ() + dz);
                    var state = level.getBlockState(pos);

                    if (!state.is(Blocks.EXPERIENCE_ATTRACTOR)) continue;

                    return Vec3.atCenterOf(pos);
                }
            }
        }

        return null;
    }
}