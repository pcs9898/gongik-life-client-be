# 1단계: 빌드 단계
FROM gradle:8.6-jdk17 AS build

# 작업 디렉토리 설정
WORKDIR /app

# Gradle Wrapper 및 소스 파일 복사
COPY ../gradlew .
COPY ../gradle gradle/
COPY ../build.gradle .
COPY ../settings.gradle .

# Gradle 캐시를 활용하여 의존성 설치
RUN ./gradlew build -x test --stacktrace --no-daemon || return 0

# 프로젝트 소스 복사
COPY gongik-life-client-be-auth-service/ gongik-life-client-be-auth-service/
COPY gongik-life-client-be-common/ gongik-life-client-be-common/
COPY gongik-life-client-be-kafka-common-dto/ gongik-life-client-be-kafka-common-dto/
COPY gongik-life-client-be-grpc-common/ gongik-life-client-be-grpc-common/

# 필요한 모듈 빌드
RUN ./gradlew -Pprotoc_platform=linux-x86_64 :gongik-life-client-be-auth-service:build -x test --stacktrace --no-daemon

# 2단계: 실행 단계
FROM openjdk:17-jdk-slim

# 작업 디렉토리 설정
WORKDIR /app

# 빌드 결과물 복사
COPY --from=build /app/gongik-life-client-be-auth-service/build/libs/*.jar app.jar

# 컨테이너 실행 시 애플리케이션 실행
ENV SPRING_PROFILE "dev"
ENTRYPOINT ["java", "-Dspring.profiles.active=${SPRING_PROFILE}", "-jar", "app.jar"]