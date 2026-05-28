package com.example.finalprojectapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Word::class], version = 1, exportSchema = false)
abstract class WordDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao

    companion object {
        @Volatile
        private var INSTANCE: WordDatabase? = null

        fun getDatabase(context: Context): WordDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WordDatabase::class.java,
                    "word_database"
                )
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // 최초 생성 시 샘플 데이터 삽입
                        // 별도의 Scope에서 INSTANCE가 준비될 때까지 대기 후 주입
                        CoroutineScope(Dispatchers.IO).launch {
                            val samples = listOf(
                                Word(english = "Apple", korean = "사과", stage = 1),
                                Word(english = "Banana", korean = "바나나", stage = 1),
                                Word(english = "Grape", korean = "포도", stage = 1),
                                Word(english = "Watermelon", korean = "수박", stage = 1),
                                Word(english = "Strawberry", korean = "딸기", stage = 1),
                                Word(english = "Dog", korean = "개", stage = 2),
                                Word(english = "Cat", korean = "고양이", stage = 2),
                                Word(english = "Lion", korean = "사자", stage = 2),
                                Word(english = "Tiger", korean = "호랑이", stage = 2),
                                Word(english = "Elephant", korean = "코끼리", stage = 2),
                                Word(english = "Computer", korean = "컴퓨터", stage = 3),
                                Word(english = "Keyboard", korean = "키보드", stage = 3),
                                Word(english = "Mouse", korean = "마우스", stage = 3),
                                Word(english = "Monitor", korean = "모니터", stage = 3),
                                Word(english = "Internet", korean = "인터넷", stage = 3)
                            )
                            
                            // 인스턴스가 할당될 때까지 최대 1초간 대기 (보통 즉시 완료됨)
                            var retry = 0
                            while (INSTANCE == null && retry < 10) {
                                kotlinx.coroutines.delay(100)
                                retry++
                            }
                            INSTANCE?.wordDao()?.insertWords(samples)
                        }
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
