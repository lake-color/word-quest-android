package com.example.finalprojectapp.ui

import android.animation.Animator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.finalprojectapp.R
import com.example.finalprojectapp.data.SettingsManager
import com.example.finalprojectapp.data.SoundManager
import com.example.finalprojectapp.data.Word
import com.example.finalprojectapp.databinding.FragmentGameBinding
import com.example.finalprojectapp.ui.viewmodel.GameViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class GameFragment : Fragment() {
    private var _binding: FragmentGameBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: GameViewModel
    private var allWords = listOf<Word>()
    private var filteredWords = listOf<Word>()
    private var currentQuestion: Word? = null
    private var isPlaying = false

    private val selectedDays = mutableSetOf<Int>()
    private lateinit var settingsManager: SettingsManager
    private lateinit var soundManager: SoundManager

    private val activeAnimators = mutableListOf<ValueAnimator>()
    private var backgroundAnimator: ValueAnimator? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[GameViewModel::class.java]
        settingsManager = SettingsManager(requireContext())
        soundManager = SoundManager.getInstance(requireContext())
        
        setupDragControl()
        setupButtons()
        initDaySelectionGrid()
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.allWords.observe(viewLifecycleOwner) { words ->
            allWords = words
            if (allWords.isNotEmpty()) {
                binding.layoutStart.isVisible = true
                binding.btnStartGame.isEnabled = true
            } else {
                binding.txtCurrentWord.text = "No words available."
                binding.btnStartGame.isEnabled = false
            }
        }

        viewModel.score.observe(viewLifecycleOwner) { score ->
            binding.txtScore.text = "SCORE: $score"
        }

        viewModel.hp.observe(viewLifecycleOwner) { hp ->
            updateHearts(hp)
            if (hp <= 0 && isPlaying) {
                gameOver()
            }
        }
    }

    private fun initDaySelectionGrid() {
        binding.gridDaySelection.removeAllViews()
        selectedDays.clear()
        selectedDays.add(1) 

        val btnSize = dpToPx(50)
        val margin = dpToPx(4)

        for (day in 1..20) {
            val btn = MaterialButton(requireContext(), null, com.google.android.material.R.attr.materialButtonOutlinedStyle).apply {
                text = day.toString()
                minWidth = 0
                minimumWidth = 0
                insetTop = 0
                insetBottom = 0
                setPadding(0, 0, 0, 0)
                layoutParams = ViewGroup.MarginLayoutParams(btnSize, btnSize).apply {
                    setMargins(margin, margin, margin, margin)
                }
                textSize = 12f
                
                if (day == 1) {
                    checkDayButton(this, true)
                } else {
                    checkDayButton(this, false)
                }

                setOnClickListener {
                    if (selectedDays.contains(day)) {
                        if (selectedDays.size > 1) {
                            selectedDays.remove(day)
                            checkDayButton(this, false)
                        }
                    } else {
                        selectedDays.add(day)
                        checkDayButton(this, true)
                    }
                }
            }
            binding.gridDaySelection.addView(btn)
        }
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    private fun checkDayButton(btn: MaterialButton, isChecked: Boolean) {
        if (isChecked) {
            btn.setBackgroundColor(requireContext().getColor(R.color.wood_brown))
            btn.setTextColor(Color.WHITE)
        } else {
            btn.setBackgroundColor(Color.TRANSPARENT)
            btn.setTextColor(requireContext().getColor(R.color.wood_brown))
        }
    }

    private fun setupButtons() {
        binding.btnStartGame.setOnClickListener {
            prepareFilteredWords()
            if (filteredWords.isEmpty()) return@setOnClickListener
            binding.layoutStart.isVisible = false
            startCountdown()
        }
        binding.btnRestart.setOnClickListener {
            binding.layoutGameOver.isVisible = false
            startCountdown()
        }
        binding.btnExit.setOnClickListener {
            binding.layoutGameOver.isVisible = false
            resetGameState()
            binding.layoutStart.isVisible = true
        }
    }

    private fun prepareFilteredWords() {
        filteredWords = allWords.filter { it.stage in selectedDays }
    }

    private fun startCountdown() {
        if (_binding == null) return

        binding.layoutCountdown.isVisible = true
        var count = 3

        val runnable = object : Runnable {
            override fun run() {
                if (_binding == null || !isAdded) return

                when {
                    count > 0 -> {
                        binding.txtCountdown.text = count.toString()
                        binding.txtCountdown.scaleX = 0.5f
                        binding.txtCountdown.scaleY = 0.5f
                        binding.txtCountdown.animate()
                            .scaleX(1.1f)
                            .scaleY(1.1f)
                            .setDuration(700)
                            .start()
                        count--
                        binding.gameRoot.postDelayed(this, 1000)
                    }
                    count == 0 -> {
                        binding.txtCountdown.text = "START!"
                        binding.txtCountdown.scaleX = 0.6f
                        binding.txtCountdown.scaleY = 0.6f
                        count--
                        binding.gameRoot.postDelayed(this, 800)
                    }
                    else -> {
                        binding.layoutCountdown.isVisible = false
                        startGame()
                    }
                }
            }
        }
        binding.gameRoot.post(runnable)
    }

    private fun startGame() {
        resetGameState()
        isPlaying = true
        startBackgroundAnimation()
        spawnLoop()
    }

    private fun resetGameState() {
        if (_binding == null) return

        isPlaying = false
        viewModel.resetGame()
        binding.txtCurrentWord.text = "READY?"
        binding.gameContainer.removeAllViews()

        binding.road1.translationY = 0f
        binding.road2.translationY = 0f

        activeAnimators.forEach { it.cancel() }
        activeAnimators.clear()

        backgroundAnimator?.cancel()
        backgroundAnimator = null
    }

    private fun startBackgroundAnimation() {
        backgroundAnimator?.cancel()
        binding.road1.post {
            if (_binding == null) return@post
            val roadHeight = binding.road1.height.toFloat()
            if (roadHeight <= 0) return@post

            backgroundAnimator = ValueAnimator.ofFloat(0f, roadHeight).apply {
                duration = 10000L
                repeatCount = ValueAnimator.INFINITE
                interpolator = LinearInterpolator()
                addUpdateListener { anim ->
                    if (_binding == null || !isPlaying) {
                        anim.cancel()
                        return@addUpdateListener
                    }
                    val offset = anim.animatedValue as Float
                    binding.road1.translationY = offset
                    binding.road2.translationY = offset - roadHeight
                }
                start()
            }
        }
    }

    private fun spawnLoop() {
        if (!isPlaying || (viewModel.hp.value ?: 0) <= 0 || !isAdded || _binding == null) return

        spawnGates()
        spawnRoadLines()

        binding.gameRoot.postDelayed({
            if (isPlaying && _binding != null && isAdded) {
                spawnLoop()
            }
        }, 2200)
    }

    private fun spawnRoadLines() {
        if (_binding == null || !isAdded) return
        val line = View(requireContext()).apply {
            layoutParams = FrameLayout.LayoutParams(10, 60).apply { gravity = Gravity.CENTER_HORIZONTAL }
            setBackgroundColor(Color.WHITE)
            alpha = 0.4f
        }
        binding.gameContainer.addView(line, 0)

        ValueAnimator.ofFloat(0f, 1.1f).apply {
            duration = 1800
            interpolator = LinearInterpolator()
            addUpdateListener { anim ->
                if (_binding == null) { anim.cancel(); return@addUpdateListener }
                val p = anim.animatedValue as Float
                line.translationY = binding.gameRoot.height * p
            }
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationEnd(a: Animator) { if (_binding != null) binding.gameContainer.removeView(line) }
                override fun onAnimationStart(a: Animator) {}
                override fun onAnimationRepeat(a: Animator) {}
                override fun onAnimationCancel(a: Animator) {}
            })
            start()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupDragControl() {
        binding.gameRoot.setOnTouchListener { _, event ->
            if (!isPlaying || _binding == null) return@setOnTouchListener false
            if (event.action == MotionEvent.ACTION_MOVE || event.action == MotionEvent.ACTION_DOWN) {
                val screenWidth = binding.gameRoot.width.toFloat()
                if (screenWidth > 0) {
                    val targetX = event.x - (binding.playerCharacter.width / 2f)
                    binding.playerCharacter.x = targetX.coerceIn(0f, screenWidth - binding.playerCharacter.width)
                }
            }
            true
        }
    }

    private fun spawnGates() {
        if (!isPlaying || !isAdded || _binding == null || filteredWords.isEmpty()) return

        val question = filteredWords.random()
        currentQuestion = question
        binding.txtCurrentWord.text = question.english

        val wrongAnswer = allWords.filter { it.id != question.id }.let {
            if (it.isNotEmpty()) it.random().korean else "???"
        }

        val gateView = LayoutInflater.from(requireContext()).inflate(R.layout.item_gate, binding.gameContainer, false)
        val tvLeft = gateView.findViewById<TextView>(R.id.tvLeft)
        val tvRight = gateView.findViewById<TextView>(R.id.tvRight)

        val isCorrectLeft = Random.nextBoolean()
        tvLeft.text = if (isCorrectLeft) question.korean else wrongAnswer
        tvRight.text = if (isCorrectLeft) wrongAnswer else question.korean

        gateView.findViewById<MaterialCardView>(R.id.cardLeft).setCardBackgroundColor(if (isCorrectLeft) 0x802196F3.toInt() else 0x80F44336.toInt())
        gateView.findViewById<MaterialCardView>(R.id.cardRight).setCardBackgroundColor(if (isCorrectLeft) 0x80F44336.toInt() else 0x802196F3.toInt())

        gateView.scaleX = 1.0f
        gateView.scaleY = 1.0f
        gateView.alpha = 1.0f
        binding.gameContainer.addView(gateView)

        val animator = ValueAnimator.ofFloat(-0.2f, 1.1f).apply {
            duration = 3500
            interpolator = LinearInterpolator()
            var collisionChecked = false

            addUpdateListener { anim ->
                if (_binding == null || !isAdded) { anim.cancel(); return@addUpdateListener }
                val progress = anim.animatedValue as Float
                gateView.translationY = binding.gameRoot.height * progress

                if (!collisionChecked && progress > 0.82f && progress < 0.90f) {
                    collisionChecked = true
                    checkCollision(isCorrectLeft)
                }
            }
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationEnd(a: Animator) { if (_binding != null) binding.gameContainer.removeView(gateView); activeAnimators.remove(this as? ValueAnimator) }
                override fun onAnimationStart(a: Animator) {}
                override fun onAnimationRepeat(a: Animator) {}
                override fun onAnimationCancel(a: Animator) {}
            })
        }
        activeAnimators.add(animator)
        animator.start()
    }

    private fun checkCollision(isCorrectLeft: Boolean) {
        if (_binding == null) return
        val playerX = binding.playerCharacter.x + (binding.playerCharacter.width / 2f)
        val isPlayerLeft = playerX < (binding.gameRoot.width / 2f)
        val isCorrect = if (isPlayerLeft) isCorrectLeft else !isCorrectLeft

        if (isCorrect) { 
            viewModel.addScore(10)
            soundManager.playSfx("correct")
        } 
        else {
            viewModel.decreaseHp()
            soundManager.playSfx("wrong")
            triggerVibration()
            currentQuestion?.let { viewModel.updateWrongCount(it) }
        }
    }

    private fun triggerVibration() {
        if (settingsManager.isVibrationEnabled) {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = requireContext().getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vibratorManager.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                requireContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(200)
            }
        }
    }

    private fun gameOver() {
        isPlaying = false
        activeAnimators.forEach { it.cancel() }
        activeAnimators.clear()
        backgroundAnimator?.cancel()
        backgroundAnimator = null
        
        if (_binding != null) {
            val score = viewModel.score.value ?: 0
            val prefs = requireContext().getSharedPreferences("WordQuestGame", Context.MODE_PRIVATE)
            val best = prefs.getInt("best_score", 0)
            if (score > best) prefs.edit().putInt("best_score", score).apply()
            
            binding.txtFinalScore.text = "Score: $score"
            binding.txtBestScore.text = "Best: ${maxOf(score, best)}"
            binding.layoutGameOver.isVisible = true
        }
    }

    private fun updateHearts(hp: Int) {
        if (_binding == null) return
        val hearts = listOf(binding.ivHeart1, binding.ivHeart2, binding.ivHeart3)
        hearts.forEachIndexed { i, iv ->
            if (i < hp) { iv.alpha = 1f; iv.scaleX = 1f; iv.scaleY = 1f }
            else { iv.alpha = 0.3f; iv.scaleX = 0.8f; iv.scaleY = 0.8f }
        }
    }

    override fun onDestroyView() {
        isPlaying = false
        activeAnimators.forEach { it.cancel() }
        activeAnimators.clear()
        backgroundAnimator?.cancel()
        backgroundAnimator = null
        _binding = null
        super.onDestroyView()
    }
}
