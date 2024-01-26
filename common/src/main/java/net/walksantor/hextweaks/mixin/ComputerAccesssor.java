package net.walksantor.hextweaks.mixin;

import dan200.computercraft.core.apis.ComputerAccess;
import dan200.computercraft.core.apis.IAPIEnvironment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ComputerAccess.class)
public interface ComputerAccesssor {
    @Accessor(remap = false)
    IAPIEnvironment getEnvironment();
}
