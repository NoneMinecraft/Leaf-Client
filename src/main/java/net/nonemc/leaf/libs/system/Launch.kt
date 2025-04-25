package net.nonemc.leaf.libs.system

import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

fun isFirstLaunch(): Boolean {
    val path = when {
        System.getProperty("os.name").startsWith("Windows", ignoreCase = true) -> {
            System.getenv("APPDATA") + File.separator + "AppDataConfig"
        }
        System.getProperty("os.name").startsWith("Mac", ignoreCase = true) -> {
            System.getProperty("user.home") + "/Library/Application Support/AppDataConfig"
        }
        System.getProperty("os.name").startsWith("Linux", ignoreCase = true) -> {
            System.getProperty("user.home") + "/.config/AppDataConfig"
        }
        else -> {
            null
        }
    }
    if (path == null) return true
    val flagFile = File(path, "isFirstLaunch.flag")
    return if (flagFile.exists()) false else {
        try {
            Files.createDirectories(Paths.get(path))
            flagFile.createNewFile()
            true
        } catch (e: Exception) {
            true
        }
    }
}