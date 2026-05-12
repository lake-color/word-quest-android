package com.example.finalprojectapp.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.finalprojectapp.data.Word
import com.example.finalprojectapp.data.WordDatabase
import com.example.finalprojectapp.databinding.FragmentBattleBinding
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

    // Player side: 0 for Left, 1 for Right, -1 for Center
    private var playerSide = -1 

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBattleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadDataAndStart()
        setupControls()
    }

    private fun loadDataAndStart() {
        lifecycleScope.launch {
            val db = WordDatabase.getDatabase(requireContext())
            // Use the new suspend function that returns List
            allWords = withContext(Dispatchers.IO) { db.wordDao().getAllWordsList() }
            if (allWords.isNotEmpty()) {
                startGame()
            } else {
                binding.txtCurrentWord.text = "No words available. Study first!"
            }
        }
    }

    private fun setupControls() {
        binding.clickLeft.setOnClickListener { movePlayer(0) }
        binding.clickRight.setOnClickListener { movePlayer(1) }
    }

    private fun movePlayer(side: Int) {
        if (!isPlaying) return
        playerSide = side
        val params = binding.playerCharacter.layoutParams as ConstraintLayout.LayoutParams
        // Move character horizontally
        params.horizontalBias = if (side == 0) 0.1f else 0.9f
        binding.playerCharacter.layoutParams = params
    }

    private fun startGame() {
        isPlaying = true
        score = 0
        hp = 3
        updateHUD()
        spawnQuestion()
    }

    private fun spawnQuestion() {
        if (hp <= 0 || !isPlaying) return

        currentQuestion = allWords.random()
        
        // Find a random wrong answer
        val wrongAnswer = allWords.filter { it.id != currentQuestion?.id }.let {
            if (it.isNotEmpty()) it.random().korean else "???"
        }
        
        binding.txtCurrentWord.text = currentQuestion?.english
        
        // Randomly place correct answer on left or right
        val isCorrectLeft = Random.nextBoolean()
        if (isCorrectLeft) {
            binding.txtGateLeft.text = currentQuestion?.korean
            binding.txtGateRight.text = wrongAnswer
            binding.gateLeft.tag = true // Tag as correct
            binding.gateRight.tag = false
        } else {
            binding.txtGateLeft.text = wrongAnswer
            binding.txtGateRight.text = currentQuestion?.korean
            binding.gateLeft.tag = false
            binding.gateRight.tag = true // Tag as correct
        }

        startGateAnimation()
    }

    private fun startGateAnimation() {
        // Move gates from top to bottom (verticalBias 0.3 to 1.0)
        val animator = ObjectAnimator.ofFloat(0.3f, 1.0f)
        animator.duration = 3000
        animator.addUpdateListener { animation ->
            val value = animation.animatedValue as Float
            val leftParams = binding.gateLeft.layoutParams as ConstraintLayout.LayoutParams
            val rightParams = binding.gateRight.layoutParams as ConstraintLayout.LayoutParams
            leftParams.verticalBias = value
            rightParams.verticalBias = value
            binding.gateLeft.layoutParams = leftParams
            binding.gateRight.layoutParams = rightParams
        }

        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                if (isPlaying) checkSelection()
            }
        })
        animator.start()
    }

    private fun checkSelection() {
        val isCorrect = when (playerSide) {
            0 -> binding.gateLeft.tag as Boolean
            1 -> binding.gateRight.tag as Boolean
            else -> false // Center or no move = wrong
        }
        applyResult(isCorrect)
    }

    private fun applyResult(isCorrect: Boolean) {
        if (isCorrect) {
            score += 10
        } else {
            hp -= 1
            // Optional: Update wrongCount in DB here
            currentQuestion?.let { updateWrongCount(it) }
        }
        updateHUD()
        
        if (hp > 0) {
            // Reset player to center and spawn next
            resetPlayer()
            spawnQuestion()
        } else {
            isPlaying = false
            binding.txtCurrentWord.text = "GAME OVER"
        }
    }

    private fun resetPlayer() {
        playerSide = -1
        val params = binding.playerCharacter.layoutParams as ConstraintLayout.LayoutParams
        params.horizontalBias = 0.5f
        binding.playerCharacter.layoutParams = params
    }

    private fun updateWrongCount(word: Word) {
        lifecycleScope.launch(Dispatchers.IO) {
            val db = WordDatabase.getDatabase(requireContext())
            word.wrongCount += 1
            db.wordDao().updateWord(word)
        }
    }

    private fun updateHUD() {
        binding.txtScore.text = "Score: $score"
        binding.txtHP.text = "HP: $hp"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        isPlaying = false
        _binding = null
    }
}