﻿package net.nonemc.leaf.features.command.commands

import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition
import net.nonemc.leaf.features.command.Command

class DamageCommand : Command("damage", arrayOf("hurt")) {
    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        var damage = 1

        if (args.size > 1) {
            try {
                damage = args[1].toInt()
            } catch (ignored: NumberFormatException) {
                chatSyntaxError()
                return
            }
        }

        // Latest NoCheatPlus damage exploit
        val x = mc.thePlayer.posX
        val y = mc.thePlayer.posY
        val z = mc.thePlayer.posZ

        for (i in 0 until 65 * damage) {
            mc.netHandler.addToSendQueue(C04PacketPlayerPosition(x, y + 0.049, z, false))
            mc.netHandler.addToSendQueue(C04PacketPlayerPosition(x, y, z, false))
        }
        mc.netHandler.addToSendQueue(C04PacketPlayerPosition(x, y, z, true))

        // Output message
        chat("You were damaged.")
    }
}