FROM gradle:8.4.0-jdk21-alpine AS BUILD
WORKDIR /usr/app/
COPY . .
RUN gradle build -x test
RUN export JAR_NAME=$(find build/libs/*.jar)

# Package stage

FROM openjdk:21-ea-1-jdk-slim-buster
#ENV JAR_NAME=fileprocessor-0.0.1-SNAPSHOT.jar
ENV APP_HOME=/usr/app/
WORKDIR $APP_HOME
COPY --from=BUILD $APP_HOME/$JAR_NAME .
EXPOSE 8080
ENTRYPOINT exec java -jar $JAR_NAME