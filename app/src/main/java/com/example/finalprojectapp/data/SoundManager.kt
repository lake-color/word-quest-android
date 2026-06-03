package com.example.finalprojectapp.data

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool

class SoundManager(private val context: Context) {
    private val settingsManager = SettingsManager(context)
    private var bgmPlayer: MediaPlayer? = null
    private var soundPool: SoundPool
    private val sounds = mutableMapOf<String, Int>()
    private var currentBgmName: String? = null

    init {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        
        soundPool = SoundPool.Builder()
            .setMaxStreams(5)
            .setAudioAttributes(audioAttributes)
            .build()

        // Load SFX: correct and wrong will use the same 'sfx_game' resource
        loadSfx("correct", "sfx_game")
        loadSfx("wrong", "sfx_game")
        // click SFX removed as requested
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
        // 이미 해당 곡이 재생 중이면 중복 실행하지 않음 (부드러운 유지)
        if (currentBgmName == fileName && bgmPlayer?.isPlaying == true) return
        
        val resId = context.resources.getIdentifier(fileName, "raw", context.packageName)
        if (resId == 0) {
            stopBgm()
            return
        }
        
        stopBgm()
        currentBgmName = fileName
        bgmPlayer = MediaPlayer.create(context, resId).apply {
            isLooping = true // 무한 반복 설정
            setVolume(calculateBgmVolume(), calculateBgmVolume())
            start()
        }
    }

    fun stopBgm() {
        bgmPlayer?.stop()
        bgmPlayer?.release()
        bgmPlayer = null
        currentBgmName = null
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
        val vol = calculateBgmVolume()
        bgmPlayer?.setVolume(vol, vol)
    }
}
