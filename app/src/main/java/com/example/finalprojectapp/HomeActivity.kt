package com.example.finalprojectapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.finalprojectapp.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 초기 화면: 학습(Learn) 설정
        if (savedInstanceState == null) {
            replaceFragment(LearnFragment())
        }

        initNavigation()
    }

    private fun initNavigation() {
        binding.btnNavLearn.setOnClickListener { replaceFragment(LearnFragment()) }
        binding.btnNavReview.setOnClickListener { replaceFragment(ReviewFragment()) }
        binding.btnNavBattle.setOnClickListener { replaceFragment(BattleFragment()) }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.home_container, fragment)
            .commit()
    }
}