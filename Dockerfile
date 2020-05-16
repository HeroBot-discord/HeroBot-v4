FROM gradle:5.0.0-jdk8-alpine AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
COPY . .
RUN gradle build --no-daemon
FROM openjdk:8-jre-slim
WORKDIR /app
COPY --from=build /home/gradle/src/build/libs/*.jar /app/
RUN ls
ENTRYPOINT ["java", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseCGroupMemoryLimitForHeap", "-Djava.security.egd=file:/dev/./urandom","-jar","HeroBot-1.0-SNAPSHOT-all.jar"]