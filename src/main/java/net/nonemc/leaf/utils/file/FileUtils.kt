package net.nonemc.leaf.utils.file

import org.apache.commons.io.IOUtils
import java.io.File
import java.io.FileOutputStream

object FileUtils {
    fun unpackFile(file: File, name: String) {
        val fos = FileOutputStream(file)
        IOUtils.copy(FileUtils::class.java.classLoader.getResourceAsStream(name), fos)
        fos.close()
    }
}
