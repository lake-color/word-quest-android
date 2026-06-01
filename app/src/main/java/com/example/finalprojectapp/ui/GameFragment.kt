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
import com.example.finalprojectapp.databinding.FragmentGameBinding
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

class GameFragment : Fragment() {
    private var _binding: FragmentGameBinding? = null
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
        _binding = FragmentGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDragControl()
        setupButtons()
        loadDataAndPrepare()
    }

    private fun setupButtons() {
        binding.btnStartGame.setOnClickListener {
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

    private fun loadDataAndPrepare() {
        lifecycleScope.launch {
            try {
                val db = WordDatabase.getDatabase(requireContext())
                var words = withContext(Dispatchers.IO) { db.wordDao().getAllWordsList() }
                
                if (words.isEmpty()) {
                    kotlinx.coroutines.delay(1000)
                    words = withContext(Dispatchers.IO) { db.wordDao().getAllWordsList() }
                }

                allWords = words

                if (_binding != null) {
                    if (allWords.isEmpty()) {
                        binding.txtCurrentWord.text = "No words available."
                        binding.btnStartGame.isEnabled = false
                    } else {
                        binding.layoutStart.isVisible = true
                        binding.btnStartGame.isEnabled = true
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
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
                            .scaleX(1.1f)
                            .scaleY(1.1f)
                            .setDuration(700)
                            .start()
                        count--
                        binding.gameRoot.postDelayed(this, 1000)
                    }
                    count == 0 -> {
                        binding.txtCountdown.text = "START!"
                        // START 문구가 길어지므로 확실하게 작게 조정
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
        updateHUD()
        startBackgroundAnimation()
        spawnLoop()
    }

    private fun resetGameState() {
        if (_binding == null) return

        isPlaying = false
        score = 0
        hp = 3
        updateHUD()
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
        if (!isPlaying || hp <= 0 || !isAdded || _binding == null) return

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
        if (!isPlaying || !isAdded || _binding == null || allWords.isEmpty()) return

        val question = allWords.random()
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

        // 원근감 제거: 상단 맨 위에서 정상 크기로 시작
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

                // 플레이어 근처에서 판정
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

        if (isCorrect) { score += 10 } 
        else {
            hp -= 1
            currentQuestion?.let { q ->
                lifecycleScope.launch(Dispatchers.IO) {
                    try {
                        val db = WordDatabase.getDatabase(requireContext())
                        q.wrongCount += 1
                        db.wordDao().updateWord(q)
                    } catch (e: Exception) {}
                }
            }
        }
        updateHUD()
        if (hp <= 0) gameOver()
    }

    private fun gameOver() {
        isPlaying = false
        activeAnimators.forEach { it.cancel() }
        activeAnimators.clear()
        backgroundAnimator?.cancel()
        backgroundAnimator = null
        
        if (_binding != null) {
            val prefs = requireContext().getSharedPreferences("WordQuestGame", Context.MODE_PRIVATE)
            val best = prefs.getInt("best_score", 0)
            if (score > best) prefs.edit().putInt("best_score", score).apply()
            
            binding.txtFinalScore.text = "Score: $score"
            binding.txtBestScore.text = "Best: ${maxOf(score, best)}"
            binding.layoutGameOver.isVisible = true
        }
    }

    private fun updateHUD() {
        if (_binding == null) return
        binding.txtScore.text = "SCORE: $score"
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