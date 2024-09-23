package net.walksantor.hextweaks.computer

import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.*
import at.petrak.hexcasting.api.casting.math.HexDir
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.common.lib.HexRegistries
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes
import com.google.gson.GsonBuilder
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.datafixers.util.Either
import dan200.computercraft.shared.util.NBTUtil
import dev.architectury.platform.Platform
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.TagParser
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.phys.Vec3
import net.walksantor.hextweaks.HexTweaks
import net.walksantor.hextweaks.SecurityLevel
import org.jblas.DoubleMatrix
import ram.talia.moreiotas.api.casting.iota.*
import ram.talia.moreiotas.common.lib.hex.MoreIotasIotaTypes
import java.util.*
import kotlin.collections.HashMap


object IotaSerdeRegistry {
    private val TypeToID = mutableMapOf<IotaType<*>, ResourceLocation>() //encode half
    private val IDToSerde = mutableMapOf<ResourceLocation, IotaSerde<*>>() //decode half
    private val DEFAULT = object : IotaSerde<Iota> {
        override fun serialize(input: Iota): Any? = NBTUtil.toLua(input.serialize())
        override fun deserialize(value: Map<*, *>, world: Level): Iota? = null
    }

    fun <T: Iota> register(id: ResourceLocation, type: IotaType<T>, serde: IotaSerde<T>) {
        if (TypeToID[type] != null) {
            throw IllegalStateException("IotaType %s already registered with id %s, trying to overwrite with %s".format(
                type, TypeToID[type], id
            ))
        }
        TypeToID[type] = id
        IDToSerde[id] = serde
    }

    @Suppress("UNCHECKED_CAST")
    fun <T: Iota> toLua(iota: T): Any? {
        val id = TypeToID[iota.type]
        var value = (IDToSerde[id] as? IotaSerde<T>)
            ?.serialize(iota)?: DEFAULT.serialize(iota)
        if (value is Map<*, *>) {
            value = value.toMutableMap()
            value["iota\$serde"] = id.toString()
        }
        return value
    }

    fun fromLua(data: Any?, world: Level): Iota? {
        return when (data) {
            is String -> if (Platform.isModLoaded("moreiotas")) {StringIota.make(data)} else {GarbageIota()}
            is Number -> DoubleIota(data.toDouble())
            is Boolean -> BooleanIota(data)
            is Map<*, *> -> {
                val type = data["iota\$serde"]
                if (type.toString() != "null" && type is String) {
                    val resloc = ResourceLocation.tryParse(type)?: ResourceLocation("hextweaks","invalid")
                    val serializer = IDToSerde[resloc]
                    if (serializer == null) {
                        println("unknown serializer %s".format(resloc))
                        GarbageIota()
                    } else {
                        serializer.deserialize(data,world)
                    }
                } else {GarbageIota()}
            }
            else -> {
                if (data == null) {
                    NullIota()
                } else {
                    println("unreachable on class %s".format(data.javaClass))
                    GarbageIota()
                }
            }
        }
    }

    //HexCasting Iotas implementations
    init {
        register(modloc("list"), HexIotaTypes.LIST, object : IotaSerde<ListIota> {
            override fun serialize(input: ListIota): Any {
                val tag = mutableMapOf<Any, Any?>()
                for ((i, iota) in input.list.withIndex()) {
                    tag[i+1] = toLua(iota)
                }
                return tag
            }

            @Suppress("UNCHECKED_CAST")
            override fun deserialize(value: Map<*, *>, world: Level): ListIota {
                return ListIota((value.filter { it.key is Number } as Map<Number,Any>).toSortedMap(compareBy { it.toLong() }).map {
                    fromLua(it.value, world)?: GarbageIota()
                })
            }
        })
        register(modloc("boolean"), HexIotaTypes.BOOLEAN, object : IotaSerde<BooleanIota> {
            override fun serialize(input: BooleanIota): Any = input.bool
            override fun deserialize(value: Map<*, *>, world: Level): BooleanIota = BooleanIota(value["value"] as? Boolean ?: false)
        })
        register(modloc("null"), HexIotaTypes.NULL, object : IotaSerde<NullIota> {
            override fun serialize(input: NullIota): Any = NullIota()
            override fun deserialize(value: Map<*, *>, world: Level): NullIota = NullIota()
        })
        register(modloc("garbage"), HexIotaTypes.GARBAGE, object : IotaSerde<GarbageIota> {
            override fun serialize(input: GarbageIota): Any = mutableMapOf<Any, Any>()
            override fun deserialize(value: Map<*, *>, world: Level): GarbageIota = GarbageIota()
        })
        register(modloc("pattern"), HexIotaTypes.PATTERN, object : IotaSerde<PatternIota> {
            override fun serialize(input: PatternIota): Any {
                val result = mutableMapOf<String,Any>()
                result["startDir"] = input.pattern.startDir.toString()
                result["angle"] = input.pattern.anglesSignature()
                return result
            }

            override fun deserialize(value: Map<*, *>, world: Level): PatternIota = PatternIota(
                HexPattern.fromAngles(
                    value["angle"] as String,
                    HexDir.fromString(value["startDir"] as String)
                )
            )
        })
        register(modloc("vec3"), HexIotaTypes.VEC3, object : IotaSerde<Vec3Iota> {
            override fun serialize(input: Vec3Iota): Any = mutableMapOf(
                "x" to input.vec3.x,
                "y" to input.vec3.y,
                "z" to input.vec3.z
            )

            override fun deserialize(value: Map<*, *>, world: Level): Vec3Iota? {
                val x = (value["x"] as? Number)?.toDouble()?: return null
                val y = (value["y"] as? Number)?.toDouble()?: return null
                val z = (value["z"] as? Number)?.toDouble()?: return null
                return Vec3Iota(Vec3(x,y,z))
            }
        })
        //to be considdered: ContinuationIota, EntityIota
        register(modloc("continuation"), HexIotaTypes.CONTINUATION, object : IotaSerde<ContinuationIota> {
            private val GSON = GsonBuilder().setLenient().create()
            override fun serialize(input: ContinuationIota): Any =
                mutableMapOf(
                    "continuation_stack" to GSON.toJson(input.continuation)
                )

            override fun deserialize(value: Map<*, *>, world: Level): ContinuationIota? {
                if (HexTweaks.CONFIG!!.allowUnsafeDeserialization == SecurityLevel.RESTRICT) {return null}
                val stack = GSON.fromJson(
                    value["continuation_stack"] as? String?: return null,
                    SpellContinuation::class.java
                )?: return null
                return ContinuationIota(stack)
            }
        })
        register(modloc("entity"), HexIotaTypes.ENTITY, object : IotaSerde<EntityIota> {
            override fun serialize(input: EntityIota): Any? {
                val tag = input.serialize() as CompoundTag
                val uuid = tag.getUUID("uuid")
                val name = tag.getString("name")
                return  mutableMapOf(
                    "uuid" to uuid.toString(),
                    "name" to name
                )
            }

            override fun deserialize(value: Map<*, *>, world: Level): EntityIota? {
                if (HexTweaks.CONFIG!!.allowUnsafeDeserialization == SecurityLevel.RESTRICT) {return null}
                val uuid = UUID.fromString(value["uuid"] as? String?: return null)
                if (HexTweaks.CONFIG!!.allowUnsafeDeserialization == SecurityLevel.TRUENAME) {
                    if (!world.server!!.profileCache!!.get(uuid).isPresent) {
                        return null
                    }
                }
                val ctag = CompoundTag()
                ctag.putUUID("uuid", uuid)
                return HexIotaTypes.ENTITY.deserialize(ctag, world as ServerLevel)
            }
        })
    }

    //MoreIotas iotatypes
    init {
        if (Platform.isModLoaded("moreiotas")) {
            register(modloc("string"), MoreIotasIotaTypes.STRING, object : IotaSerde<StringIota> {
                override fun serialize(input: StringIota): Any = input.string
                override fun deserialize(value: Map<*, *>, world: Level): StringIota = throw IllegalStateException("This should have been handled by fromLua's when statement")
            })
            register(modloc("iotatype"), MoreIotasIotaTypes.IOTA_TYPE, object : IotaSerde<IotaTypeIota> {
                @Suppress("UNCHECKED_CAST")
                val REGISTRY: Registry<IotaType<*>> = BuiltInRegistries.REGISTRY.get(HexRegistries.IOTA_TYPE.location()) as? Registry<IotaType<*>>?: throw IllegalStateException("This should be loaded by now... this class isn't even touched until CC starts executing... why does the registry not exists")
                override fun serialize(input: IotaTypeIota): Any = mutableMapOf(
                    "id" to REGISTRY.getKey(input.iotaType).toString()
                )

                override fun deserialize(value: Map<*, *>, world: Level): IotaTypeIota? {
                    return IotaTypeIota(REGISTRY.get(ResourceLocation.tryParse(value["id"] as? String?: return null)) as IotaType<*>)
                }
            })
            register(modloc("entitytype"), MoreIotasIotaTypes.ENTITY_TYPE, object : IotaSerde<EntityTypeIota> {
                val REGISTRY: Registry<EntityType<*>> = BuiltInRegistries.ENTITY_TYPE
                override fun serialize(input: EntityTypeIota): Any = mutableMapOf(
                    "id" to REGISTRY.getKey(input.entityType).toString()
                )

                override fun deserialize(value: Map<*, *>, world: Level): EntityTypeIota? {
                    return EntityTypeIota(REGISTRY.get(ResourceLocation.tryParse(value["id"] as? String?: return null)) as EntityType<*>)
                }
            })
            register(modloc("itemstack"), MoreIotasIotaTypes.ITEM_STACK, object : IotaSerde<ItemStackIota> {
                val REGISTRY = BuiltInRegistries.ITEM
                override fun serialize(input: ItemStackIota): Any = mutableMapOf<String,Any?>(
                    "id" to REGISTRY.getKey(input.itemStack.item),
                    "count" to input.itemStack.count,
                    "data" to if (input.itemStack.tag != null) {input.itemStack.tag.toString()} else {null}
                )

                override fun deserialize(value: Map<*, *>, world: Level): ItemStackIota? {
                    val id = value["id"] as? String ?: return null
                    val resloc = ResourceLocation.tryParse(id)?: return null
                    if (!REGISTRY.containsKey(resloc)) {return null}
                    val stack = ItemStack(
                        REGISTRY.get(resloc),
                        (value["count"] as? Number)?.toInt()?: 1
                    )
                    value["data"].let {
                        if (it is String) {
                            try {
                                stack.tag = TagParser.parseTag(it)
                            } catch(_: CommandSyntaxException) { }
                        }
                    }
                    return ItemStackIota.createFiltered(stack)
                }

            })
            register(modloc("itemtype"), MoreIotasIotaTypes.ITEM_TYPE, object : IotaSerde<ItemTypeIota> {
                val BLOCKS = BuiltInRegistries.BLOCK
                val ITEMS  = BuiltInRegistries.ITEM
                override fun serialize(input: ItemTypeIota): Any {
                    val either: Either<Item, Block> = input.either
                    val oleft = either.left()
                    val map = mutableMapOf<String,Any?>()
                    if (oleft.isPresent) {
                        val left = oleft.get()
                        map["type"] = "item"
                        map["id"] = ITEMS.getKey(left)
                    } else {
                        val right = either.right().get()
                        map["type"] = "block"
                        map["id"] = BLOCKS.getKey(right)
                    }
                    return map
                }

                override fun deserialize(value: Map<*, *>, world: Level): ItemTypeIota? {
                    val type = value["type"] as? String ?: return null
                    return if (type == "item") {
                        ItemTypeIota(ITEMS.get(
                            ResourceLocation.tryParse(value["id"] as? String?: return null)?: return null
                        ))
                    } else {
                        ItemTypeIota(BLOCKS.get(
                            ResourceLocation.tryParse(value["id"] as? String?: return null)?: return null
                        ))
                    }
                }

            })
            register(modloc("matrix"), MoreIotasIotaTypes.MATRIX, object : IotaSerde<MatrixIota> {
                override fun serialize(input: MatrixIota): Any {
                    val matrix = input.matrix
                    val matrixTable: MutableMap<String, Any> = HashMap()
                    matrixTable["col"] = matrix.columns
                    matrixTable["row"] = matrix.rows
                    val matrixData: MutableMap<Double, Any> = HashMap()
                    for (i in 1..(matrix.columns * matrix.rows)) {
                        matrixData[i.toDouble()] = matrix.get(i - 1)
                    }
                    matrixTable["matrix"] = matrixData
                    return matrixTable
                }

                override fun deserialize(value: Map<*, *>, world: Level): MatrixIota? {
                    val col = (value["col"] as? Number)?.toInt()?: return null
                    val row = (value["row"] as? Number)?.toInt()?: return null

                    // should just default to full of 0?
                    val matrix = DoubleMatrix(row, col)
                    @Suppress("UNCHECKED_CAST") val matrixTable: Map<Number, Any> = value["matrix"] as? Map<Number,Any>?: return null

                    // just check for each, ignore extra and keep zero if
                    for (i in 1..(col * row)) {
                        if (matrixTable.containsKey(i.toDouble()) && matrixTable[i.toDouble()] is Number) {
                            matrix.put(i - 1, (matrixTable[i.toDouble()] as Number).toDouble())
                        }
                    }
                    return try {
                        MatrixIota(matrix)
                    } catch (e: MishapInvalidIota) {
                        null
                    }
                }

            })
        }
    }

}

private fun modloc(string: String) = ResourceLocation(HexTweaks.MOD_ID, string)

/**
 * this is an interface that controls Iota Serialization (to lua) and deserialization (from lua)
 */
interface IotaSerde<T: Iota> {
    /**
     * WARNING! although the output if of "Any" if it is anything other than Map you will need to mixin to fromLua to make it work (and various Cobalt functions to make it fix)
     * @param input the Iota input to be serialized
     * @return the value sent to lua
     */
    fun serialize(input: T): Any?
    fun deserialize(value: Map<*, *>, world: Level): T?
}