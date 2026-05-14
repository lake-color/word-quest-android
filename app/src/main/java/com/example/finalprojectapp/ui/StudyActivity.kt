package com.example.finalprojectapp.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.finalprojectapp.R
import com.example.finalprojectapp.data.Word
import com.example.finalprojectapp.data.WordDatabase
import com.example.finalprojectapp.databinding.ActivityStudyBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StudyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStudyBinding
    private var wordList = listOf<Word>()
    private var currentIndex = 0
    private var isShowingEnglish = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. 스테이지 번호 가져오기
        val stageNum = intent.getIntExtra("STAGE_NUM", 1)
        binding.txtStudyTitle.text = getString(R.string.stage_format, stageNum)

        // 2. DB에서 해당 스테이지 단어 불러오기
        loadWords(stageNum)

        // 3. 카드 클릭 시 단어 뒤집기 (영어 <-> 한국어)
        binding.cardWord.setOnClickListener {
            if (wordList.isNotEmpty()) {
                isShowingEnglish = !isShowingEnglish
                updateUI()
            }
        }

        // 4. 이전/다음 버튼 설정
        binding.btnPrev.setOnClickListener {
            if (currentIndex > 0) {
                currentIndex--
                isShowingEnglish = true
                updateUI()
            }
        }

        binding.btnNext.setOnClickListener {
            if (currentIndex < wordList.size - 1) {
                currentIndex++
                isShowingEnglish = true
                updateUI()
            }
        }

        // 5. 나가기 버튼
        binding.btnBack.setOnClickListener {
            finish()
        }

        // 6. 별(즐겨찾기/암기) 버튼 설정
        binding.btnStar.setOnClickListener {
            if (wordList.isNotEmpty()) {
                toggleMemorized()
            }
        }
    }

    private fun toggleMemorized() {
        val currentWord = wordList[currentIndex]
        currentWord.isMemorized = !currentWord.isMemorized
        
        lifecycleScope.launch(Dispatchers.IO) {
            WordDatabase.getDatabase(this@StudyActivity).wordDao().updateWord(currentWord)
            withContext(Dispatchers.Main) {
                updateStarIcon(currentWord.isMemorized)
            }
        }
    }

    private fun loadWords(stageNum: Int) {
        lifecycleScope.launch {
            val db = WordDatabase.getDatabase(this@StudyActivity)
            // 비동기로 데이터 조회
            val words = withContext(Dispatchers.IO) {
                db.wordDao().getWordsByStage(stageNum)
            }
            
            wordList = words
            if (wordList.isNotEmpty()) {
                currentIndex = 0
                isShowingEnglish = true
                
                // 프로그레스 바 초기화
                binding.progressStudy.max = wordList.size
                
                updateUI()
            } else {
                binding.txtWord.text = getString(R.string.no_words_found, stageNum)
                binding.layoutButtons.visibility = View.GONE
                binding.layoutProgress.visibility = View.GONE
                binding.btnStar.visibility = View.GONE
            }
        }
    }

    private fun updateUI() {
        val currentWord = wordList[currentIndex]
        
        // 영어 단어는 크게 상단에 유지
        binding.txtWord.text = currentWord.english
        
        // 뜻(한국어) 가시성 조절
        binding.txtMean.text = currentWord.korean
        binding.txtMean.visibility = if (isShowingEnglish) View.INVISIBLE else View.VISIBLE
        
        // 프로그레스 업데이트
        binding.txtProgress.text = getString(R.string.study_progress_format, currentIndex + 1, wordList.size)
        binding.progressStudy.progress = currentIndex + 1
        
        // 별 아이콘 업데이트
        updateStarIcon(currentWord.isMemorized)

        // 버튼 활성화 상태 조절
        binding.btnPrev.isEnabled = currentIndex > 0
        binding.btnNext.isEnabled = currentIndex < wordList.size - 1
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