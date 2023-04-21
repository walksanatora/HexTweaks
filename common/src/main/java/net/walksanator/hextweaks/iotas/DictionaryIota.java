package net.walksanator.hextweaks.iotas;

import at.petrak.hexcasting.api.spell.iota.Iota;
import at.petrak.hexcasting.api.spell.iota.IotaType;
import at.petrak.hexcasting.api.spell.iota.NullIota;
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class DictionaryIota extends Iota {
    public DictionaryIota(@NotNull Map<Iota,Iota> data) {
        super(HextweaksIotaType.DICTIONARY, data);
    }
    @Override
    public boolean isTruthy() {
        return ! getPayload().isEmpty();
    }

    public Map<Iota, Iota> getPayload() {
        if (payload instanceof Map<?, ?>) {
            @SuppressWarnings("unchecked")
            Map<Iota, Iota> map = (Map<Iota, Iota>) payload;
            return map;
        }
        return new HashMap<>();
    }

    @Override
    protected boolean toleratesOther(Iota that) {
        return that.getClass() == getClass();
    }

    @Override
    public @NotNull Tag serialize() {
        Map<Iota,Iota> data = getPayload();
        ListTag keys = new ListTag();
        ListTag vals = new ListTag();
        for (Map.Entry<Iota,Iota> dat : data.entrySet() ) {
            keys.add(HexIotaTypes.serialize(dat.getKey()));
            vals.add(HexIotaTypes.serialize(dat.getValue()));
        }
        CompoundTag output = new CompoundTag();
        output.put("k",keys);
        output.put("v",vals);
        return output;
    }

    public static IotaType<DictionaryIota> TYPE = new IotaType<>() {
        @Override
        public DictionaryIota deserialize(Tag tag, ServerLevel world) throws IllegalArgumentException {
            if (!(tag instanceof CompoundTag)) {
                throw new IllegalArgumentException("Expected this tag to be of type CompoundTag, but found %s".formatted(tag.getType().getName()));
            }
            ListTag keys = ((CompoundTag) tag).getList("k",Tag.TAG_COMPOUND);
            ListTag vals = ((CompoundTag) tag).getList("v",Tag.TAG_COMPOUND);

            ArrayList<Iota> keyiotas = new ArrayList<>();
            for (Tag keytag : keys) {
                keyiotas.add(
                        HexIotaTypes.deserialize((CompoundTag) keytag, world)
                );
            }
            ArrayList<Iota> valueiotas = new ArrayList<>();
            for (Tag valtag : vals) {
                valueiotas.add(
                        HexIotaTypes.deserialize((CompoundTag) valtag, world)
                );
            }
            short idx = 0;
            HashMap<Iota,Iota> output = new HashMap<>();
            if (keyiotas.size() <= valueiotas.size()) {
                for (Iota key : keyiotas) {
                    output.put(key, valueiotas.get(idx));
                    idx++;
                }
            } else {
                for (Iota val : valueiotas) {
                    output.put(keyiotas.get(idx), val);
                    idx++;
                }
            }
            return new DictionaryIota(output);
        }

        @Override
        public Component display(Tag tag) {
            if (!(tag instanceof CompoundTag)) {
                return Component.translatable("hexcasting.spelldata.unknown");
            }
            ListTag keys = ((CompoundTag) tag).getList("k",Tag.TAG_COMPOUND);
            ListTag vals = ((CompoundTag) tag).getList("v",Tag.TAG_COMPOUND);
            HashMap<Tag,Tag> kvs = new HashMap<>();
            int idx = 0;
            if (keys.size() <= vals.size()) {
                for (Tag key : keys) {
                    kvs.put(key,vals.get(idx));
                    idx++;
                }
            } else {
                for (Tag val : vals) {
                    kvs.put(keys.get(idx),val);
                    idx++;
                }
            }
            MutableComponent output = Component.empty();
            int left = kvs.size()-1;
            for (Map.Entry<Tag,Tag> dat : kvs.entrySet() ) {
                output.append(HexIotaTypes.getDisplay((CompoundTag) dat.getKey()));
                output.append(Component.literal(": ").withStyle(ChatFormatting.AQUA));
                output.append(HexIotaTypes.getDisplay((CompoundTag) dat.getValue()));
                if (!(left==0)) {output.append(Component.literal(",").withStyle(ChatFormatting.AQUA));}
                left--;
            }
            return Component.translatable("hextweaks.iota.dictionary", output).withStyle(ChatFormatting.AQUA);
        }

        @Override
        public int color() {
            return 0xff_00ffaa;
        }
    };
    public Iota get(Iota key) {
        for (Iota idx : getPayload().keySet()) {
            if (Iota.tolerates(idx,key)) {
                return getPayload().get(idx);
            }
        }
        return new NullIota();
    }
    public void set(Iota key, Iota value) {
        var targetKey = key;
        for (Iota idx: getPayload().keySet()) {
            if (Iota.tolerates(idx, key)) {
                targetKey = idx;
                break;
            }
        }
        getPayload().put(targetKey, value);
    }

    public void remove(Iota key) {
        Iota tgt = null;
        for (Iota idx: getPayload().keySet()) {
            if (Iota.tolerates(idx,key)) {
                tgt = idx;
            }
        }
        if (tgt != null) {
            getPayload().remove(tgt);
        }
    }

}
