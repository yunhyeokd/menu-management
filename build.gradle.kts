
plugins {
    id("java")
    id("war")
}

group = "com.dozycoffee"
version = "1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}



dependencies {

    // BOM
    implementation(platform("org.springframework:spring-framework-bom:6.2.19"))
    implementation(platform("org.springframework.security:spring-security-bom:6.5.11"))
    testImplementation(platform("org.junit:junit-bom:6.0.0"))
    implementation(platform("com.fasterxml.jackson:jackson-bom:2.22.0"))

    // DI
    implementation("org.springframework:spring-context")

    // Logging
    implementation("org.slf4j:slf4j-api:2.0.18")
    implementation("ch.qos.logback:logback-classic:1.5.37")

    // Test
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.assertj:assertj-core:3.27.7")
    testImplementation("org.springframework:spring-test")

    // Jackson
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")

    // Web
    compileOnly("jakarta.servlet:jakarta.servlet-api:6.0.0")
    testImplementation("jakarta.servlet:jakarta.servlet-api:6.0.0")
    implementation("org.springframework:spring-webmvc")

    // Validation
    implementation("jakarta.validation:jakarta.validation-api:3.0.2")
    implementation("org.hibernate.validator:hibernate-validator:8.0.4.Final")
    runtimeOnly("jakarta.el:jakarta.el-api:5.0.0")
    runtimeOnly("org.glassfish.expressly:expressly:5.0.0")

    // Database
    implementation("org.springframework:spring-jdbc")
    implementation("com.mysql:mysql-connector-j:8.4.0")
    implementation("org.mybatis:mybatis:3.5.19")
    implementation("org.mybatis:mybatis-spring:3.0.5")
    implementation("com.zaxxer:HikariCP:7.1.0")

    // Security
    implementation("org.springframework.security:spring-security-crypto")

    // YAML
    implementation("org.yaml:snakeyaml:2.6")

    // UUID v7
    implementation("com.fasterxml.uuid:java-uuid-generator:5.2.0")

    // Lombok
    implementation("org.projectlombok:lombok:1.18.46")
    annotationProcessor("org.projectlombok:lombok:1.18.46")

    // Swagger (springdoc-openapi 2.x = Jakarta/Spring Framework 6 line; Boot 없이 쓰려면
    // springdoc의 자동구성 클래스가 참조하는 spring-boot-autoconfigure를 별도로 얹어야 함)
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.17")
    implementation("org.springframework.boot:spring-boot-autoconfigure:3.5.16")

}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-parameters")
}

tasks.test {
    useJUnitPlatform()
}