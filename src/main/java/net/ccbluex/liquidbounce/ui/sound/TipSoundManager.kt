package net.ccbluex.liquidbounce.ui.sound

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.utils.FileUtils
import java.io.File

class TipSoundManager {
    var winSound: TipSoundPlayer
    var winSound2: TipSoundPlayer
    var winSound3: TipSoundPlayer
    var winSound4: TipSoundPlayer
    var winSound5: TipSoundPlayer
    var winSound6: TipSoundPlayer
    var winSound7: TipSoundPlayer
    var winSound8: TipSoundPlayer
    var winSound9: TipSoundPlayer
    var startSound: TipSoundPlayer
    var startSound2: TipSoundPlayer
    var startSound3: TipSoundPlayer
    var startSound4: TipSoundPlayer
    var startSound5: TipSoundPlayer
    var startSound6: TipSoundPlayer
    var startSound7: TipSoundPlayer
    var startSound8: TipSoundPlayer
    var startSound9: TipSoundPlayer
    var enableSound: TipSoundPlayer
    var disableSound: TipSoundPlayer

    init {
        val enableSoundFile = File(LiquidBounce.fileManager.soundsDir, "enable.wav")
        val disableSoundFile = File(LiquidBounce.fileManager.soundsDir, "disable.wav")
        val winSoundFile = File(LiquidBounce.fileManager.soundsDir, "win.wav")
        val win2SoundFile = File(LiquidBounce.fileManager.soundsDir, "win2.wav")
        val win3SoundFile = File(LiquidBounce.fileManager.soundsDir, "win3.wav")
        val win4SoundFile = File(LiquidBounce.fileManager.soundsDir, "win4.wav")
        val win5SoundFile = File(LiquidBounce.fileManager.soundsDir, "win5.wav")
        val win6SoundFile = File(LiquidBounce.fileManager.soundsDir, "win6.wav")
        val win7SoundFile = File(LiquidBounce.fileManager.soundsDir, "win7.wav")
        val win8SoundFile = File(LiquidBounce.fileManager.soundsDir, "win8.wav")
        val win9SoundFile = File(LiquidBounce.fileManager.soundsDir, "win9.wav")
        val startSoundFile = File(LiquidBounce.fileManager.soundsDir, "start.wav")
        val start2SoundFile = File(LiquidBounce.fileManager.soundsDir, "start2.wav")
        val start3SoundFile = File(LiquidBounce.fileManager.soundsDir, "start3.wav")
        val start4SoundFile = File(LiquidBounce.fileManager.soundsDir, "start4.wav")
        val start5SoundFile = File(LiquidBounce.fileManager.soundsDir, "start5.wav")
        val start6SoundFile = File(LiquidBounce.fileManager.soundsDir, "start6.wav")
        val start7SoundFile = File(LiquidBounce.fileManager.soundsDir, "start7.wav")
        val start8SoundFile = File(LiquidBounce.fileManager.soundsDir, "start8.wav")
        val start9SoundFile = File(LiquidBounce.fileManager.soundsDir, "start9.wav")

        if (!winSoundFile.exists()) {
            FileUtils.unpackFile(winSoundFile, "assets/minecraft/leaf/sound/win.wav")
        }
        if (!win2SoundFile.exists()) {
            FileUtils.unpackFile(win2SoundFile, "assets/minecraft/leaf/sound/win2.wav")
        }
        if (!win3SoundFile.exists()) {
            FileUtils.unpackFile(win3SoundFile, "assets/minecraft/leaf/sound/win3.wav")
        }
        if (!win4SoundFile.exists()) {
            FileUtils.unpackFile(win4SoundFile, "assets/minecraft/leaf/sound/win4.wav")
        }
        if (!win5SoundFile.exists()) {
            FileUtils.unpackFile(win5SoundFile, "assets/minecraft/leaf/sound/win5.wav")
        }
        if (!win6SoundFile.exists()) {
            FileUtils.unpackFile(win6SoundFile, "assets/minecraft/leaf/sound/win6.wav")
        }
        if (!win7SoundFile.exists()) {
            FileUtils.unpackFile(win7SoundFile, "assets/minecraft/leaf/sound/win7.wav")
        }
        if (!win8SoundFile.exists()) {
            FileUtils.unpackFile(win8SoundFile, "assets/minecraft/leaf/sound/win8.wav")
        }
        if (!win9SoundFile.exists()) {
            FileUtils.unpackFile(win9SoundFile, "assets/minecraft/leaf/sound/win9.wav")
        }
        if (!startSoundFile.exists()) {
            FileUtils.unpackFile(startSoundFile, "assets/minecraft/leaf/sound/start.wav")
        }
        if (!start2SoundFile.exists()) {
            FileUtils.unpackFile(start2SoundFile, "assets/minecraft/leaf/sound/start2.wav")
        }
        if (!start3SoundFile.exists()) {
            FileUtils.unpackFile(start3SoundFile, "assets/minecraft/leaf/sound/start3.wav")
        }
        if (!start4SoundFile.exists()) {
            FileUtils.unpackFile(start4SoundFile, "assets/minecraft/leaf/sound/start4.wav")
        }
        if (!start5SoundFile.exists()) {
            FileUtils.unpackFile(start5SoundFile, "assets/minecraft/leaf/sound/start5.wav")
        }
        if (!start6SoundFile.exists()) {
            FileUtils.unpackFile(start6SoundFile, "assets/minecraft/leaf/sound/start6.wav")
        }
        if (!start7SoundFile.exists()) {
            FileUtils.unpackFile(start7SoundFile, "assets/minecraft/leaf/sound/start7.wav")
        }
        if (!start8SoundFile.exists()) {
            FileUtils.unpackFile(start8SoundFile, "assets/minecraft/leaf/sound/start8.wav")
        }
        if (!start9SoundFile.exists()) {
            FileUtils.unpackFile(start9SoundFile, "assets/minecraft/leaf/sound/start9.wav")
        }
        if (!enableSoundFile.exists()) {
            FileUtils.unpackFile(enableSoundFile, "assets/minecraft/leaf/sound/enable.wav")
        }
        if (!disableSoundFile.exists()) {
            FileUtils.unpackFile(disableSoundFile, "assets/minecraft/leaf/sound/disable.wav")
        }

        winSound = TipSoundPlayer(winSoundFile)
        winSound2 = TipSoundPlayer(win2SoundFile)
        winSound3 = TipSoundPlayer(win3SoundFile)
        winSound4 = TipSoundPlayer(win4SoundFile)
        winSound5 = TipSoundPlayer(win5SoundFile)
        winSound6 = TipSoundPlayer(win6SoundFile)
        winSound7 = TipSoundPlayer(win7SoundFile)
        winSound8 = TipSoundPlayer(win8SoundFile)
        winSound9 = TipSoundPlayer(win9SoundFile)
        startSound = TipSoundPlayer(startSoundFile)
        startSound2 = TipSoundPlayer(start2SoundFile)
        startSound3 = TipSoundPlayer(start3SoundFile)
        startSound4 = TipSoundPlayer(start4SoundFile)
        startSound5 = TipSoundPlayer(start5SoundFile)
        startSound6 = TipSoundPlayer(start6SoundFile)
        startSound7 = TipSoundPlayer(start7SoundFile)
        startSound8 = TipSoundPlayer(start8SoundFile)
        startSound9 = TipSoundPlayer(start9SoundFile)

        enableSound = TipSoundPlayer(enableSoundFile)
        disableSound = TipSoundPlayer(disableSoundFile)
    }
}
