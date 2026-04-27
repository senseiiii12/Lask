# Lask

Lask — это Android-приложение для чтения и исследования новостей с акцентом на персонализацию, современный UI и модульную архитектуру.

## 🚀 Стек технологий

### Язык и платформа
- **Kotlin**
- **Android (minSdk 26, targetSdk 36)**

### UI
- **Jetpack Compose**
- **Material 3**
- **Navigation Compose**

### Архитектура и состояние
- **Модульная feature/core архитектура**
- **MVI-подход** во feature-модулях (State / Intent / SideEffect / ViewModel)
- **Repository + UseCase / Interactor**

### DI
- **Koin**

### Данные и сеть
- **Ktor Client** (сериализация, логирование, ContentNegotiation)
- **Kotlinx Serialization**
- **Room** (локальный кэш и состояние пользователя)
- **DataStore** (пользовательские настройки)

### Медиа и изображения
- **Coil 3**
- **Media3 (ExoPlayer)**

### Асинхронность и утилиты
- **Kotlin Coroutines / Flow**
- **kotlinx-datetime**

### Производительность и тесты
- **Unit tests / Instrumented tests**
- **Macrobenchmark**

---

## ✨ Ключевые фичи

- **News Feed** — основной поток новостей и трендов.
- **Article Detail** — экран статьи с чтением, переводом, лайками (clap), закладками и шарингом.
- **Explore** — исследование контента по интересам.
- **Search** — поиск с фильтрами (категории, язык, дата и др.).
- **Bookmarks** — сохранённые статьи.
- **Profile** — профиль, интересы, выбор локали и системные настройки.
- **Welcome / Splash / Theme switch** — onboarding и базовый UX-каркас приложения.

---

## 🧩 Архитектура проекта

Проект разделён на независимые gradle-модули:

```text
:lask                     # App module (точка входа)
:core:*                   # Общие инфраструктурные модули
  :core:network
  :core:database
  :core:datastore
  :core:models
  :core:ui
  :core:navigation
  :core:utils
  :core:platform

:feature:*                # Бизнес-фичи
  :feature:<name>:api     # Контракты навигации / публичный API фичи
  :feature:<name>:impl    # Реализация UI, domain, data

:macrobenchmark           # Бенчмарки запуска/производительности
```
