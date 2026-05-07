# 🎮 Word Quest - 프로젝트 진행 현황 (Final Project)

## 📅 2026-05-07-1 업데이트 로그

### 🛠 현재까지 완료된 작업 (Status: ✅ 네비게이션 표준화 및 Review 기능 실전 투입 준비 완료)
- **UI 구조 현대화 (Material TabLayout 도입):**
    - `activity_home.xml`: 기존의 커스텀 버튼 기반 하단 바를 `com.google.android.material.tabs.TabLayout`으로 교체.
    - 테마 연동: 나무 질감 배경(`bg_bottom_nav_wood`)과 조화되도록 인디케이터 및 텍스트 컬러를 최적화하여 시각적 완성도 향상.
- **네비게이션 로직 고도화:**
    - `HomeActivity.kt`: `TabLayout.OnTabSelectedListener`를 적용하여 탭 전환에 따른 프래그먼트(`Learn`, `Review`, `Battle`) 교체 로직을 표준 방식으로 변경.
- **Review 기능 실질적 연결:**
    - `ReviewFragment.kt`: 임시 레이아웃(`fragment_simple`)을 제거하고 실제 오답 리스트 레이아웃(`fragment_review`)으로 교체.
    - 데이터 바인딩: `ReviewViewModel`과 `ReviewAdapter`를 연동하여 DB에 저장된 오답 데이터를 실시간으로 리스트에 출력하도록 구현.
    - 예외 처리: 오답 데이터 유무에 따라 "완벽해요!" 안내 문구가 노출되도록 동적 UI 제어 로직 추가.
- **문서 관리 및 가이드:**
    - 프로젝트 내 주요 파일(Activity, Fragment, Layout 등)의 역할을 재정의하고 사용자에게 가이드 제공.

### 🚀 향후 작업 계획 (Next Steps)
1. **Battle(미니게임) 설계:** 양갈래 문을 통과하는 '워드 러너' 퀴즈 게임 프로토타입 개발.
2. **Review 항목 상호작용:** 오답 리스트에서 특정 단어를 클릭했을 때 상세 정보를 보거나 학습 상태를 변경하는 기능 추가.

---

## 📅 2026-05-06-1 업데이트 로그

### 🛠 현재까지 완료된 작업 (Status: 🔵 협업 규칙 정립 및 Review 기능 레이아웃 완성)
- **협업 및 관리 시스템 구축 (AGENTS.md & README.md & CONTEXT.md):**
    - **AGENTS.md**: `!로그` 명령어를 통한 세션 관리 규칙 확정 및 프로젝트 관리 문서(AGENTS, CONTEXT, README) 변경사항을 로그에 반드시 포함하도록 규칙 강화.
    - **README.md**: 프로젝트 개요, 핵심 기능(Learn/Review/Battle), 기술 스택 및 개발 규칙을 명시한 가이드 문서 생성.
    - **CONTEXT.md**: 프로젝트 진행 상황을 역순으로 기록하는 히스토리 관리 체계 도입 및 실제 파일 상태 동기화.
- **Review UI 및 리소스 설계:**
    - `fragment_review.xml` & `item_review.xml`: 오답 리스트 전용 UI 및 카드 아이템 레이아웃 설계 완료.
    - `bg_wrong_count.xml`: 오답 횟수 시각화(배지)를 위한 Drawable 제작.
    - `ReviewAdapter.kt`: ListAdapter를 활용한 효율적인 오답 리스트 어댑터 구현.
- **데이터 레이어 최적화:**
    - `WordDao.kt`: 실시간 데이터 관찰을 위한 `Flow` 반환 타입 적용 및 오답 쿼리(`getReviewWords`) 추가.
- **구조 표준화:**
    - 중복 파일 및 미사용 레이아웃 정리, 패키지(`ui`, `data`) 기반 폴더 구조 정립 완료.

### 🚀 향후 작업 계획 (Next Steps)
1. **Review 기능 최종 연결:** `ReviewViewModel` 생성 및 `ReviewFragment` 데이터 바인딩 완성.
2. **Battle(미니게임) 설계:** 양갈래 문을 통과하는 '워드 러너' 퀴즈 게임 프로토타입 개발.

---

## 📅 2025-05-05-4 업데이트 로그

### 🛠 현재까지 완료된 작업 (Status: ✅ 빌드 환경 최적화 및 에러 해결 완료)
- **KSP 및 Room 호환성 해결:**
    - Kotlin 2.x 및 KSP2 엔진 환경에서 발생하던 `unexpected jvm signature V` 에러 해결을 위해 Room 버전을 `2.7.0-alpha13`으로 업그레이드함.
    - `gradle.properties`에 `android.disallowKotlinSourceSets=false` 설정을 추가하여 AGP 9.x 환경에서의 소스 세트 충돌 오류 해결.
- **빌드 안정화:** 의존성 및 플러그인 설정 변경 후 Gradle Sync 및 빌드 무결성 재검증 완료.

### 🚀 향후 작업 계획 (Next Steps)
1. **미니게임(Battle) 프로토타입:** 양갈래 문을 통과하며 정답을 맞히는 '워드 러너' 기본 UI 및 로직 설계.
2. **복습(Review) 기능:** 오답 기록(`wrongCount`)을 기반으로 틀린 단어들만 모아보는 리스트 구현.

---

## 📅 2025-05-05-3 업데이트 로그

### 🛠 현재까지 완료된 작업 (Status: 🔵 핵심 학습 로직 및 구조 표준화 완료)
- **프로젝트 구조 표준화 (Industry Standard):**
    - **패키지 분리:** `ui`, `ui.adapter`, `data` 패키지로 역할을 명확히 구분하여 재배치.
    - **리소스 명명 규칙:** Drawable 리소스를 접두어(`bg_`, `sl_`, `img_`) 기반으로 전면 재정리.
- **학습 시스템(Learn) 고도화:**
    - **스테이지 맵 구현:** `LearnFragment`에서 지그재그 형태의 스테이지 선택 맵(RecyclerView) 구현 완료.
    - **StudyActivity 연동:** 스테이지 클릭 시 해당 번호의 학습 화면으로 이동하는 인텐트 로직 연결.
- **데이터베이스 기능 확장:**
    - **스테이지 필드 추가:** `Word` 엔티티에 스테이지 구분 필드 추가 및 DAO 쿼리 업데이트.
    - **시드 데이터 주입:** 앱 최초 실행 시 스테이지 1, 2에 해당하는 샘플 단어 10개를 자동 삽입하도록 `WordDatabase` 개선.
- **카드 학습 UI 구현:**
    - `StudyActivity`에서 단어 카드를 클릭하여 영어/한국어를 토글(뒤집기)하는 기능 및 이전/다음 단어 탐색 로직 완성.

### 🚀 향후 작업 계획 (Next Steps)
1. **미니게임(Battle) 프로토타입:** 양갈래 문을 통과하며 정답을 맞히는 '워드 러너' 기본 UI 및 로직 설계.
2. **복습(Review) 기능:** 오답 기록(`wrongCount`)을 기반으로 틀린 단어들만 모아보는 리스트 구현.

---

## 📅 2025-05-05-2 업데이트 로그

### 🛠 현재까지 완료된 작업 (Status: 🟢 협업 규칙 및 UI 기틀 마련)
- **AI 협업 규칙 최적화:** `AGENTS.md`에 세션 및 맥락 관리(Incremental Logging) 규칙을 최종 확정함. 
    - `!로그` 명령어를 통한 자동 업데이트 및 날짜별 순번(`-번호`) 기록 방식 도입.
- **UI 리소스 최종 점검:** `HomeActivity`의 상/하단 바와 나무 테마 Drawable 리소스의 정합성 확인 완료.
- **프로젝트 무결성 확인:** Room DB 관련 컴파일 에러 및 경고 사항 분석 완료 (실사용 시 해결될 Unused Warning 위주).

### 🚀 향후 작업 계획 (Next Steps)
1. **DB 시드 데이터:** 앱 기동 시 `Room` DB에 기초 단어 데이터를 주입하는 초기화 로직 구현.
2. **UI 바인딩:** `LearnFragment`와 `WordDao`를 연결하여 실제 데이터를 화면에 렌더링.

---

## 📅 2025-05-05-1 업데이트 로그

### 🛠 현재까지 완료된 작업 (Status: 🟢 UI/DB 기초 공사 완료)
- **아키텍처 최적화:** 졸업과제 규모에 맞게 프로젝트를 [Review / Learn / Battle] 3개 핵심 화면으로 단순화하고, `Equip` 관련 파일 및 로직을 모두 제거함.
- **데이터베이스 구축:** 
    - `Word` 엔티티(id, english, korean, wrongCount, isMemorized) 및 DAO, Database 클래스 구현 완료.
    - Room 2.6.1 및 KSP 플러그인 설정(`build.gradle.kts`) 및 빌드 안정화 완료.
    - 잘못 임포트된 `privacysandbox` 참조 제거 및 데이터 레이어 버그 수정.
- **UI 및 리소스 고도화:**
    - `HomeActivity`: 상단 바(닉네임, 설정 버튼)와 하단 네비게이션(3버튼) 레이아웃 완성.
    - 테마 적용: 나무 질감의 배경(`bg_top_bar_wood`, `bg_bottom_nav_wood`) 및 클릭 피드백을 위한 `btn_nav_selector` 제작 및 적용.
    - 텍스트 컬러: 전체 테마에 어울리는 다크 브라운(`#5A341A`)으로 가독성 개선.
- **AI 협업 규칙 수립:** `AGENTS.md`를 수정하여 세션 간 맥락 보존을 위한 `!update_context` 프로세스 확립.

### 🚀 향후 작업 계획 (Next Steps)
1. **샘플 데이터 삽입:** 앱 최초 실행 시 기초 영단어(20~30개)를 DB에 자동 생성하는 로직 구현.
2. **학습(Learn) 리스트:** `LearnFragment`에서 DB 단어들을 `RecyclerView`로 시각화.
3. **미니게임(Battle) 로직:** 영단어 퀴즈를 풀며 정답 문을 통과하는 '워드 러너' 프로토타입 개발.

---

## 📌 프로젝트 개요
- **정체성:** 영단어 학습과 런게임(선택형)이 결합된 하이퍼 캐주얼 앱
- **핵심 목표:** 학습(Learn) -> 게임(Battle/Game) -> 복습(Review)의 선순환 구조

## 💡 다음 세션 가이드 (AI 전달용)
"현재 Word Quest 프로젝트의 하단 네비게이션이 TabLayout으로 개편되었고, Review(오답 노트) 기능이 DB와 실시간으로 연동되어 작동합니다. 다음 단계는 **Battle(미니게임)의 구체적인 UI 설계와 게임 로직 프로토타입 개발**을 시작할 차례입니다."
