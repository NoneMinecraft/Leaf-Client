package net.nonemc.leaf.file.configs

import com.google.gson.JsonArray
import com.google.gson.JsonParser
import net.minecraft.block.Block
import net.nonemc.leaf.Leaf
import net.nonemc.leaf.features.module.modules.render.XRay
import net.nonemc.leaf.file.FileManager
import net.nonemc.leaf.file.FileConfigManager
import java.io.File

class XRayConfig(file: File) : FileManager(file) {

    override fun loadConfig(config: String) {
        val xRay = Leaf.moduleManager[XRay::class.java]!!
        val jsonArray = JsonParser().parse(config).asJsonArray
        xRay.xrayBlocks.clear()

        for (jsonElement in jsonArray) {
            try {
                val block = Block.getBlockFromName(jsonElement.asString)
                if (xRay.xrayBlocks.contains(block)) {
                    println("[FileManager] Skipped xray block '" + block.registryName + "' because the block is already added.")
                    continue
                }
                xRay.xrayBlocks.add(block)
            } catch (throwable: Throwable) {
                println("[FileManager] Failed to add block to xray.")
            }
        }
    }

    override fun saveConfig(): String {
        val xRay = Leaf.moduleManager[XRay::class.java]!!
        val jsonArray = JsonArray()

        for (block in xRay.xrayBlocks)
            jsonArray.add(FileConfigManager.PRETTY_GSON.toJsonTree(Block.getIdFromBlock(block)))

        return FileConfigManager.PRETTY_GSON.toJson(jsonArray)
    }
}