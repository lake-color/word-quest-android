package com.example.finalprojectapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.finalprojectapp.R
import com.example.finalprojectapp.databinding.ActivityHomeBinding
import com.google.android.material.tabs.TabLayout

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    
    // 프래그먼트 재사용을 위한 캐싱
    private val learnFragment by lazy { LearnFragment() }
    private val reviewFragment by lazy { ReviewFragment() }
    private val battleFragment by lazy { BattleFragment() }
    private var activeFragment: Fragment = learnFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initNavigation()
    }

    private fun initNavigation() {
        // 모든 프래그먼트를 미리 추가하고 Learn만 보이게 설정
        supportFragmentManager.beginTransaction().apply {
            add(R.id.home_container, battleFragment, "battle").hide(battleFragment)
            add(R.id.home_container, reviewFragment, "review").hide(reviewFragment)
            add(R.id.home_container, learnFragment, "learn")
            // Learn을 마지막에 추가하여 위로 올림
        }.commit()

        binding.bottomTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> showFragment(reviewFragment)
                    1 -> showFragment(learnFragment)
                    2 -> showFragment(battleFragment)
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