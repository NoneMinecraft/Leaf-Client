package net.nonemc.leaf.libs.ai

import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

fun getUrl(prompt: String, w: Int, h: Int): String {
    val encodedPrompt = URLEncoder.encode(prompt, "UTF-8")
    return "https://image.pollinations.ai/prompt/$encodedPrompt?width=$w&height=$h&seed=&model=flux&nologo=true"
}

fun downloadImage(imageUrl: String, file: File) {
    val url = URL(imageUrl)
    val conn = url.openConnection() as HttpURLConnection
    conn.setRequestProperty("User-Agent", "Mozilla/5.0")
    conn.connect()
    if (conn.responseCode == 200) {
        conn.inputStream.use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }
    } else {
        println("${conn.responseCode}")
    }
}