package net.nonemc.leaf.features.module.modules.rage.rage.utils

import net.minecraft.entity.player.EntityPlayer
import net.nonemc.leaf.libs.packet.PacketText.chatPrint
import net.nonemc.leaf.features.module.modules.rage.RageBot.getPlayerPosMode
import net.nonemc.leaf.features.module.modules.rage.RageBot.getPosMode
import net.nonemc.leaf.features.module.modules.rage.RageBot.getPosModeCustomCode
import net.nonemc.leaf.features.module.modules.rage.RageBot.getVelocityMode
import net.nonemc.leaf.features.module.modules.rage.RageBot.getVelocityModeCustomCode
import net.nonemc.leaf.features.module.modules.rage.RageBot.scriptEngineMode
import net.nonemc.leaf.libs.base.mc
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager
import javax.script.ScriptException
import kotlin.math.abs

fun posX(it: EntityPlayer): Double =
    getPos(it.posX, it.prevPosX, it.serverPosX.toDouble(), it.lastTickPosX, it.motionX, it.motionY, it.motionZ)

fun posY(it: EntityPlayer): Double =
    getPos(it.posY, it.prevPosY, it.serverPosY.toDouble(), it.lastTickPosY, it.motionX, it.motionY, it.motionZ)

fun posZ(it: EntityPlayer): Double =
    getPos(it.posZ, it.prevPosZ, it.serverPosZ.toDouble(), it.lastTickPosZ, it.motionX, it.motionY, it.motionZ)

fun playerPosX(): Double = getPlayerPos(net.nonemc.leaf.libs.base.mc.thePlayer.posX, net.nonemc.leaf.libs.base.mc.thePlayer.serverPosX.toDouble())
fun playerPosY(): Double = getPlayerPos(net.nonemc.leaf.libs.base.mc.thePlayer.posY, net.nonemc.leaf.libs.base.mc.thePlayer.serverPosY.toDouble())
fun playerPosZ(): Double = getPlayerPos(net.nonemc.leaf.libs.base.mc.thePlayer.posZ, net.nonemc.leaf.libs.base.mc.thePlayer.serverPosZ.toDouble())

fun velocityX(it: EntityPlayer): Double =
    getVelocity(it.posX, it.prevPosX, it.serverPosX.toDouble(), it.lastTickPosX, it.motionX, it.motionY, it.motionZ)

fun velocityY(it: EntityPlayer): Double =
    getVelocity(it.posY, it.prevPosY, it.serverPosY.toDouble(), it.lastTickPosY, it.motionX, it.motionY, it.motionZ)

fun velocityZ(it: EntityPlayer): Double =
    getVelocity(it.posZ, it.prevPosZ, it.serverPosZ.toDouble(), it.lastTickPosZ, it.motionX, it.motionY, it.motionZ)

fun velocityABSX(it: EntityPlayer): Double =
    getVelocityABS(it.posX, it.prevPosX, it.serverPosX.toDouble(), it.lastTickPosX, it.motionX, it.motionY, it.motionZ)

fun velocityABSY(it: EntityPlayer): Double =
    getVelocityABS(it.posY, it.prevPosY, it.serverPosY.toDouble(), it.lastTickPosY, it.motionX, it.motionY, it.motionZ)

fun velocityABSZ(it: EntityPlayer): Double =
    getVelocityABS(it.posZ, it.prevPosZ, it.serverPosZ.toDouble(), it.lastTickPosZ, it.motionX, it.motionY, it.motionZ)

fun getPlayerPos(pos: Double, serverPos: Double): Double {
    return when (getPlayerPosMode.get()) {
        "ServerPos" -> {
            serverPos / 32.0
        }

        "Pos" -> {
            pos
        }

        else -> pos
    }
}

fun getPos(
    pos: Double,
    prevPos: Double,
    serverPos: Double,
    lastTickPos: Double,
    motionX: Double,
    motionY: Double,
    motionZ: Double,
): Double {
    return when (getPosMode.get()) {
        "Pos" -> pos
        "ServerPos" -> serverPos / 32
        "Custom" -> customCode(
            getPosModeCustomCode.get(),
            pos,
            prevPos,
            serverPos,
            lastTickPos,
            ping(),
            motionX,
            motionY,
            motionZ
        )

        else -> pos
    }
}

fun getVelocity(
    pos: Double,
    prevPos: Double,
    serverPos: Double,
    lastTickPos: Double,
    motionX: Double,
    motionY: Double,
    motionZ: Double,
): Double {
    val velocityMode = getVelocityMode.get()

    return when (velocityMode) {
        "Pos" -> pos - prevPos
        "ServerPos" -> serverPos / 32 - lastTickPos
        "MixServerPosAndPrevPos" -> serverPos / 32 - prevPos
        "MixPosAndLastTickPos" -> pos - lastTickPos
        "PracticalityMix" -> (serverPos / 32 - lastTickPos) - (pos - prevPos) / 1.1
        "Custom" -> {
            customCode(
                getVelocityModeCustomCode.get(),
                pos,
                prevPos,
                serverPos,
                lastTickPos,
                ping(),
                motionX,
                motionY,
                motionZ
            )
        }

        else -> 0.0
    }
}

fun getVelocityABS(
    pos: Double,
    prevPos: Double,
    serverPos: Double,
    lastTickPos: Double,
    motionX: Double,
    motionY: Double,
    motionZ: Double,
): Double {
    return abs(getVelocity(pos, prevPos, serverPos, lastTickPos, motionX, motionY, motionZ))
}

fun customCode(
    code: String,
    pos: Double,
    prevPos: Double,
    serverPos: Double,
    lastTickPos: Double,
    ping: Int,
    motionX: Double,
    motionY: Double,
    motionZ: Double,
): Double {
    val engine: ScriptEngine = ScriptEngineManager().getEngineByName(scriptEngineMode.get())

    val formattedExpression = code
        .replace("serverPos", serverPos.toString())
        .replace("lastTickPos", lastTickPos.toString())
        .replace("pos", pos.toString())
        .replace("prevPos", prevPos.toString())
        .replace("ping", ping.toString())
        .replace("motionX", motionX.toString())
        .replace("motionY", motionY.toString())
        .replace("motionZ", motionZ.toString())
    return try {
        engine.eval(formattedExpression) as Double
    } catch (e: ScriptException) {
        chatPrint("Error (return 0.0) : $e")
        e.printStackTrace()
        0.0
    }
}
