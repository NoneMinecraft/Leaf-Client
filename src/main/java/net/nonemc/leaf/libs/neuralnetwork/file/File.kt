package net.nonemc.leaf.libs.neuralnetwork.file

import java.io.File

fun getList(file: File): List<Double> {
    val string = file.readText()
    val regex = Regex("""\[([^,\]]+),""")
    return regex.findAll(string)
        .mapNotNull { match ->
            match.groupValues[1]
                .trim()
                .toDoubleOrNull()
        }
        .toList()
}