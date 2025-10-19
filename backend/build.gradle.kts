import java.util.Locale;
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.Copy
import org.gradle.api.plugins.BasePlugin

plugins {
	java
	id("org.springframework.boot") version "3.5.4"
	id("io.spring.dependency-management") version "1.1.7"
	id("com.github.node-gradle.node") version "7.0.2" // ★ 추가
}

group = "com.dobidan"
version = "0.0.1-SNAPSHOT"
description = "dobidan project for Spring Boot"

java {
	toolchain {
		languageVersion.set(JavaLanguageVersion.of(17)) /*Kotlin은 1.5.30버전 부터 Java toolchains를 공식적으로 지원*/
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

	/* Database dependency */
	implementation ("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation ("com.fasterxml.jackson.datatype:jackson-datatype-hibernate5")
	implementation("org.mariadb.jdbc:mariadb-java-client:3.3.3")
	/* Database dependency */

}

tasks.withType<Test> {
	useJUnitPlatform()
}


/* 2025.10.19
 * 서버(Spring Boot)만 실행해도 localhost:8080에서 Vue 정적 파일이 같이 뜨도록 Gradle에서 프론트 빌드를 자동화
 */

node {
	// Node 자동 다운로드(로컬에 Node 없어도 됨)
	download.set(true)
	version.set("20.16.0")
	npmVersion.set("10.8.1")
	// Vue 폴더 경로
	workDir.set(file("${project.projectDir}/.gradle/node"))
	// npm이 실행될 디렉토리
	nodeProjectDir.set(file("${project.rootDir}/../frontend"))
}

// npm install
//val npmInstall by tasks.registering(com.github.gradle.node.npm.task.NpmInstallTask::class)

// npm run build (Vite)
val npmBuild by tasks.registering(com.github.gradle.node.npm.task.NpmTask::class) {
	dependsOn("npmInstall")
	args.set(listOf("run", "build"))
	workingDir.set(file("${project.rootDir}/../frontend"))
	inputs.dir("${project.rootDir}/../frontend/src")
	inputs.file("${project.rootDir}/../frontend/package.json")
	inputs.file("${project.rootDir}/../frontend/package-lock.json")
	outputs.dir("${project.rootDir}/../frontend/dist")
}

// Vue dist → Spring 정적 리소스로 복사
val copyFrontend by tasks.registering(Copy::class) {
	dependsOn(npmBuild)
	from("${project.rootDir}/../frontend/dist")
//	into("$buildDir/generated-resources/frontend") // 임시 복사 위치
	into("$buildDir/resources/main/static") // 임시 복사 위치
}

// 정적 리소스 포함시키기
tasks.processResources {
	dependsOn(copyFrontend)
//	from("$buildDir/generated-resources/frontend") {
//		// dist의 index.html, assets/* 등을 포함
//		into("") // classpath:/ 에 루트로 포함 -> /static 과 동일 효과
//	}
}

// 배포/실행 산출물에도 프론트 포함되게
//tasks.bootJar {
//	dependsOn(tasks.processResources)
//}

// 로컬 실행에서도 프론트 포함(HMR 안 쓰는 경우)
//tasks.bootRun {
//	dependsOn(tasks.processResources)
//}