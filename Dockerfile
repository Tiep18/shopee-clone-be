FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY .mvn/ .mvn
COPY mvnw pom.xml ./

RUN ./mvnw dependency:resolve

COPY src ./src

#COPY ./target/myspringapp.jar ./

EXPOSE 8080

CMD ["./mvnw", "spring-boot:run"]
#ENTRYPOINT ["java", "-jar", "myspringapp.jar"]