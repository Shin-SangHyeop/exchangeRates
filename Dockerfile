# -------- build stage --------
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

RUN apt-get update && apt-get install -y --no-install-recommends bash file && rm -rf /var/lib/apt/lists/*

COPY gradlew .
COPY gradle gradle
COPY build.gradle* settings.gradle* gradle.properties* ./
RUN sed -i 's/\r$//' gradlew && chmod +x gradlew

COPY . .
RUN bash ./gradlew clean bootJar -x test --stacktrace --info

# -------- run stage --------
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
