FROM java:openjdk-8-jre
MAINTAINER  Miroslav Veith <mveith@hotmail.cz>

ADD ./target/ app/			
EXPOSE 10555
WORKDIR ./app/
ENTRYPOINT ["java", "-jar", "/app/sparta-calendar.jar"]