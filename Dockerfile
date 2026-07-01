# Étape de construction (Build)
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copier le fichier pom.xml et télécharger les dépendances (cache Docker)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copier le code source et compiler
COPY src ./src
RUN mvn clean package -DskipTests

# Étape d'exécution (Run)
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copier le .jar compilé depuis l'étape précédente
COPY --from=build /app/target/java-banque-api-1.0-SNAPSHOT.jar app.jar

# Exposer le port (Render par défaut = 8080)
EXPOSE 8080

# Commande de lancement
ENTRYPOINT ["java", "-jar", "app.jar"]
