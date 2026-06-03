package com.example.finalprojectapp.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.finalprojectapp.R
import com.example.finalprojectapp.databinding.ActivityStudyBinding
import com.example.finalprojectapp.ui.viewmodel.StudyViewModel

class StudyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStudyBinding
    private lateinit var viewModel: StudyViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[StudyViewModel::class.java]

        val stageNum = intent.getIntExtra("STAGE_NUM", 1)
        binding.txtStudyTitle.text = getString(R.string.stage_format, stageNum)

        setupListeners()
        observeViewModel()
        
        viewModel.loadWords(stageNum)
    }

    private fun setupListeners() {
        binding.cardWord.setOnClickListener {
            viewModel.toggleLanguage()
        }

        binding.btnPrev.setOnClickListener {
            viewModel.prevWord()
        }

        binding.btnNext.setOnClickListener {
            viewModel.nextWord()
        }

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnStar.setOnClickListener {
            viewModel.toggleMemorized()
        }
    }

    private fun observeViewModel() {
        viewModel.words.observe(this) { wordList ->
            if (wordList.isNotEmpty()) {
                binding.progressStudy.max = wordList.size
                updateUI()
            } else {
                binding.txtWord.text = "No words found"
                binding.layoutButtons.visibility = View.GONE
                binding.layoutProgress.visibility = View.GONE
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
        val isEng = viewModel.isShowingEnglish.value ?: true

        if (index in words.indices) {
            val word = words[index]
            binding.txtWord.text = word.english
            binding.txtMean.text = word.korean
            binding.txtMean.visibility = if (isEng) View.INVISIBLE else View.VISIBLE
            
            binding.txtProgress.text = getString(R.string.study_progress_format, index + 1, words.size)
            binding.progressStudy.progress = index + 1
            
            updateStarIcon(word.isMemorized)
            
            binding.btnPrev.isEnabled = index > 0
            binding.btnNext.isEnabled = index < words.size - 1
        }
    }

    private fun updateStarIcon(isMemorized: Boolean) {
        val iconRes = if (isMemorized) {
            android.R.drawable.btn_star_big_on
        } else {
            android.R.drawable.btn_star_big_off
        }
        binding.btnStar.setIconResource(iconRes)
    }
}
