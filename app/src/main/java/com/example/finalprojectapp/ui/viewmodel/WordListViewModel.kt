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

class WordListViewModel(application: Application) : AndroidViewModel(application) {
    private val db = WordDatabase.getDatabase(application)
    
    private val _words = MutableLiveData<List<Word>>()
    val words: LiveData<List<Word>> = _words

    fun loadWords(day: Int) {
        viewModelScope.launch {
            val wordList = withContext(Dispatchers.IO) {
                db.wordDao().getWordsByStage(day)
            }
            _words.postValue(wordList)
        }
    }

    fun toggleMemorized(word: Word) {
        viewModelScope.launch(Dispatchers.IO) {
            val updatedWord = word.copy(isMemorized = !word.isMemorized)
            db.wordDao().updateWord(updatedWord)
            
            // Refresh list
            val day = word.stage
            val wordList = db.wordDao().getWordsByStage(day)
            _words.postValue(wordList)
        }
    }
}
