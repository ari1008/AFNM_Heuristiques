FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY backend/. .
RUN mvn package -DskipTests -Dquarkus.package.type=fast-jar

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/quarkus-app/lib/ /app/lib/
COPY --from=build /app/target/quarkus-app/*.jar /app/
COPY --from=build /app/target/quarkus-app/app/ /app/app/
COPY --from=build /app/target/quarkus-app/quarkus/ /app/quarkus/

EXPOSE 8080
ENV JAVA_OPTS="-Dquarkus.http.host=0.0.0.0"
ENV JAVA_APP_JAR="quarkus-run.jar"
ENTRYPOINT ["java", "-jar", "/app/quarkus-run.jar"]
