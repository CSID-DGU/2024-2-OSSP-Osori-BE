# 기존 Dockerfile 예시
FROM bellsoft/liberica-openjdk-alpine:17

# 작업 디렉터리 설정
WORKDIR /app

# application.yml을 복사합니다
COPY src/resources/application.yml /app/resources/application.yml

# JAR 파일 복사
COPY build/libs/app.jar /app/app.jar

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
