package net.walksantor.hextweaks

class HexTweaksConfig {
    var enableChunkreset = false
    var allowUnsafeDescription = "there are 3 security levels, 'UNSAFE' which allows all iotas deserialization, 'TRUENAME' which is basically UNSAFE but Entity Iotas are checked for truenames, and 'RESTRICT' which blocks unsafe iotas "
    var allowUnsafeDeserialization = SecurityLevel.TRUENAME
}

enum class SecurityLevel {
    UNSAFE,
    TRUENAME,
    RESTRICT
}