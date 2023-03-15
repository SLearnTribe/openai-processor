FROM adoptopenjdk/openjdk11:alpine-jre
VOLUME /tmp
COPY target/classes/security.crt security.crt
RUN keytool -importcert -file security.crt -cacerts -storepass changeit -noprompt -alias smilebat
COPY target/openai-processor-*.jar openai-processor.jar
ENTRYPOINT ["java","-jar","openai-processor.jar"]