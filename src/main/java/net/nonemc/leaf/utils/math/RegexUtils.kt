package net.nonemc.leaf.utils.math

import java.math.BigDecimal
import java.math.RoundingMode
import java.util.regex.Matcher

object RegexUtils {
    fun match(matcher: Matcher): Array<String> {
        val result = mutableListOf<String>()

        while (matcher.find()) {
            result.add(matcher.group())
        }

        return result.toTypedArray()
    }

    fun round(value: Double, places: Int): Double {
        require(places >= 0)
        return BigDecimal.valueOf(value).setScale(places, RoundingMode.HALF_UP).toDouble()
    }
}