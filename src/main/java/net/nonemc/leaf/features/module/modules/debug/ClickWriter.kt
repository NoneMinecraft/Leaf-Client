package net.nonemc.leaf.features.module.modules.debug

import net.minecraft.network.play.client.C02PacketUseEntity
import net.nonemc.leaf.event.EventTarget
import net.nonemc.leaf.event.PacketEvent
import net.nonemc.leaf.features.MainLib.ChatPrint
import net.nonemc.leaf.features.module.Module
import net.nonemc.leaf.features.module.ModuleCategory
import net.nonemc.leaf.features.module.ModuleInfo
import net.nonemc.leaf.value.BoolValue
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException

@ModuleInfo(name = "ClickWriter", category = ModuleCategory.DEBUG)
class ClickWriter : Module() {
    private var lastClickTime: Long? = null
    private var writer: BufferedWriter? = null
    private val retainHistoricalData = BoolValue("RetainHistoricalData",false)
    override fun onEnable() {
        try {
            val dataDir = File("Leaf-Data")
            if (!dataDir.exists()) dataDir.mkdirs()
            val dataFile = File(dataDir, "click-data.txt").apply {
                if (exists()) {
                    if (!retainHistoricalData.get()) writeText("")
                } else {
                    createNewFile()
                }
            }
            writer = BufferedWriter(FileWriter(dataFile, true))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDisable() {
        try {
            writer?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        lastClickTime = null
        writer = null
    }

    @EventTarget
    private fun onPacket(event: PacketEvent) {
        if (event.packet !is C02PacketUseEntity) return
        val currentTime = System.currentTimeMillis()

        lastClickTime?.let { last ->
                val interval = currentTime - last
                try {
                writer?.apply {
                    write("$interval,")
                    ChatPrint("[ClickWriter] Click")
                    flush()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        lastClickTime = currentTime
    }
}