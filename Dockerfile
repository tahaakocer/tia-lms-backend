FROM alpine/java:17-jdk
# Uygulamanın JAR dosyasını kopyala
VOLUME /tmp
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java","-jar","/app.jar"]
