#Smaller images and faster setup
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

#Copying Jar dynamivally (No hardcoding)
#Work with any target no CI/CD Break
COPY target/*.jar app.jar

#JVM Mmeory Tuning (MEMORY )
#Memory Control initial and max memory specifying
ENV JAVA_OPTS="-Xms128m -Xmx512m"

EXPOSE 8080

ENTRYPOINT ["sh","-c","java ${JAVA_OPTS} -jar /app/app.jar"]