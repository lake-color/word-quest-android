package com.example.finalprojectapp.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
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
        binding.txtStudyTitle.text = "Stage $stageNum"

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
                updateUI()
            } else {
                binding.txtWord.text = "No words found\nin Stage $stageNum"
                binding.layoutButtons.visibility = View.GONE
            }
        }
    }

    private fun updateUI() {
        val currentWord = wordList[currentIndex]
        
        // 영어 단어는 크게 상단에 유지
        binding.txtWord.text = currentWord.english
        
        // 뜻(한국어)은 클릭 시에만 보이거나 혹은 항상 보이게 설정 가능
        // 현재는 클릭 시 토글되는 로직에 맞춰 뜻의 가시성을 조절하는 방식으로 변경 제안
        binding.txtMean.text = currentWord.korean
        binding.txtMean.visibility = if (isShowingEnglish) View.INVISIBLE else View.VISIBLE
        
        // 버튼 활성화 상태 조절
        binding.btnPrev.isEnabled = currentIndex > 0
        binding.btnNext.isEnabled = currentIndex < wordList.size - 1
    }
}