# 🏨 SkyPeak Hotel - Pet Project

Полнофункциональное веб-приложение для управления отелем с REST API backend'ом. Система позволяет управлять комнатами, бронированиями, пользователями и их балансом.

## 📋 Содержание

- [О проекте](#-о-проекте)
- [Используемые технологии](#-используемые-технологии)
- [Архитектура](#-архитектура)
- [Установка и запуск](#-установка-и-запуск)
- [Конфигурация](#-конфигурация)
- [API Endpoints](#-api-endpoints)
- [Функциональность](#-функциональность)
- [В планах](#-в-планах)
- [Структура проекта](#-структура-проекта)
- [От автора](#-от-автора)

## 🎯 О проекте

**SkyPeak Hotel** - это проект (pet-project) на Spring Boot, созданный для демонстрации:
- Построения REST API с использованием Spring MVC
- Аутентификации и авторизации через JWT токены
- Работы с базой данных PostgreSQL через Spring Data JPA
- Управления миграциями БД через Liquibase
- Использования контейнеризации Docker

## 🛠️ Используемые технологии

### Backend
- **Framework**: Spring Boot 4.0.1
- **Java**: JDK 25
- **Database**: PostgreSQL
- **ORM**: Spring Data JPA (Hibernate)
- **Migration**: Liquibase
- **Authentication**: JWT (jjwt-0.12.5)
- **Security**: Spring Security
- **Validation**: Spring Validation
- **Build Tool**: Maven
- **Additional Libraries**:
  - Lombok (для boilerplate code)
  - MapStruct (для маппинга объектов)

### DevOps
- **Containerization**: Docker & Docker Compose
- **Database**: PostgreSQL in Docker

## 🏗️ Архитектура

### Backend архитектура (Layered Architecture)

```
Controller Layer (REST endpoints)
    ↓
Service Layer (Business Logic)
    ↓
Repository Layer (Data Access)
    ↓
Entity Layer (JPA entities)
    ↓
Database (PostgreSQL)
```

### Основные компоненты

**Controllers** (`controller/`):
- `AuthController` - аутентификация и регистрация
- `RoomController` - просмотр доступных комнат
- `RoomManagementController` - управление комнатами (админ функции)
- `BookingController` - создание и управление бронированиями
- `BalanceController` - управление балансом пользователя

**Entities** (`entity/`):
- `UserEntity` - информация о пользователе
- `RoomEntity` - информация о комнате
- `BookingEntity` - данные о бронировании
- `UserBalanceEntity` - баланс пользователя
- `BalanceTransactionEntity` - история транзакций
- `RoleEntity` - роли пользователей

**Services** (`service/`):
- Бизнес-логика для всех операций
- Валидация данных
- Трансформация моделей

**Repositories** (`repository/`):
- Spring Data JPA interfaces
- CRUD операции и кастомные запросы

**Security** (`security/`):
- JWT токен провайдер
- Фильтры для аутентификации
- Конфигурация безопасности

**DTO** (`dto/`):
- Transfer objects для API запросов/ответов
- Разделены по функциям (auth/, room/, booking/, balance/)

## 📦 Установка и запуск

### Требования
- JDK 25+
- Maven 3.8+
- Docker и Docker Compose (опционально)
- PostgreSQL 12+ (можно запустить в Docker)

### Быстрый старт с Docker

1. **Клонируйте репозиторий**
```bash
git clone <repository-url>
cd hotel
```

2. **Запустите весь стек через Docker Compose**
```bash
docker-compose up -d
```

Это запустит:
- PostgreSQL на `localhost:5432`
- Spring Boot API на `localhost:8080`

3. **Проверьте статус**
```bash
docker-compose ps
```

### Ручной запуск

#### Backend

1. **Установите переменные окружения**
```bash
export DB_URL=jdbc:postgresql://localhost:5432/skypeak_hotel
export DB_USERNAME=postgres
export DB_PASSWORD=postgres
export ADMIN_EMAIL=admin@skypeak.com
export ADMIN_PASSWORD=admin123
export ADMIN_STATUS=ACTIVE
export JWT_TOKEN=your-secret-jwt-token-here
```

2. **Соберите и запустите**
```bash
mvn clean install
mvn spring-boot:run
```

API будет доступен на `http://localhost:8080`

## ⚙️ Конфигурация

### Backend конфигурация (`application.properties`)

```properties
# Database
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

# Admin user
admin.email=${ADMIN_EMAIL}
admin.password=${ADMIN_PASSWORD}
admin.status=${ADMIN_STATUS}

# JWT
jwt.secret=${JWT_TOKEN}
jwt.expiration-ms=3600000

# Роли
role.admin=ADMIN
role.manager=MANAGER
role.user=USER
```

### Environment переменные

Для локальной разработки создайте файл `.env`:

```bash
DB_URL=jdbc:postgresql://localhost:5432/skypeak_hotel
DB_USERNAME=postgres
DB_PASSWORD=postgres
ADMIN_EMAIL=admin@skypeak.com
ADMIN_PASSWORD=admin123
ADMIN_STATUS=ACTIVE
JWT_TOKEN=your-super-secret-jwt-token-minimum-256-bits-long
```

## 🔌 API Endpoints

### Authentication (`/api/auth`)
- `POST /auth/register` - Регистрация нового пользователя
- `POST /auth/login` - Вход в систему

### Rooms (`/api/rooms`)
- `GET /api/rooms` - Получить все комнаты

### Bookings (`/api/bookings`)
- `POST /api/bookings` - Создать новое бронирование
- `GET /api/bookings/my` - Получить мои бронирования
- `DELELE /api/bookings/{id}` - Отменить бронирование

### Room Management (Admin/Manager) (`/api/management/rooms`)
- `POST /api/management/rooms` - Создать новую комнату
- `PUT /api/management/rooms/{id}` - Обновить информацию о комнате
- `PATCH /api/management/rooms/{id}/deactivate` - Деактивировать комнату

### Balance (`/api/balance`)
- `GET /api/balance` - Получить баланс пользователя
- `POST /api/balance/deposit` - Пополнить баланс
- `GET /api/balance/transactions` - История транзакций

## ✅ Функциональность

### Реализованное ✅
- ✅ Аутентификация и авторизация через JWT
- ✅ Управление пользователями (регистрация, вход)
- ✅ Система ролей (ADMIN, MANAGER, USER)
- ✅ CRUD операции для комнат
- ✅ Система бронирований
- ✅ Управление балансом пользователей
- ✅ История транзакций
- ✅ Валидация данных
- ✅ Обработка ошибок
- ✅ Миграции БД через Liquibase
- ✅ REST API
- ✅ Docker контейнеризация

## 📋 В планах

#### Backend
- [ ] Unit и Integration тесты
- [ ] Logging и мониторинг
- [ ] Swagger/OpenAPI документация для API
- [ ] Регистрация и вход по Email и паролю
- [ ] Расширенная фильтрация и поиск бронирований
- [ ] Email уведомления для подтверждения бронирования

#### Frontend
- [ ] Полная реализация всех страниц
- [ ] Поиск и фильтр комнат
- [ ] Календарь для выбора дат бронирования
- [ ] Личный кабинет пользователя
- [ ] Панель администратора

#### DevOps
- [ ] Nginx reverse proxy
- [ ] SSL/TLS сертификаты

## 📁 Структура проекта

```
hotel/
├── docker/                          # Docker конфигурация
│   └── docker-compose.yaml
├── src/
│   ├── main/
│   │   ├── java/com/skypeak/hotel/
│   │   │   ├── config/              # Конфигурация приложения
│   │   │   ├── controller/          # REST контроллеры
│   │   │   ├── dto/                 # Data transfer objects
│   │   │   ├── entity/              # JPA сущности
│   │   │   ├── exception/           # Кастомные исключения
│   │   │   ├── mapper/              # Маппинг объектов
│   │   │   ├── repository/          # Data access layer
│   │   │   ├── security/            # JWT и безопасность
│   │   │   ├── service/             # Сервисы (бизнес логика)
│   │   │   └── SkyPeakHotelApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── db/changelog/        # Liquibase миграции
│   └── test/                        # Тесты
├── pom.xml                          # Maven конфигурация
└── README.md                        # Этот файл
```

## 👨‍💻 От автора

**SkyPeak Hotel** - это личный проект, который может быть расширен и улучшен в будущем. Цель - показать навыки разработки полнофункционального веб-приложения с использованием современных технологий и лучших практик.

---

