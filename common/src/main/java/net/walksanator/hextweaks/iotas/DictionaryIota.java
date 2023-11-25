package net.walksanator.hextweaks.iotas;

import at.petrak.hexcasting.api.spell.iota.EntityIota;
import at.petrak.hexcasting.api.spell.iota.Iota;
import at.petrak.hexcasting.api.spell.iota.IotaType;
import at.petrak.hexcasting.api.spell.iota.NullIota;
import at.petrak.hexcasting.api.spell.mishaps.MishapInvalidIota;
import at.petrak.hexcasting.api.spell.mishaps.MishapOthersName;
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import kotlin.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.walksanator.hextweaks.HexTweaks;
import net.walksanator.hextweaks.mishap.MishapDictionaryTooBig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static net.walksanator.hextweaks.HexTweaks.cannotBeDictKey;
import static net.walksanator.hextweaks.HexTweaks.cannotBeDictValue;

public class DictionaryIota extends Iota {
    public DictionaryIota(@NotNull Pair<List<Iota>,List<Iota>> data) {
        super(HextweaksIotaType.DICTIONARY, data);
    }
    @Override
    public boolean isTruthy() {
        return getPayload().getFirst().size()!=0;
    }

    public Pair<List<Iota>,List<Iota>> getPayload() {
        if (payload instanceof Pair<?,?>) {
            @SuppressWarnings("unchecked")
            Pair<List<Iota>,List<Iota>> map = (Pair<List<Iota>,List<Iota>>) payload;
            return map;
        }
        return new Pair<>(new ArrayList<>(),new ArrayList<>());
    }

    public DictionaryIota() {super(HextweaksIotaType.DICTIONARY,new Pair<>(
            new ArrayList<>(),
            new ArrayList<>()
    ));}

    @Override
    protected boolean toleratesOther(Iota that) {
        return that.getClass() == getClass();
    }

    @Override
    public @NotNull Tag serialize() {
        Pair<List<Iota>,List<Iota>> data = getPayload();
        ListTag keys = new ListTag();
        ListTag vals = new ListTag();
        for (Iota dat : data.getFirst() ) {
            keys.add(HexIotaTypes.serialize(dat));
        }
        for (Iota dat : data.getSecond() ) {
            vals.add(HexIotaTypes.serialize(dat));
        }
        CompoundTag output = new CompoundTag();
        output.put("k",keys);
        output.put("v",vals);
        return output;
    }

    public static final IotaType<DictionaryIota> TYPE = new IotaType<>() {
        @Override
        public DictionaryIota deserialize(Tag tag, ServerLevel world) throws IllegalArgumentException {
            if (!(tag instanceof CompoundTag)) {
                throw new IllegalArgumentException("Expected this tag to be of type CompoundTag, but found %s".formatted(tag.getType().getName()));
            }
            ListTag nbtkeys = ((CompoundTag) tag).getList("k",Tag.TAG_COMPOUND);
            ListTag nbtvals = ((CompoundTag) tag).getList("v",Tag.TAG_COMPOUND);

            ArrayList<Iota> keys = new ArrayList<>();
            for (Tag keytag : nbtkeys) {
                keys.add(
                        HexIotaTypes.deserialize((CompoundTag) keytag, world)
                );
            }
            ArrayList<Iota> values = new ArrayList<>();
            for (Tag valtag : nbtvals) {
                values.add(
                        HexIotaTypes.deserialize((CompoundTag) valtag, world)
                );
            }
            return new DictionaryIota(new Pair<>(keys,values));
        }

        @Override
        public Component display(Tag tag) {
            if (!(tag instanceof CompoundTag)) {
                return Component.translatable("hexcasting.spelldata.unknown");
            }
            ListTag keys = ((CompoundTag) tag).getList("k",Tag.TAG_COMPOUND);
            ListTag vals = ((CompoundTag) tag).getList("v",Tag.TAG_COMPOUND);
            MutableComponent output = Component.empty();
            int left = keys.size()-1;
            int idx = 0;
            for (Tag dat : keys ) {
                output.append(HexIotaTypes.getDisplay((CompoundTag) dat));
                output.append(Component.literal(": ").withStyle(ChatFormatting.AQUA));
                output.append(HexIotaTypes.getDisplay((CompoundTag) vals.get(idx)));
                if (!(left==0)) {output.append(Component.literal(",").withStyle(ChatFormatting.AQUA));}
                left--; idx++;
            }
            return Component.translatable("hextweaks.iota.dictionary", output).withStyle(ChatFormatting.AQUA);
        }

        @Override
        public int color() {
            return 0xff_00ffaa;
        }
    };
    public Iota get(Iota key) {
        Pair<List<Iota>,List<Iota>> data = getPayload();
        if (data.getFirst().isEmpty()) {return new NullIota();}
        for (int i = 0; i < data.getFirst().size(); i++)
        {
            Iota idx = data.getFirst().get(i);
            if (Iota.tolerates(idx,key)) {
                return data.getSecond().get(i);
            }
        }
        return new NullIota();
    }

    // --Commented out by Inspection (5/5/23, 3:08 PM):public void set(Iota key, Iota value) throws MishapOthersName, MishapInvalidIota, MishapDictionaryTooBig {set(key,value,null);}

    public void set(Iota key, Iota value, @Nullable Player caster) throws MishapOthersName, MishapInvalidIota, MishapDictionaryTooBig {
        set(key,value,caster,false);
    }

    public void set(Iota key, Iota value, @Nullable Player caster, Boolean sudo) throws MishapOthersName, MishapInvalidIota, MishapDictionaryTooBig {
        if (getPayload().getFirst().size() >= HexTweaks.MaxKeysInDictIota && !sudo) {
            throw new MishapDictionaryTooBig(this);
        }
        if (cannotBeDictKey.contains(key.getClass())&& !sudo) {
            throw new MishapInvalidIota(key,0,Component.translatable("hextweaks.mishap.cannotbekey"));
        } else if (cannotBeDictValue.contains(value.getClass())&& !sudo){
            throw new MishapInvalidIota(value,1,Component.translatable("hextweaks.mishap.cannotbevalue"));
        }
        if (((key instanceof EntityIota) || (value instanceof EntityIota)) && caster != null && !sudo) {
            Player truename = MishapOthersName.getTrueNameFromArgs(List.of(key,value),caster);
            if (truename != null)
                throw new MishapOthersName(truename);
        }
        Pair<List<Iota>,List<Iota>> data = getPayload();
        int targetKey = -1;
        for (int i = 0; i < data.getFirst().size(); i++)
        {
            Iota idx = data.getFirst().get(i);
            if (Iota.tolerates(idx,key)) {
                targetKey = i;
            }
        }
        if (targetKey == -1) { //key is not in the DictIota, add it
            data.getFirst().add(key);
            data.getSecond().add(value);
            return;
        }
        data.getSecond().set(targetKey,value); //since key is already in the dict we can just set the value
    }

    public void remove(Iota key) {
        int targetKey = -1;
        Pair<List<Iota>,List<Iota>> data = getPayload();
        if (data.getFirst().isEmpty()) {return;}
        for (int i = 0; i < data.getFirst().size(); i++)
        {
            Iota idx = data.getFirst().get(i);
            if (Iota.tolerates(idx,key)) {
                targetKey = i;
            }
        }
        if (targetKey == -1) {return;} //key is not in dictionary
        data.getFirst().remove(targetKey);
        data.getSecond().remove(targetKey);
    }

}
