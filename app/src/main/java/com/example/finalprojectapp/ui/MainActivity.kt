package com.example.finalprojectapp.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.finalprojectapp.R
import com.example.finalprojectapp.data.SettingsManager
import com.example.finalprojectapp.data.SoundManager
import com.example.finalprojectapp.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayout
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity(), BgmChangeListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var soundManager: SoundManager
    private lateinit var settingsManager: SettingsManager
    
    private val timeTickReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == Intent.ACTION_TIME_TICK) {
                updateDateDisplay()
            }
        }
    }

    private val learnFragment by lazy { NavListFragment.newInstance(true) }
    private val wordbookFragment by lazy { NavListFragment.newInstance(false) }
    private val gameFragment by lazy { 
        supportFragmentManager.findFragmentByTag("game") as? GameFragment ?: GameFragment() 
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        soundManager = SoundManager.getInstance(this)
        settingsManager = SettingsManager(this)
        settingsManager.applySettings(this)

        updateDateDisplay()
        setupWindowInsets()
        
        if (savedInstanceState == null) {
            showStartScreen()
        }

        initNavigation()
        
        supportFragmentManager.addOnBackStackChangedListener {
            updateUIByFragment()
        }
    }

    private fun showStartScreen() {
        binding.appBarLayout.visibility = View.GONE
        binding.bottomTabLayout.visibility = View.GONE
        
        supportFragmentManager.beginTransaction()
            .replace(R.id.home_container, StartFragment(), "start")
            .commit()
    }

    fun navigateToHome() {
        binding.appBarLayout.visibility = View.VISIBLE
        binding.bottomTabLayout.visibility = View.VISIBLE
        
        val tab = binding.bottomTabLayout.getTabAt(1)
        if (tab?.isSelected == true) {
            showTabFragment(learnFragment, "learn")
        } else {
            tab?.select() // 이 호출이 onTabSelected를 트리거함
        }
    }

    private fun updateUIByFragment() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.home_container)
        when (currentFragment) {
            is StartFragment -> {
                binding.appBarLayout.visibility = View.GONE
                binding.bottomTabLayout.visibility = View.GONE
            }
            is NavListFragment, is GameFragment -> {
                binding.appBarLayout.visibility = View.VISIBLE
                binding.bottomTabLayout.visibility = View.VISIBLE
            }
            else -> {
                binding.appBarLayout.visibility = View.GONE
                binding.bottomTabLayout.visibility = View.GONE
            }
        }
    }

    private fun initNavigation() {
        binding.bottomTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                soundManager.playSfx("click")
                supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                
                when (tab?.position) {
                    0 -> showTabFragment(wordbookFragment, "wordbook")
                    1 -> showTabFragment(learnFragment, "learn")
                    2 -> showTabFragment(gameFragment, "game")
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {
                // 이미 선택된 탭을 다시 눌렀을 때도 프래그먼트가 보이게 보장
                when (tab?.position) {
                    0 -> showTabFragment(wordbookFragment, "wordbook")
                    1 -> showTabFragment(learnFragment, "learn")
                    2 -> showTabFragment(gameFragment, "game")
                }
            }
        })

        binding.btnHomeSettings.setOnClickListener {
            soundManager.playSfx("click")
            val dialog = SettingsDialog()
            dialog.setBgmChangeListener(this)
            dialog.show(supportFragmentManager, "SettingsDialog")
        }
    }

    private fun showTabFragment(fragment: Fragment, tag: String) {
        val transaction = supportFragmentManager.beginTransaction()
            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
        
        // 메인 탭 프래그먼트들과 시작 화면을 모두 숨김
        listOf("learn", "wordbook", "game", "start").forEach { t ->
            supportFragmentManager.findFragmentByTag(t)?.let { transaction.hide(it) }
        }

        if (!fragment.isAdded) {
            transaction.add(R.id.home_container, fragment, tag)
        }
        transaction.show(fragment).commit()
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
        sdf.timeZone = java.util.TimeZone.getTimeZone("Asia/Seoul")
        binding.txtCurrentDate.text = sdf.format(Date())
    }

    override fun onResume() {
        super.onResume()
        updateDateDisplay()
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

    override fun onBgmChanged(index: Int) {
        val bgmName = when (index) {
            1 -> "bgm_main"
            2 -> "bgm_main2"
            else -> "bgm_main3"
        }
        soundManager.playBgm(bgmName)
    }

    override fun onDestroy() {
        super.onDestroy()
        soundManager.release()
    }
}