FROM adoptopenjdk/openjdk11:alpine-jre
VOLUME /tmp
COPY target/learntribe-inquisitve-*.jar learntribe-inquisitve.jar
ENTRYPOINT ["java","-jar","learntribe-inquisitve.jar"]