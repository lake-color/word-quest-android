package com.example.finalprojectapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {
    @Query("SELECT * FROM word_table ORDER BY id ASC")
    fun getAllWords(): Flow<List<Word>>

    @Query("SELECT * FROM word_table WHERE wrongCount > 0 ORDER BY wrongCount DESC")
    fun getReviewWords(): Flow<List<Word>>

    @Query("SELECT * FROM word_table WHERE stage = :stageNum")
    suspend fun getWordsByStage(stageNum: Int): List<Word>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWords(words: List<Word>)

    @Update
    suspend fun updateWord(word: Word)
}