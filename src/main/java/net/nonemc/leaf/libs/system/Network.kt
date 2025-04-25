package net.nonemc.leaf.libs.system

import java.net.NetworkInterface
import java.net.SocketException

fun isNetworkConnected(): Boolean {
    return try {
        NetworkInterface.getNetworkInterfaces().toList().any { ni ->
            !ni.isLoopback && ni.isUp
        }
    } catch (e: SocketException) {
        false
    }
}