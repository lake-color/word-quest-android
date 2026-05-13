package com.example.finalprojectapp.ui

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
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
            val db = WordDatabase.getDatabase(requireContext())
            allWords = withContext(Dispatchers.IO) { db.wordDao().getAllWordsList() }
            if (allWords.isNotEmpty()) {
                startCountdown()
            } else {
                binding.txtCurrentWord.text = "No words available."
            }
        }
    }

    private fun startCountdown() {
        binding.layoutCountdown.isVisible = true
        var count = 3
        
        val countdownRunnable = object : Runnable {
            override fun run() {
                if (_binding == null) return
                if (count > 0) {
                    binding.txtCountdown.text = count.toString()
                    binding.txtCountdown.scaleX = 0.5f
                    binding.txtCountdown.scaleY = 0.5f
                    binding.txtCountdown.animate().scaleX(1.2f).scaleY(1.2f).setDuration(800).start()
                    count--
                    binding.battleRoot.postDelayed(this, 1000)
                } else if (count == 0) {
                    binding.txtCountdown.text = "START!"
                    count--
                    binding.battleRoot.postDelayed(this, 800)
                } else {
                    binding.layoutCountdown.isVisible = false
                    startGame()
                }
            }
        }
        binding.battleRoot.post(countdownRunnable)
    }

    private fun resetGameState() {
        isPlaying = false
        score = 0
        hp = 3
        updateHUD()
        binding.txtCurrentWord.text = "READY?"
        binding.gameContainer.removeAllViews()
        activeAnimators.forEach { it.cancel() }
        activeAnimators.clear()
    }

    private fun startGame() {
        resetGameState()
        isPlaying = true
        spawnLoop()
    }

    private fun spawnLoop() {
        if (!isPlaying || hp <= 0) return
        
        spawnGates()
        spawnRoadLines()
        
        binding.battleRoot.postDelayed({
            if (isPlaying) spawnLoop()
        }, 2000)
    }

    private fun spawnRoadLines() {
        val line = View(requireContext())
        line.layoutParams = FrameLayout.LayoutParams(10, 60).apply {
            gravity = Gravity.CENTER_HORIZONTAL
        }
        line.setBackgroundColor(Color.WHITE)
        line.alpha = 0.5f
        binding.gameContainer.addView(line, 0) // 로드 라인은 게이트보다 뒤에 배치

        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.duration = 2000
        animator.interpolator = LinearInterpolator()
        animator.addUpdateListener { anim ->
            if (_binding == null) return@addUpdateListener
            val p = anim.animatedValue as Float
            line.translationY = binding.battleRoot.height * p
            line.scaleY = 1f + p * 2f
            line.alpha = 0.5f * (1f - p)
        }
        animator.addListener(object : android.animation.AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: android.animation.Animator) {
                if (_binding != null) {
                    binding.gameContainer.removeView(line)
                }
            }
        })
        animator.start()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupDragControl() {
        binding.battleRoot.setOnTouchListener { _, event ->
            if (!isPlaying) return@setOnTouchListener false
            
            when (event.action) {
                MotionEvent.ACTION_MOVE, MotionEvent.ACTION_DOWN -> {
                    // 터치 위치를 플레이어의 X 좌표로 반영
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
        if (!isPlaying || !isAdded) return

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
        
        // 게이트 디자인 최적화: 정답 쪽은 파란색 계열, 오답 쪽은 빨간색 계열 투명도 유지
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
        
        animator.addUpdateListener { animation ->
            if (_binding == null) return@addUpdateListener
            val progress = animation.animatedValue as Float
            
            gateView.translationY = binding.battleRoot.height * progress
            
            val scale = 0.1f + (progress * 1.4f)
            gateView.scaleX = scale
            gateView.scaleY = scale
            gateView.alpha = progress.coerceAtLeast(0.1f) * 2f

            if (progress > 0.85f && progress < 0.90f) {
                checkCollision(gateView, isCorrectLeft)
                animator.removeAllUpdateListeners() 
                animator.addUpdateListener { anim ->
                    if (_binding == null) return@addUpdateListener
                    val p = anim.animatedValue as Float
                    gateView.translationY = binding.battleRoot.height * p
                }
            }
        }

        animator.addListener(object : android.animation.AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: android.animation.Animator) {
                if (_binding != null) {
                    binding.gameContainer.removeView(gateView)
                }
                activeAnimators.remove(animator)
            }
        })

        activeAnimators.add(animator)
        animator.start()
    }

    private fun checkCollision(gateView: View, isCorrectLeft: Boolean) {
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
        saveAndShowResults()
    }

    private fun saveAndShowResults() {
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
            val db = WordDatabase.getDatabase(requireContext())
            word.wrongCount += 1
            db.wordDao().updateWord(word)
        }
    }

    private fun updateHUD() {
        binding.txtScore.text = "SCORE: $score"
        binding.hpBar.progress = hp
    }

    override fun onDestroyView() {
        super.onDestroyView()
        isPlaying = false
        activeAnimators.forEach { it.cancel() }
        activeAnimators.clear()
        _binding = null
    }
}