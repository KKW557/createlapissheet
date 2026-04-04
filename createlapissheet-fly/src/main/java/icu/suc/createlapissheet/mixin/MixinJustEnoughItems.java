package icu.suc.createlapissheet.mixin;

import icu.suc.createlapissheet.RecipeSerializers;
import mezz.jei.fabric.JustEnoughItems;
import net.fabricmc.fabric.api.recipe.v1.sync.RecipeSynchronization;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("UnusedMixin")
@Mixin(JustEnoughItems.class)
public abstract class MixinJustEnoughItems {
    @Inject(method = "onInitialize()V", at = @At("TAIL"))
    private void syncRecipe(CallbackInfo ci) {
        RecipeSynchronization.synchronizeRecipeSerializer(RecipeSerializers.WOLOLO);
    }
}
