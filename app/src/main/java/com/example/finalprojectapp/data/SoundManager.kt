package com.example.finalprojectapp.data

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import android.util.Log

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
            .setMaxStreams(10)
            .setAudioAttributes(audioAttributes)
            .build()

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
        if (currentBgmName == fileName && bgmPlayer?.isPlaying == true) return
        
        var resId = context.resources.getIdentifier(fileName, "raw", context.packageName)
        if (resId == 0) {
            val fallbackName = if (fileName.contains("game")) "bgm_game" else "bgm_main"
            resId = context.resources.getIdentifier(fallbackName, "raw", context.packageName)
            if (resId == 0) {
                stopBgm()
                return
            }
        }
        
        try {
            stopBgm()
            currentBgmName = fileName
            
            bgmPlayer = MediaPlayer.create(context, resId)?.apply {
                isLooping = true
                val vol = calculateBgmVolume()
                setVolume(vol, vol)
                setOnErrorListener { mp, _, _ ->
                    try { mp.reset() } catch (e: Exception) {}
                    false
                }
                start()
            }
        } catch (e: Exception) {
            Log.e("SoundManager", "BGM 재생 실패 ($fileName): ${e.message}")
            currentBgmName = null
        }
    }

    fun stopBgm() {
        bgmPlayer?.let {
            try {
                it.setOnPreparedListener(null)
                it.setOnErrorListener(null)
                if (it.isPlaying) it.stop()
                it.reset()
                it.release()
            } catch (e: Exception) {}
        }
        bgmPlayer = null
        currentBgmName = null
    }

    fun playSfx(name: String) {
        val soundId = sounds[name] ?: return
        var volume = calculateSfxVolume()
        
        // 정답/오답 효과음만 2.0 배로 상향
        if (name == "correct" || name == "wrong") {
            volume = (volume * 2.0f).coerceAtMost(1.0f)
        }
        
        // 클릭음은 아주 작게 (10% 수준)
        if (name == "click") {
            volume = (volume * 0.1f).coerceAtMost(1.0f)
        }

        soundPool.play(soundId, volume, volume, 1, 0, 1f)
    }

    private fun calculateBgmVolume(): Float {
        val master = settingsManager.masterVolume / 10f
        val bgm = settingsManager.bgmVolume / 10f
        return (master * bgm).coerceIn(0f, 1f)
    }

    private fun calculateSfxVolume(): Float {
        val master = settingsManager.masterVolume / 10f
        val sfx = settingsManager.sfxVolume / 10f
        return (master * sfx).coerceIn(0f, 1f)
    }

    fun updateVolumes() {
        val vol = calculateBgmVolume()
        bgmPlayer?.setVolume(vol, vol)
    }
}
