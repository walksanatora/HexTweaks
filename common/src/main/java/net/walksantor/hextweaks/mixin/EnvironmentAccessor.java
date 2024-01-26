package net.walksantor.hextweaks.mixin;

import dan200.computercraft.core.computer.ComputerEnvironment;
import dan200.computercraft.core.computer.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Environment.class)
public interface EnvironmentAccessor {
    @Accessor(remap = false)
    ComputerEnvironment getEnvironment();
}
