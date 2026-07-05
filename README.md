# Dozy Coffee - Menu System

카페 메뉴 관리 시스템 백엔드 구현 프로젝트입니다.

## 기술 스택

- Java 17
- Gradle 9.3, `war` 패키징 (Spring Boot 미사용, 서블릿 컨테이너 배포)
- Spring WebMVC (`@EnableWebMvc`), Jackson
- Spring Security Crypto(Argon2) — Security 필터체인은 미사용, 인증/인가는 자체 구현
- MyBatis + MySQL + HikariCP
- jakarta.validation / hibernate-validator
- JUnit 5 / AssertJ

## 프로젝트 구조

애그리게잇을 최상위 기준으로, 레이어(도메인/애플리케이션/프레젠테이션)를 하위 구조로 구성합니다.

```
src/main/java/com/dozycoffee
├── auth/                       # 공유 커널 — 로그인/로그아웃 및 세션 발급 유스케이스
│   ├── domain/                 # SessionPrincipal, AuthException
│   ├── application/            # AuthenticationService, AuthorizationService, AuthSessionManager
│   │                           # AuthenticationResolver, AuthSessionRepository
│   └── presentation/           # AuthController, dto/
├── admin/
│   ├── domain/                 # AdminPrincipal(상위 타입) ← SystemAdmin, Admin(profile 보유)
│   ├── application/            # AdminService, AdminUsernameAuthenticator, AdminRepository
│   └── presentation/           # AdminController, dto/
├── branch/
│   ├── domain/                 # Branch, BranchCode, BranchStatus, ProductSalesOverride
│   ├── application/            # BranchService, BranchOperationService, BranchAuthenticator
│   │                           # BranchProductQueryPort, BranchProductLifecyclePort
│   │                           # model/BranchProduct (product 애그리게잇 read model)
│   └── presentation/           # BranchController, dto/
├── product/
│   ├── domain/                 # Product, Category, Tag, OptionGroup, OptionItem, ...
│   ├── application/            # ProductService, CategoryService, TagService, OptionService, ...
│   │                           # usecase/ (서비스 조합이 필요한 유스케이스), repository/, dto/
│   └── presentation/           # ProductController, CategoryController, TagController, OptionController, dto/
├── core/                       # 공유 커널 — 범용 추상화
│   ├── security/                # Principal, Credential, Authenticator, CredentialGenerator, PasswordHasher
│   ├── session/                 # Session<T>, SessionId, SessionManager<T>
│   ├── id/                      # Identifier, IdentifierGenerator
│   └── exception/                # DomainException, RepositoryException, ServiceException 계열, ServiceCode
└── infrastructure/              # 인프라 레이어 (MyBatis/DB/Security/Web 설정)
    ├── adapter/                 # 애그리게잇 간 포트 구현
    ├── generator/               # IdentifierGenerator 구현체
    ├── persistance/              # MyBatis 엔티티/매퍼/리포지토리 구현체
    ├── security/                 # SecurityConfig (PasswordHasher, AuthenticationService 빈 조립)
    └── web/                      # ServletConfig, GlobalExceptionHandler, IdConverterFactory
```

## 애그리게잇 간 의존 방향

### 도메인 레이어

```
admin, branch, auth ──→ core   (Principal/Credential/Session 등 공유 추상화 사용)
branch ──→ product            (ProductSalesOverride가 ProductId 참조)
product ──→ branch            (Product가 BranchId 참조)
```

`core`는 공유 커널로, 다른 애그리게잇에 의존하지 않습니다.
`auth`는 로그인/로그아웃 유스케이스만 담당하며 `core`에만 의존합니다.
`product ↔ branch` 상호 참조는 ID 값 객체 수준에서만 허용합니다.

### 애플리케이션 레이어

다른 애그리게잇에 직접 의존하는 대신 포트 인터페이스를 통해 간접 참조합니다.

```
admin, branch ──→ core   (PasswordHasher, SessionInvalidationPort 등 사용)
branch ──→ product        (BranchProductQueryPort, BranchProductLifecyclePort — 포트 추상화)
product ──→ branch        (BranchExistencePort로 지점 존재 확인)
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
