package com.example.finalprojectapp.ui.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.finalprojectapp.data.Word
import com.example.finalprojectapp.data.WordDatabase

class WordbookViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = WordDatabase.getDatabase(application).wordDao()

    private val _stageRange = MutableLiveData<IntRange>(1..5)
    
    val words: LiveData<List<Word>> = _stageRange.switchMap { range ->
        dao.getWordsByStageRange(range.first, range.last).asLiveData()
    }

    fun setStageRange(start: Int, end: Int) {
        _stageRange.value = start..end
    }
}