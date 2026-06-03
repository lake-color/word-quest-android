package com.example.finalprojectapp.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import com.example.finalprojectapp.R
import com.example.finalprojectapp.data.SoundManager
import com.example.finalprojectapp.databinding.ActivityHomeBinding
import com.google.android.material.tabs.TabLayout

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var soundManager: SoundManager
    
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

        setupWindowInsets()
        initNavigation()
    }

    override fun onResume() {
        super.onResume()
        // 학습창 등에서 돌아왔을 때 설정된 메인 BGM 재생
        val settings = com.example.finalprojectapp.data.SettingsManager(this)
        val bgmName = if (settings.mainBgmIndex == 1) "bgm_main" else "bgm_main2"
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
            SettingsDialog().show(supportFragmentManager, "SettingsDialog")
        }

        // 초기 탭 설정: Learn(Index 1)
        binding.bottomTabLayout.post {
            binding.bottomTabLayout.getTabAt(1)?.select()
        }
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