# -------- build stage --------
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

# bash / file 유틸 설치(진단용)
RUN apt-get update && apt-get install -y --no-install-recommends bash file && rm -rf /var/lib/apt/lists/*

COPY gradlew .
COPY gradle gradle
COPY build.gradle* settings.gradle* gradle.properties* ./

# 윈도우 CRLF 제거 + 실행권한
RUN sed -i 's/\r$//' gradlew && chmod +x gradlew

# gradlew 상태 진단 출력
RUN ls -al gradlew && head -n 2 gradlew && file gradlew

COPY . .

# bash로 강제 실행 (sh에서 126 나는 케이스 방지)
RUN bash ./gradlew clean bootJar -x test --stacktrace --info

# -------- run stage --------
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
