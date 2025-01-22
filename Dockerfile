FROM bitnami/java:latest
LABEL maintainer=toteuch.com
RUN ln -sf /usr/share/zoneinfo/Europe/Paris /etc/localtime
COPY target/anime-reco-0.1.2.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]