FROM bellsoft/liberica-openjdk-alpine:17
RUN apk add --no-cache redis
ARG JAR_FILE=build/libs/osori-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
