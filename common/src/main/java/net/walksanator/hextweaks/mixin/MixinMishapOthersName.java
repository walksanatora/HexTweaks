package net.walksanator.hextweaks.mixin;

import at.petrak.hexcasting.api.spell.SpellList;
import at.petrak.hexcasting.api.spell.iota.Iota;
import at.petrak.hexcasting.api.spell.iota.ListIota;
import at.petrak.hexcasting.api.spell.mishaps.MishapOthersName;
import net.walksanator.hextweaks.iotas.DictionaryIota;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.ArrayList;
import java.util.List;

@Mixin(MishapOthersName.Companion.class)
public abstract class MixinMishapOthersName {
    @ModifyVariable(method = "getTrueNameFromDatum", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private Iota injected(Iota iota) {
        if (iota instanceof DictionaryIota dict) {
            List<Iota> list = new ArrayList<>();
            list.addAll(dict.getPayload().getFirst());
            list.addAll(dict.getPayload().getSecond());

            return new ListIota(new SpellList.LList(list));
        } else return iota;
    }
}
