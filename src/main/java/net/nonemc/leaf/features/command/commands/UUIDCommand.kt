package net.nonemc.leaf.features.command.commands

import net.nonemc.leaf.features.command.Command
import net.nonemc.leaf.features.module.modules.misc.AntiBot
import net.nonemc.leaf.features.special.UUIDSpoofer

class UUIDCommand : Command("uuid", emptyArray()) {

    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        if (args.size == 2) {
            val theName = args[1]

            if (theName.equals("reset", true)) {
                UUIDSpoofer.spoofId = null
                chat("§aSuccessfully resetted your UUID.")
                return
            }

            // Get target entity data
            val targetPlayer = mc.theWorld.playerEntities
                .filter { !AntiBot.isBot(it) && it.name.equals(theName, true) }
                .firstOrNull()

            if (targetPlayer == null)
                UUIDSpoofer.spoofId = theName
            else
                UUIDSpoofer.spoofId = targetPlayer.gameProfile.id.toString()
            chat("§aSuccessfully changed your UUID to §6${UUIDSpoofer.spoofId!!}§a. Make sure to turn on BungeeCordSpoof in server selection.")
            return
        }

        if (args.size == 1) {
            chat("§6Session's UUID is §7${mc.session.playerID}§6.")
            chat("§6Player's UUID is §7${mc.thePlayer.uniqueID}§6.")
        }

        chatSyntax("uuid <entity's name in current world/uuid/reset>")
    }

}