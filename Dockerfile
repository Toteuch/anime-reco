FROM bitnami/java:latest
LABEL maintainer=toteuch.com
RUN ln -sf /usr/share/zoneinfo/Europe/Paris /etc/localtime
COPY target/anime-reco-*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]