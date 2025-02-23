package net.nonemc.leaf.value

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive

open class TextValue(name: String, value: String) : Value<String>(name, value) {
    override fun toJson() = JsonPrimitive(value)

    override fun fromJson(element: JsonElement) {
        if (element.isJsonPrimitive) {
            value = element.asString
        }

    }
    fun append(o: Any): TextValue {
        set(get() + o)
        return this
    }

    }