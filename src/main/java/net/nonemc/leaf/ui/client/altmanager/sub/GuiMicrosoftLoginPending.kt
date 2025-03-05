package net.nonemc.leaf.ui.client.altmanager.sub

import me.liuli.elixir.account.MicrosoftAccount
import me.liuli.elixir.compat.OAuthServer
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiScreen
import net.nonemc.leaf.Leaf
import net.nonemc.leaf.ui.i18n.LanguageManager
import net.nonemc.leaf.utils.ClientUtils
import net.nonemc.leaf.utils.extensions.drawCenteredString
import net.nonemc.leaf.utils.misc.MiscUtils

class GuiMicrosoftLoginPending(private val prevGui: GuiScreen) : GuiScreen() {
    private var stage = "Initializing..."
    private lateinit var server: OAuthServer

    override fun initGui() {
        server = MicrosoftAccount.Companion.buildFromOpenBrowser(object : MicrosoftAccount.OAuthHandler {
            override fun openUrl(url: String) {
                stage = "Check your browser for continue..."
                ClientUtils.logInfo("Opening URL: $url")
                MiscUtils.showURL(url)
            }

            override fun authError(error: String) {
                stage = "Error: $error"
            }

            override fun authResult(account: MicrosoftAccount) {
                if (Leaf.fileManager.accountsConfig.altManagerMinecraftAccounts.any { it.name == account.name }) {
                    stage = "§c${LanguageManager.getAndFormat("ui.alt.alreadyAdded")}"
                    return
                }
                Leaf.fileManager.accountsConfig.altManagerMinecraftAccounts.add(account)
                Leaf.fileManager.saveConfig(Leaf.fileManager.accountsConfig)
                mc.displayGuiScreen(prevGui)
            }
        })

        buttonList.add(GuiButton(0, width / 2 - 100, height / 4 + 120 + 12, "Cancel"))
    }

    override fun actionPerformed(button: GuiButton) {
        if (button.id == 0) {
            server.stop(true)
            mc.displayGuiScreen(prevGui)
        }
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        this.drawDefaultBackground()

        fontRendererObj.drawCenteredString(stage, width / 2f, height / 2f - 50, 0xffffff)

        super.drawScreen(mouseX, mouseY, partialTicks)
    }
}