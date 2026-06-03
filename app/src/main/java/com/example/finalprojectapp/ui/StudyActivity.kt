package com.example.finalprojectapp.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.ViewModelProvider
import com.example.finalprojectapp.R
import com.example.finalprojectapp.data.SoundManager
import com.example.finalprojectapp.databinding.ActivityStudyBinding
import com.example.finalprojectapp.ui.viewmodel.StudyViewModel

class StudyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStudyBinding
    private lateinit var viewModel: StudyViewModel
    private lateinit var soundManager: SoundManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityStudyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        soundManager = SoundManager.getInstance(this)
        soundManager.stopBgm() // 학습에 집중할 수 있도록 BGM 중지

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

        viewModel = ViewModelProvider(this)[StudyViewModel::class.java]

        val stageNum = intent.getIntExtra("STAGE_NUM", 1)
        binding.txtStudyTitle.text = getString(R.string.stage_format, stageNum)

        setupListeners()
        observeViewModel()
        
        viewModel.loadWords(stageNum)
    }

    private fun setupListeners() {
        // 뜻 카드 클릭 시 토글
        binding.cardMean.setOnClickListener {
            viewModel.toggleLanguage()
        }

        binding.btnPrev.setOnClickListener {
            viewModel.prevWord()
            playCardAnimation()
        }

        binding.btnNext.setOnClickListener {
            viewModel.nextWord()
            playCardAnimation()
        }

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnStar.setOnClickListener {
            viewModel.toggleMemorized()
            playStarAnimation()
        }
    }

    private fun playCardAnimation() {
        val animX = ObjectAnimator.ofFloat(binding.cardWord, "scaleX", 0.9f, 1.0f)
        val animY = ObjectAnimator.ofFloat(binding.cardWord, "scaleY", 0.9f, 1.0f)
        AnimatorSet().apply {
            playTogether(animX, animY)
            duration = 300
            interpolator = OvershootInterpolator()
            start()
        }
    }

    private fun playStarAnimation() {
        val animX = ObjectAnimator.ofFloat(binding.btnStar, "scaleX", 1.0f, 1.5f, 1.0f)
        val animY = ObjectAnimator.ofFloat(binding.btnStar, "scaleY", 1.0f, 1.5f, 1.0f)
        AnimatorSet().apply {
            playTogether(animX, animY)
            duration = 400
            interpolator = OvershootInterpolator()
            start()
        }
    }

    private fun observeViewModel() {
        viewModel.words.observe(this) { wordList ->
            if (wordList.isNotEmpty()) {
                binding.progressStudy.max = wordList.size
                updateUI()
            } else {
                binding.txtWord.text = getString(R.string.no_words_found, 1) // fallback
                binding.btnPrev.visibility = View.GONE
                binding.btnNext.visibility = View.GONE
                binding.btnStar.visibility = View.GONE
            }
        }

        viewModel.currentIndex.observe(this) {
            updateUI()
        }

        viewModel.isShowingEnglish.observe(this) {
            updateUI()
        }
    }

    private fun updateUI() {
        val words = viewModel.words.value ?: return
        val index = viewModel.currentIndex.value ?: 0
        val isShowingMean = !(viewModel.isShowingEnglish.value ?: true)

        if (index in words.indices) {
            val word = words[index]
            binding.txtWord.text = word.english
            
            if (isShowingMean) {
                binding.txtMean.text = word.korean
                binding.txtMean.alpha = 1.0f
                binding.cardMean.setCardBackgroundColor(getColor(R.color.wood_brown).let { 
                    // M3 Tonal variant simulation
                    it 
                })
                binding.txtMean.setTextColor(getColor(android.R.color.white))
            } else {
                binding.txtMean.text = getString(R.string.click_to_see_meaning)
                binding.txtMean.alpha = 0.5f
                binding.cardMean.setCardBackgroundColor(getColor(R.color.cream_background))
                binding.txtMean.setTextColor(getColor(R.color.wood_brown))
            }
            
            binding.txtProgress.text = getString(R.string.study_progress_format, index + 1, words.size)
            binding.progressStudy.setProgress(index + 1, true)
            
            updateStarIcon(word.isMemorized)
            
            binding.btnPrev.isEnabled = index > 0
            binding.btnNext.isEnabled = index < words.size - 1
        }
    }

    private fun updateStarIcon(isMemorized: Boolean) {
        val iconRes = if (isMemorized) {
            R.drawable.ic_star_filled
        } else {
            R.drawable.ic_star_hollow
        }
        binding.btnStar.setIconResource(iconRes)
        // Tint 제거 (드로어블 자체 색상 사용)
        binding.btnStar.iconTint = null
    }
}
