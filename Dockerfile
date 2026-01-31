FROM maven:3-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml .
COPY src ./src
COPY header.txt ./header.txt
RUN mvn -Pserver -DskipTests package

FROM eclipse-temurin:21.0.9_10-jre
WORKDIR /app
COPY --from=build /workspace/target/*-jar-with-dependencies.jar /app/server.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/server.jar"]
CMD ["/app/server_config.json"]
