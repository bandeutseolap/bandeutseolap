# JDK 17 slim 버전을 베이스 이미지로 사용 -> 조금 더 가벼운 jre로 변경
FROM eclipse-temurin:17-jre

# 컨테이너 내부 작업 디렉터리를 /app 으로 설정
WORKDIR /app

# 빌드 시점에 전달할 수 있는 변수 정의 (기본값은 build/libs/*SNAPSHOT.jar)
ARG JAR_FILE=build/libs/*SNAPSHOT.jar

# 위 ARG로 지정된 JAR 파일을 컨테이너 /app/app.jar 로 복사
COPY ${JAR_FILE} app.jar

# 컨테이너 시작 시 실행할 명령어: java -jar app.jar
ENTRYPOINT ["java", "-jar", "appar"]