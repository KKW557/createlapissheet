package icu.suc.createlapissheet.mixin;

import com.zurrtum.create.content.kinetics.belt.BeltBlockEntity;
import org.jspecify.annotations.NonNull;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.Arrays;

@Mixin(BeltBlockEntity.CasingType.class)
public abstract class MixinBeltBlockEntityCasingType {
    @Shadow
    @Final
    @Mutable
    private static BeltBlockEntity.CasingType[] $VALUES;

    @Inject(method = "<clinit>", at = @At(value = "FIELD", target = "Lcom/zurrtum/create/content/kinetics/belt/BeltBlockEntity$CasingType;$VALUES:[Lcom/zurrtum/create/content/kinetics/belt/BeltBlockEntity$CasingType;", shift = At.Shift.AFTER, opcode = Opcodes.PUTSTATIC))
    private static void injectClinit(CallbackInfo ci) throws Exception {
        var field = Unsafe.class.getDeclaredField("theUnsafe");
        field.setAccessible(true);
        var unsafe = (Unsafe) field.get(null);

        var clazz = BeltBlockEntity.CasingType.class;
        var value = (BeltBlockEntity.CasingType) unsafe.allocateInstance(clazz);

        modifyField(unsafe, value, Enum.class.getDeclaredField("name"), "LAPIS");
        modifyField(unsafe, value, Enum.class.getDeclaredField("ordinal"), $VALUES.length);

        var newValues = Arrays.copyOf($VALUES, $VALUES.length + 1);
        newValues[newValues.length - 1] = value;
        $VALUES = newValues;

        modifyField(unsafe, clazz, Class.class.getDeclaredField("enumConstants"), null);
        modifyField(unsafe, clazz, Class.class.getDeclaredField("enumConstantDirectory"), null);
    }

    @SuppressWarnings("deprecation")
    @Unique
    private static void modifyField(@NonNull Unsafe unsafe, Object object, Field field, Object value) {
        long offset = unsafe.objectFieldOffset(field);
        unsafe.putObject(object, offset, value);
    }

    @SuppressWarnings("deprecation")
    @Unique
    private static void modifyField(@NonNull Unsafe unsafe, Object object, Field field, int value) {
        long offset = unsafe.objectFieldOffset(field);
        unsafe.putInt(object, offset, value);
    }
}
