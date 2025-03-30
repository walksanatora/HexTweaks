package net.walksanator.hextweaks

import com.mojang.datafixers.util.Either
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.StringRepresentable
import net.minecraft.util.StringRepresentable.EnumCodec
import org.apache.commons.codec.binary.Hex

class HexTweaksConfig(
    val computerAmbitMult: Float = 1.0f,
    val computerCostMult: Float = 1.0f,
    val computerBanList: List<String> = emptyList(),
    val allowUnsafeDeserialization: SecurityLevel = SecurityLevel.TRUENAME
) {


    @Transient
    var allowCache = mutableMapOf<ResourceLocation,Boolean>()

    fun isPatternAllowed(pattern: ResourceLocation?): Boolean {
        if (pattern == null) {return true}
        if (allowCache.containsKey(pattern)) {
            return allowCache[pattern]!!
        } else {
            val allowed = !computerBanList.contains(pattern.toString())
            allowCache[pattern] = allowed
            return allowed
        }
    }
    companion object {
        val DEFAULT = HexTweaksConfig()
        val CODEC = Codec.either<HexTweaksConfig,HexTweaksConfig>(
            RecordCodecBuilder.create {
                it.group(
                    Codec.FLOAT.fieldOf("computerAmbitMult").forGetter({it.computerAmbitMult}),
                    Codec.FLOAT.fieldOf("computerCostMult").forGetter({it.computerCostMult}),
                    Codec.STRING.listOf().fieldOf("computerBanList").forGetter({it.computerBanList}),
                    StringRepresentable.fromEnum(SecurityLevel::values).fieldOf("allowUnsafeDeserialization").forGetter({it.allowUnsafeDeserialization})
                ).apply(it, ::HexTweaksConfig)
            },
            Codec.unit(DEFAULT)
        )
    }
}

enum class SecurityLevel: StringRepresentable {
    UNSAFE {
        override fun getSerializedName(): String = "UNSAFE"
    },
    TRUENAME {
        override fun getSerializedName(): String = "TRUENAME"
    },
    RESTRICT {
        override fun getSerializedName(): String = "RESTRICT"
    }
}