﻿package net.nonemc.leaf.utils

import java.math.BigDecimal
import java.math.RoundingMode
import java.util.regex.Matcher
import java.util.regex.Pattern

object RegexUtils {
    fun match(matcher: Matcher): Array<String> {
        val result = mutableListOf<String>()

        while (matcher.find()) {
            result.add(matcher.group())
        }

        return result.toTypedArray()
    }

    fun match(text: String, pattern: Pattern): Array<String> {
        return match(pattern.matcher(text))
    }

    fun match(text: String, pattern: String): Array<String> {
        return match(text, Pattern.compile(pattern))
    }

    fun round(value: Double, places: Int): Double {
        require(places >= 0)
        return BigDecimal.valueOf(value).setScale(places, RoundingMode.HALF_UP).toDouble()
    }
}