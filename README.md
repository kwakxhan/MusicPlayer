# Music Player
Android 로컬 음악 재생 앱

## 1. 주요 기능
- 기기 내 음악 파일 자동 검색 및 재생
- 백그라운드 재생 지원
- 미니 플레이어 / 전체 화면 플레이어
- 재생 제어 (재생/일시정지, 이전/다음, 탐색)
- 반복 모드 (전체 반복, 한곡 반복, 반복 안함)
- 셔플 재생
- 다크 모드 지원

## 2. 아키텍처

### Domain 중심의 Clean Architecture + MVVM
> Android App Architecture 가이드를 사용하지 않고, Domain을 중심으로 한 순수 Clean Architecture 적용

```
                    ┌──────────┐
                    │   App    │
                    │ (조립층)  │
                    └─────┬────┘
                          │
        ┌─────────────────┼─────────────────┐
        │                 │                 │
        │     ┌───────────▼───────────┐     │
        │     │    Domain (중심)       │     │
        │     │                       │     │
        │     │  • Model              │     │
        │     │  • UseCase            │     │
        │     │  • Repository Intf    │     │
        │     │  • Controller Intf    │     │
        │     └──────┬─────────┬──────┘     │
        │            │         │            │
    ┌───▼────────┐   │         │   ┌────────▼───┐
    │  Feature   │◄──┘         └──►│    Data    │
    │            │                 │            │
    │ • Fragment │                 │ • Repo Impl│
    │ • ViewModel│                 │ • DataSrc  │
    │ • View     │                 │ • Controller
    └─────┬──────┘                 └──────┬─────┘
          │                               │
          │         ┌──────────┐          │
          └────────►│   Core   │◄─────────┘
                    │ (공통층)  │
                    │• Utils   │
                    │• Constants
                    └──────────┘
```

### 의존성 규칙
- `app` → `feature`, `data`, `domain`, `core` (모든 모듈 조립)
- `feature` → `domain`, `core`
- `data` → `domain`, `core`
- `domain` → **아무것도 의존하지 않음** (순수 Kotlin)
- `core` → **아무것도 의존하지 않음** (유틸리티만)

### 모듈 구조
- **domain** (핵심) - 비즈니스 로직, 모델, 인터페이스 정의
- **feature** - UI 레이어 (Fragment, ViewModel)
- **data** - 데이터 레이어 (Repository 구현, DataSource, MediaController)
- **app** - Application 진입점, DI 설정, 모든 모듈 조립
- **core** - 공통 유틸리티 (Extensions, Constants)

## 3. 기술 스택

### Android & Kotlin
- **Min SDK** - 24 (Android 7.0)
- **Target SDK** - 36
- **Kotlin**
- **Gradle** - Version Catalog

### Architecture Components
- **ViewModel** - UI 관련 데이터 관리
- **Lifecycle** - 생명주기 인식 컴포넌트
- **StateFlow** - 반응형 데이터 스트림
- **Data Binding** - 선언적 UI 바인딩
- **Navigation Component** - Fragment 간 화면 전환

### Dependency Injection
- **Hilt** - 의존성 주입

### Asynchronous
- **Coroutines** - 비동기 처리
- **Flow** - 반응형 데이터 스트림

### Media
- **Media3 ExoPlayer** - 미디어 재생
- **Media3 Session** - 백그라운드 재생 및 미디어 컨트롤

### UI
- **Material Design 3**
- **Activity & Fragment**
- **ConstraintLayout** - 유연한 레이아웃 구성
- **RecyclerView + ListAdapter** - 효율적인 리스트 표시
- **Coil** - 이미지 로딩

### Utilities
- **Timber** - 로깅
- **Paging 3** - 페이징 처리

## 4. 권한
앱 실행을 위해 다음 권한이 필요합니다:
- **Android 13 (API 33) 이상**
  - `READ_MEDIA_AUDIO`
- **Android 12 이하**
  - `READ_EXTERNAL_STORAGE`

## 5. 라이선스
- 이 프로젝트는 과제 제출 목적으로 제작되었습니다.
- YouTube Music 앱의 UI/UX를 참조하였습니다.

## 6. 개발자
- **xhan(곽찬)**