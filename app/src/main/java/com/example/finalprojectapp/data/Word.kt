package com.example.finalprojectapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "word_table")
data class Word(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val english: String,
    val korean: String,
    val stage: Int = 1, // 스테이지 번호 추가
    var wrongCount: Int = 0, // 틀린 횟수 (Review에서 사용)
    var isMemorized: Boolean = false
)