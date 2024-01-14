FROM openjdk:17-jdk
VOLUME /redis-service
ADD build/libs/myapp.jar redis-service.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "redis-service.jar"]