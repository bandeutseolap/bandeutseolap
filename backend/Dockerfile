    # ---- 1단계: 빌드 (JDK 필요) ----
    FROM eclipse-temurin:17-jdk AS builder
    WORKDIR /app

    # Gradle Wrapper 및 설정 먼저 복사 (캐시 최적화)
    COPY gradlew gradlew.bat ./
    COPY gradle ./gradle
    COPY build.gradle.kts settings.gradle.kts ./

    # 윈도우 개행 제거 + 실행권한 부여
    RUN sed -i 's/\r$//' gradlew && chmod +x gradlew

    # 의존성만 먼저 받아서 캐시
    RUN ./gradlew --no-daemon --stacktrace dependencies || true

    # 소스 복사
    COPY src ./src

    # 빌드
    RUN ./gradlew bootJar --no-daemon --stacktrace

    # ---- 2단계: 실행 (JRE만 필요) ----
    FROM eclipse-temurin:17-jre
    WORKDIR /app
    COPY --from=builder /app/build/libs/*.jar app.jar
    EXPOSE 8080
    ENTRYPOINT ["java","-jar","app.jar"]
