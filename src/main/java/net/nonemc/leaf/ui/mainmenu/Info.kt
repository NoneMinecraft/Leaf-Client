package net.nonemc.leaf.ui.mainmenu

import net.minecraft.client.gui.ScaledResolution
import net.nonemc.leaf.features.module.modules.rage.rage.render.drawText
import net.nonemc.leaf.libs.base.mc
import net.nonemc.leaf.libs.render.RenderUtils.drawRect
import net.nonemc.leaf.libs.render.RenderUtils.drawRoundedRect
import net.nonemc.leaf.font.Fonts
import java.awt.Color

private fun drawWarningPanel(mouseX: Int, mouseY: Int, barHeight: Int, barColor: Color, message: String, label: String) {
    val sr = ScaledResolution(mc)
    val screenWidth = sr.scaledWidth

    drawRect(0f, 0f, screenWidth.toFloat(), barHeight.toFloat(), barColor.rgb)

    val isHovering = mouseX in 0..screenWidth && mouseY in 0..barHeight
    if (isHovering) {
        val font = mc.fontRendererObj
        val textWidth = font.getStringWidth(message)
        val textHeight = 8
        val hPadding = 16
        val vPadding = 10
        val panelWidth = textWidth + hPadding * 2
        val panelHeight = textHeight + vPadding * 2
        val panelX = (screenWidth - panelWidth) / 2f
        val panelY = barHeight + 5f

        drawRoundedRect(panelX, panelY, panelX + panelWidth, panelY + panelHeight, 2f, Color(0, 0, 0, 170).rgb)
        font.drawStringWithShadow(message, panelX + hPadding, panelY + (panelHeight - textHeight) / 2, -0x1)
    }

    val labelWidth = mc.fontRendererObj.getStringWidth(label)
    val labelX = (screenWidth - labelWidth) / 2
    val labelY = 4

    drawText(Fonts.noto, label, labelX, labelY, 255, 255, 255)
}

fun drawBan(mouseX: Int, mouseY: Int) {
    drawWarningPanel(
        mouseX, mouseY,
        barHeight = 15,
        barColor = Color(154, 0, 0, 255),
        message = "由于作弊违规,该账号已被安全检查封禁",
        label = "VAC  ( Valve 反作弊 ) "
    )
}

fun drawGlobalBan(mouseX: Int, mouseY: Int) {
    drawWarningPanel(
        mouseX, mouseY,
        barHeight = 15,
        barColor = Color(210, 210, 0, 255),
        message = "VAC已将您的游戏行为标记为违规.",
        label = "全球冷却时间  永久"
    )
}

fun drawNetworkConnection(mouseX: Int, mouseY: Int) {
    drawWarningPanel(
        mouseX, mouseY,
        barHeight = 15,
        barColor = Color(0, 210, 210, 255),
        message = "正在连接网络,请稍候.",
        label = "正在连接到 我的世界 网络......"
    )
}