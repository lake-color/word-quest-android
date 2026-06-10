package com.example.finalprojectapp.ui.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.finalprojectapp.data.Word
import com.example.finalprojectapp.data.WordDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val db = WordDatabase.getDatabase(application)
    private val dao = db.wordDao()

    // --- 공통 데이터 ---
    val allWords: LiveData<List<Word>> = dao.getAllWords().asLiveData()

    // --- 학습(Study) & 리스트 관련 ---
    private val _currentWords = MutableLiveData<List<Word>>()
    val currentWords: LiveData<List<Word>> = _currentWords

    val currentIndex = MutableLiveData(0)
    val isShowingEnglish = MutableLiveData(true)

    fun loadWordsByDay(day: Int) {
        viewModelScope.launch {
            val list = withContext(Dispatchers.IO) { dao.getWordsByStage(day) }
            _currentWords.postValue(list)
            currentIndex.postValue(0)
            isShowingEnglish.postValue(true)
        }
    }

    fun toggleMemorized(word: Word) {
        viewModelScope.launch(Dispatchers.IO) {
            val updated = word.copy(isMemorized = !word.isMemorized)
            dao.updateWord(updated)
            // 현재 리스트 새로고침
            val list = dao.getWordsByStage(word.stage)
            _currentWords.postValue(list)
        }
    }

    // --- 게임(Game) 관련 ---
    val score = MutableLiveData(0)
    val hp = MutableLiveData(3)

    fun addScore(points: Int) { score.value = (score.value ?: 0) + points }
    fun decreaseHp() { hp.value = (hp.value ?: 0) - 1 }
    fun resetGame() {
        score.value = 0
        hp.value = 3
    }

    fun updateWrongCount(word: Word) {
        viewModelScope.launch(Dispatchers.IO) {
            // DB 수준에서 직접 1 증가 (원자적 연산)
            dao.incrementWrongCount(word.id)
        }
    }
}