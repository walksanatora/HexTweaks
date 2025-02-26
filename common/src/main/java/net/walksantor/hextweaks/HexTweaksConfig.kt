package net.walksantor.hextweaks

import net.minecraft.resources.ResourceLocation

class HexTweaksConfig {
    var computerAmbitMult = 1.0
    var computerCostMult = 1.0
    var computerBanList = emptyList<String>()
    var allowUnsafeDescription = "there are 3 security levels, 'UNSAFE' which allows all iotas deserialization, 'TRUENAME' which is basically UNSAFE but Entity Iotas are checked for truenames, and 'RESTRICT' which blocks unsafe iotas "
    var allowUnsafeDeserialization = SecurityLevel.TRUENAME

    @Transient
    var bannedCache = mutableMapOf<ResourceLocation,Boolean>()

    fun isPatternAllowed(pattern: ResourceLocation?): Boolean {
        if (pattern == null) {return true}
        if (bannedCache.containsKey(pattern)) {
            return bannedCache[pattern]!!
        } else {
            val banned = computerBanList.contains(pattern.toString())
            bannedCache[pattern] = banned
            return banned
        }
    }
}

enum class SecurityLevel {
    UNSAFE,
    TRUENAME,
    RESTRICT
}