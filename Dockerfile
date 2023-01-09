FROM adoptopenjdk/openjdk11:alpine-jre
VOLUME /tmp
COPY target/learntribe-assessment-reactor-*.jar learntribe-assessment-reactor.jar
ENTRYPOINT ["java","-jar","learntribe-assessment-reactor.jar"]