# =====================================================
# SkyPeak Hotel - Dockerfile
# =====================================================

# Многоступенчатая сборка для оптимизации размера образа

# Стадия 1: Сборка приложения
FROM maven:3.9.9-eclipse-temurin-21 AS build

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем pom отдельно для кэширования зависимостей
COPY pom.xml .
RUN mvn dependency:go-offline

# Копируем исходники
COPY src ./src

# Собираем приложение (пропускаем тесты для скорости сборки)
RUN mvn clean package -DskipTests

# Стадия 2: Запуск приложения
FROM eclipse-temurin:21-jre-jammy

# Устанавливаем рабочую директорию
WORKDIR /app

# Создаем пользователя для безопасности
RUN groupadd -r skypeak && useradd -r -g skypeak skypeak

# Копируем JAR файл из стадии сборки
COPY --from=build /app/target/*.jar app.jar

# Меняем владельца файлов
RUN chown -R skypeak:skypeak /app

# Переключаемся на непривилегированного пользователя
USER skypeak

# Указываем порт
EXPOSE 8080

# Команда запуска
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

# Метаданные
LABEL maintainer="Dmitry Eltsov"
LABEL description="SkyPeak Hotel Backend"
LABEL version="1.0.0"
