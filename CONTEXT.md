# 🎮 Word Quest - 프로젝트 진행 현황 (Final Project)

## 📌 프로젝트 개요
- **정체성:** 영단어 학습과 런게임(선택형)이 결합된 하이퍼 캐주얼 앱
- **핵심 목표:** 학습(Learn) -> 게임(Battle/Game) -> 복습(Review)의 선순환 구조

## 🛠 현재까지 완료된 작업 (Status: 🟢 기초 공사 완료)

### 1. 아키텍처 및 데이터베이스
- **구조 최적화:** 졸업과제 규모에 맞춰 '장비/파밍' 시스템을 제거하고 [학습/복습/게임] 3단계로 슬림화.
- **Room DB 설정:** `Word` 엔티티, `WordDao`, `WordDatabase` 구현 완료.
    - 필드: `id`, `english`, `korean`, `wrongCount`, `isMemorized`
- **Dependency:** Room 2.6.1 및 KSP 플러그인 설정 및 빌드 안정화 완료.

### 2. UI 및 리소스
- **Main UI:** `HomeActivity` 내에 상단 바(닉네임/설정) 및 하단 네비게이션(3버튼) 레이아웃 완성.
- **테마:** 나무(Wood) 질감의 전용 Drawable 리소스(`bg_wood`, `btn_selector` 등) 일괄 적용.
- **화면 전환:** FragmentManager를 이용한 [Review, Learn, Battle] 프래그먼트 교체 로직 구현.

### 3. 주요 수정 사항
- `WordDatabase` 내 잘못 임포트된 `privacysandbox` 라이브러리 제거 및 빌드 에러 해결.
- `EquipFragment` 제거 및 관련 하단 바 가중치(weightSum=3) 조정.

---

## 🚀 향후 작업 계획 (Next Steps)

### 1단계: 데이터 생명력 불어넣기 (Data Layer)
- **DB 초기화:** 앱 최초 실행 시 기본 영단어(20~30개) 자동 `Insert` 로직 구현.
- **LearnFragment:** DB 데이터를 불러와 `RecyclerView` 리스트로 출력.

### 2단계: 게임 로직 구현 (Game Layer)
- **BattleRunner:** 영단어 퀴즈를 풀며 정답 문을 통과하는 미니게임 개발.
- **오답 피드백:** 게임에서 틀린 단어는 자동으로 DB의 `wrongCount` 증가.

### 3단계: 마무리 (Polishing)
- **ReviewFragment:** 오답 횟수가 높은 단어들을 우선적으로 보여주는 복습 화면.
- **Settings:** 상단 설정 버튼 클릭 시 닉네임 변경 및 간단한 설정 다이얼로그 연동.

---

## 💡 다음 세션 가이드 (AI 전달용)
"현재 Word Quest 프로젝트의 UI 뼈대와 Room DB 설정이 완료된 상태입니다. `HomeActivity` 레이아웃과 `data` 패키지가 구성되어 있으니, 다음에는 **DB에 샘플 데이터를 삽입하고 LearnFragment에서 리스트를 보여주는 기능**부터 시작해 주세요."