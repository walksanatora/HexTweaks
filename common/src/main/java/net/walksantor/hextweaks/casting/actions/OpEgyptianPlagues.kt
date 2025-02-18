package net.walksantor.hextweaks.casting.actions

import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.castables.SpellAction.Result
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getInt
import at.petrak.hexcasting.api.casting.getLivingEntityButNotArmorStand
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.misc.MediaConstants
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.LivingEntity
import ram.talia.moreiotas.api.casting.iota.StringIota
import kotlin.jvm.optionals.getOrNull
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.pow

object OpEgyptianPlagues : SpellAction {
    override val argc: Int = 4
    override fun execute(args: List<Iota>, env: CastingEnvironment): Result {
        val target = args.getLivingEntityButNotArmorStand(0,argc)
        env.assertEntityInRange(target)
        val plague = args[1];
        if (plague !is StringIota) {throw MishapInvalidIota.ofType(plague,3,"string")}
        val plagueId = plague.string
        val realPlague = ResourceLocation.read(plagueId).result().getOrNull() ?: throw MishapInvalidIota.of(plague,3,"resloc")

        val thePlague = BuiltInRegistries.MOB_EFFECT.get(realPlague) ?: throw MishapInvalidIota.of(plague,3,"mobeffect")
        val duration = max(args.getInt(2,argc).absoluteValue,1) * 20
        val potency = max(args.getInt(3,argc),1)

        val mobi = MobEffectInstance(thePlague,duration,potency)
        return SpellAction.Result(
            EgyptianPlague(mobi,target),
            plagueId.length * (mobi.duration/20) * mobi.amplifier.toDouble().pow(4).toLong() * MediaConstants.DUST_UNIT,
            listOf()
        )
    }

    private class EgyptianPlague(val effect: MobEffectInstance, val target: LivingEntity) :
        VariableMediaActionResult() {
        override fun execute(env: CastingEnvironment): List<Iota> {
            target.addEffect(effect)
            return listOf()
        }

    }
}