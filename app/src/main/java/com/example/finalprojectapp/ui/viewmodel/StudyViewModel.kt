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

class StudyViewModel(application: Application) : AndroidViewModel(application) {
    private val db = WordDatabase.getDatabase(application)
    
    private val _words = MutableLiveData<List<Word>>()
    val words: LiveData<List<Word>> = _words

    private val _currentIndex = MutableLiveData(0)
    val currentIndex: LiveData<Int> = _currentIndex

    private val _isShowingEnglish = MutableLiveData(true)
    val isShowingEnglish: LiveData<Boolean> = _isShowingEnglish

    fun loadWords(day: Int) {
        viewModelScope.launch {
            val list = withContext(Dispatchers.IO) {
                db.wordDao().getWordsByStage(day)
            }
            _words.postValue(list)
        }
    }

    fun nextWord() {
        val current = _currentIndex.value ?: 0
        val size = _words.value?.size ?: 0
        if (current < size - 1) {
            _currentIndex.value = current + 1
            _isShowingEnglish.value = true
        }
    }

    fun prevWord() {
        val current = _currentIndex.value ?: 0
        if (current > 0) {
            _currentIndex.value = current - 1
            _isShowingEnglish.value = true
        }
    }

    fun toggleLanguage() {
        _isShowingEnglish.value = !(_isShowingEnglish.value ?: true)
    }

    fun toggleMemorized() {
        val wordsList = _words.value ?: return
        val index = _currentIndex.value ?: 0
        if (index in wordsList.indices) {
            val word = wordsList[index]
            word.isMemorized = !word.isMemorized
            viewModelScope.launch(Dispatchers.IO) {
                db.wordDao().updateWord(word)
                _words.postValue(wordsList) // Notify observers
            }
        }
    }
}
