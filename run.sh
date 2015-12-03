#!/bin/sh
mkdir -p ./src/main/resources/static
cp ./src/main/js/html/* ./src/main/resources/static
mvn clean spring-boot:run -Djava.security.egd=file:/dev/./urandom -Dspring.profiles.active=dev
