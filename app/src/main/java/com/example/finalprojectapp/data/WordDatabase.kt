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
                ).addCallback(DatabaseCallback(context.applicationContext))
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(private val context: Context) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                CoroutineScope(Dispatchers.IO).launch {
                    val samples = mutableListOf<Word>()
                    
                    val wordsPerDay = 20
                    val totalDays = 20
                    
                    val commonNouns = listOf(
                        "Time", "Year", "People", "Way", "Day", "Man", "Thing", "Woman", "Life", "Child",
                        "World", "School", "State", "Family", "Student", "Group", "Country", "Problem", "Hand", "Part",
                        "Place", "Case", "Week", "Company", "System", "Program", "Question", "Work", "Government", "Number",
                        "Night", "Mr", "Point", "Home", "Water", "Room", "Mother", "Area", "Money", "Story"
                    )
                    
                    val commonVerbs = listOf(
                        "Be", "Have", "Do", "Say", "Get", "Make", "Go", "Know", "Take", "See",
                        "Come", "Think", "Look", "Want", "Give", "Use", "Find", "Tell", "Ask", "Work",
                        "Seem", "Feel", "Try", "Leave", "Call", "Keep", "Let", "Begin", "Help", "Talk",
                        "Turn", "Start", "Might", "Show", "Hear", "Play", "Run", "Move", "Like", "Live"
                    )

                    val commonAdjectives = listOf(
                        "Good", "New", "First", "Last", "Long", "Great", "Little", "Own", "Other", "Old",
                        "Right", "Big", "High", "Different", "Small", "Large", "Next", "Early", "Young", "Important",
                        "Few", "Public", "Bad", "Same", "Able", "Best", "Better", "Economic", "Strong", "Possible",
                        "Whole", "Free", "Military", "True", "Federal", "International", "Full", "Special", "Easy", "Clear"
                    )

                    val allSourceWords = (commonNouns + commonVerbs + commonAdjectives).distinct().shuffled()
                    
                    var wordIdx = 0
                    for (day in 1..totalDays) {
                        for (i in 1..wordsPerDay) {
                            val english = if (wordIdx < allSourceWords.size) {
                                allSourceWords[wordIdx]
                            } else {
                                "Word ${day}-${i}"
                            }
                            samples.add(Word(english = english, korean = "뜻 ${day}-${i}", stage = day))
                            wordIdx++
                        }
                    }

                    getDatabase(context).wordDao().insertWords(samples)
                }
            }
        }
    }
}
