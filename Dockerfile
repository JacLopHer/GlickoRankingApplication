# Usa una imagen base de Maven
FROM maven:3.8.6-openjdk-11

# Define el directorio de trabajo
WORKDIR /app

# Copia los archivos del proyecto al contenedor
COPY . .

# Ejecuta el comando de Maven para construir el proyecto
RUN mvn clean package -DskipTests

# Expón el puerto que tu aplicación usará (ajústalo si es necesario)
EXPOSE 8080

# Define el comando que ejecutará la aplicación
CMD ["java", "-jar", "target/GlickoRankingApplication-0.0.1-SNAPSHOT.jar"]
