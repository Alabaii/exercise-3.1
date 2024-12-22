# Используем официальный образ Maven с JDK
FROM maven:3.9.9-eclipse-temurin-21 AS build-stage

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем файлы проекта
COPY . .

# Предварительно загружаем зависимости
EXPOSE 40457
RUN mvn dependency:resolve

# Собираем проект и запускаем тесты
RUN mvn clean test -Dmaven.test.failure.ignore=true -Dallure.results.directory=target/allure-results

# Создаем образ для отчета
FROM openjdk:21-jdk-slim

# Устанавливаем Allure CLI
RUN apt-get update && apt-get install -y wget unzip && \
    wget -qO allure.tgz https://github.com/allure-framework/allure2/releases/download/2.20.1/allure-2.20.1.tgz && \
    tar -xzf allure.tgz && mv allure-2.20.1 /opt/allure && ln -s /opt/allure/bin/allure /usr/local/bin/allure

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем результаты тестов из предыдущего этапа
COPY --from=build-stage /app/target/allure-results /app/allure-results

# Открываем порт для Allure
EXPOSE 40457

# Запускаем Allure Report на фиксированном порту 40457
ENTRYPOINT ["allure", "serve", "/app/allure-results", "--port", "40457"]
