# 베이스 이미지 선택
FROM openjdk:17-jdk-slim

# 작업 디렉토리 설정
WORKDIR /app

# Gradle 빌드 결과물 복사
COPY build/libs/your-app.jar app.jar

# 앱 실행
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
