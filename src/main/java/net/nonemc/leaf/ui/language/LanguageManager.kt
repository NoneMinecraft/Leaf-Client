package net.nonemc.leaf.ui.language

import java.util.regex.Matcher
import java.util.regex.Pattern
fun match(matcher: Matcher): Array<String> {
    val result = mutableListOf<String>()

    while (matcher.find()) {
        result.add(matcher.group())
    }

    return result.toTypedArray()
}
object LanguageManager {
    val key = "%"
    val defaultLocale = "en_us"

    var language = Language(defaultLocale)
        private set(value) {
            cachedStrings.clear()
            field = value
        }
    private val pattern = Pattern.compile("$key[A-Za-z0-9\u002E]*$key")

    private val cachedStrings = HashMap<String, String>()

    fun replace(text: String): String {
        if (!text.contains(key)) {
            return text
        }

        if (cachedStrings.containsKey(text)) {
            return cachedStrings[text]!!
        }

        val matcher = pattern.matcher(text)
        var result = text
        match(matcher).forEach {
            val spliced = it.substring(1, it.length - 1)
            val converted = get(spliced)
            if (spliced != converted) {
                result = result.replace(it, converted)
            }
        }
        cachedStrings[text] = result

        return result
    }

    fun get(key: String): String {
        return language.get(key)
    }

    fun getAndFormat(key: String, vararg argsIn: Any?): String {
        val args = argsIn.toList().toMutableList()
        args.forEachIndexed { index, arg ->
            if (arg is String) {
                args[index] = replace(arg)
            }
        }
        return String.format(get(key), *args.toTypedArray())
    }

    fun switchLanguage(languageStr: String) {
        language = Language(languageStr)
    }
}