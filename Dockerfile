# 베이스 이미지 선택
FROM openjdk:17-jdk-slim AS build

# 작업 디렉토리 설정
WORKDIR /app

# Gradle 관련 파일만 먼저 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# 의존성 캐시를 이용하여 미리 다운로드
RUN ./gradlew dependencies

# 전체 소스 복사 및 빌드
COPY . .
RUN ./gradlew build -x test

# 실행 이미지 생성
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/build/libs/your-app.jar app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
