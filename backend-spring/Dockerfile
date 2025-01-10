FROM openjdk:17-jdk-slim

WORKDIR /app

# Installation de Maven
RUN apt-get update && \
    apt-get install -y maven

EXPOSE 8099

CMD ["./mvnw", "spring-boot:run"]