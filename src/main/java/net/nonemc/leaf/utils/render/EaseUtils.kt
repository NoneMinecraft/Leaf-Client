package net.nonemc.leaf.utils.render

import net.nonemc.leaf.value.ListValue
import kotlin.math.pow
import kotlin.math.sqrt

object EaseUtils {
    fun easeInOutQuad(x: Double): Double {
        return if (x < 0.5) {
            2 * x * x
        } else {
            1 - (-2 * x + 2).pow(2) / 2
        }
    }

    fun easeInQuart(x: Double): Double {
        return x * x * x * x
    }

    @JvmStatic
    fun easeOutQuart(x: Double): Double {
        return 1 - (1 - x).pow(4)
    }

    fun easeInQuint(x: Double): Double {
        return x * x * x * x * x
    }

    @JvmStatic
    fun easeInExpo(x: Double): Double {
        return if (x == 0.0) {
            0.0
        } else {
            2.0.pow(10 * x - 10)
        }
    }

    fun easeOutExpo(x: Double): Double {
        return if (x == 1.0) {
            1.0
        } else {
            1 - 2.0.pow(-10 * x)
        }
    }
    fun easeOutCirc(x: Double): Double {
        return sqrt(1 - (x - 1).pow(2))
    }
    @JvmStatic
    fun easeOutBack(x: Double): Double {
        val c1 = 1.70158
        val c3 = c1 + 1

        return 1 + c3 * (x - 1).pow(3) + c1 * (x - 1).pow(2)
    }

    enum class EnumEasingType {
        NONE,
        SINE,
        QUAD,
        CUBIC,
        QUART,
        QUINT,
        EXPO,
        CIRC,
        BACK,
        ELASTIC;

        val friendlyName = name.substring(0, 1).uppercase() + name.substring(1, name.length).lowercase()
    }

    enum class EnumEasingOrder(val methodName: String) {
        FAST_AT_START("Out"),
        FAST_AT_END("In"),
        FAST_AT_START_AND_END("InOut")
    }

    fun getEnumEasingList(name: String) =
        ListValue(name, EnumEasingType.values().map { it.toString() }.toTypedArray(), EnumEasingType.SINE.toString())

    fun getEnumEasingOrderList(name: String) = ListValue(
        name,
        EnumEasingOrder.values().map { it.toString() }.toTypedArray(),
        EnumEasingOrder.FAST_AT_START.toString()
    )

    fun apply(type: EnumEasingType, order: EnumEasingOrder, value: Double): Double {
        if (type == EnumEasingType.NONE) {
            return value
        }

        val methodName = "ease${order.methodName}${type.friendlyName}"

        this.javaClass.declaredMethods.find { it.name.equals(methodName) }.also {
            return if (it != null) {
                it.invoke(this, value) as Double
            } else {
                println("Cannot found easing method: $methodName")
                value
            }
        }
    }
}