package net.walksantor.hextweaks.casting.environment;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.mishaps.Mishap;
import net.minecraft.server.level.ServerLevel;

import java.util.List;
import java.util.Optional;

public abstract class MishapAwareCastingEnvironment extends CastingEnvironment {
    protected MishapAwareCastingEnvironment(ServerLevel world) {
        super(world);
    }

    /**
     *
     * @param mishap - the mishap being called
     * @param ctx - the mishap context
     * @param stack - the stack at the time of the mishap
     * @return the stack post-modification (or none if you want the mishap it's self to handle it), if it is some it will cancel the rest
     */
    public Optional<List<Iota>> onMishap(Mishap mishap, Mishap.Context ctx, List<Iota> stack) {
        return Optional.empty();
    }

}
