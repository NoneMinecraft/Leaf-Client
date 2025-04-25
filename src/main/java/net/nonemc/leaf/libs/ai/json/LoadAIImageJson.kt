package net.nonemc.leaf.libs.ai.json

import com.google.gson.JsonParser
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.*

fun loadAIImageJson(file: File): String? {
    if (!file.exists()) {
        println("File not found: $file")
        return null
    }

    return try {
        val inputStream = FileInputStream(file)
        val reader = InputStreamReader(inputStream, Charset.forName("UTF-8"))
        val bufferedReader = BufferedReader(reader)

        val jsonElement = JsonParser().parse(bufferedReader)
        bufferedReader.close()

        if (!jsonElement.isJsonObject) {
            println("JSON is not an object")
            return null
        }

        val jsonObject = jsonElement.asJsonObject
        val rawString = if (jsonObject.has("str")) jsonObject.get("str").asString else null
        val currentTime = SimpleDateFormat("HH mm", Locale.getDefault()).format(Date())

        rawString?.replace("%time%", currentTime)
    } catch (e: Exception) {
        println("Failed to load or parse JSON: ${e.message}")
        null
    }
}