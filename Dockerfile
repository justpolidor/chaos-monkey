ARG BUILD_IMAGE=fra.ocir.io/sisalspa/base-image/maven:release-3.8.3-eclipse-temurin-17
ARG RUNTIME_IMAGE=fra.ocir.io/sisalspa/base-image/distroless-java:release-17

FROM ${BUILD_IMAGE} as builder
WORKDIR /output
COPY pom.xml .
COPY src /output/src
RUN mvn clean package
RUN java -Djarmode=layertools -jar /output/target/app.jar extract

FROM ${RUNTIME_IMAGE} as final
WORKDIR /application
COPY --from=builder /output/dependencies/ ./
COPY --from=builder /output/spring-boot-loader/ ./
COPY --from=builder /output/snapshot-dependencies/ ./
COPY --from=builder /output/application/ ./

ENTRYPOINT ["java","-Dfile.encoding=UTF-8","-Dspring.config.additional-location=/config/","org.springframework.boot.loader.JarLauncher"]
