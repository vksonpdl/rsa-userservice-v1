FROM openjdk:8-jdk-alpine
EXPOSE 8080
ARG JAR_FILE=target/rsa-userservice-v1-0.0.1-SNAPSHOT.jar
ADD ${JAR_FILE} app.jar
ENTRYPOINT exec java -jar /app.jar