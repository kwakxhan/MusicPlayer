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
- **feature** - UI 레이어 (Fragment, ViewModel, Custom Views, Compose Components)
- **data** - 데이터 레이어 (Repository 구현, DataSource, MediaController)
- **app** - Application 진입점, DI 설정, Navigation 설정
- **core** - 공통 유틸리티 (Extensions, Constants)

## 3. 기술 스택

### Android & Kotlin
- **Min SDK** - 24 (Android 7.0)
- **Target SDK** - 36
- **Kotlin** - 2.0.21
- **Gradle** - 8.12.3 (Version Catalog)

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
- **Material Design 3** - 머티리얼 디자인 컴포넌트
- **Activity & Fragment** - 기본 UI 구성
- **ConstraintLayout** - 유연한 레이아웃 구성
- **RecyclerView + ListAdapter** - 효율적인 리스트 표시
- **Coil** - 이미지 로딩
- **Jetpack Compose** - MiniPlayer 구현 (커스텀 View)
- **Coil Compose** - Compose용 이미지 로딩

### Utilities
- **Timber** - 로깅
- **Paging 3** - 페이징 처리

## 4. Compose 컴포넌트

### MiniPlayer (Compose)
커스텀 View에서 Jetpack Compose로 전환한 미니 플레이어 구현

**주요 기능**:
- 재생 중인 트랙 정보 표시 (앨범 아트, 제목, 아티스트)
- 재생/일시정지 버튼
- 재생 진행 바 (LinearProgressIndicator)
- 클릭 시 전체 화면 플레이어로 전환

## 5. 라이브러리 버전

### Core Libraries
| 라이브러리 | 버전 | 용도 |
|----------|------|------|
| Kotlin | 2.0.21 | 프로그래밍 언어 |
| Android Gradle Plugin | 8.12.3 | 빌드 도구 |
| Core KTX | 1.17.0 | Kotlin 확장 함수 |
| AppCompat | 1.7.1 | 하위 호환성 |
| Material | 1.13.0 | Material Design |

### Jetpack Compose
| 라이브러리 | 버전 | 용도 |
|----------|------|------|
| Compose BOM | 2025.01.00 | Compose 버전 관리 |
| Material3 | BOM | Material Design 3 Compose |
| Lifecycle Compose | 2.10.0 | 생명주기 통합 (collectAsStateWithLifecycle) |
| Coil Compose | 2.7.0 | 이미지 로딩 (AsyncImage) |

### Architecture & DI
| 라이브러리 | 버전 | 용도 |
|----------|------|------|
| Lifecycle | 2.10.0 | ViewModel, StateFlow |
| Hilt | 2.57.2 | 의존성 주입 |
| Navigation Fragment | 2.9.6 | Fragment 화면 전환 |
| Activity KTX | 1.12.2 | Activity 확장 함수 |
| Fragment KTX | 1.8.9 | Fragment 확장 함수 |

### Media & Async
| 라이브러리 | 버전 | 용도 |
|----------|------|------|
| Media3 ExoPlayer | 1.9.0 | 미디어 재생 |
| Media3 Session | 1.9.0 | 백그라운드 재생 |
| Coroutines | 1.10.2 | 비동기 처리 |

### Utilities & UI
| 라이브러리 | 버전 | 용도 |
|----------|------|------|
| Timber | 5.0.1 | 로깅 |
| Paging 3 | 3.3.6 | 페이징 처리 |
| Coil | 2.7.0 | 이미지 로딩 (일반 View용) |

## 6. 권한
앱 실행을 위해 다음 권한이 필요합니다:
- **Android 13 (API 33) 이상**
  - `READ_MEDIA_AUDIO`
- **Android 12 이하**
  - `READ_EXTERNAL_STORAGE`

## 7. 라이선스
- 이 프로젝트는 과제 제출 목적으로 제작되었습니다.
- YouTube Music 앱의 UI/UX를 참조하였습니다.

## 8. 개발자
- **xhan(곽찬)**