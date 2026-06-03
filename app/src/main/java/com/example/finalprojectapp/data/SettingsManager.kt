package com.example.finalprojectapp.data

import android.content.Context
import android.content.SharedPreferences

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
        // FPS 설정에 따른 로직 (필요 시 확장)
        // 실제 고주사율 지원 기기에서 Window의 FrameRate를 조절하는 등의 처리가 가능합니다.
    }
}
