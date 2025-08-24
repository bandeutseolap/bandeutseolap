# ---- 1단계: 빌드 (JDK 필요) ----
FROM eclipse-temurin:17-jdk AS builder
WORKDIR /app

# 캐시 효율을 위해 래퍼/설정 먼저 복사
COPY gradlew gradlew.bat gradle/ ./
COPY build.gradle.kts settings.gradle.kts ./

# 소스 복사
COPY src ./src

# 권한 부여 후 빌드
RUN chmod +x ./gradlew && ./gradlew bootJar --no-daemon

# ---- 2단계: 실행 (JRE만 필요) ----
FROM eclipse-temurin:17-jre
WORKDIR /app

# 빌드 결과물만 가져오기
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]