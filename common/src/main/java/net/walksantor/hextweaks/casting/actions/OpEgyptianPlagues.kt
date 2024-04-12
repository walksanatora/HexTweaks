package net.walksantor.hextweaks.casting.actions

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getInt
import at.petrak.hexcasting.api.casting.getLivingEntityButNotArmorStand
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.LivingEntity
import ram.talia.moreiotas.api.casting.iota.StringIota
import kotlin.jvm.optionals.getOrNull
import kotlin.math.pow

object OpEgyptianPlagues : VariableMediaAction {
    override val argc: Int = 4
    override fun render(args: List<Iota>, env: CastingEnvironment): VariableMediaAction.VariableMediaActionResult {
        val target = args.getLivingEntityButNotArmorStand(0,argc)
        env.assertEntityInRange(target)
        val plague = args[1];
        if (plague !is StringIota) {throw MishapInvalidIota.ofType(plague,3,"string")}
        val plagueId = plague.string
        val realPlague = ResourceLocation.read(plagueId).result().getOrNull() ?: throw MishapInvalidIota.of(plague,3,"resloc")

        val thePlague = BuiltInRegistries.MOB_EFFECT.get(realPlague) ?: throw MishapInvalidIota.of(plague,3,"mobeffect")
        val duration = args.getInt(2,argc)
        val potency = args.getInt(3,argc)

        val mobi = MobEffectInstance(thePlague,duration,potency)
        return EgyptianPlague(plagueId,mobi,target)
    }

    private class EgyptianPlague(effectId: String, val effect: MobEffectInstance, val target: LivingEntity) :
        VariableMediaAction.VariableMediaActionResult(((effectId.length * effect.duration * effect.amplifier).toDouble().pow(4)).toLong()) {
        override fun execute(env: CastingEnvironment): List<Iota> {
            target.addEffect(effect)
            return listOf()
        }

    }
}