package com.example.finalprojectapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.finalprojectapp.R
import com.example.finalprojectapp.databinding.ActivityHomeBinding
import com.google.android.material.tabs.TabLayout

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initNavigation()
    }

    private fun initNavigation() {
        binding.bottomTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> replaceFragment(LearnFragment())
                    1 -> replaceFragment(ReviewFragment())
                    2 -> replaceFragment(BattleFragment())
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        // 초기 탭 설정 (LearnFragment가 자동으로 선택 및 로드됨)
        binding.bottomTabLayout.getTabAt(0)?.select()
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.home_container, fragment)
            .commit()
    }
}