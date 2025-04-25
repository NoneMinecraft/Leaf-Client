import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.util.ResourceLocation
import net.nonemc.leaf.libs.render.RenderUtils.drawImage
import java.awt.image.BufferedImage
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import javax.imageio.ImageIO
private val textureCache = mutableMapOf<String, Pair<ResourceLocation, Long>>()

fun drawDynamicImage(filePath: String, x: Int, y: Int, width: Int, height: Int) {
    val mc = Minecraft.getMinecraft()
    val file = File(filePath)
    if (!file.exists()) return
    val lastModified = file.lastModified()
    val (cachedLoc, cachedTime) = textureCache[filePath] ?: (null to -1L)
    val resourceLocation: ResourceLocation
    if (cachedTime != lastModified) {
        cachedLoc?.let {
            mc.textureManager.getTexture(it)?.let { tex ->
                if (tex is DynamicTexture) tex.deleteGlTexture()
            }
            mc.textureManager.deleteTexture(it)
            textureCache.remove(filePath)
        }
        try {
            val tmpFile = File(mc.mcDataDir, "tmp_dynamic_img.png")
            Files.copy(file.toPath(), tmpFile.toPath(), StandardCopyOption.REPLACE_EXISTING)

            val image = ImageIO.read(tmpFile)
            val dynamicTexture = DynamicTexture(image)
            resourceLocation = ResourceLocation("leaf/dynamic/${file.nameWithoutExtension}_${System.currentTimeMillis()}")
            mc.textureManager.loadTexture(resourceLocation, dynamicTexture)

            textureCache[filePath] = resourceLocation to lastModified
        } catch (e: Exception) {
            e.printStackTrace()
            return
        }
    } else {
        resourceLocation = cachedLoc!!
    }
    drawImage(resourceLocation,x,y,width,height)
}
