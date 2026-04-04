package icu.suc.createlapissheet.mixin;

import com.zurrtum.create.content.kinetics.deployer.DeployerBlockEntity;
import icu.suc.createlapissheet.Advancements;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(DeployerBlockEntity.class)
public abstract class MixinDeployerBlockEntity {
    @Redirect(method = "getAwardables", at = @At(value = "INVOKE", target = "Ljava/util/List;of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/List;"))
    private @NonNull @Unmodifiable List<Object> redirectGetAwardables(Object e1, Object e2, Object e3, Object e4, Object e5, Object e6, Object e7) {
        return List.of(e1, e2, e3, e4, e5, e6, e7, Advancements.LAPIS_CASING, Advancements.SEAT_OF_UNDYING);
    }
}
