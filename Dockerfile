FROM gradle:8.4.0-jdk21-alpine AS build
ARG JAR_NAME
WORKDIR /usr/app/
COPY . .
RUN gradle bootJar -x test

# Package stage

FROM build
ENV APP_HOME=/usr/app/
WORKDIR $APP_HOME
COPY --from=build $APP_HOME/build/libs/$JAR_NAME ./app.jar
RUN ls -lah
ENTRYPOINT ["java", "-jar", "app.jar"]