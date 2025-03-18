package net.nonemc.leaf.value

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import net.nonemc.leaf.utils.AnimationHelper
open class BoolValue(name: String, value: Boolean) : Value<Boolean>(name, value) {
    val animation = AnimationHelper(this)
    override fun toJson() = JsonPrimitive(value)

    override fun fromJson(element: JsonElement) {
        if (element.isJsonPrimitive) {
            value = element.asBoolean || element.asString.equals("true", ignoreCase = true)
        }
    }
    init {
        animation.animationX = if (value) 5F else -5F
    }
    open fun toggle(){
        this.value = !this.value
    }
}
