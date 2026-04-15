FROM amazoncorretto:17-alpine

ARG PROFILE
ENV SPRING_PROFILE_ACTIVE=${PROFILE}

ARG JAR=./bootstrap/bootstrap-gateway/build/libs/bootstrap-gateway-0.0.1-SNAPSHOT.jar
COPY ${JAR} app.jar

ENTRYPOINT ["java", "-Dspring.profiles.active=${SPRING_PROFILE_ACTIVE}", "-jar", "/app.jar"]
