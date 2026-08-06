# =====================================================
# SkyPeak Hotel - Dockerfile
# =====================================================

# Многоступенчатая сборка для оптимизации размера образа

# Стадия 1: Сборка приложения
FROM maven:3.9.9-eclipse-temurin-21 AS build

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем файлы проекта
COPY pom.xml .
COPY src ./src

# Собираем приложение (пропускаем тесты для скорости сборки)
RUN mvn clean package -DskipTests

# Стадия 2: Запуск приложения
FROM eclipse-temurin:21-jdk-jammy

# Устанавливаем рабочую директорию
WORKDIR /app

# Создаем пользователя для безопасности
RUN groupadd -r skypeak && useradd -r -g skypeak skypeak

# Копируем JAR файл из стадии сборки
COPY --from=build /app/target/hotel-0.0.1-SNAPSHOT.jar app.jar

# Меняем владельца файлов
RUN chown -R skypeak:skypeak /app

# Переключаемся на непривилегированного пользователя
USER skypeak

# Указываем порт
EXPOSE 8080

# Команда запуска
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

# Метаданные
LABEL maintainer="SkyPeak Hotel Team"
LABEL description="SkyPeak Hotel Management System"
LABEL version="0.0.1-SNAPSHOT"
