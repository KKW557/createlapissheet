package icu.suc.createlapissheet.mixin;

import com.zurrtum.create.catnip.data.Pair;
import com.zurrtum.create.content.fluids.drain.ItemDrainBlockEntity;
import com.zurrtum.create.content.fluids.transfer.GenericItemEmptying;
import com.zurrtum.create.infrastructure.fluids.FluidStack;
import icu.suc.createlapissheet.Advancements;
import icu.suc.createlapissheet.Fluids;
import icu.suc.createlapissheet.Tags;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(ItemDrainBlockEntity.class)
public abstract class MixinItemDrainBlockEntity {
    @Unique
    private boolean canBeDisenchanted;

    @Unique
    private boolean canItemBeDisenchanted(@NonNull Level level, @NonNull ItemStack stack) {
        var pos = ((ItemDrainBlockEntity) (Object) this).getBlockPos().above();
        return !EnchantmentHelper.getEnchantmentsForCrafting(stack).isEmpty() && level.getBlockState(pos).is(Tags.Block.DISENCHANTERS);
    }

    @Redirect(method = "continueProcessing", at = @At(value = "INVOKE", target = "Lcom/zurrtum/create/content/fluids/transfer/GenericItemEmptying;canItemBeEmptied(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;)Z"))
    private boolean redirectContinueProcessingCanItemBeEmptied(Level level, ItemStack stack) {
        canBeDisenchanted = false;
        if (GenericItemEmptying.canItemBeEmptied(level, stack)) return true;
        return canBeDisenchanted = canItemBeDisenchanted(level, stack);
    }

    @Redirect(method = "continueProcessing", at = @At(value = "INVOKE", target = "Lcom/zurrtum/create/content/fluids/transfer/GenericItemEmptying;emptyItem(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Z)Lcom/zurrtum/create/catnip/data/Pair;"))
    private Pair<FluidStack, ItemStack> redirectEmptyItem(Level world, ItemStack stack, boolean simulate) {
        if (canBeDisenchanted) {
            int amount = 0;
            for (var entry : EnchantmentHelper.getEnchantmentsForCrafting(stack).entrySet()) {
                amount += entry.getKey().value().getMinCost(entry.getIntValue()) * 4860;
            }
            if (simulate) {
                world.playSound(null, ((ItemDrainBlockEntity) (Object) this).getBlockPos().above(), SoundEvents.GRINDSTONE_USE, SoundSource.BLOCKS, 1.0f, world.random.nextFloat() * 0.1f + 0.9f);
            } else {
                stack.set(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
                if (stack.is(Items.ENCHANTED_BOOK)) {
                    stack = stack.transmuteCopy(Items.BOOK);
                }
                ((ItemDrainBlockEntity) (Object) this).award(Advancements.DISENCHANTING);
            }
            return Pair.of(new FluidStack(Fluids.EXPERIENCE, amount), stack);
        }
        return GenericItemEmptying.emptyItem(world, stack, simulate);
    }

    @Redirect(method = "tryInsertingFromSide", at = @At(value = "INVOKE", target = "Lcom/zurrtum/create/content/fluids/transfer/GenericItemEmptying;canItemBeEmptied(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;)Z"))
    private boolean redirectTryInsertingFromSideCanItemBeEmptied(Level world, ItemStack stack) {
        if (GenericItemEmptying.canItemBeEmptied(world, stack)) return true;
        return canItemBeDisenchanted(world, stack);
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lcom/zurrtum/create/content/fluids/transfer/GenericItemEmptying;canItemBeEmptied(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;)Z"))
    private boolean redirectTickCanItemBeEmptied(Level world, ItemStack stack) {
        if (GenericItemEmptying.canItemBeEmptied(world, stack)) return true;
        return canItemBeDisenchanted(world, stack);
    }

    @Redirect(method = "getAwardables", at = @At(value = "INVOKE", target = "Ljava/util/List;of(Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/List;"))
    private @NonNull @Unmodifiable List<Object> redirectGetAwardables(Object e1, Object e2) {
        return List.of(e1, e2, Advancements.DISENCHANTING);
    }
}
