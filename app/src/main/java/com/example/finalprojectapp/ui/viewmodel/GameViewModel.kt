package com.example.finalprojectapp.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.finalprojectapp.data.Word
import com.example.finalprojectapp.data.WordDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GameViewModel(application: Application) : AndroidViewModel(application) {
    private val db = WordDatabase.getDatabase(application)
    
    private val _allWords = MutableLiveData<List<Word>>()
    val allWords: LiveData<List<Word>> = _allWords

    private val _score = MutableLiveData(0)
    val score: LiveData<Int> = _score

    private val _hp = MutableLiveData(3)
    val hp: LiveData<Int> = _hp

    init {
        loadAllWords()
    }

    private fun loadAllWords() {
        viewModelScope.launch {
            val words = withContext(Dispatchers.IO) {
                db.wordDao().getAllWordsList()
            }
            _allWords.postValue(words)
        }
    }

    fun addScore(points: Int) {
        _score.value = (_score.value ?: 0) + points
    }

    fun decreaseHp() {
        _hp.value = (_hp.value ?: 0) - 1
    }

    fun resetGame() {
        _score.value = 0
        _hp.value = 3
    }

    fun updateWrongCount(word: Word) {
        viewModelScope.launch(Dispatchers.IO) {
            word.wrongCount += 1
            db.wordDao().updateWord(word)
        }
    }
}
