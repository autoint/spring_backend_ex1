FROM maven:3.6.0-jdk-8-slim as maven-builder

COPY . .

RUN mvn package -DskipTests

FROM java:8

RUN mkdir /var/www && chmod o+w /var/www && mkdir /var/www/uploads

COPY --from=maven-builder target/demo-0.0.1-SNAPSHOT.jar /var/www
COPY src/main/resources/uploads /var/www/uploads/.

USER www-data
WORKDIR /var/www

ENTRYPOINT java -jar demo-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
