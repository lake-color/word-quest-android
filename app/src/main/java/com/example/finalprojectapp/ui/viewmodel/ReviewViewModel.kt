package com.example.finalprojectapp.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import com.example.finalprojectapp.data.WordDatabase

class ReviewViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = WordDatabase.getDatabase(application).wordDao()
    
    // Flow를 LiveData로 변환하여 UI에서 관찰 가능하게 함
    val reviewWords = dao.getReviewWords().asLiveData()
}
