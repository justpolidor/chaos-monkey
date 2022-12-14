ARG BUILD_IMAGE=maven:3.8.3-eclipse-temurin-17
ARG RUNTIME_IMAGE=gcr.io/distroless/java17-debian11

FROM ${BUILD_IMAGE} as builder
WORKDIR /output
COPY pom.xml .
COPY src /output/src
RUN mvn clean package -DskipTests
RUN java -Djarmode=layertools -jar /output/target/app.jar extract

FROM ${RUNTIME_IMAGE} as final
WORKDIR /application
COPY --from=builder /output/dependencies/ ./
COPY --from=builder /output/spring-boot-loader/ ./
COPY --from=builder /output/snapshot-dependencies/ ./
COPY --from=builder /output/application/ ./
USER nonroot

ENTRYPOINT ["java","-Dfile.encoding=UTF-8","-Dspring.config.additional-location=/config/","org.springframework.boot.loader.JarLauncher"]
