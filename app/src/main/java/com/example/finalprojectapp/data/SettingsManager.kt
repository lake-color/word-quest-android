package com.example.finalprojectapp.data

import android.content.Context
import android.content.SharedPreferences

import android.os.Build

class SettingsManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("word_quest_settings", Context.MODE_PRIVATE)

    var masterVolume: Int
        get() = prefs.getInt("master_volume", 5)
        set(value) = prefs.edit().putInt("master_volume", value).apply()

    var bgmVolume: Int
        get() = prefs.getInt("bgm_volume", 7)
        set(value) = prefs.edit().putInt("bgm_volume", value).apply()

    var sfxVolume: Int
        get() = prefs.getInt("sfx_volume", 7)
        set(value) = prefs.edit().putInt("sfx_volume", value).apply()

    var fps: Int
        get() = prefs.getInt("fps", 120)
        set(value) = prefs.edit().putInt("fps", value).apply()

    var isVibrationEnabled: Boolean
        get() = prefs.getBoolean("vibration_enabled", true)
        set(value) = prefs.edit().putBoolean("vibration_enabled", value).apply()

    var mainBgmIndex: Int
        get() = prefs.getInt("main_bgm_index", 1) // 1, 2 or 3
        set(value) = prefs.edit().putInt("main_bgm_index", value).apply()

    var gameBgmIndex: Int
        get() = prefs.getInt("game_bgm_index", 1) // 1 or 2
        set(value) = prefs.edit().putInt("game_bgm_index", value).apply()

    fun applySettings(activity: android.app.Activity) {
        // FPS 적용 (주사율 설정)
        val layoutParams = activity.window.attributes
        layoutParams.preferredRefreshRate = fps.toFloat()
        activity.window.attributes = layoutParams
    }
}
