package com.example.finalprojectapp.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.finalprojectapp.R
import com.example.finalprojectapp.data.SettingsManager
import com.example.finalprojectapp.data.SoundManager
import com.example.finalprojectapp.databinding.FragmentStudyBinding
import com.example.finalprojectapp.ui.viewmodel.MainViewModel

class StudyFragment : Fragment() {
    private var _binding: FragmentStudyBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewModel: MainViewModel
    private lateinit var soundManager: SoundManager
    private lateinit var settingsManager: SettingsManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStudyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        soundManager = SoundManager.getInstance(requireContext())
        settingsManager = SettingsManager(requireContext())
        settingsManager.applySettings(requireActivity())
        soundManager.stopBgm()

        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        val stageNum = arguments?.getInt("STAGE_NUM") ?: 1
        binding.txtStudyTitle.text = getString(R.string.stage_format, stageNum)

        setupListeners()
        observeViewModel()
        
        viewModel.loadWordsByDay(stageNum)
    }

    private fun setupListeners() {
        binding.cardMean.setOnClickListener {
            soundManager.playSfx("click")
            val current = viewModel.isShowingEnglish.value ?: true
            viewModel.isShowingEnglish.value = !current
        }

        binding.btnPrev.setOnClickListener {
            soundManager.playSfx("click")
            val index = viewModel.currentIndex.value ?: 0
            if (index > 0) {
                viewModel.currentIndex.value = index - 1
                viewModel.isShowingEnglish.value = true
                playCardAnimation()
            }
        }

        binding.btnNext.setOnClickListener {
            soundManager.playSfx("click")
            val index = viewModel.currentIndex.value ?: 0
            val size = viewModel.currentWords.value?.size ?: 0
            if (index < size - 1) {
                viewModel.currentIndex.value = index + 1
                viewModel.isShowingEnglish.value = true
                playCardAnimation()
            }
        }

        binding.btnBack.setOnClickListener {
            soundManager.playSfx("click")
            parentFragmentManager.popBackStack()
        }

        binding.btnStar.setOnClickListener {
            soundManager.playSfx("click")
            val words = viewModel.currentWords.value ?: return@setOnClickListener
            val index = viewModel.currentIndex.value ?: 0
            if (index in words.indices) {
                viewModel.toggleMemorized(words[index])
                playStarAnimation()
            }
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
        viewModel.currentWords.observe(viewLifecycleOwner) { wordList ->
            if (wordList.isNotEmpty()) {
                binding.progressStudy.max = wordList.size
                updateUI()
            } else {
                binding.txtWord.text = getString(R.string.no_words_found, 1)
                binding.btnPrev.visibility = View.GONE
                binding.btnNext.visibility = View.GONE
                binding.btnStar.visibility = View.GONE
            }
        }

        viewModel.currentIndex.observe(viewLifecycleOwner) { updateUI() }
        viewModel.isShowingEnglish.observe(viewLifecycleOwner) { updateUI() }
    }

    private fun updateUI() {
        val words = viewModel.currentWords.value ?: return
        val index = viewModel.currentIndex.value ?: 0
        val isShowingMean = !(viewModel.isShowingEnglish.value ?: true)

        if (index in words.indices) {
            val word = words[index]
            binding.txtWord.text = word.english
            
            if (isShowingMean) {
                binding.txtMean.text = word.korean
                binding.txtMean.alpha = 1.0f
                binding.cardMean.setCardBackgroundColor(requireContext().getColor(R.color.wood_brown))
                binding.txtMean.setTextColor(requireContext().getColor(android.R.color.white))
            } else {
                binding.txtMean.text = getString(R.string.click_to_see_meaning)
                binding.txtMean.alpha = 0.5f
                binding.cardMean.setCardBackgroundColor(requireContext().getColor(R.color.cream_background))
                binding.txtMean.setTextColor(requireContext().getColor(R.color.wood_brown))
            }
            
            binding.txtProgress.text = getString(R.string.study_progress_format, index + 1, words.size)
            binding.progressStudy.setProgress(index + 1, true)
            
            updateStarIcon(word.isMemorized)
            
            binding.btnPrev.isEnabled = index > 0
            binding.btnNext.isEnabled = index < words.size - 1
        }
    }

    private fun updateStarIcon(isMemorized: Boolean) {
        val iconRes = if (isMemorized) R.drawable.ic_star_filled else R.drawable.ic_star_hollow
        binding.btnStar.setIconResource(iconRes)
        binding.btnStar.iconTint = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}