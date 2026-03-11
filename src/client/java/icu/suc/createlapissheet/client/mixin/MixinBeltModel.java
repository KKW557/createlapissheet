package icu.suc.createlapissheet.client.mixin;

import com.zurrtum.create.catnip.data.Iterate;
import com.zurrtum.create.client.catnip.render.SpriteShiftEntry;
import com.zurrtum.create.client.infrastructure.model.BeltModel;
import com.zurrtum.create.client.infrastructure.model.WrapperBlockStateModel;
import com.zurrtum.create.client.model.NormalsBakedQuad;
import com.zurrtum.create.content.kinetics.belt.BeltBlock;
import com.zurrtum.create.content.kinetics.belt.BeltBlockEntity;
import icu.suc.createlapissheet.client.PartialModels;
import icu.suc.createlapissheet.client.SpriteShifts;
import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.SimpleModelWrapper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.QuadCollection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(BeltModel.class)
public abstract class MixinBeltModel extends WrapperBlockStateModel {
    @Unique
    private static final SpriteShiftEntry SPRITE_SHIFT = SpriteShifts.LAPIS_CASING_BELT;

    @Inject(method = "particleSpriteWithInfo", at = @At("HEAD"), cancellable = true)
    private void injectParticleSpriteWithInfo(@NonNull BlockAndTintGetter world, BlockPos pos, BlockState state, CallbackInfoReturnable<TextureAtlasSprite> cir) {
        if (world.getBlockEntity(pos) instanceof BeltBlockEntity be && "LAPIS".equals(be.casing.name()))
            cir.setReturnValue(SpriteShifts.LAPIS_CASING.getOriginal());
    }

    @Inject(method = "addPartsWithInfo", at = @At(value = "INVOKE", target = "Lcom/zurrtum/create/client/catnip/render/SpriteShiftEntry;getOriginal()Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;"), cancellable = true)
    private void injectAddPartsWithInfo(@NonNull BlockAndTintGetter world, BlockPos pos, BlockState state, RandomSource random, List<BlockModelPart> parts, CallbackInfo ci) {
        if (!(world.getBlockEntity(pos) instanceof BeltBlockEntity be)) return;
        if (!be.casing.name().equals("LAPIS")) return;
        var original = SPRITE_SHIFT.getOriginal();
        if (be.covered) {
            boolean alongX = state.getValue(BeltBlock.HORIZONTAL_FACING).getAxis() == Direction.Axis.X;
            parts.add(replaceQuads(original, alongX ? PartialModels.LAPIS_BELT_COVER_X.get() : PartialModels.LAPIS_BELT_COVER_Z.get()));
        }
        for (var part : model.collectParts(random)) {
            parts.add(replaceQuads(original, part));
        }
        ci.cancel();
    }

    @Unique
    private static @NonNull BlockModelPart replaceQuads(TextureAtlasSprite replace, @NonNull BlockModelPart part) {
        var builder = new QuadCollection.Builder();
        for (var quad : part.getQuads(null)) {
            builder.addUnculledFace(replaceQuad(replace, quad));
        }
        for (var direction : Iterate.directions) {
            for (var quad : part.getQuads(direction)) {
                builder.addCulledFace(direction, replaceQuad(replace, quad));
            }
        }
        return new SimpleModelWrapper(builder.build(), part.useAmbientOcclusion(), part.particleIcon());
    }

    @Unique
    private static long calcSpriteUv(long packedUv) {
        float u = UVPair.unpackU(packedUv);
        float v = UVPair.unpackV(packedUv);
        return UVPair.pack(SPRITE_SHIFT.getTargetU(u), SPRITE_SHIFT.getTargetV(v));
    }

    @Unique
    private static @NonNull BakedQuad replaceQuad(TextureAtlasSprite replace, @NonNull BakedQuad quad) {
        var original = quad.sprite();
        if (original != replace) {
            return quad;
        }
        var newQuad = new BakedQuad(
                quad.position0(),
                quad.position1(),
                quad.position2(),
                quad.position3(),
                calcSpriteUv(quad.packedUV0()),
                calcSpriteUv(quad.packedUV1()),
                calcSpriteUv(quad.packedUV2()),
                calcSpriteUv(quad.packedUV3()),
                quad.tintIndex(),
                quad.direction(),
                quad.sprite(),
                quad.shade(),
                quad.lightEmission()
        );
        NormalsBakedQuad.setNormals(newQuad, NormalsBakedQuad.getNormals(quad));
        return newQuad;
    }
}
