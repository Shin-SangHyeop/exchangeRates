# -------- build stage --------
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

# Gradle wrapper + 설정 먼저 복사(캐시 효율)
COPY gradlew .
COPY gradle gradle
COPY build.gradle* settings.gradle* gradle.properties* ./
RUN sed -i 's/\r$//' gradlew && chmod +x gradlew

# 나머지 소스 복사
COPY . .

# wrapper로 빌드 (실패 원인 로그 자세히)
RUN ./gradlew clean bootJar -x test --stacktrace --info

# -------- run stage --------
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
