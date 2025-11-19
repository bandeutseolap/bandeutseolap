plugins {
	java
	id("org.springframework.boot") version "3.5.4"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.dobidan"
version = "0.0.1-SNAPSHOT"
description = "dobidan project for Spring Boot"

java {
	toolchain {
		languageVersion.set(JavaLanguageVersion.of(17))
	}
}

repositories {
	mavenCentral()
}

dependencies {
	// Web
	implementation("org.springframework.boot:spring-boot-starter-web")

	// Security
	implementation("org.springframework.boot:spring-boot-starter-security")

	// JPA
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")

	// Bean Validation (요청 DTO 검증용)
	implementation("org.springframework.boot:spring-boot-starter-validation")

	// MariaDB
	runtimeOnly("org.mariadb.jdbc:mariadb-java-client:3.3.3")

	// JWT
	implementation("io.jsonwebtoken:jjwt-api:0.11.5")
	runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
	runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")

	// Lombok
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")

	// Test
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
