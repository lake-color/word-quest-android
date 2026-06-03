package com.example.finalprojectapp.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import com.example.finalprojectapp.R
import com.example.finalprojectapp.data.SettingsManager
import com.example.finalprojectapp.data.SoundManager
import com.example.finalprojectapp.databinding.ActivityHomeBinding
import com.google.android.material.tabs.TabLayout
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var soundManager: SoundManager
    private lateinit var settingsManager: SettingsManager
    
    private val timeTickReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == Intent.ACTION_TIME_TICK) {
                updateDateDisplay()
            }
        }
    }

    // 프래그먼트 재사용을 위한 캐싱
    private val learnFragment by lazy { LearnFragment() }
    private val wordbookFragment by lazy { WordbookFragment() }
    private val gameFragment by lazy { GameFragment() }
    private var activeFragment: Fragment = learnFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        soundManager = SoundManager.getInstance(this)
        settingsManager = SettingsManager(this)
        settingsManager.applySettings(this)

        updateDateDisplay()
        setupWindowInsets()
        initNavigation()
    }

    override fun onStart() {
        super.onStart()
        registerReceiver(timeTickReceiver, IntentFilter(Intent.ACTION_TIME_TICK))
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(timeTickReceiver)
    }

    private fun updateDateDisplay() {
        val sdf = SimpleDateFormat(getString(R.string.date_format), Locale.KOREAN)
        sdf.timeZone = java.util.TimeZone.getTimeZone("Asia/Seoul") // 한국 표준시 강제 설정
        binding.txtCurrentDate.text = sdf.format(Date())
    }

    override fun onResume() {
        super.onResume()
        updateDateDisplay() // 화면에 보일 때마다 날짜 최신화
        // 학습창 등에서 돌아왔을 때 설정된 메인 BGM 재생
        val bgmName = when (settingsManager.mainBgmIndex) {
            1 -> "bgm_main"
            2 -> "bgm_main2"
            else -> "bgm_main3"
        }
        soundManager.playBgm(bgmName)
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(
                left = systemBars.left,
                top = systemBars.top,
                right = systemBars.right,
                bottom = systemBars.bottom
            )
            insets
        }
    }

    private fun initNavigation() {
        // 모든 프래그먼트를 미리 추가하고 Learn만 보이게 설정
        supportFragmentManager.beginTransaction().apply {
            add(R.id.home_container, gameFragment, "game").hide(gameFragment)
            add(R.id.home_container, wordbookFragment, "wordbook").hide(wordbookFragment)
            add(R.id.home_container, learnFragment, "learn")
            // Learn을 마지막에 추가하여 위로 올림
        }.commit()

        binding.bottomTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                soundManager.playSfx("click")
                when (tab?.position) {
                    0 -> showFragment(wordbookFragment)
                    1 -> showFragment(learnFragment)
                    2 -> showFragment(gameFragment)
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        binding.btnHomeSettings.setOnClickListener {
            soundManager.playSfx("click")
            SettingsDialog().show(supportFragmentManager, "SettingsDialog")
        }

        // 초기 탭 설정: Learn(Index 1)
        binding.bottomTabLayout.post {
            binding.bottomTabLayout.getTabAt(1)?.select()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun showFragment(fragment: Fragment) {
        if (activeFragment == fragment) return
        
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out) // 부드러운 전환 애니메이션
            .hide(activeFragment)
            .show(fragment)
            .commit()
        
        activeFragment = fragment
    }
}