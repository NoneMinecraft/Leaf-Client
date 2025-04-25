package net.nonemc.leaf.ui.hud

import net.nonemc.leaf.injection.access.StaticStorage
import net.nonemc.leaf.ui.hud.designer.GuiHudDesigner
import net.nonemc.leaf.ui.hud.element.Element
import net.nonemc.leaf.ui.hud.element.elements.Notification
import net.nonemc.leaf.ui.hud.element.elements.Notifications
import net.nonemc.leaf.ui.hud.element.elements.ScoreboardElement
import net.nonemc.leaf.ui.hud.element.elements.Text
import net.nonemc.leaf.libs.clazz.ClassReflect
import net.nonemc.leaf.libs.base.MinecraftInstance
import org.lwjgl.opengl.GL11
import kotlin.math.max
import kotlin.math.min

open class HUD : MinecraftInstance() {

    val elements = mutableListOf<Element>()
    val notifications = mutableListOf<Notification>()

    companion object {

        val elements =
            ClassReflect.resolvePackage("${HUD::class.java.`package`.name}.element.elements", Element::class.java)
                .toTypedArray()

        /**
         * Create default HUD
         */
        fun createDefault(): HUD {
            val text1 = Text(x = 5.0, y = 8.0)
            text1.displayString.set("Leaf")
            text1.colorModeValue.set("Rainbow")
            text1.rectValue.set("Logo")
            text1.rectColorModeValue.set("Rainbow")

            return HUD()
                .addElement(ScoreboardElement())
                .addElement(Notifications())
        }
    }

    fun render(designer: Boolean, partialTicks: Float) {
        for (element in elements) {
            GL11.glPushMatrix()
            GL11.glScalef(element.scale, element.scale, element.scale)
            GL11.glTranslated(element.renderX, element.renderY, 0.0)

            try {
                element.border = element.drawElement(partialTicks)

                if (designer) {
                    element.border?.draw()
                }
            } catch (ex: Exception) {
                println("Something went wrong while drawing ${element.name} element in HUD.")
            }

            GL11.glPopMatrix()
        }
    }

    /**
     * Update all elements
     */
    fun update() {
        for (element in elements)
            element.updateElement()
    }

    /**
     * Handle mouse click
     */
    fun handleMouseClick(mouseX: Int, mouseY: Int, button: Int) {
        for (element in elements)
            element.handleMouseClick(
                (mouseX / element.scale) - element.renderX, (mouseY / element.scale) -
                        element.renderY, button
            )

        if (button == 0) {
            for (element in elements.reversed()) {
                if (!element.isInBorder(
                        (mouseX / element.scale) - element.renderX,
                        (mouseY / element.scale) - element.renderY
                    )
                ) {
                    continue
                }

                element.drag = true
                elements.remove(element)
                elements.add(element)
                break
            }
        }
    }

    /**
     * Handle released mouse key
     */
    fun handleMouseReleased() {
        for (element in elements)
            element.drag = false
    }

    /**
     * Handle mouse move
     */
    fun handleMouseMove(mouseX: Int, mouseY: Int) {
        if (mc.currentScreen !is GuiHudDesigner) {
            return
        }

        val scaledResolution = StaticStorage.scaledResolution

        for (element in elements) {
            val scaledX = mouseX / element.scale
            val scaledY = mouseY / element.scale
            val prevMouseX = element.prevMouseX
            val prevMouseY = element.prevMouseY

            element.prevMouseX = scaledX
            element.prevMouseY = scaledY

            if (element.drag) {
                val moveX = scaledX - prevMouseX
                val moveY = scaledY - prevMouseY

                if (moveX == 0F && moveY == 0F) {
                    continue
                }

                val border = element.border ?: continue

                val minX = min(border.x, border.x2) + 1
                val minY = min(border.y, border.y2) + 1

                val maxX = max(border.x, border.x2) - 1
                val maxY = max(border.y, border.y2) - 1

                val width = scaledResolution.scaledWidth / element.scale
                val height = scaledResolution.scaledHeight / element.scale

                if ((element.renderX + minX + moveX >= 0.0 || moveX > 0) && (element.renderX + maxX + moveX <= width || moveX < 0)) {
                    element.renderX = moveX.toDouble()
                }
                if ((element.renderY + minY + moveY >= 0.0 || moveY > 0) && (element.renderY + maxY + moveY <= height || moveY < 0)) {
                    element.renderY = moveY.toDouble()
                }
            }
        }
    }

    /**
     * Handle incoming key
     */
    fun handleKey(c: Char, keyCode: Int) {
        for (element in elements)
            element.handleKey(c, keyCode)
    }

    /**
     * Add [element] to HUD
     */
    fun addElement(element: Element): HUD {
        elements.add(element)
        return this
    }

    /**
     * Remove [element] from HUD
     */
    fun removeElement(element: Element): HUD {
        element.destroyElement()
        elements.remove(element)
        return this
    }

    /**
     * Clear all elements
     */
    fun clearElements() {
        for (element in elements)
            element.destroyElement()

        elements.clear()
    }

    /**
     * Add [notification]
     */
    fun addNotification(notification: Notification) =
        elements.any { it is Notifications } && notifications.add(notification)

}
