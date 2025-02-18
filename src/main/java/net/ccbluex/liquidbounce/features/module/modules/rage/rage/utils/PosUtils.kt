package net.ccbluex.liquidbounce.features.module.modules.rage.rage.utils

import net.ccbluex.liquidbounce.features.MainLib.ChatPrint
import net.ccbluex.liquidbounce.features.module.modules.rage.RageBot.delayControlPosMode
import net.ccbluex.liquidbounce.features.module.modules.rage.RageBot.delayControlPosModeMinDelayValue
import net.ccbluex.liquidbounce.features.module.modules.rage.RageBot.getPlayerPosMode
import net.ccbluex.liquidbounce.features.module.modules.rage.RageBot.getPosMode
import net.ccbluex.liquidbounce.features.module.modules.rage.RageBot.getPosModeCustomCode
import net.ccbluex.liquidbounce.features.module.modules.rage.RageBot.getVelocityMode
import net.ccbluex.liquidbounce.features.module.modules.rage.RageBot.getVelocityModeCustomCode
import net.ccbluex.liquidbounce.features.module.modules.rage.RageBot.scriptEngineMode
import net.ccbluex.liquidbounce.utils.mc
import net.minecraft.entity.player.EntityPlayer
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager
import javax.script.ScriptException
import kotlin.math.abs

fun posX(it: EntityPlayer): Double = getPos(it.posX, it.prevPosX, it.serverPosX.toDouble(), it.lastTickPosX, it.motionX , it.motionY , it.motionZ)
fun posY(it: EntityPlayer): Double = getPos(it.posY, it.prevPosY, it.serverPosY.toDouble(), it.lastTickPosY, it.motionX , it.motionY , it.motionZ)
fun posZ(it: EntityPlayer): Double = getPos(it.posZ, it.prevPosZ, it.serverPosZ.toDouble(), it.lastTickPosZ, it.motionX , it.motionY , it.motionZ)
fun playerPosX(): Double = getPlayerPos(mc.thePlayer.posX, mc.thePlayer.serverPosX.toDouble())
fun playerPosY(): Double = getPlayerPos(mc.thePlayer.posY, mc.thePlayer.serverPosY.toDouble())
fun playerPosZ(): Double = getPlayerPos(mc.thePlayer.posZ, mc.thePlayer.serverPosZ.toDouble())

fun velocityX(it: EntityPlayer): Double = getVelocity(it.posX, it.prevPosX, it.serverPosX.toDouble(), it.lastTickPosX, it.motionX , it.motionY , it.motionZ)
fun velocityY(it: EntityPlayer): Double = getVelocity(it.posY, it.prevPosY, it.serverPosY.toDouble(), it.lastTickPosY, it.motionX , it.motionY , it.motionZ)
fun velocityZ(it: EntityPlayer): Double = getVelocity(it.posZ, it.prevPosZ, it.serverPosZ.toDouble(), it.lastTickPosZ, it.motionX , it.motionY , it.motionZ)

fun velocityABSX(it: EntityPlayer): Double = getVelocityABS(it.posX, it.prevPosX, it.serverPosX.toDouble(), it.lastTickPosX, it.motionX , it.motionY , it.motionZ)
fun velocityABSY(it: EntityPlayer): Double = getVelocityABS(it.posY, it.prevPosY, it.serverPosY.toDouble(), it.lastTickPosY, it.motionX , it.motionY , it.motionZ)
fun velocityABSZ(it: EntityPlayer): Double = getVelocityABS(it.posZ, it.prevPosZ, it.serverPosZ.toDouble(), it.lastTickPosZ, it.motionX , it.motionY , it.motionZ)

fun getPlayerPos(pos: Double, serverPos: Double): Double {
    return if (getPlayerPosMode.get() == "Pos" || (delayControlPosMode.get() && ping() > delayControlPosModeMinDelayValue.get())) pos
    else if (getPlayerPosMode.get() == "ServerPos") serverPos/32
    else if (getPlayerPosMode.get() == "Mix") pos + (serverPos/32 - pos)
    else pos
}

fun getPos(pos: Double, prevPos: Double, serverPos: Double, lastTickPos: Double , motionX: Double , motionY: Double , motionZ: Double): Double {
    val playerPosMode = getPosMode.get()
    val delayControlEnabled = delayControlPosMode.get()
    val pingValue = ping()
    val minDelayValue = delayControlPosModeMinDelayValue.get()

    return when {
        playerPosMode == "Pos" || (delayControlEnabled && pingValue > minDelayValue) -> pos
        playerPosMode == "ServerPos" -> serverPos / 32
        playerPosMode == "Mix" -> pos + (serverPos / 32 - pos)
        playerPosMode == "Custom" -> {
            customCode(getPosModeCustomCode.get(), pos, prevPos, serverPos, lastTickPos , ping() , motionX,motionY,motionZ)
        }
        else -> pos
    }
}
fun getVelocity(pos: Double, prevPos: Double, serverPos: Double, lastTickPos: Double  , motionX: Double , motionY: Double , motionZ: Double): Double {
    val velocityMode = getVelocityMode.get()

    return when (velocityMode) {
        "Pos" -> pos - prevPos
        "ServerPos" -> serverPos/32 - lastTickPos
        "MixServerPosAndPrevPos" -> serverPos/32 - prevPos
        "MixPosAndLastTickPos" -> pos - lastTickPos
        "PracticalityMix" -> (serverPos/32 - lastTickPos) - (pos - prevPos) / 1.1
        "Custom" -> {
            customCode(getVelocityModeCustomCode.get(), pos, prevPos, serverPos, lastTickPos , ping() , motionX,motionY,motionZ)
        }
        else -> 0.0
    }
}
fun getVelocityABS(pos: Double, prevPos: Double, serverPos: Double, lastTickPos: Double , motionX: Double , motionY: Double , motionZ: Double): Double {
    return abs(getVelocity(pos, prevPos, serverPos, lastTickPos ,motionX,motionY,motionZ))
}

fun customCode(code: String, pos: Double, prevPos: Double, serverPos: Double, lastTickPos: Double , ping: Int , motionX : Double , motionY : Double ,motionZ : Double): Double {
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
        ChatPrint("Error (return 0.0) : $e")
        e.printStackTrace()
        0.0
    }
}
