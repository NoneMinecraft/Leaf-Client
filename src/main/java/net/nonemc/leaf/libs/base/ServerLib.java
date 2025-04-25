package net.nonemc.leaf.libs.base;

import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;
import net.nonemc.leaf.Leaf;

public final class ServerLib extends MinecraftInstance {

    public static ServerData serverData;

    public static void connectToLastServer() {
        if(serverData == null) return;
        mc.displayGuiScreen(new GuiConnecting(new GuiMultiplayer(Leaf.mainMenu), mc, serverData));
    }
}