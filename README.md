# Dozy Coffee - Menu System

카페 메뉴 관리 시스템 백엔드 구현 프로젝트입니다.

## 기술 스택

- Java 21
- Gradle 9.3
- JUnit 5 / AssertJ

## 프로젝트 구조

```
src
├── main/java/com/dozycoffee
│   └── domain
│       ├── admin       # 관리자 계정 및 프로필
│       ├── branch      # 지점
│       ├── product     # 상품, 카테고리, 옵션, 태그, 알레르기
│       └── common      # 공통 예외
└── test/java/com/dozycoffee
    └── domain
        ├── admin
        ├── branch
        └── product
```

## 브랜치 전략

| 브랜치 | 용도 |
|--------|------|
| `main` | 릴리즈 (안정 버전) |
| `dev` | 개발 통합 |
| `feat/<기능명>` | 기능 개발 |
| `fix/<버그명>` | 버그 수정 |

`feat/*` → `dev` → `main` 순으로 머지합니다.