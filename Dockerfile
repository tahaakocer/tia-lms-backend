FROM alpine/java:17-jdk
# Uygulamanın JAR dosyasını kopyala
VOLUME /tmp
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
COPY src/main/resources/wsdl /app/resources/wsdl
# Çalışma zamanı komutunu belirle
ENTRYPOINT ["java","-jar","/app.jar"]
