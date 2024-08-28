package net.ccbluex.liquidbounce.ui.sound

import java.io.File
import javax.sound.sampled.AudioSystem

class TipSoundPlayer(private val file: File) {
    fun asyncPlay() {
        Thread {
            val audioInputStream = AudioSystem.getAudioInputStream(file)
            val clip = AudioSystem.getClip()
            clip.open(audioInputStream)
            clip.start()
        }.start()
    }
}