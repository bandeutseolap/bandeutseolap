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
	implementation("org.springframework.boot:spring-boot-starter-web")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

	// Database
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("com.fasterxml.jackson.datatype:jackson-datatype-hibernate5")
	implementation("org.mariadb.jdbc:mariadb-java-client:3.3.3")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
