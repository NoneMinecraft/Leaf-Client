package net.nonemc.leaf.libs.file

import org.apache.commons.io.IOUtils
import java.io.File
import java.io.FileOutputStream

object Unpack {
    fun unpackFile(file: File, name: String) {
        val fos = FileOutputStream(file)
        IOUtils.copy(Unpack::class.java.classLoader.getResourceAsStream(name), fos)
        fos.close()
    }
}
