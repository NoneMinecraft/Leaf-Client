package net.ccbluex.liquidbounce.features.module.modules.render


import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.Render2DEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.init.Items
import net.minecraft.item.Item
import org.lwjgl.opengl.GL11
import java.awt.Color

@ModuleInfo(name = "DynamicCrosshair", category = ModuleCategory.RENDER)
class DynamicCrosshair : Module() {
    private val colorRedValue = IntegerValue("Red", 255, 0, 255)
    private val colorGreenValue = IntegerValue("Green", 255, 0, 255)
    private val colorBlueValue = IntegerValue("Blue", 255, 0, 255)
    private val colorAlphaValue = IntegerValue("Alpha", 255, 0, 255)
    private val widthValue = FloatValue("Width", 0.5f, 0.25f, 10f)
    private val sizeValue = FloatValue("Length", 7f, 0.25f, 15f)
    private val gapValue = FloatValue("Gap", 5f, 0.25f, 15f)
    private val dynamicValue = BoolValue("Dynamic", true)
   var expansionMultiplier = 0
    var expansionMultiplier2 = 0

    @EventTarget
    fun onRender2D(event: Render2DEvent) {
        val mc = Minecraft.getMinecraft()
        val screenWidth = event.scaledResolution.scaledWidth / 2
        val screenHeight = event.scaledResolution.scaledHeight / 2



        if (!mc.thePlayer.onGround) {expansionMultiplier = 25}
        if (mc.thePlayer.heldItem.item == Items.iron_hoe || mc.thePlayer.heldItem.item == Items.stone_hoe) {expansionMultiplier2 = 15}
        if (mc.thePlayer.heldItem.item == Items.diamond_shovel) {expansionMultiplier2 = 20}
        if (mc.thePlayer.heldItem.item == Items.golden_hoe) {expansionMultiplier2 = 25}
        if (mc.thePlayer.heldItem.item == Items.wooden_pickaxe) { expansionMultiplier2 = 5}
        if (mc.thePlayer.heldItem.item == Items.golden_pickaxe) {expansionMultiplier2 = 8}
        if (mc.thePlayer.heldItem.item == Items.stone_shovel) {expansionMultiplier2 = 3}
        if (mc.thePlayer.heldItem.item == Items.iron_axe || mc.thePlayer.heldItem.item == Items.stone_axe) {expansionMultiplier2 = -1}
        if (mc.thePlayer.heldItem.item == null) {expansionMultiplier2 = 0}

            val player = mc.thePlayer
        val movementSpeed = Math.sqrt((player.motionX * player.motionX + player.motionZ * player.motionZ).toDouble()).toFloat()

        val gap = if (dynamicValue.get()) gapValue.get() + movementSpeed * (expansionMultiplier+expansionMultiplier2+6) else gapValue.get()

        val red = colorRedValue.get() / 255f
        val green = colorGreenValue.get() / 255f
        val blue = colorBlueValue.get() / 255f
        val alpha = colorAlphaValue.get() / 255f


        GlStateManager.pushMatrix()
        GlStateManager.enableBlend()
        GlStateManager.disableTexture2D()
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)

        GL11.glColor4f(red, green, blue, alpha)
        GL11.glLineWidth(widthValue.get())

        GL11.glBegin(GL11.GL_LINES)

        // 上部线
        GL11.glVertex2f(screenWidth.toFloat(), (screenHeight - gap).toFloat())
        GL11.glVertex2f(screenWidth.toFloat(), (screenHeight - gap - sizeValue.get()).toFloat())

        // 下部线
        GL11.glVertex2f(screenWidth.toFloat(), (screenHeight + gap).toFloat())
        GL11.glVertex2f(screenWidth.toFloat(), (screenHeight + gap + sizeValue.get()).toFloat())

        // 左部线
        GL11.glVertex2f((screenWidth - gap).toFloat(), screenHeight.toFloat())
        GL11.glVertex2f((screenWidth - gap - sizeValue.get()).toFloat(), screenHeight.toFloat())

        // 右部线
        GL11.glVertex2f((screenWidth + gap).toFloat(), screenHeight.toFloat())
        GL11.glVertex2f((screenWidth + gap + sizeValue.get()).toFloat(), screenHeight.toFloat())

        GL11.glEnd()

        GlStateManager.enableTexture2D()
        GlStateManager.disableBlend()
        GlStateManager.popMatrix()
    }
}
