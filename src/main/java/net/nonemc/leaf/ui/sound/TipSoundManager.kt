package net.nonemc.leaf.ui.sound

import net.nonemc.leaf.file.soundsDir
import net.nonemc.leaf.libs.file.Unpack
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
        val enableSoundFile = File(soundsDir, "enable.wav")
        val disableSoundFile = File(soundsDir, "disable.wav")
        val winSoundFile = File(soundsDir, "win.wav")
        val win2SoundFile = File(soundsDir, "win2.wav")
        val win3SoundFile = File(soundsDir, "win3.wav")
        val win4SoundFile = File(soundsDir, "win4.wav")
        val win5SoundFile = File(soundsDir, "win5.wav")
        val win6SoundFile = File(soundsDir, "win6.wav")
        val win7SoundFile = File(soundsDir, "win7.wav")
        val win8SoundFile = File(soundsDir, "win8.wav")
        val win9SoundFile = File(soundsDir, "win9.wav")
        val win10SoundFile = File(soundsDir, "win10.wav")
        val win11SoundFile = File(soundsDir, "win11.wav")
        val win12SoundFile = File(soundsDir, "win12.wav")
        val win13SoundFile = File(soundsDir, "win13.wav")
        val win14SoundFile = File(soundsDir, "win14.wav")
        val win15SoundFile = File(soundsDir, "win15.wav")
        val win16SoundFile = File(soundsDir, "win16.wav")
        val win17SoundFile = File(soundsDir, "win17.wav")
        val win18SoundFile = File(soundsDir, "win18.wav")
        val win19SoundFile = File(soundsDir, "win19.wav")
        val win20SoundFile = File(soundsDir, "win20.wav")
        val win21SoundFile = File(soundsDir, "win21.wav")
        val win22SoundFile = File(soundsDir, "win22.wav")
        val win23SoundFile = File(soundsDir, "win23.wav")
        val win24SoundFile = File(soundsDir, "win24.wav")
        val win25SoundFile = File(soundsDir, "win25.wav")


        if (!winSoundFile.exists()) {
            Unpack.unpackFile(winSoundFile, "assets/minecraft/leaf/sound/win.wav")
        }
        if (!win2SoundFile.exists()) {
            Unpack.unpackFile(win2SoundFile, "assets/minecraft/leaf/sound/win2.wav")
        }
        if (!win3SoundFile.exists()) {
            Unpack.unpackFile(win3SoundFile, "assets/minecraft/leaf/sound/win3.wav")
        }
        if (!win4SoundFile.exists()) {
            Unpack.unpackFile(win4SoundFile, "assets/minecraft/leaf/sound/win4.wav")
        }
        if (!win5SoundFile.exists()) {
            Unpack.unpackFile(win5SoundFile, "assets/minecraft/leaf/sound/win5.wav")
        }
        if (!win6SoundFile.exists()) {
            Unpack.unpackFile(win6SoundFile, "assets/minecraft/leaf/sound/win6.wav")
        }
        if (!win7SoundFile.exists()) {
            Unpack.unpackFile(win7SoundFile, "assets/minecraft/leaf/sound/win7.wav")
        }
        if (!win8SoundFile.exists()) {
            Unpack.unpackFile(win8SoundFile, "assets/minecraft/leaf/sound/win8.wav")
        }
        if (!win9SoundFile.exists()) {
            Unpack.unpackFile(win9SoundFile, "assets/minecraft/leaf/sound/win9.wav")
        }
        if (!win10SoundFile.exists()) {
            Unpack.unpackFile(win10SoundFile, "assets/minecraft/leaf/sound/win10.wav")
        }
        if (!win11SoundFile.exists()) {
            Unpack.unpackFile(win11SoundFile, "assets/minecraft/leaf/sound/win11.wav")
        }
        if (!win12SoundFile.exists()) {
            Unpack.unpackFile(win12SoundFile, "assets/minecraft/leaf/sound/win12.wav")
        }
        if (!win13SoundFile.exists()) {
            Unpack.unpackFile(win13SoundFile, "assets/minecraft/leaf/sound/win13.wav")
        }
        if (!win14SoundFile.exists()) {
            Unpack.unpackFile(win14SoundFile, "assets/minecraft/leaf/sound/win14.wav")
        }
        if (!win15SoundFile.exists()) {
            Unpack.unpackFile(win15SoundFile, "assets/minecraft/leaf/sound/win15.wav")
        }
        if (!win16SoundFile.exists()) {
            Unpack.unpackFile(win16SoundFile, "assets/minecraft/leaf/sound/win16.wav")
        }
        if (!win17SoundFile.exists()) {
            Unpack.unpackFile(win17SoundFile, "assets/minecraft/leaf/sound/win17.wav")
        }
        if (!win18SoundFile.exists()) {
            Unpack.unpackFile(win18SoundFile, "assets/minecraft/leaf/sound/win18.wav")
        }
        if (!win19SoundFile.exists()) {
            Unpack.unpackFile(win19SoundFile, "assets/minecraft/leaf/sound/win19.wav")
        }
        if (!win20SoundFile.exists()) {
            Unpack.unpackFile(win20SoundFile, "assets/minecraft/leaf/sound/win20.wav")
        }
        if (!win21SoundFile.exists()) {
            Unpack.unpackFile(win21SoundFile, "assets/minecraft/leaf/sound/win21.wav")
        }
        if (!win22SoundFile.exists()) {
            Unpack.unpackFile(win22SoundFile, "assets/minecraft/leaf/sound/win22.wav")
        }
        if (!win23SoundFile.exists()) {
            Unpack.unpackFile(win23SoundFile, "assets/minecraft/leaf/sound/win23.wav")
        }
        if (!win24SoundFile.exists()) {
            Unpack.unpackFile(win24SoundFile, "assets/minecraft/leaf/sound/win24.wav")
        }
        if (!win25SoundFile.exists()) {
            Unpack.unpackFile(win25SoundFile, "assets/minecraft/leaf/sound/win25.wav")
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
