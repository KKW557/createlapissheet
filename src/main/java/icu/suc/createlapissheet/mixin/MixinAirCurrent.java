package icu.suc.createlapissheet.mixin;

import com.google.common.collect.Maps;
import com.llamalad7.mixinextras.sugar.Local;
import com.zurrtum.create.AllSynchedDatas;
import com.zurrtum.create.content.kinetics.fan.AirCurrent;
import com.zurrtum.create.content.kinetics.fan.IAirCurrentSource;
import com.zurrtum.create.content.kinetics.fan.processing.FanProcessing;
import com.zurrtum.create.content.kinetics.fan.processing.FanProcessingType;
import icu.suc.createlapissheet.FanProcessingTypes;
import icu.suc.createlapissheet.Tags;
import icu.suc.createlapissheet.content.kinetics.fan.EnchantmentFilterSegment;
import icu.suc.createlapissheet.content.kinetics.fan.EnchantmentFilterSegmentHolder;
import icu.suc.createlapissheet.content.kinetics.fan.processing.EnchantingFanProcessingCatalyst;
import icu.suc.createlapissheet.foundation.blockentity.behaviour.filtering.EnchantmentFilter;
import icu.suc.createlapissheet.foundation.blockentity.behaviour.filtering.EnchantmentFiltering;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.EnchantingTableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mixin(AirCurrent.class)
public abstract class MixinAirCurrent implements EnchantmentFilterSegmentHolder {
    @Unique
    private final List<EnchantmentFilterSegment> createlapissheet$enchantmentSegments = new ArrayList<>();

    @Shadow
    @Final
    public IAirCurrentSource source;

    @Shadow
    public Direction direction;

    @Shadow
    public float maxDistance;

    @Shadow
    public boolean pushing;

    @Inject(method = "shouldAlwaysPass", at = @At("RETURN"), cancellable = true)
    private static void injectShouldAlwaysPass(BlockState state, @NonNull CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue()) return;
        if (!state.is(Tags.Block.FAN_TRANSPARENT_REQUIRES_UNPOWERED)) return;
        if (state.hasProperty(BlockStateProperties.POWERED) && state.getValue(BlockStateProperties.POWERED))
            cir.setReturnValue(false);
    }

    @SuppressWarnings("AddedMixinMembersNamePattern")
    @Override
    public @NotNull List<@NotNull EnchantmentFilterSegment> getEnchantmentFilterSegments() {
        return createlapissheet$enchantmentSegments;
    }

    @SuppressWarnings("AddedMixinMembersNamePattern")
    @Override
    public @Nullable EnchantmentFilterSegment getSegmentAt(float offset) {
        if (offset >= 0 && offset <= maxDistance) {
            if (pushing) {
                for (var segment : createlapissheet$enchantmentSegments) {
                    if (offset <= segment.getEndOffset()) {
                        return segment;
                    }
                }
            } else {
                for (var segment : createlapissheet$enchantmentSegments) {
                    if (offset >= segment.getEndOffset()) {
                        return segment;
                    }
                }
            }
        }
        return null;
    }

    @Inject(method = "rebuild", at = @At("TAIL"))
    private void injectRebuild(CallbackInfo ci) {
        if (source.getSpeed() == 0) {
            createlapissheet$enchantmentSegments.clear();
            return;
        }

        createlapissheet$enchantmentSegments.clear();

        var level = source.getAirCurrentWorld();
        if (level == null || direction == null) return;

        EnchantmentFilterSegment currentSegment = null;
        var filter = EnchantmentFilter.EMPTY;
        BlockPos enchantingSource = null;

        var start = source.getAirCurrentPos();
        int limit = (int) Math.ceil(maxDistance);
        int searchStart = pushing ? 1 : limit;
        int searchEnd = pushing ? limit : 1;
        int searchStep = pushing ? 1 : -1;
        int toOffset = pushing ? -1 : 0;

        var side = pushing ? direction.getOpposite() : direction;

        for (int i = searchStart; i * searchStep <= searchEnd * searchStep; i += searchStep) {
            var currentPos = start.relative(direction, i);
            if (level.getBlockState(currentPos).is(Tags.Block.FAN_PROCESSING_CATALYSTS_ENCHANTING) || level.getFluidState(currentPos).is(Tags.Fluid.FAN_PROCESSING_CATALYSTS_ENCHANTING)) {
                enchantingSource = currentPos;
            }
            var newFilter = level.getBlockEntity(currentPos) instanceof EnchantmentFiltering filtering ? filtering.getEnchantmentFilter(side) : EnchantmentFilter.EMPTY;
            if (!newFilter.isEmpty()) {
                filter = newFilter;
            }
            if (currentSegment == null) {
                currentSegment = new EnchantmentFilterSegment();
                currentSegment.setFilter(filter);
                currentSegment.setStartOffset(i + toOffset);
                currentSegment.setSource(enchantingSource);
            } else if (!currentSegment.getFilter().equals(filter) || currentSegment.getSource() != enchantingSource) {
                currentSegment.setEndOffset(i + toOffset);
                createlapissheet$enchantmentSegments.add(currentSegment);
                filter = filter.plus(filter, currentSegment.getFilter());
                currentSegment = new EnchantmentFilterSegment();
                currentSegment.setFilter(filter);
                currentSegment.setStartOffset(i + toOffset);
                currentSegment.setSource(enchantingSource);
            }
        }
        if (currentSegment != null) {
            currentSegment.setEndOffset(searchEnd + searchStep + toOffset);
            createlapissheet$enchantmentSegments.add(currentSegment);
        }
    }

    @Redirect(method = "tickAffectedEntities", at = @At(value = "INVOKE", target = "Lcom/zurrtum/create/content/kinetics/fan/processing/FanProcessing;applyProcessing(Lnet/minecraft/world/entity/item/ItemEntity;Lcom/zurrtum/create/content/kinetics/fan/processing/FanProcessingType;)Z"))
    private boolean redirectApplyProcessing(ItemEntity entity, FanProcessingType type, @Local(name = "entityDistance") double entityDistance) {
        boolean b = FanProcessing.applyProcessing(entity, type);
        if (b && type == FanProcessingTypes.ENCHANTING) {
            var segment = getSegmentAt((float) entityDistance);
            if (segment == null) return false;
            var level = entity.level();
            var lookup = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
            var item = entity.getItem();
            var filter = segment.getFilter();

            var source = segment.getSource();
            if (source == null) return false;

            float i = 1;
            for (var pos : EnchantingTableBlock.BOOKSHELF_OFFSETS) {
                if (i == 16) break;
                if (EnchantingTableBlock.isValidBookShelf(level, source, pos)) ++i;
            }
            boolean flag = i == 1;
            if (!flag) i = i / 16;

            Map<Holder<Enchantment>, Integer> enchantments = Maps.newHashMap();
            int cost = 0;
            for (var enchantment : lookup) {
                if (!enchantment.canEnchant(item)) continue;
                var holder = lookup.wrapAsHolder(enchantment);
                if (!filter.invoke(holder)) continue;
                int maxLevel = enchantment.getMaxLevel();
                int l = flag ? maxLevel == 1 ? 0 : 1 : (int) (maxLevel * i);
                if (l == 0) continue;
                enchantments.put(holder, l);
                cost += enchantment.getAnvilCost() * l;
            }

            if (enchantments.isEmpty()) return false;

            if (EnchantingFanProcessingCatalyst.enchant(cost, level, source)) {
                enchantments.forEach(item::enchant);
            } else {
                AllSynchedDatas.ITEM_TYPE.set(entity, "");
                AllSynchedDatas.ITEM_TIME.set(entity, 0);
                return false;
            }
        }
        return b;
    }
}
