FROM java:8-jdk
MAINTAINER James Fairbairn <jaybono30@hotmail.com>

ENV JAVA_HOME              /usr/lib/jvm/java-8-openjdk-amd64
ENV JAVA_OPTS              ""
ENV PATH                   $PATH:$JAVA_HOME/bin

ENV SPRING_PROFILES_ACTIVE prod
ENV SERVER_PORT 1112
ENV CONFIG_URL http://10.52.180.42:1111/config-api/

WORKDIR /app

RUN apt-get update && apt-get install -y wget
RUN wget https://github.com/jwilder/dockerize/releases/download/v0.1.0/dockerize-linux-amd64-v0.1.0.tar.gz
RUN tar -C /usr/local/bin -xzvf dockerize-linux-amd64-v0.1.0.tar.gz

EXPOSE $SERVER_PORT

ADD @executable.server.jar.name@.jar app.jar

CMD ["/bin/sh", "-c", "java $JAVA_OPTS -jar app.jar --server.port=$SERVER_PORT --spring.profiles.active=$SPRING_PROFILES_ACTIVE --spring.cloud.config.uri=$CONFIG_URL"]