# base build image
FROM maven:3.5-jdk-8 as maven

# copy the project files
COPY ./pom.xml ./pom.xml

# build all dependencies
RUN mvn dependency:go-offline --batch-mode

# copy source files
COPY ./src ./src

# build for release
RUN mvn clean package --batch-mode

# final base image
FROM openjdk:8-jre-alpine

RUN mkdir -p /opt/stellar-notifier/

COPY --from=maven target/stellar-notifier.jar /opt/stellar-notifier

RUN wget -O /bin/smell-baron https://github.com/ohjames/smell-baron/releases/download/v0.4.2/smell-baron.musl && chmod a+x /bin/smell-baron
ENTRYPOINT ["/bin/smell-baron"]

CMD ["java", "-jar", "/opt/stellar-notifier/stellar-notifier.jar"]
