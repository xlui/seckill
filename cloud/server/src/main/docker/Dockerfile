FROM openjdk:8-jdk-alpine
VOLUME /tmp
ADD server-0.0.1.jar server.jar
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/server.jar"]
