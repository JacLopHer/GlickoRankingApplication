# Usa una imagen base de Maven con OpenJDK 11
FROM maven:3.8.6-openjdk-11 AS build

# Define el directorio de trabajo
WORKDIR /app

# Copia el archivo pom.xml y descarga las dependencias para aprovechar la cache de Docker
COPY pom.xml .
RUN mvn dependency:go-offline

# Copia el resto de los archivos del proyecto
COPY . .

# Ejecuta el comando de Maven para construir el proyecto (sin tests)
RUN mvn clean package -DskipTests

# Usar una imagen base más ligera para ejecutar la app
FROM openjdk:11-jre-slim

# Define el directorio de trabajo en el contenedor
WORKDIR /app

# Copia el archivo JAR generado desde el contenedor de construcción
COPY --from=build /app/target/GlickoRankingApplication-0.0.1-SNAPSHOT.jar /app/

# Expón el puerto en el que la aplicación escuchará
EXPOSE 8080

# Define el comando para ejecutar la aplicación
CMD ["java", "-jar", "GlickoRankingApplication-0.0.1-SNAPSHOT.jar"]
