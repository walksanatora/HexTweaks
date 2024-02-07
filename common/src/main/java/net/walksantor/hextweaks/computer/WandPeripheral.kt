package net.walksantor.hextweaks.computer

import at.petrak.hexcasting.api.HexAPI
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.casting.iota.NullIota
import at.petrak.hexcasting.api.casting.iota.PatternIota
import at.petrak.hexcasting.api.casting.math.HexDir
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes
import com.samsthenerd.duckyperiphs.hexcasting.utils.IotaLuaUtils
import dan200.computercraft.api.lua.LuaFunction
import dan200.computercraft.api.lua.MethodResult
import dan200.computercraft.api.peripheral.IComputerAccess
import dan200.computercraft.api.peripheral.IPeripheral
import dan200.computercraft.api.pocket.IPocketAccess
import dan200.computercraft.api.turtle.ITurtleAccess
import dan200.computercraft.api.turtle.TurtleSide
import net.minecraft.server.level.ServerLevel
import net.walksantor.hextweaks.casting.environment.ComputerCastingEnv

class WandPeripheral(val turtleData: Pair<ITurtleAccess,TurtleSide>?, val pocketData: IPocketAccess?) : IPeripheral {
    var vm: CastingVM? = null


    override fun attach(computer: IComputerAccess?) {
        vm = CastingVM(CastingImage(), ComputerCastingEnv(turtleData,pocketData,getWorld(),computer!!))
    }

    override fun detach(computer: IComputerAccess?) {
        vm = null
    }


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
    fun getStack(): MethodResult {
        val world = getWorld()
        return MethodResult.of(vm!!.image.stack.map {IotaLuaUtils.getLuaObject(it,world)})
    }

    @LuaFunction
    fun pushStack(obj: Any) {
        if (vm!!.image.stack is MutableList) {//WRONG it can be EmptyList which causes errors
            (vm!!.image.stack as MutableList).add(IotaLuaUtils.getIota(obj,getWorld()))
        }
    }

    @LuaFunction
    fun popStack(): Any {
        if (vm!!.image.stack is MutableList) {//WRONG it can be EmptyList which causes errors
            val iota = (vm!!.image.stack as MutableList).removeLast()
            return IotaLuaUtils.getLuaObject(iota,getWorld())
        }
        return IotaLuaUtils.getLuaObject(NullIota(),getWorld())
    }

    @LuaFunction
    fun clearStack(): Int {
        if (vm!!.image.stack is MutableList) {//WRONG it can be EmptyList which causes errors
            val size = vm!!.image.stack.size
            (vm!!.image.stack as MutableList).clear()
            return size
        }
        return 0
    }

    @LuaFunction
    fun setStack(stack: List<Any>) {
        if (vm!!.image.stack is MutableList) {//WRONG it can be EmptyList which causes errors
            val world = getWorld()
            (vm!!.image.stack as MutableList).clear()
            (vm!!.image.stack as MutableList).addAll(stack.map { IotaLuaUtils.getIota(it,world)})
        }
    }

    @LuaFunction
    fun enlightened(): Boolean = vm!!.env.isEnlightened

    @LuaFunction
    fun getRavenmind(): Any {
        val nbt = vm!!.image.userData.getCompound(HexAPI.RAVENMIND_USERDATA)
        if (nbt != null) {
            val world = getWorld()
            val iota = IotaType.deserialize(nbt,world)
            return IotaLuaUtils.getLuaObject(iota,world)
        }
        return MethodResult.of(null)
    }

    @LuaFunction
    fun setRavenmind(iota: Any) {
        val newLocal = IotaLuaUtils.getIota(iota,getWorld())
        if (newLocal.type == HexIotaTypes.NULL)
            vm!!.image.userData.remove(HexAPI.RAVENMIND_USERDATA)
        else
            vm!!.image.userData.put(HexAPI.RAVENMIND_USERDATA, IotaType.serialize(newLocal))
    }

    @LuaFunction(mainThread = true)
    fun runPattern(dir: String, pattern: String) {
        val iota = PatternIota(HexPattern.fromAngles(pattern, HexDir.fromString(dir)))
        (vm!!.env as ComputerCastingEnv).level = getWorld()
        vm!!.queueExecuteAndWrapIota(iota,getWorld())
    }

}