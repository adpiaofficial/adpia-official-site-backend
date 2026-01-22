# ---------- build stage ----------
FROM gradle:8.5-jdk17 AS builder
WORKDIR /app

# 캐시 최적화
COPY build.gradle settings.gradle ./
COPY gradle ./gradle
RUN gradle dependencies --no-daemon

# 소스 복사 후 빌드
COPY . .
RUN gradle clean build -x test --no-daemon

# ---------- runtime stage ----------
FROM eclipse-temurin:17-jre
WORKDIR /app

# 빌드 결과 JAR 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# JVM 옵션 (메모리 작은 VPS용)
ENV JAVA_OPTS="-Xms256m -Xmx512m"

EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
