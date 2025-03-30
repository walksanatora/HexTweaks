package net.walksanator.hextweaks.casting.actions.tcp

import net.walksantor.hextweaks.casting.mishap.tcp.MishapNoConnection
import java.net.Socket

object TcpConnections {
    //note to myself reading
    // outputStream is the OUTGOING bytes
    // and inputStream is the INCOMING bytes
    private val CONNECTIONS = HashMap<Int,Socket>()

    fun readBytes(sid: Int, bytes: Int): ByteArray {
        val array = ByteArray(bytes)
        CONNECTIONS[sid]?.getInputStream()?.read(array) ?: throw MishapNoConnection()
        return array
    }

    fun writeBytes(sid: Int, bytes: ByteArray) {
        CONNECTIONS[sid]?.getOutputStream()?.write(bytes) ?: throw MishapNoConnection()
    }
}