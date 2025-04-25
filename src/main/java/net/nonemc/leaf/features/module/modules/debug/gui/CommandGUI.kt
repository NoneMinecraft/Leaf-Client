package net.nonemc.leaf.features.module.modules.debug.gui

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.GuiTextField
import net.minecraft.util.EnumChatFormatting
import net.nonemc.leaf.features.module.modules.debug.CMD
import org.lwjgl.input.Keyboard
import org.lwjgl.input.Mouse
import org.lwjgl.opengl.GL11
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*
import kotlin.math.max
import kotlin.math.min

class CommandGUI(private val module: CMD) : GuiScreen() {

    private lateinit var inputField: GuiTextField
    private val outputLines = LinkedList<String>()
    private var scrollOffset = 0
    private var maxVisibleLines = 0
    private var isDraggingScroll = false
    private var lastMouseY = 0
    private var scrollBarWidth = 6
    private var scrollBarHeight = 0
    private var scrollBarY = 0
    private var scrollBarDraggingOffset = 0
    private var savedInputText = ""
    private var savedScrollOffset = 0

    override fun initGui() {
        val guiWidth = 320
        val guiHeight = 180
        val left = width / 2 - guiWidth / 2
        val top = height / 2 - guiHeight / 2

        inputField = GuiTextField(
            0, mc.fontRendererObj,
            left + 2,
            top + guiHeight - 20,
            guiWidth - 4 - scrollBarWidth,
            18
        ).apply {
            text = savedInputText
            maxStringLength = 256
            isFocused = true
        }

        maxVisibleLines = (guiHeight - 30) / mc.fontRendererObj.FONT_HEIGHT
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        drawDefaultBackground()
        val guiWidth = 320
        val guiHeight = 180
        val left = width / 2 - guiWidth / 2
        val top = height / 2 - guiHeight / 2

        drawRect(left, top, left + guiWidth, top + guiHeight, 0x99000000.toInt())

        GL11.glEnable(GL11.GL_SCISSOR_TEST)
        val scaleFactor = mc.displayHeight.toFloat() / height
        GL11.glScissor(
            ((left + 2) * scaleFactor).toInt(),
            ((height - (top + guiHeight - 20)) * scaleFactor).toInt(),
            ((guiWidth - scrollBarWidth - 4) * scaleFactor).toInt(),
            ((guiHeight - 30) * scaleFactor).toInt()
        )

        var yPos = top + 5
        val startLine = max(0, outputLines.size - maxVisibleLines - scrollOffset)
        val endLine = min(outputLines.size, startLine + maxVisibleLines)

        for (i in startLine until endLine) {
            mc.fontRendererObj.drawStringWithShadow(
                outputLines[i],
                (left + 5).toFloat(),
                yPos.toFloat(),
                0xFFFFFF
            )
            yPos += mc.fontRendererObj.FONT_HEIGHT
        }

        GL11.glDisable(GL11.GL_SCISSOR_TEST)

        if (outputLines.size > maxVisibleLines) {
            val totalContentHeight = outputLines.size * mc.fontRendererObj.FONT_HEIGHT
            val visibleAreaHeight = (guiHeight - 30)
            scrollBarHeight = (visibleAreaHeight.toFloat() / totalContentHeight * visibleAreaHeight).toInt()
            scrollBarHeight = max(scrollBarHeight, 10)

            val scrollRange = visibleAreaHeight - scrollBarHeight
            scrollBarY =
                (top + 5 + (scrollOffset.toFloat() / (outputLines.size - maxVisibleLines)) * scrollRange).toInt()

            drawRect(
                left + guiWidth - scrollBarWidth - 2,
                scrollBarY,
                left + guiWidth - 2,
                scrollBarY + scrollBarHeight,
                0xFF666666.toInt()
            )
        }

        inputField.drawTextBox()
    }

    override fun handleMouseInput() {
        super.handleMouseInput()
        val mouseWheel = Mouse.getEventDWheel()
        if (mouseWheel != 0) {
            scrollOffset += if (mouseWheel > 0) -1 else 1
            scrollOffset = scrollOffset.coerceIn(0, max(outputLines.size - maxVisibleLines, 0))
        }
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        inputField.mouseClicked(mouseX, mouseY, mouseButton)

        val guiWidth = 320
        val guiHeight = 180
        val left = width / 2 - guiWidth / 2

        if (mouseButton == 0 && outputLines.size > maxVisibleLines) {
            if (mouseX in (left + guiWidth - scrollBarWidth - 2)..(left + guiWidth - 2)) {
                if (mouseY in scrollBarY..(scrollBarY + scrollBarHeight)) {
                    isDraggingScroll = true
                    scrollBarDraggingOffset = mouseY - scrollBarY
                    lastMouseY = mouseY
                }
            }
        }
    }

    override fun mouseReleased(mouseX: Int, mouseY: Int, state: Int) {
        isDraggingScroll = false
    }

    override fun mouseClickMove(mouseX: Int, mouseY: Int, clickedMouseButton: Int, timeSinceLastClick: Long) {
        if (isDraggingScroll) {
            val deltaY = mouseY - lastMouseY
            val scrollableRange = outputLines.size - maxVisibleLines
            val scrollSpeed = scrollableRange.toFloat() / (180 - 30 - scrollBarHeight)
            scrollOffset += (deltaY * scrollSpeed).toInt()
            scrollOffset = scrollOffset.coerceIn(0, scrollableRange)
            lastMouseY = mouseY
        }
    }

    override fun keyTyped(typedChar: Char, keyCode: Int) {
        when (keyCode) {
            Keyboard.KEY_ESCAPE -> {
                mc.displayGuiScreen(null)
                module.toggle()
                return
            }

            Keyboard.KEY_RETURN -> executeCommand()
            Keyboard.KEY_UP -> if (inputField.text.isEmpty()) inputField.text = history.previous()
            Keyboard.KEY_DOWN -> if (inputField.text.isEmpty()) inputField.text = history.next()
            else -> inputField.textboxKeyTyped(typedChar, keyCode)
        }
    }

    private fun executeCommand() {
        val command = inputField.text.trim()
        if (command.isEmpty()) return

        addOutput("> $command")
        history.add(command)
        inputField.text = ""

        Thread {
            try {
                val process = Runtime.getRuntime().exec(arrayOf("cmd.exe", "/c", command))
                val input = BufferedReader(InputStreamReader(process.inputStream))
                val error = BufferedReader(InputStreamReader(process.errorStream))

                var line: String?
                while (input.readLine().also { line = it } != null) {
                    addOutput(line!!)
                }
                while (error.readLine().also { line = it } != null) {
                    addOutput("${EnumChatFormatting.RED}$line")
                }
                process.waitFor()
            } catch (e: Exception) {
                addOutput("${EnumChatFormatting.RED}Error: ${e.message}")
            }
        }.start()
    }

    private fun addOutput(text: String) {
        Minecraft.getMinecraft().addScheduledTask {
            outputLines.addAll(text.split("\n"))
            if (scrollOffset == savedScrollOffset) {
                scrollOffset = max(outputLines.size - maxVisibleLines, 0)
            }
            while (outputLines.size > 1000) {
                outputLines.removeFirst()
            }
        }
    }

    override fun doesGuiPauseGame() = false

    override fun onGuiClosed() {
        savedInputText = inputField.text
        savedScrollOffset = scrollOffset
    }

    private object history {
        private val commands = LinkedList<String>()
        private var position = 0

        fun add(command: String) {
            if (command.isNotEmpty() && (commands.isEmpty() || commands.last != command)) {
                commands.add(command)
                if (commands.size > 256) commands.removeFirst()
            }
            position = commands.size
        }

        fun previous(): String {
            if (commands.isEmpty()) return ""
            position = max(position - 1, 0)
            return commands[position]
        }

        fun next(): String {
            if (commands.isEmpty()) return ""
            position = min(position + 1, commands.size)
            return if (position == commands.size) "" else commands[position]
        }
    }
}