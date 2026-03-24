package icu.suc.createlapissheet.mixin.accessor;

import com.zurrtum.create.content.kinetics.fan.AirCurrent;
import com.zurrtum.create.content.kinetics.fan.processing.FanProcessingType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AirCurrent.AirCurrentSegment.class)
public interface AccessorAirCurrent$AirCurrentSegment {
    @Accessor("type")
    FanProcessingType getType();
}
