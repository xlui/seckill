FROM openjdk:8-jdk-alpine
VOLUME /tmp
ADD service-0.0.1.jar service.jar
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/service.jar"]
