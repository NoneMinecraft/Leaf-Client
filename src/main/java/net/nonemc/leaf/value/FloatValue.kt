package net.nonemc.leaf.value

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
open class FloatValue(name: String, value: Float, val minimum: Float = 0F, val maximum: Float = Float.MAX_VALUE) : Value<Float>(name, value) {

    fun set(newValue: Number) {
        set(newValue.toFloat())
    }

    override fun toJson() = JsonPrimitive(value)

    override fun fromJson(element: JsonElement) {
        if (element.isJsonPrimitive) {
            value = element.asFloat
        }
    }
}
