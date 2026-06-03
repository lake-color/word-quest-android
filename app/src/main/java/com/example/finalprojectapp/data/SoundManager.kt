package com.example.finalprojectapp.data

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import com.example.finalprojectapp.R

class SoundManager(private val context: Context) {
    private val settingsManager = SettingsManager(context)
    private var bgmPlayer: MediaPlayer? = null
    private var soundPool: SoundPool
    private val sounds = mutableMapOf<String, Int>()

    init {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        
        soundPool = SoundPool.Builder()
            .setMaxStreams(5)
            .setAudioAttributes(audioAttributes)
            .build()

        // Load SFX (Resources will be added to res/raw)
        loadSfx("correct", "sfx_correct")
        loadSfx("wrong", "sfx_wrong")
        loadSfx("click", "sfx_click")
    }

    private fun loadSfx(name: String, fileName: String) {
        val resId = context.resources.getIdentifier(fileName, "raw", context.packageName)
        if (resId != 0) {
            sounds[name] = soundPool.load(context, resId, 1)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: SoundManager? = null

        fun getInstance(context: Context): SoundManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SoundManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    fun playBgm(fileName: String) {
        val resId = context.resources.getIdentifier(fileName, "raw", context.packageName)
        if (resId == 0) return

        stopBgm()
        bgmPlayer = MediaPlayer.create(context, resId).apply {
            isLooping = true
            setVolume(calculateBgmVolume(), calculateBgmVolume())
            start()
        }
    }

    fun stopBgm() {
        bgmPlayer?.stop()
        bgmPlayer?.release()
        bgmPlayer = null
    }

    fun playSfx(name: String) {
        val soundId = sounds[name] ?: return
        val volume = calculateSfxVolume()
        soundPool.play(soundId, volume, volume, 1, 0, 1f)
    }

    private fun calculateBgmVolume(): Float {
        val master = settingsManager.masterVolume / 10f
        val bgm = settingsManager.bgmVolume / 10f
        return master * bgm
    }

    private fun calculateSfxVolume(): Float {
        val master = settingsManager.masterVolume / 10f
        val sfx = settingsManager.sfxVolume / 10f
        return master * sfx
    }

    fun updateVolumes() {
        bgmPlayer?.setVolume(calculateBgmVolume(), calculateBgmVolume())
    }
}
