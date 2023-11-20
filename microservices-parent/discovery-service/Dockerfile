# -----------------------
# Dockerfile for discovery-service
#------------------------

#----
# BUILD stage
#--
FROM gradle:8-jdk19-alpine AS builder

# Copy the application files from the host machine to the image filesystem
COPY --chown=gradle:gradle . /home/gradle/src

# set the directory within the image, for executing future commands
WORKDIR /home/gradle/src

# run gradle build within the container - not the host machine
RUN gradle build -x test

#----
# Package stage
#--
FROM openjdk:19-alpine

RUN mkdir /app

COPY --from=builder /home/gradle/src/build/libs/*.jar /app/discovery-service.jar

EXPOSE 8761

ENTRYPOINT ["java", "-jar", "/app/discovery-service.jar"]
