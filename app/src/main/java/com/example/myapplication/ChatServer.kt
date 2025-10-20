package com.example.myapplication

import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import java.lang.Exception
import java.net.InetSocketAddress
import java.net.NetworkInterface

class ChatServer(port: Int) : WebSocketServer(InetSocketAddress(port)) {
    private val connections = mutableMapOf<String, WebSocket>()
    private val messagesHistory = mutableListOf<String>()

    override fun onOpen(
        conn: WebSocket,
        handshake: ClientHandshake
    ) {
        val userId = handshake.getFieldValue("user-id") ?: "unknown"
        connections[userId] = conn
        conn.send("HISTORY|${messagesHistory.joinToString("||")}")
    }

    override fun onClose(
        conn: WebSocket,
        code: Int,
        reason: String,
        remote: Boolean
    ) {
        connections.values.remove(conn)
    }

    override fun onMessage(conn: WebSocket, message: String) {
        messagesHistory.add(message)
        connections.values.forEach { it.send(message) }
    }

    override fun onError(conn: WebSocket?, ex: Exception) {
        ex.printStackTrace()
    }

    override fun onStart() {
        println("Server started successfully")
    }

}

fun main() {
    val server = ChatServer(8888)
    server.start()
    println("Server running at ws://${getLocalIp()}:8888")
}



fun getLocalIp(): String {
    val interfaces = NetworkInterface.getNetworkInterfaces()
    while (interfaces.hasMoreElements()) {
        val iface = interfaces.nextElement()
        // Skip loopback and inactive interfaces
        if (!iface.isLoopback && iface.isUp) {
            val addresses = iface.inetAddresses
            while (addresses.hasMoreElements()) {
                val addr = addresses.nextElement()
                if (addr.isSiteLocalAddress) {
                    return addr.hostAddress
                }
            }
        }
    }
    throw Exception("No local IP address found")
}

