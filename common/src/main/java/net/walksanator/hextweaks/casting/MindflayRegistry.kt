package net.walksanator.hextweaks.casting

import at.petrak.hexcasting.api.mod.HexConfig
import at.petrak.hexcasting.ktxt.tellWitnessesThatIWasMurdered
import at.petrak.hexcasting.xplat.IXplatAbstractions
import dev.architectury.platform.Platform
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.npc.Villager
import net.walksanator.hextweaks.HexTweaks
import net.walksantor.hextweaks.casting.mindflay.*
import java.util.function.Function
import kotlin.math.pow

object MindflayRegistry {
    private val functions: HashMap<ResourceLocation,Function<MindflayInput, net.walksanator.hextweaks.casting.mindflay.MindflayResult>> = HashMap()

    /**
     * performs mindflays in sequential order.
     * @return whether any mindflay succeded and which registered function succeded
     */
    fun performMindflays(input: MindflayInput): Pair<Boolean,ResourceLocation?> {
        for (kv in functions) {
            val res = kv.value.apply(input)
            if (res.succeeded) {
                return Pair(true,kv.key)
            }
        }
        return Pair(false,null)
    }

    fun put(key: ResourceLocation, func: Function<MindflayInput, net.walksanator.hextweaks.casting.mindflay.MindflayResult>) {
        if (functions.containsKey(key)) {
            throw IllegalArgumentException("cannot register %s as %s is allready registered".format(key,key))
        }
        functions[key] = func;
    }

    fun del(key: ResourceLocation) {
        if (!functions.containsKey(key)) {
            throw IllegalArgumentException("cannot unregister %s as %s is not registered".format(key,key))
        }
        functions.remove(key)
    }

    /**
     * calculates the number of "points" (power level) of the villagers in this list
     * stronger minds make better ingredients
     *
     * @return the number of "points" for villagers
     */
    fun calcuateVillagerPoints(entities: List<Entity>): Int {
        var accu = 0.0;
        entities.filterIsInstance<Villager>().filter { !IXplatAbstractions.INSTANCE.isBrainswept(it) } .forEach { accu += 2.0.pow(it.villagerData.level) }
        return accu.toInt()
    }

    /**
     * performs the brainsweeps on brainsweepable entities
     * currently lets you flay anything TODO: make it not flay bosses
     */
    fun performBrainsweeps(inputs: List<Mob>, caster: ServerPlayer?) {
        val alert = HexConfig.server().doVillagersTakeOffenseAtMindMurder()
        for (pensive in inputs) {
            IXplatAbstractions.INSTANCE.setBrainsweepAddlData(pensive)
            if (pensive is Villager && alert)  {
                if (caster != null ) {
                    pensive.tellWitnessesThatIWasMurdered(caster)
                }
            }
        }
    }

    fun register() {
        if (Platform.isModLoaded("hexal")) {
            put(
                ResourceLocation(net.walksanator.hextweaks.HexTweaks.MOD_ID,"slipway/create"),
                MindflaySlipwayRitual::createSlipway
            )
            put(
                ResourceLocation(net.walksanator.hextweaks.HexTweaks.MOD_ID,"slipway/destroy"),
                MindflaySlipwayRitual::burstSlipway
            )
        }
        put(
            ResourceLocation(net.walksanator.hextweaks.HexTweaks.MOD_ID, "restock"),
            RestockingRitual::restockVillager
        )

    }
}