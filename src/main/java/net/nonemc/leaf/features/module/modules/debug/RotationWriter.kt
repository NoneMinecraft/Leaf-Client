package net.nonemc.leaf.features.module.modules.debug

import net.minecraft.util.MathHelper
import net.nonemc.leaf.event.EventTarget
import net.nonemc.leaf.event.UpdateEvent
import net.nonemc.leaf.features.MainLib.ChatPrint
import net.nonemc.leaf.features.module.Module
import net.nonemc.leaf.features.module.ModuleCategory
import net.nonemc.leaf.features.module.ModuleInfo
import net.nonemc.leaf.value.BoolValue
import java.io.BufferedWriter
import java.io.File

@ModuleInfo(name = "RotationWriter", category = ModuleCategory.DEBUG)
class RotationWriter : Module() {
    private var writer: BufferedWriter? = null
    private val wrapAngleTo180 = BoolValue("WrapAngleTo180",true)
    private val retainHistoricalData = BoolValue("RetainHistoricalData",false)
    override fun onEnable() {
        val dataDir = File("Leaf-Data").apply {
            if (!exists()) mkdirs()
        }
        val dataFile = File(dataDir, "rotation-data.txt").apply {
            if (exists()) {
                if (!retainHistoricalData.get()) writeText("")
            } else {
                createNewFile()
            }
        }
      if (!retainHistoricalData.get()) writer = dataFile.bufferedWriter().apply {
            write("")
            flush()
        }
    }

    @EventTarget
    private fun onUpdate(event: UpdateEvent) {
        val player = mc.thePlayer ?: return

        val yaw = if (wrapAngleTo180.get()) MathHelper.wrapAngleTo180_float(player.rotationYaw) else player.rotationYaw
        val pitch = if (wrapAngleTo180.get()) MathHelper.wrapAngleTo180_float(player.rotationPitch) else player.rotationPitch
        writer?.apply {
            write("[$yaw,$pitch],")
            flush()
            ChatPrint("[$yaw,$pitch]")
        }
    }
    override fun onDisable() {
        writer?.close()
    }
}