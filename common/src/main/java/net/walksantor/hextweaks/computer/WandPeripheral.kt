package net.walksantor.hextweaks.computer

import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM
import at.petrak.hexcasting.api.casting.iota.PatternIota
import at.petrak.hexcasting.api.casting.math.HexDir
import at.petrak.hexcasting.api.casting.math.HexPattern
import dan200.computercraft.api.lua.LuaFunction
import dan200.computercraft.api.lua.MethodResult
import dan200.computercraft.api.peripheral.IPeripheral
import dan200.computercraft.api.pocket.IPocketAccess
import dan200.computercraft.api.turtle.ITurtleAccess
import dan200.computercraft.api.turtle.TurtleSide
import net.minecraft.server.level.ServerLevel
import net.walksantor.hextweaks.casting.ComputerCastingEnv

class WandPeripheral(val turtleData: Pair<ITurtleAccess,TurtleSide>?, val pocketData: IPocketAccess?) : IPeripheral {
    val vm: CastingVM = CastingVM(CastingImage(), ComputerCastingEnv(turtleData,pocketData,getWorld()))



    fun getWorld(): ServerLevel {
        return if (turtleData==null) {
            pocketData!!.entity!!.level()
        } else {
            turtleData.first.level
        } as ServerLevel
    }

    override fun equals(other: IPeripheral?): Boolean = other is WandPeripheral

    override fun getType(): String = "wand"

    @LuaFunction
    fun getStack(): MethodResult = MethodResult.of(vm.image.stack.map {it.serialize()})

    @LuaFunction
    fun runPattern(dir: String, pattern: String) {
        val iota = PatternIota(HexPattern.fromAngles(pattern, HexDir.fromString(dir)))
        (vm.env as ComputerCastingEnv).level = getWorld()
        vm.queueExecuteAndWrapIota(iota,getWorld())
    }

}