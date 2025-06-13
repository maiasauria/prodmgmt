# Descarga la imagen base de Eclipse Temurin JDK 17
FROM eclipse-temurin:17.0.15_6-jdk

# Puerto en el que la aplicación escuchará. Es solo informativo, expone ese puerto.
EXPOSE 8080

# Establece el directorio de trabajo dentro del contenedor
WORKDIR /root

# Copia el Pom y MVM
COPY ./pom.xml /root/
COPY ./.mvn /root/.mvn
COPY ./mvnw /root/

# Descarga las dependencias del proyecto
RUN ./mvnw dependency:go-offline

# Copia el código fuente del proyecto al contenedor
COPY ./src /root/src

# Compila el proyecto y empaqueta la aplicación
RUN ./mvnw clean install -DskipTests

# Levanta la aplicación
ENTRYPOINT ["java", "-jar", "/root/target/prodmgmt-0.0.1-SNAPSHOT.jar"]