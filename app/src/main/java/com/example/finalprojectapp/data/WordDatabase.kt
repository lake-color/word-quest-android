package com.example.finalprojectapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Word::class], version = 2, exportSchema = false)
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
                .fallbackToDestructiveMigration(true)
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(private val context: Context) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                seedDatabase()
            }

            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                CoroutineScope(Dispatchers.IO).launch {
                    val dao = getDatabase(context).wordDao()
                    // 데이터가 완전히 비어있을 때만 초기화 (사용자 데이터 보호)
                    if (dao.getAllWordsList().isEmpty()) {
                        seedDatabase()
                    }
                }
            }

            private fun seedDatabase() {
                CoroutineScope(Dispatchers.IO).launch {
                    val db = getDatabase(context)
                    val dao = db.wordDao()
                    
                    // 기존 데이터 삭제
                    dao.deleteAllWords()

                    val samples = mutableListOf<Word>()
                    
                    // 20 Days * 20 Words = 400 Words (중복 제거 및 20개 기준 정립)
                    val daysData = listOf(
                        // Day 1
                        listOf("Time" to "시간", "Year" to "년", "People" to "사람들", "Way" to "방법", "Day" to "날", "Man" to "남자", "Thing" to "물건", "Woman" to "여자", "Life" to "삶", "Child" to "아이", "World" to "세계", "School" to "학교", "State" to "상태/주", "Family" to "가족", "Student" to "학생", "Group" to "집단", "Country" to "나라", "Problem" to "문제", "Hand" to "손", "Part" to "부분"),
                        // Day 2
                        listOf("Place" to "장소", "Case" to "사건/경우", "Week" to "주", "Company" to "회사", "System" to "체계", "Program" to "프로그램", "Question" to "질문", "Work" to "일", "Government" to "정부", "Number" to "숫자", "Night" to "밤", "Point" to "지점/점수", "Home" to "집", "Water" to "물", "Room" to "방", "Mother" to "어머니", "Area" to "지역", "Money" to "돈", "Story" to "이야기", "Fact" to "사실"),
                        // Day 3
                        listOf("Month" to "달", "Lot" to "많음/운명", "Right" to "권리/오른쪽", "Study" to "공부", "Book" to "책", "Eye" to "눈", "Job" to "직업", "Word" to "단어", "Business" to "사업", "Issue" to "쟁점/발행", "Side" to "측면", "Kind" to "종류", "Head" to "머리", "House" to "집", "Service" to "서비스", "Friend" to "친구", "Father" to "아버지", "Power" to "힘/권력", "Hour" to "시간(60분)", "Game" to "게임"),
                        // Day 4
                        listOf("Line" to "선", "End" to "끝", "Member" to "회원", "Law" to "법", "Car" to "자동차", "City" to "도시", "Community" to "공동체", "Name" to "이름", "President" to "대통령", "Team" to "팀", "Minute" to "분", "Idea" to "아이디어", "Kid" to "어린이", "Body" to "몸", "Information" to "정보", "Back" to "등/뒤", "Parent" to "부모", "Face" to "얼굴", "Others" to "타인들", "Level" to "수준"),
                        // Day 5
                        listOf("Office" to "사무실", "Door" to "문", "Health" to "건강", "Person" to "사람", "Art" to "예술", "War" to "전쟁", "History" to "역사", "Party" to "파티/정당", "Result" to "결과", "Change" to "변화", "Morning" to "아침", "Reason" to "이유", "Research" to "연구", "Girl" to "소녀", "Guy" to "남자/녀석", "Moment" to "순간", "Air" to "공기", "Teacher" to "선생님", "Force" to "힘", "Education" to "교육"),
                        // Day 6
                        listOf("Have" to "가지다", "Do" to "하다", "Say" to "말하다", "Get" to "얻다", "Make" to "만들다", "Go" to "가다", "Know" to "알다", "Take" to "가져가다", "See" to "보다", "Come" to "오다", "Think" to "생각하다", "Look" to "보다", "Want" to "원하다", "Give" to "주다", "Use" to "사용하다", "Find" to "찾다", "Tell" to "말하다", "Ask" to "묻다", "Work" to "일하다", "Seem" to "~처럼 보이다"),
                        // Day 7
                        listOf("Feel" to "느끼다", "Try" to "시도하다", "Leave" to "떠나다", "Call" to "부르다", "Keep" to "유지하다", "Help" to "돕다", "Talk" to "이야기하다", "Turn" to "돌리다", "Start" to "시작하다", "Show" to "보여주다", "Hear" to "듣다", "Play" to "놀다", "Run" to "달리다", "Move" to "움직이다", "Live" to "살다", "Believe" to "믿다", "Bring" to "가져오다", "Happen" to "발생하다", "Write" to "쓰다", "Sit" to "앉다"),
                        // Day 8
                        listOf("Stand" to "서다", "Lose" to "잃다", "Pay" to "지불하다", "Meet" to "만나다", "Include" to "포함하다", "Continue" to "계속하다", "Set" to "설정하다", "Learn" to "배우다", "Watch" to "지켜보다", "Follow" to "따르다", "Stop" to "멈추다", "Create" to "창조하다", "Speak" to "말하다", "Read" to "읽다", "Allow" to "허용하다", "Add" to "더하다", "Spend" to "소비하다", "Grow" to "성장하다", "Open" to "열다", "Walk" to "걷다"),
                        // Day 9
                        listOf("Good" to "좋은", "New" to "새로운", "First" to "첫 번째의", "Last" to "마지막의", "Long" to "긴", "Great" to "위대한", "Little" to "작은", "Own" to "자신의", "Other" to "다른", "Old" to "오래된", "High" to "높은", "Next" to "다음의", "Early" to "이른", "Young" to "젊은", "Important" to "중요한", "Few" to "거의 없는", "Public" to "공공의", "Bad" to "나쁜", "Same" to "같은", "Able" to "할 수 있는"),
                        // Day 10
                        listOf("Big" to "큰", "Small" to "작은", "Large" to "거대한", "Better" to "더 나은", "Best" to "최고의", "Possible" to "가능한", "True" to "진실한", "Full" to "가득 찬", "Special" to "특별한", "Easy" to "쉬운", "Clear" to "명확한", "Recent" to "최근의", "Certain" to "확실한", "Personal" to "개인적인", "Short" to "짧은", "Real" to "진짜의", "Single" to "단일의", "Free" to "자유로운", "Strong" to "강한", "Happy" to "행복한"),
                        // Day 11
                        listOf("Policy" to "정책", "Market" to "시장", "Court" to "법정", "Effect" to "효과", "Experience" to "경험", "Social" to "사회적인", "Economic" to "경제적인", "Political" to "정치적인", "Culture" to "문화", "Society" to "사회", "Value" to "가치", "Tax" to "세금", "Price" to "가격", "Investment" to "투자", "Consumer" to "소비자", "Growth" to "성장", "Agreement" to "동의/협정", "Trade" to "무역", "Profit" to "이익", "Loss" to "손실"),
                        // Day 12
                        listOf("Interest" to "흥미/이자", "Budget" to "예산", "Debt" to "빚", "Revenue" to "수입", "Stock" to "재고/주식", "Bond" to "채권/유대", "Asset" to "자산", "Credit" to "신용", "Bank" to "은행", "Loan" to "대출", "Cash" to "현금", "Account" to "계좌/설명", "Bill" to "지폐/계산서", "Payment" to "지불", "Strategy" to "전략", "Goal" to "목표", "Mission" to "임무", "Vision" to "비전", "Structure" to "구조", "Function" to "기능"),
                        // Day 13
                        listOf("Process" to "과정", "Access" to "접근", "Network" to "네트워크", "Device" to "장치", "Security" to "보안", "Privacy" to "사생활", "Data" to "데이터", "Content" to "내용/콘텐츠", "Platform" to "플랫폼", "Media" to "미디어", "Digital" to "디지털의", "Global" to "세계적인", "Local" to "지역의", "Standard" to "표준", "Quality" to "품질", "Resource" to "자원", "Energy" to "에너지", "Environment" to "환경", "Impact" to "영향/충격", "Benefit" to "혜택/이익"),
                        // Day 14
                        listOf("Risk" to "위험", "Safety" to "안전", "Crisis" to "위기", "Warning" to "경고", "Damage" to "피해", "Protection" to "보호", "Support" to "지원", "Control" to "통제", "Management" to "경영/관리", "Leadership" to "리더십", "Success" to "성공", "Failure" to "실패", "Challenge" to "도전", "Opportunity" to "기회", "Advantage" to "이점", "Progress" to "진보/진전", "Innovation" to "혁신", "Solution" to "해결책", "Answer" to "답변", "Option" to "선택권"),
                        // Day 15
                        listOf("Selection" to "선택", "Variety" to "다양성", "Diversity" to "다양성", "Unity" to "통일/단결", "Union" to "조합/연합", "Agreement" to "합의", "Conflict" to "갈등", "Peace" to "평화", "Justice" to "정의", "Liberty" to "자유", "Rights" to "권리", "Freedom" to "자유", "Responsibility" to "책임", "Duty" to "의무", "Task" to "과업", "Project" to "프로젝트", "Plan" to "계획", "Schedule" to "일정", "Event" to "행사", "Meeting" to "회의"),
                        // Day 16
                        listOf("Carry out" to "수행하다", "Look for" to "찾다", "Depend on" to "의존하다", "Focus on" to "집중하다", "Result in" to "결과를 낳다", "Set up" to "설치하다/설정하다", "Point out" to "지적하다", "Take part in" to "참여하다", "Bring up" to "꺼내다/기르다", "Hold on" to "기다리다/견디다", "Put off" to "미루다", "Call off" to "취소하다", "Get along" to "잘 지내다", "Look after" to "돌보다", "Make up" to "구성하다/화해하다", "Run out of" to "다 써버리다", "Keep up with" to "따라잡다", "Give up" to "포기하다", "Carry on" to "계속하다", "Turn down" to "거절하다"),
                        // Day 17
                        listOf("Break through" to "돌파하다", "Catch up" to "따라잡다", "Deal with" to "다루다/처리하다", "End up" to "결국 ~하게 되다", "Figure out" to "알아내다", "Get over" to "극복하다", "Go through" to "겪다", "Look forward to" to "기대하다", "Make sense" to "이해가 되다", "Pay off" to "성과를 거두다", "Show off" to "뽐내다", "Take over" to "인계받다", "Turn out" to "판명되다", "Work out" to "운동하다/해결하다", "Back up" to "뒷받침하다", "Check in" to "체크인하다", "Fall apart" to "무너지다", "Get away" to "탈출하다", "Give in" to "굴복하다", "Keep on" to "계속하다"),
                        // Day 18
                        listOf("In order to" to "~하기 위해서", "As a result" to "결과적으로", "By the way" to "그런데", "On the other hand" to "반면에", "In addition" to "게다가", "For example" to "예를 들어", "In fact" to "사실상", "At the same time" to "동시에", "According to" to "~에 따르면", "In terms of" to "~라는 면에서", "With respect to" to "~에 관하여", "In general" to "일반적으로", "On behalf of" to "~을 대신하여", "Up to date" to "최신의", "Out of order" to "고장 난", "In case of" to "~의 경우에", "In advance" to "미리", "So far" to "지금까지", "Step by step" to "차근차근", "Face to face" to "마주보고"),
                        // Day 19
                        listOf("Phenomenon" to "현상", "Perspective" to "관점", "Assumption" to "가정", "Hypothesis" to "가설", "Evidence" to "증거", "Analysis" to "분석", "Conclusion" to "결론", "Concept" to "개념", "Theory" to "이론", "Principle" to "원칙", "Significant" to "중요한", "Essential" to "필수적인", "Objective" to "객관적인/목표", "Subjective" to "주관적인", "Efficient" to "효율적인", "Effective" to "효과적인", "Stable" to "안정적인", "Flexible" to "유연한", "Sustainable" to "지속 가능한", "Reliable" to "신뢰할 수 있는"),
                        // Day 20
                        listOf("Artificial Intelligence" to "인공지능", "Climate Change" to "기후 변화", "Social Media" to "소셜 미디어", "Virtual Reality" to "가상 현실", "Blockchain" to "블록체인", "Cybersecurity" to "사이버 보안", "E-commerce" to "전자상거래", "Remote Work" to "원격 근무", "Renewable Energy" to "재생 에너지", "Globalization" to "세계화", "Innovation" to "혁신", "Infrastructure" to "기반 시설", "Collaboration" to "협업", "Productivity" to "생산성", "Transformation" to "변화/변형", "Integration" to "통합", "Paradigm" to "패러다임", "Aesthetics" to "미학", "Architecture" to "건축/구조", "Philosophy" to "철학")
                    )

                    for ((dayIdx, dayWords) in daysData.withIndex()) {
                        val day = dayIdx + 1
                        for (pair in dayWords) {
                            samples.add(Word(english = pair.first, korean = pair.second, stage = day))
                        }
                    }

                    dao.insertWords(samples)
                }
            }
        }
    }
}
