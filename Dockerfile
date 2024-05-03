FROM amazoncorretto:17

ARG PROFILE
ENV SPRING_PROFILE_ACTIVE=${PROFILE}

ARG JAR=./build/libs/*-SNAPSHOT.jar
COPY ${JAR} app.jar

ENTRYPOINT ["java", "-Dspring.profiles.active=${SPRING_PROFILE_ACTICE}", "-jar", "/app.jar"]
