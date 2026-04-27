###LASK

# Lask is an Android app for reading and exploring news with a focus on personalization, a modern UI, and modular architecture.

## 🚀 Tech Stack

### Language and Platform
- **Kotlin**
- **Android (minSdk 26, targetSdk 36)**

### UI
- **Jetpack Compose**
- **Material 3**
- **Navigation Compose**

### Architecture and State
- **Modular Feature/Core Architecture**
- **MVI Approach**
- **Repository + UseCase**

### DI
- **Koin**

### Data and Network
- **Ktor Client**
- **Kotlinx Serialization**
- **Room** (local cache and user state)
- **DataStore** (user settings)

### Media and Images
- **Coil 3**
- **Media3 (ExoPlayer)**

### Asynchrony and Utilities
- **Kotlin Coroutines / Flow**

### API
- **https://worldnewsapi.com/**

---

## ✨ Key Features

- **News Feed** — main feed of news and trends.
- **Article Detail** — article screen with reading, translation, clapping, bookmarking, and sharing options.
- **Explore** — content exploration by interests.
- **Search** — search with filters (categories, language, date, etc.).
- **Bookmarks** — saved articles.
- **Profile** — profile, interests, locale selection, and system settings.
- **Welcome / Splash / Theme Switch** — onboarding and basic UX framework for the app.

---

## 🧩 Project Architecture

The project is divided into independent Gradle modules:

```text
:lask # App module (entry point)
:core:* # Common infrastructure modules
:core:network
:core:database
:core:datastore
:core:models
:core:ui
:core:navigation
:core:utils
:core:platform

:feature:* # Business features
:feature:<name>:api # Navigation contracts / public API features
:feature:<name>:impl # UI, domain, and data implementation

```

## 📱 Screenshots

<p align="center">
  <img src="docs/images/onboarding.png" width="250"/>
  <img src="docs/images/trends.png" width="250"/>
  <img src="docs/images/article_detail.png" width="250"/>
</p>

<p align="center">
  <img src="docs/images/bookmark.png" width="250"/>
  <img src="docs/images/interests.png" width="250"/>
  <img src="docs/images/explore.png" width="250"/>
</p>

<p align="center">
  <img src="docs/images/search.png" width="250"/>
  <img src="docs/images/profile.png" width="250"/>
  <img src="docs/images/locale.png" width="250"/>
  <img src="docs/images/system_settings.png" width="250"/>
</p>
