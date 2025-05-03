# 🎵 Vinyl Store Backend

Це бекенд для інтернет-магазину вінілових платівок, написаний на Java 24 + Spring Boot.

## 🔧 Технології

- Java 21+ (Amazon Corretto)
- Spring Boot 3+
- PostgreSQL
- Spring Security + JWT (access & refresh токени)
- Apache POI (імпорт з Excel)
- Docker + Docker Compose
- Email розсилка через Brevo
- Swagger/OpenAPI
- JUnit & Mockito

## 📦 Функціонал

- 📜 Реєстрація/логін з токенами
- 👤 Управління користувачами (адмін)
- 🎶 Додавання/перегляд/імпорт вінілових платівок
- ✉️ Email-розсилка
- 🔐 Авторизація: USER/ADMIN
- ⚙️ Dockerized

## 🚀 Запуск

```bash
docker-compose up --build
