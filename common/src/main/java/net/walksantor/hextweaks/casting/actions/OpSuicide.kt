import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import net.minecraft.world.damagesource.DamageSources
import net.walksantor.hextweaks.HexTweaksRegistry

class OpSuicide : ConstMediaAction {
    override val argc = 0

    override fun execute(args: List<Iota>, ctx: CastingEnvironment): List<Iota> {
        ctx.caster?.hurt(HexTweaksRegistry.SUS_DAMMAGE,Float.MAX_VALUE)
        return listOf()
    }
}