# Dozy Coffee - Menu System

카페 메뉴 관리 시스템 백엔드 구현 프로젝트입니다.

## 기술 스택

- Java 21
- Gradle 9.3
- JUnit 5 / AssertJ

## 프로젝트 구조

애그리게잇을 최상위 기준으로, 레이어를 하위 구조로 구성합니다.

```
src/main/java/com/dozycoffee
├── auth/                       # 공유 커널 — 인증 추상화 및 세션 관리
│   ├── domain/                 # Principal, Credential, Authenticator, CredentialGenerator
│   └── application/            # AuthenticationService, AuthorizationService, AuthSessionManager
│                               # AuthSessionRepository, SessionInvalidationPort, PasswordHasher
├── admin/
│   ├── domain/                 # AdminAccount, AdminProfile
│   └── application/            # AdminService, AdminUsernameAuthenticator
├── branch/
│   ├── domain/                 # BranchAccount, BranchProfile, ProductSalesOverride
│   └── application/            # BranchService, BranchOperationService, BranchAuthenticator
│       └── model/              # BranchProduct (product aggregate read model)
├── product/
│   ├── domain/                 # Product, Category, Tag, OptionGroup, OptionItem, ...
│   └── application/
│       ├── service/            # ProductService, CategoryService, TagService, OptionService
│       ├── repository/         # ProductRepository, CategoryRepository, ...
│       └── dto/                # ProductData, BranchProductRegisterCommand, ...
└── core/                       # 공유 커널 — 범용 추상화
    ├── domain/                 # DomainException, Identifier, IdentifierGenerator, Session<T>, SessionId
    └── application/            # AppException, RepositoryException, ServiceCode, SessionManager<T>
```

## 애그리게잇 간 의존 방향

### 도메인 레이어

```
admin   ──→ auth      (Principal 구현)
branch  ──→ auth      (Principal 구현)
branch  ──→ product   (ProductSalesOverride가 ProductId 참조)
product ──→ branch    (Product가 BranchId 참조)
```

`auth`와 `core`는 공유 커널로, 다른 애그리게잇에 의존하지 않습니다.
`product ↔ branch` 상호 참조는 ID 값 객체 수준에서만 허용합니다.

### 애플리케이션 레이어

다른 애그리게잇에 직접 의존하는 대신 포트 인터페이스를 통해 간접 참조합니다.

```
admin   ──→ auth      (PasswordHasher, SessionInvalidationPort 사용)
branch  ──→ auth      (PasswordHasher, SessionInvalidationPort 사용)
branch  ──→ product   (BranchProductQueryPort, BranchProductLifecyclePort — 포트 추상화)
product ──→ branch    (BranchAccountRepository로 지점 존재 확인)
```

서비스 간 직접 참조는 금지하며, 다른 애그리게잇과의 상호작용은 포트 인터페이스를 통해서만 허용합니다.

## 브랜치 전략

| 브랜치 | 용도 |
|--------|------|
| `main` | 릴리즈 (안정 버전) |
| `dev` | 개발 통합 |
| `feat/<기능명>` | 기능 개발 |
| `fix/<버그명>` | 버그 수정 |

`feat/*` → `dev` → `main` 순으로 머지합니다.
