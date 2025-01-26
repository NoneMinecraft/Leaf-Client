package net.ccbluex.liquidbounce.features.module.modules.combat.aura.code

import net.ccbluex.liquidbounce.features.MainLib
import net.ccbluex.liquidbounce.features.module.modules.combat.Aura
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager
import javax.script.ScriptException
 fun customCode(current: Float, target: Float, speed: Float): Double {
    val engine: ScriptEngine = ScriptEngineManager().getEngineByName("JavaScript")
    val formattedExpression = Aura.customSmoothCode.get()
        .replace("current", current.toString())
        .replace("target", target.toString())
        .replace("speed", speed.toString())
    return try {
        engine.eval(formattedExpression) as Double
    } catch (e: ScriptException) {
        MainLib.ChatPrint("Error (return 0.0) : $e")
        e.printStackTrace()
        0.0
    }
}
