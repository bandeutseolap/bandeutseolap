import java.util.Locale;
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.Copy
import org.gradle.api.plugins.BasePlugin

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
		languageVersion = JavaLanguageVersion.of(17)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
	useJUnitPlatform()
}


/* 2025.08.17
 * SpringBoot (Back-End)와 React (Front-End)를 하나의 패키지로 만들자
 * SpringBoot 빌드될 때 React가 먼저 빌드되고,
 * 결과물을 SpringBoot 빌드 결과물에 포함시킨다는 내용
 *
 * 둘 다 실행시킬 필요 없이 서버만 키면 locahost:8080 프론트엔드 내용 같이 나오게
 */

val frontendDir = file("${projectDir.parent}/frontend")

sourceSets {
	named("main") {
		resources {
			srcDirs("$projectDir/src/main/resources")
		}
	}
}

tasks.processResources {
	/*dependsOn ("copyReactBuildFiles")*/ // 도커 빌드 전까지 주석
	duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

tasks {
	/* // 도커 빌드 전까지 주석
	val installReact by registering(Exec::class) {
		workingDir = file(frontendDir)
		inputs.dir(frontendDir)
		group = BasePlugin.BUILD_GROUP

		if (System.getProperty("os.name").lowercase(Locale.getDefault()).contains("windows")) {
			commandLine("npm.cmd", "audit", "fix")
			commandLine("npm.cmd", "install")
		} else {
			commandLine("npm", "audit", "fix")
			commandLine("npm", "install")
		}
	}

	val buildReact by registering(Exec::class) {
		dependsOn(installReact)
		workingDir = file(frontendDir)
		inputs.dir(frontendDir)
		group = BasePlugin.BUILD_GROUP

		if (System.getProperty("os.name").lowercase(Locale.getDefault()).contains("windows")) {
			commandLine("npm.cmd", "run-script", "build")
		} else {
			commandLine("npm", "run-script", "build")
		}
	}

	val copyReactBuildFiles by registering(Copy::class) {
		dependsOn(buildReact)
		from("$frontendDir/build")
		into("$projectDir/src/main/resources/static")
	}

	/*
	* 로컬 환경에서 개발 시,  불필요하게 npm install과 react build가 실행
	* -> 배포 시에만 React 빌드 파일 포함시키기
	* tasks.bootJar {
		dependsOn ("copyReactBuildFiles")
	  }
	* ./gradlew bootJar 실행
	*/

	 */
}
