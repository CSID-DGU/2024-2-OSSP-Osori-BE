FROM bellsoft/liberica-openjdk-alpine:17
ENV TZ=Asia/Seoul
RUN apk add --no-cache tzdata \
    && cp /usr/share/zoneinfo/${TZ} /etc/localtime \
    && echo "${TZ}" > /etc/timezone
ARG JAR_FILE=build/libs/osori-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-Duser.timezone=Asia/Seoul", "-jar", "/app.jar"]
