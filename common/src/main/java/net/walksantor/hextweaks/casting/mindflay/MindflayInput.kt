package net.walksantor.hextweaks.casting.mindflay

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.EntityIota
import at.petrak.hexcasting.api.casting.iota.Vec3Iota
import com.mojang.datafixers.util.Either
import net.minecraft.world.entity.Mob

data class MindflayInput(val inputs: List<Mob>, val target: Either<EntityIota, Vec3Iota>, val env: CastingEnvironment)