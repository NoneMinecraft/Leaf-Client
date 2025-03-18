package net.nonemc.leaf.script.api.global

import jdk.nashorn.api.scripting.JSObject
import jdk.nashorn.api.scripting.ScriptUtils
import net.nonemc.leaf.features.*
import net.nonemc.leaf.value.*
object Setting {
    @JvmStatic
    fun boolean(settingInfo: JSObject): BoolValue {
        val name = settingInfo.getMember("name") as String
        val default = settingInfo.getMember("default") as Boolean

        return BoolValue(name, default)
    }
    @JvmStatic
    fun integer(settingInfo: JSObject): IntegerValue {
        val name = settingInfo.getMember("name") as String
        val default = (settingInfo.getMember("default") as Number).toInt()
        val min = (settingInfo.getMember("min") as Number).toInt()
        val max = (settingInfo.getMember("max") as Number).toInt()

        return IntegerValue(name, default, min, max)
    }
    @JvmStatic
    fun float(settingInfo: JSObject): FloatValue {
        val name = settingInfo.getMember("name") as String
        val default = (settingInfo.getMember("default") as Number).toFloat()
        val min = (settingInfo.getMember("min") as Number).toFloat()
        val max = (settingInfo.getMember("max") as Number).toFloat()

        return FloatValue(name, default, min, max)
    }
    @JvmStatic
    fun text(settingInfo: JSObject): TextValue {
        val name = settingInfo.getMember("name") as String
        val default = settingInfo.getMember("default") as String

        return TextValue(name, default)
    }
    @JvmStatic
    fun block(settingInfo: JSObject): BlockValue {
        val name = settingInfo.getMember("name") as String
        val default = (settingInfo.getMember("default") as Number).toInt()

        return BlockValue(name, default)
    }
    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    fun list(settingInfo: JSObject): ListValue {
        val name = settingInfo.getMember("name") as String
        val values = ScriptUtils.convert(settingInfo.getMember("values"), Array<String>::class.java) as Array<String>
        val default = settingInfo.getMember("default") as String

        return ListValue(name, values, default)
    }
}