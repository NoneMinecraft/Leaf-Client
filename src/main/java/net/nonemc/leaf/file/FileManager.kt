﻿package net.nonemc.leaf.file

import java.io.File

/**
 * Constructor of config
 *
 * @param file of config
 */
abstract class FileManager(val file: File) {

    /**
     * Load config from file
     */
    abstract fun loadConfig(config: String)

    /**
     * Save config to file
     */
    abstract fun saveConfig(): String

    /**
     * Create config
     */
    fun createConfig() {
        file.createNewFile()
    }

    /**
     * Load config file
     */
    fun loadConfigFile(): String {
        return file.readText(Charsets.UTF_8)
    }

    /**
     * Save config file
     */
    fun saveConfigFile(config: String) {
        file.writeText(config, Charsets.UTF_8)
    }

    /**
     * @return config file exist
     */
    fun hasConfig(): Boolean {
        return file.exists()
    }
}