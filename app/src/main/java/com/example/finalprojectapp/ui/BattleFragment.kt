package com.example.finalprojectapp.ui

import android.animation.Animator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
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
import androidx.lifecycle.lifecycleScope
import com.example.finalprojectapp.R
import com.example.finalprojectapp.data.Word
import com.example.finalprojectapp.data.WordDatabase
import com.example.finalprojectapp.databinding.FragmentBattleBinding
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

class BattleFragment : Fragment() {
    private var _binding: FragmentBattleBinding? = null
    private val binding get() = _binding!!

    private var allWords = listOf<Word>()
    private var currentQuestion: Word? = null
    private var score = 0
    private var hp = 3
    private var isPlaying = false

    private val activeAnimators = mutableListOf<ValueAnimator>()
    private var backgroundAnimator: ValueAnimator? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBattleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDragControl()
        setupButtons()
        loadDataAndPrepare()
    }

    private fun setupButtons() {
        binding.btnRestart.setOnClickListener {
            binding.layoutGameOver.isVisible = false
            startCountdown()
        }
        binding.btnExit.setOnClickListener {
            binding.layoutGameOver.isVisible = false
            resetGameState()
        }
    }

    private fun loadDataAndPrepare() {
        lifecycleScope.launch {
            try {
                val db = WordDatabase.getDatabase(requireContext())
                allWords = withContext(Dispatchers.IO) {
                    db.wordDao().getAllWordsList()
                }

                if (allWords.isNotEmpty()) {
                    startCountdown()
                } else {
                    if (_binding != null) {
                        binding.txtCurrentWord.text = "No words available."
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                if (_binding != null) {
                    binding.txtCurrentWord.text = "Error loading words"
                }
            }
        }
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
                            .scaleX(1.2f)
                            .scaleY(1.2f)
                            .setDuration(800)
                            .start()
                        count--
                        binding.battleRoot.postDelayed(this, 1000)
                    }
                    count == 0 -> {
                        binding.txtCountdown.text = "START!"
                        count--
                        binding.battleRoot.postDelayed(this, 800)
                    }
                    else -> {
                        binding.layoutCountdown.isVisible = false
                        startGame()
                    }
                }
            }
        }
        binding.battleRoot.post(runnable)
    }

    private fun resetGameState() {
        if (_binding == null) return

        isPlaying = false
        score = 0
        hp = 3
        updateHUD()
        binding.txtCurrentWord.text = "READY?"
        binding.gameContainer.removeAllViews()

        // 배경 위치 초기화
        binding.road1.translationY = 0f
        binding.road2.translationY = 0f

        // 모든 애니메이터 정리
        activeAnimators.forEach { it.cancel() }
        activeAnimators.clear()

        backgroundAnimator?.cancel()
        backgroundAnimator = null
    }

    private fun startGame() {
        resetGameState()
        isPlaying = true
        updateHUD()
        startBackgroundAnimation()
        spawnLoop()
    }

    private fun startBackgroundAnimation() {
        // 기존 배경 애니메이션 취소
        backgroundAnimator?.cancel()

        binding.road1.post {
            if (_binding == null) return@post

            val roadHeight = binding.road1.height.toFloat()
            if (roadHeight <= 0) return@post

            binding.road2.translationY = -roadHeight

            val animator = ValueAnimator.ofFloat(0f, roadHeight)
            animator.duration = 10000L
            animator.repeatCount = ValueAnimator.INFINITE
            animator.interpolator = LinearInterpolator()

            animator.addUpdateListener { anim ->
                if (_binding == null || !isPlaying) {
                    anim.cancel()
                    return@addUpdateListener
                }

                val offset = anim.animatedValue as Float

                binding.road1.translationY = offset
                binding.road2.translationY = offset - roadHeight

                // 순환 처리
                if (binding.road1.translationY >= roadHeight) {
                    binding.road1.translationY -= roadHeight * 2
                }
                if (binding.road2.translationY >= roadHeight) {
                    binding.road2.translationY -= roadHeight * 2
                }
            }

            backgroundAnimator = animator
            animator.start()
        }
    }

    private fun spawnLoop() {
        if (!isPlaying || hp <= 0 || !isAdded) return

        spawnGates()
        spawnRoadLines()

        binding.battleRoot.postDelayed({
            if (isPlaying && _binding != null && isAdded) {
                spawnLoop()
            }
        }, 2000)
    }

    private fun spawnRoadLines() {
        if (!isAdded || _binding == null) return

        val line = View(requireContext())
        line.layoutParams = FrameLayout.LayoutParams(10, 60).apply {
            gravity = Gravity.CENTER_HORIZONTAL
        }
        line.setBackgroundColor(Color.WHITE)
        line.alpha = 0.5f
        binding.gameContainer.addView(line, 0)

        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.duration = 2000
        animator.interpolator = LinearInterpolator()

        animator.addUpdateListener { anim ->
            if (_binding == null || !isAdded) {
                anim.cancel()
                return@addUpdateListener
            }

            val progress = anim.animatedValue as Float
            line.translationY = binding.battleRoot.height * progress
            line.scaleY = 1f + progress * 2f
            line.alpha = 0.5f * (1f - progress)
        }

        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) {
                if (_binding != null && isAdded) {
                    binding.gameContainer.removeView(line)
                }
            }
        })

        animator.start()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupDragControl() {
        binding.battleRoot.setOnTouchListener { _, event ->
            if (!isPlaying || _binding == null) return@setOnTouchListener false

            when (event.action) {
                MotionEvent.ACTION_MOVE, MotionEvent.ACTION_DOWN -> {
                    val screenWidth = binding.battleRoot.width.toFloat()
                    if (screenWidth > 0) {
                        val targetX = event.x - (binding.playerCharacter.width / 2f)
                        val minX = 0f
                        val maxX = screenWidth - binding.playerCharacter.width
                        binding.playerCharacter.x = targetX.coerceIn(minX, maxX)
                    }
                }
            }
            true
        }
    }

    private fun spawnGates() {
        if (!isPlaying || !isAdded || _binding == null || allWords.isEmpty()) return

        val question = allWords.random()
        currentQuestion = question
        binding.txtCurrentWord.text = question.english

        val wrongAnswer = allWords.filter { it.id != question.id }.let {
            if (it.isNotEmpty()) it.random().korean else "???"
        }

        val inflater = LayoutInflater.from(requireContext())
        val gateView = inflater.inflate(R.layout.item_gate, binding.gameContainer, false)

        val tvLeft = gateView.findViewById<TextView>(R.id.tvLeft)
        val tvRight = gateView.findViewById<TextView>(R.id.tvRight)

        val isCorrectLeft = Random.nextBoolean()
        tvLeft.text = if (isCorrectLeft) question.korean else wrongAnswer
        tvRight.text = if (isCorrectLeft) wrongAnswer else question.korean

        gateView.findViewById<MaterialCardView>(R.id.cardLeft).setCardBackgroundColor(
            if (isCorrectLeft) 0x802196F3.toInt() else 0x80F44336.toInt()
        )
        gateView.findViewById<MaterialCardView>(R.id.cardRight).setCardBackgroundColor(
            if (isCorrectLeft) 0x80F44336.toInt() else 0x802196F3.toInt()
        )

        gateView.scaleX = 0.1f
        gateView.scaleY = 0.1f
        gateView.alpha = 0f
        binding.gameContainer.addView(gateView)

        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.duration = 4000
        animator.interpolator = LinearInterpolator()

        var collisionChecked = false

        animator.addUpdateListener { animation ->
            if (_binding == null || !isAdded) {
                animation.cancel()
                return@addUpdateListener
            }

            val progress = animation.animatedValue as Float

            gateView.translationY = binding.battleRoot.height * progress

            val scale = 0.1f + (progress * 1.4f)
            gateView.scaleX = scale
            gateView.scaleY = scale
            gateView.alpha = (progress * 2f).coerceAtMost(1f)

            // 충돌 체크 (한 번만)
            if (!collisionChecked && progress > 0.80f && progress < 0.95f) {
                collisionChecked = true
                checkCollision(gateView, isCorrectLeft)
            }
        }

        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
            override fun onAnimationCancel(animation: Animator) {
                activeAnimators.remove(animator)
            }
            override fun onAnimationEnd(animation: Animator) {
                if (_binding != null && isAdded) {
                    binding.gameContainer.removeView(gateView)
                }
                activeAnimators.remove(animator)
            }
        })

        activeAnimators.add(animator)
        animator.start()
    }

    private fun checkCollision(gateView: View, isCorrectLeft: Boolean) {
        if (_binding == null) return

        val playerX = binding.playerCharacter.x + (binding.playerCharacter.width / 2f)
        val centerX = binding.battleRoot.width / 2f

        val isPlayerLeft = playerX < centerX
        val isCorrect = if (isPlayerLeft) isCorrectLeft else !isCorrectLeft

        if (isCorrect) {
            score += 10
        } else {
            hp -= 1
            currentQuestion?.let { updateWrongCount(it) }
        }

        updateHUD()

        if (hp <= 0) {
            gameOver()
        }
    }

    private fun gameOver() {
        isPlaying = false
        activeAnimators.forEach { it.cancel() }
        activeAnimators.clear()
        backgroundAnimator?.cancel()
        backgroundAnimator = null

        saveAndShowResults()
    }

    private fun saveAndShowResults() {
        if (!isAdded || _binding == null) return

        val prefs = requireContext().getSharedPreferences("WordQuestGame", Context.MODE_PRIVATE)
        val bestScore = prefs.getInt("best_score", 0)

        if (score > bestScore) {
            prefs.edit().putInt("best_score", score).apply()
        }

        binding.txtFinalScore.text = "Score: $score"
        binding.txtBestScore.text = "Best: ${if (score > bestScore) score else bestScore}"
        binding.layoutGameOver.isVisible = true
    }

    private fun updateWrongCount(word: Word) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val db = WordDatabase.getDatabase(requireContext())
                word.wrongCount += 1
                db.wordDao().updateWord(word)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun updateHUD() {
        if (_binding == null) return

        binding.txtScore.text = "SCORE: $score"
        binding.hpBar.progress = hp
    }

    override fun onPause() {
        super.onPause()
        if (isPlaying) {
            // 일시 정지 처리
            isPlaying = false
            activeAnimators.forEach {
                if (it.isRunning) {
                    it.pause()
                }
            }
            backgroundAnimator?.let {
                if (it.isRunning) {
                    it.pause()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        isPlaying = false

        // 모든 애니메이션 정리
        activeAnimators.forEach { it.cancel() }
        activeAnimators.clear()

        backgroundAnimator?.cancel()
        backgroundAnimator = null

        _binding = null
    }
}