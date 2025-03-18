package net.nonemc.leaf.value

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import java.util.*

open class ListValue(name: String, val values: Array<String>, value: String) : Value<String>(name, value) {
    @JvmField
    var openList = false
    fun getModeListNumber(mode: String) = values.indexOf(mode)
    init {
        this.value = value
    }
    override fun changeValue(value: String) {
        for (element in values) {
            if (element.equals(value, ignoreCase = true)) {
                this.value = element
                break
            }
        }
    }

    override fun toJson() = JsonPrimitive(value)

    override fun fromJson(element: JsonElement) {
        if (element.isJsonPrimitive) changeValue(element.asString)
    }
}


