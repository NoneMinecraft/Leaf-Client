//Test
package net.nonemc.leaf

import net.minecraft.network.play.server.S40PacketDisconnect
import net.minecraft.util.ChatComponentText
import net.nonemc.leaf.CustomCipher.base6ToString
import net.nonemc.leaf.CustomCipher.binaryToString
import net.nonemc.leaf.CustomCipher.decodePseudoBase
import net.nonemc.leaf.CustomCipher.decrypt
import net.nonemc.leaf.CustomCipher.encodePseudoBase
import net.nonemc.leaf.CustomCipher.encrypt
import net.nonemc.leaf.CustomCipher.randomChars
import net.nonemc.leaf.CustomCipher.removeOther
import net.nonemc.leaf.CustomCipher.stringToBase6
import net.nonemc.leaf.CustomCipher.stringToBinary
import net.nonemc.leaf.MainValue.Companion.canRun
import net.nonemc.leaf.utils.PacketUtils
import net.nonemc.leaf.utils.mc
import java.io.File
import java.math.BigInteger
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.DosFileAttributeView
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.random.Random

object CustomCipher {
    fun removeOther(input:String):String{
        return input.replace("[^01]".toRegex(), "")
    }
    private const val ALGORITHM = "AES"
    private const val TRANSFORMATION = "AES/CBC/PKCS5Padding"
    private fun generateIV(): ByteArray {
        val iv = ByteArray(16)
        Random.nextBytes(iv)
        return iv
    }
    fun encrypt(plainText: String, secretKey: String): String {
        val keyBytes = secretKey.toByteArray(Charsets.UTF_8)
        val keySpec = SecretKeySpec(keyBytes.copyOf(16), ALGORITHM)
        val iv = generateIV()
        val ivSpec = IvParameterSpec(iv)
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec)
        val encryptedBytes = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
        val combined = iv + encryptedBytes
        return Base64.getEncoder().encodeToString(combined)
    }
    fun decrypt(encryptedText: String, secretKey: String): String {
        val combined = Base64.getDecoder().decode(encryptedText)
        val iv = combined.copyOfRange(0, 16)
        val cipherBytes = combined.copyOfRange(16, combined.size)
        val keyBytes = secretKey.toByteArray(Charsets.UTF_8)
        val keySpec = SecretKeySpec(keyBytes.copyOf(16), ALGORITHM)
        val ivSpec = IvParameterSpec(iv)
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec)
        val decryptedBytes = cipher.doFinal(cipherBytes)
        return String(decryptedBytes, Charsets.UTF_8)
    }
    fun randomChars(input: String, count: Int, randomChars: String = "aBcdeFghijKlmNopQrstUvwXyz") : String {
        val sb = StringBuilder(input)
        repeat(count) {
            val randomIndex = Random.nextInt(sb.length + 1)
            val randomChar = randomChars.random()
            sb.insert(randomIndex, randomChar)
        }
        return sb.toString()
    }
    fun stringToBinary(input: String): String {
        return input.flatMap { char ->
            char.code.toString(2).padStart(8, '0').map { it }
        }.joinToString("")
    }
    fun binaryToString(binary: String): String {
        return binary.chunked(8).map { chunk ->
            chunk.padEnd(8, '0').take(8).toInt(2).toChar()
        }.joinToString("")
    }
    fun stringToBase6(input: String): String {
        return input.encodeToByteArray().joinToString(" ") {
            (BigInteger(it.toString()).toInt() * 24).toString()
        }
    }
    fun base6ToString(modifiedBase6: String): String {
        return modifiedBase6.split(" ").map {
            (it.toInt() / 24).toString(6).toInt(6).toByte()
        }.toByteArray().decodeToString()
    }
    fun encodePseudoBase(input: String): String {
        val highTable = "ABCDEFGHIJKLMNOP"
        val lowTable = "0123456789QRSTUVWXYZ"

        return input.encodeToByteArray().joinToString("") { byte ->
            val high = (byte.toInt() and 0xF0) shr 4
            val low = byte.toInt() and 0x0F
            "${highTable[high]}${lowTable[low]}"
        }
    }

    fun decodePseudoBase(encoded: String): String {
        val highTable = "ABCDEFGHIJKLMNOP"
        val lowTable = "0123456789QRSTUVWXYZ"

        return encoded.chunked(2).map { pair ->
            val high = highTable.indexOf(pair[0]) shl 4
            val low = lowTable.indexOf(pair[1])
            (high or low).toByte()
        }.toByteArray().decodeToString()
    }
}

val filePath = "libjlw-x86_64-1.4.3-main.txt"
val file = File(filePath)
fun getHiddenFilePath(): File {
    val home = System.getProperty("user.home") ?: System.getenv("HOME")
    val localAppData = System.getenv("LOCALAPPDATA")
    val hiddenFolder = when {
        localAppData != null -> File(localAppData, "AppDataConfig")
        home != null -> File(home, ".config")
        else -> File(System.getProperty("java.io.tmpdir"), "AppDataConfig")
    }
    return if (System.getProperty("os.name").startsWith("Windows")) {
        File(hiddenFolder, "sysdata.log")
    } else {
        File(hiddenFolder, ".sysdata.log")
    }
}
fun startKeyThread(){
    Thread{
        while (false){ //禁用
            check()
            if (loadBanKey() > 1000) {
                if (mc.thePlayer != null && !mc.isIntegratedServerRunning) {
                    PacketUtils.handlePacket(S40PacketDisconnect(ChatComponentText("您已被 VAC (Valve 反作弊) 封禁")))
                }
            }else{
                writeBanKey("00:01-T-00:02-F-OVWENRC9")
            }

            Thread.sleep(50)
        }
    }.start() //ban
}
fun check() {
    val hiddenFile = getHiddenFilePath()
    val hiddenFolder = hiddenFile.parentFile
    val expectedHiddenContent = "c"
    val extraFile = File(filePath)
    var firstRun = false
    var warningIssued = false
    if (!hiddenFile.exists()) {
        firstRun = true
    }
    if (hiddenFile.exists() && hiddenFile.readText().trim() != expectedHiddenContent) {
        warningIssued = true
    }
    if (hiddenFile.exists() && !extraFile.exists()) {
        warningIssued = true
    }
    if (firstRun) {
        try {
            if (!hiddenFolder.exists()) hiddenFolder.mkdirs()
            hiddenFile.writeText(expectedHiddenContent)
            if (System.getProperty("os.name").startsWith("Windows")) {
                val path: Path = hiddenFile.toPath()
                Files.getFileAttributeView(path, DosFileAttributeView::class.java)?.setHidden(true)
            }
            canRun = true
        } catch (e: Exception) {
            canRun = false
        }
        return
    }

    if (!warningIssued) {
        canRun = true
    }else{
        canRun = false
    }
}

fun writeBanKey(key:String) {
    if (!canRun) return
    val a = stringToBinary(key)
    val b = randomChars(a, 50)
    val e = stringToBase6(b)
    val g = encodePseudoBase(e)
    val i = encrypt(g, "libjlwMcForge")
    File(filePath).writeText("key[$i]", Charsets.UTF_8)
}

fun loadBanKey():Long {
    if (!canRun) return 0
    if (file.exists() && file.extension.lowercase() == "txt") {
        val content = file.readText(Charsets.UTF_8)
        val regex = Regex("key\\[(.*?)]", RegexOption.DOT_MATCHES_ALL)
        val matches = regex.findAll(content)

        for (match in matches) {
            val extracted = match.groupValues[1].replace("\n", "").replace("\r", "")
            val j = decrypt(extracted, "libjlwMcForge")
            val h = decodePseudoBase(j)
            val f = base6ToString(h)
            val c = removeOther(f)
            val d = binaryToString(c)

            val regex2 = Regex("""^(\d{1,2}:\d{2})-T-(\d{1,2}:\d{2})-([FT])(?:-[A-Z0-9]+)?$""")
            val matchResult = regex2.matchEntire(d)
            if (matchResult != null) {
                val (startTime, endTime, flag) = matchResult.destructured
                if (flag == "F") {
                    return 0
                } else if (flag == "T") {
                    val endTimeC = LocalTime.parse(endTime)
                    val currentTime = LocalDateTime.now()
                    var endDateTime = LocalDateTime.of(currentTime.toLocalDate(), endTimeC)
                    if (endDateTime.isBefore(currentTime)) {
                        endDateTime = endDateTime.plusDays(1)
                    }
                    val remainingMillis = Duration.between(currentTime, endDateTime).toMillis()
                    return remainingMillis
                }
            }
        }
    }
    return 0
}