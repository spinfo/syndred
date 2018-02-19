FROM alpine:latest as build
WORKDIR /tmp/syndred
RUN \
    apk --no-cache add git maven nodejs-npm openjdk8 && \
    git clone https://github.com/spinfo/syndred . && \
    npm install && \
    npm run package && \
    mvn package

FROM alpine:latest
RUN apk --no-cache add openjdk8-jre
EXPOSE 8080
WORKDIR /srv
COPY --from=build /tmp/syndred/target/*.jar syndred.jar
CMD java -jar /srv/syndred.jar