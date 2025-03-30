package net.walksanator.hextweaks.fabric.mixin;


import de.dafuqs.spectrum.blocks.titration_barrel.TitrationBarrelBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TitrationBarrelBlockEntity.class)
public interface SealTimeAccessor {
    @Accessor("sealTime")
    long getSealTime();
    @Accessor("sealTime")
    void setSealTime(long sealTime);
}
