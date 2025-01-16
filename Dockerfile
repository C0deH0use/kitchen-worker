FROM gradle:8.4.0-jdk21 AS build-stage

WORKDIR /home/gradle
COPY . .

RUN gradle clean build -x integrationTest --no-daemon

FROM azul/zulu-openjdk:21

COPY --from=build-stage /home/gradle/build/libs/worker-*.jar /worker.jar


ENTRYPOINT ["java","-jar", "/worker.jar" ]
HEALTHCHECK CMD curl --fail http://localhost:8080/actuator/health || exit