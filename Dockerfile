FROM openjdk:17-slim

WORKDIR /app

COPY target/homework-0.0.1-SNAPSHOT.jar homework-0.0.1-SNAPSHOT.jar

EXPOSE 8080

CMD ["java", "-jar", "homework-0.0.1-SNAPSHOT.jar"]