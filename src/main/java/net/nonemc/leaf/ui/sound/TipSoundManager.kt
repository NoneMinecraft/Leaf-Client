package net.nonemc.leaf.ui.sound

import net.nonemc.leaf.Leaf
import net.nonemc.leaf.utils.file.FileUtils
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
    var winSound10: TipSoundPlayer
    var winSound11: TipSoundPlayer
    var winSound12: TipSoundPlayer
    var winSound13: TipSoundPlayer
    var winSound14: TipSoundPlayer
    var winSound15: TipSoundPlayer
    var winSound16: TipSoundPlayer
    var winSound17: TipSoundPlayer
    var winSound18: TipSoundPlayer
    var winSound19: TipSoundPlayer
    var winSound20: TipSoundPlayer
    var winSound21: TipSoundPlayer
    var winSound22: TipSoundPlayer
    var winSound23: TipSoundPlayer
    var winSound24: TipSoundPlayer
    var winSound25: TipSoundPlayer
    var enableSound: TipSoundPlayer
    var disableSound: TipSoundPlayer

    init {
        val enableSoundFile = File(Leaf.fileManager.soundsDir, "enable.wav")
        val disableSoundFile = File(Leaf.fileManager.soundsDir, "disable.wav")
        val winSoundFile = File(Leaf.fileManager.soundsDir, "win.wav")
        val win2SoundFile = File(Leaf.fileManager.soundsDir, "win2.wav")
        val win3SoundFile = File(Leaf.fileManager.soundsDir, "win3.wav")
        val win4SoundFile = File(Leaf.fileManager.soundsDir, "win4.wav")
        val win5SoundFile = File(Leaf.fileManager.soundsDir, "win5.wav")
        val win6SoundFile = File(Leaf.fileManager.soundsDir, "win6.wav")
        val win7SoundFile = File(Leaf.fileManager.soundsDir, "win7.wav")
        val win8SoundFile = File(Leaf.fileManager.soundsDir, "win8.wav")
        val win9SoundFile = File(Leaf.fileManager.soundsDir, "win9.wav")
        val win10SoundFile = File(Leaf.fileManager.soundsDir, "win10.wav")
        val win11SoundFile = File(Leaf.fileManager.soundsDir, "win11.wav")
        val win12SoundFile = File(Leaf.fileManager.soundsDir, "win12.wav")
        val win13SoundFile = File(Leaf.fileManager.soundsDir, "win13.wav")
        val win14SoundFile = File(Leaf.fileManager.soundsDir, "win14.wav")
        val win15SoundFile = File(Leaf.fileManager.soundsDir, "win15.wav")
        val win16SoundFile = File(Leaf.fileManager.soundsDir, "win16.wav")
        val win17SoundFile = File(Leaf.fileManager.soundsDir, "win17.wav")
        val win18SoundFile = File(Leaf.fileManager.soundsDir, "win18.wav")
        val win19SoundFile = File(Leaf.fileManager.soundsDir, "win19.wav")
        val win20SoundFile = File(Leaf.fileManager.soundsDir, "win20.wav")
        val win21SoundFile = File(Leaf.fileManager.soundsDir, "win21.wav")
        val win22SoundFile = File(Leaf.fileManager.soundsDir, "win22.wav")
        val win23SoundFile = File(Leaf.fileManager.soundsDir, "win23.wav")
        val win24SoundFile = File(Leaf.fileManager.soundsDir, "win24.wav")
        val win25SoundFile = File(Leaf.fileManager.soundsDir, "win25.wav")

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
        if (!win10SoundFile.exists()) {
            FileUtils.unpackFile(win10SoundFile, "assets/minecraft/leaf/sound/win10.wav")
        }
        if (!win11SoundFile.exists()) {
            FileUtils.unpackFile(win11SoundFile, "assets/minecraft/leaf/sound/win11.wav")
        }
        if (!win12SoundFile.exists()) {
            FileUtils.unpackFile(win12SoundFile, "assets/minecraft/leaf/sound/win12.wav")
        }
        if (!win13SoundFile.exists()) {
            FileUtils.unpackFile(win13SoundFile, "assets/minecraft/leaf/sound/win13.wav")
        }
        if (!win14SoundFile.exists()) {
            FileUtils.unpackFile(win14SoundFile, "assets/minecraft/leaf/sound/win14.wav")
        }
        if (!win15SoundFile.exists()) {
            FileUtils.unpackFile(win15SoundFile, "assets/minecraft/leaf/sound/win15.wav")
        }
        if (!win16SoundFile.exists()) {
            FileUtils.unpackFile(win16SoundFile, "assets/minecraft/leaf/sound/win16.wav")
        }
        if (!win17SoundFile.exists()) {
            FileUtils.unpackFile(win17SoundFile, "assets/minecraft/leaf/sound/win17.wav")
        }
        if (!win18SoundFile.exists()) {
            FileUtils.unpackFile(win18SoundFile, "assets/minecraft/leaf/sound/win18.wav")
        }
        if (!win19SoundFile.exists()) {
            FileUtils.unpackFile(win19SoundFile, "assets/minecraft/leaf/sound/win19.wav")
        }
        if (!win20SoundFile.exists()) {
            FileUtils.unpackFile(win20SoundFile, "assets/minecraft/leaf/sound/win20.wav")
        }
        if (!win21SoundFile.exists()) {
            FileUtils.unpackFile(win21SoundFile, "assets/minecraft/leaf/sound/win21.wav")
        }
        if (!win22SoundFile.exists()) {
            FileUtils.unpackFile(win22SoundFile, "assets/minecraft/leaf/sound/win22.wav")
        }
        if (!win23SoundFile.exists()) {
            FileUtils.unpackFile(win23SoundFile, "assets/minecraft/leaf/sound/win23.wav")
        }
        if (!win24SoundFile.exists()) {
            FileUtils.unpackFile(win24SoundFile, "assets/minecraft/leaf/sound/win24.wav")
        }
        if (!win25SoundFile.exists()) {
            FileUtils.unpackFile(win25SoundFile, "assets/minecraft/leaf/sound/win25.wav")
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
        winSound10 = TipSoundPlayer(win10SoundFile)
        winSound11 = TipSoundPlayer(win11SoundFile)
        winSound12 = TipSoundPlayer(win12SoundFile)
        winSound13 = TipSoundPlayer(win13SoundFile)
        winSound14 = TipSoundPlayer(win14SoundFile)
        winSound15 = TipSoundPlayer(win15SoundFile)
        winSound16 = TipSoundPlayer(win16SoundFile)
        winSound17 = TipSoundPlayer(win17SoundFile)
        winSound18 = TipSoundPlayer(win18SoundFile)
        winSound19 = TipSoundPlayer(win19SoundFile)
        winSound20 = TipSoundPlayer(win20SoundFile)
        winSound21 = TipSoundPlayer(win21SoundFile)
        winSound22 = TipSoundPlayer(win22SoundFile)
        winSound23 = TipSoundPlayer(win23SoundFile)
        winSound24 = TipSoundPlayer(win24SoundFile)
        winSound25 = TipSoundPlayer(win25SoundFile)

        enableSound = TipSoundPlayer(enableSoundFile)
        disableSound = TipSoundPlayer(disableSoundFile)
    }
}
