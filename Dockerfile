FROM adoptopenjdk/openjdk11:alpine-jre
VOLUME /tmp
COPY target/openai-processor-*.jar openai-processor.jar
ENTRYPOINT ["java","-jar","openai-processor.jar"]