# ----------
# Dockerfile for product-service
# ----------

# ----
# BUILD Stage
# ----

#Base image
FROM gradle:8-jdk19-alpine AS builder

#Copy the application files from the host machine to the image filesystem
COPY --chown=gradle:gradle . /home/gradle/src

#Set the directory within the image, for executing future commands
WORKDIR /home/gradle/src

#Run grade build within the container - NOT the host machine
RUN gradle build -x test

# ----------
# Dockerfile for product-service
# ----------

FROM openjdk:19-alpine

RUN mkdir /app

COPY --from=builder /home/gradle/src/build/libs/*.jar /app/order-service.jar

ENV POSTGRES_USERNAME=admin \
    POSTGRES_PASSWORD=password

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "/app/order-service.jar"]