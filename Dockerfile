FROM openjdk:17-slim

WORKDIR /app

COPY target/homework-0.0.1-SNAPSHOT.jar /app/app.jar

COPY input/OUTPH_CUP_20200204_1829.TXT /app/input/OUTPH_CUP_20200204_1829.TXT
COPY input/CUSTCOMP01.txt /app/input/CUSTCOMP01.txt
COPY input/ZTPSPF.txt /app/input/ZTPSPF.txt

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]