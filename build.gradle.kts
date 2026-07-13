
plugins {
    java
    alias(libs.plugins.spring.boot)
}

group = "com.dozycoffee"
version = "1.0-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.java.asProvider().get().toInt()))
    }
}

repositories {
    mavenCentral()
}

dependencies {

    // BOM (spring-security-crypto 포함 대부분의 하위 라이브러리 버전을 여기서 관리)
    implementation(platform(libs.spring.boot.dependencies))
    testImplementation(platform(libs.spring.boot.dependencies))

    // Web
    implementation(libs.spring.boot.starter.web)

    // Validation
    implementation(libs.spring.boot.starter.validation)

    // Database / MyBatis (mybatis-spring-boot-starter가 아직 Boot 4.x를 지원하지 않아 core+spring 직접 배선)
    implementation(libs.spring.boot.starter.jdbc)
    implementation(libs.bundles.mybatis)
    runtimeOnly(libs.mysql.connector.j)

    // DB 마이그레이션 (MySQL 8은 flyway-core만으론 동작 안 하고 flyway-mysql이 별도로 필요)
    implementation(libs.spring.boot.starter.flyway)
    runtimeOnly(libs.flyway.mysql)

    // Security (spring-boot-starter-security 미사용 — 자체 인증/인가 구현과 충돌 방지)
    // Argon2PasswordEncoder에서 내부적으로 BouncyCastle 구현체 요구
    implementation(libs.bundles.security.crypto)

    // UUID v7
    implementation(libs.java.uuid.generator)

    // Lombok
    implementation(libs.lombok)
    annotationProcessor(libs.lombok)

    // Swagger
    implementation(libs.springdoc.openapi.starter.webmvc.ui)

    // Test
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.spring.boot.starter.webmvc.test)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-parameters")
}

tasks.test {
    useJUnitPlatform()
}
