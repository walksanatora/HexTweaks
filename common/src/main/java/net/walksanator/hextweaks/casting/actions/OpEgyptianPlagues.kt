package net.walksanator.hextweaks.casting.actions

import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.castables.SpellAction.Result
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getDoubleBetween
import at.petrak.hexcasting.api.casting.getIntBetween
import at.petrak.hexcasting.api.casting.getLivingEntityButNotArmorStand
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapDisallowedSpell
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.misc.MediaConstants
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.LivingEntity
import net.walksanator.hextweaks.HexTweaks
import ram.talia.moreiotas.api.casting.iota.StringIota
import kotlin.jvm.optionals.getOrNull
import kotlin.math.pow

object OpEgyptianPlagues : SpellAction {
    override val argc: Int = 4
    override fun execute(args: List<Iota>, env: CastingEnvironment): Result {
        val target = args.getLivingEntityButNotArmorStand(0,argc)
        env.assertEntityInRange(target)
        val argEffect = args[1];
        if (argEffect !is StringIota) {throw MishapInvalidIota.ofType(argEffect,3,"string")}
        val effectResLoc = ResourceLocation.read(argEffect.string).result().getOrNull() ?: throw MishapInvalidIota.of(argEffect,3,"resloc")

        val effectString = effectResLoc.toString()
        /*Blacklist*/ if(!HexTweaks.getCONFIG().isNadithEffectAllowed(effectString)) throw MishapDisallowedSpell()

        val mobEffect = BuiltInRegistries.MOB_EFFECT.get(effectResLoc) ?: throw MishapInvalidIota.of(argEffect,3,"mobeffect")
        val durationTicks = (args.getDoubleBetween(2, 0.05, 1000000.0, argc)*20).toInt()
        val potency =  args.getIntBetween(3,1,5,argc)
        val mobEffInst = MobEffectInstance(mobEffect,durationTicks,potency-1)

        val multiplier = HexTweaks.getCONFIG().nadithEffectMultipliers.get(effectString)?: //Use config
            (effectString.length - if(effectResLoc.namespace == "minecraft") 10 else 0) //Use string length

        return SpellAction.Result(
            Nadith(mobEffInst,target),
                (MediaConstants.DUST_UNIT.toDouble()
                    * multiplier.toDouble()
                    * (if (mobEffect.isInstantenous) 1 else durationTicks.toDouble()/20).toDouble()
                    * potency.toDouble().pow(4)
                ).toLong(),
            listOf()
        )
    }

    private class Nadith(val effect: MobEffectInstance, val target: LivingEntity) :
        VariableMediaActionResult() {
        override fun execute(env: CastingEnvironment): List<Iota> {
            if (effect.effect.isInstantenous)
                effect.applyEffect(target) else
                target.addEffect(effect)

            return listOf()
        }

    }
}