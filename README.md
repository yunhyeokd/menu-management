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
│   ├── domain
│   │   ├── admin       # 관리자 계정 및 프로필
│   │   ├── auth        # 인증 공통 인터페이스 (Account, Credential)
│   │   ├── branch      # 지점 계정, 프로필, 상품 판매 재정의
│   │   ├── product     # 상품, 카테고리, 옵션 그룹/항목, 태그, 알레르기
│   │   └── common      # 공통 도메인 예외, 식별자 추상화
│   └── application
│       ├── admin       # AdminService
│       ├── auth        # AuthService (미구현)
│       ├── branch      # BranchService
│       ├── product     # CategoryService, TagService, OptionService, ProductService
│       └── common      # BusinessException, RepositoryException, IdentifierGenerator
└── test/java/com/dozycoffee
    ├── domain          # 도메인 단위 테스트
    └── application     # 서비스 통합 테스트 (Fake Repository 사용)
```

## 도메인 레이어 패키지 간 의존 방향

```
admin   ──→ auth      (Principal 구현)
branch  ──→ auth      (Principal 구현)
branch  ──→ product   (ProductSalesOverride가 ProductId 참조)
product ──→ branch    (Product가 BranchId 참조)
```

`auth`는 최하위 공통 인터페이스로 어떤 패키지도 의존받지 않습니다.
`product ↔ branch` 상호 참조는 값 객체(Id) 수준에서만 허용하며 엔티티 직접 참조는 금지합니다.

## 애그리거트 간 의존 방향 (응용 계층)

응용 계층에서 서로 다른 애그리거트의 Repository를 참조할 수 있습니다.
단, 아래 방향을 지키고 역방향 의존은 금지합니다.

```
admin   ──→ auth      (PasswordHasher, SessionInvalidationPort 사용)
branch  ──→ auth      (PasswordHasher, CredentialKeyGenerator, SessionInvalidationPort 사용)
branch  ──→ product   (BranchService가 지점 삭제 시 ProductRepository로 상품 비활성화)
product ──→ branch    (ProductService가 지점 전용 상품 생성 시 BranchAccountRepository로 지점 존재 확인)
```

- **서비스 → 다른 애그리거트의 Repository**: 허용
- **서비스 → 다른 애그리거트의 서비스**: 단방향만 허용 (순환 참조 금지)
- `branch ↔ product` 상호 참조는 Repository 레벨에서만 허용하며 서비스 간 직접 참조는 금지합니다.

### 패키지 내 서비스 간 의존 방향

```
[product]
ProductService ──→ TagService    (상품 생성 시 태그 findOrCreate)
```

같은 패키지 내 서비스 간 참조도 단방향만 허용합니다. (`TagService → ProductService` 금지)

## 브랜치 전략

| 브랜치 | 용도 |
|--------|------|
| `main` | 릴리즈 (안정 버전) |
| `dev` | 개발 통합 |
| `feat/<기능명>` | 기능 개발 |
| `fix/<버그명>` | 버그 수정 |

`feat/*` → `dev` → `main` 순으로 머지합니다.