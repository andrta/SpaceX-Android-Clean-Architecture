# SpaceX Android Clean Architecture

An Android application demonstrating modern development practices, built with a focus on scalability, maintainability, and testability. This project showcases the implementation of **Clean Architecture**, **MVI (Model-View-Intent)**, and **Offline-First** strategies.

## 🚀 Key Features

- **Hybrid Networking:** Demonstrates proficiency in both **GraphQL (Apollo)** and **REST (Retrofit)** to consume the SpaceX API.
- **Modern UI Stack:** Implementation of **Jetpack Compose** alongside traditional **XML / View Binding**, showcasing the transition to modern declarative UI.
- **Offline-First:** Robust local data persistence with Room database.
- **MVI Pattern:** Unidirectional Data Flow (UDF) for predictable UI state management.
- **Multi-module Architecture:** Clean separation of concerns through specialized modules.
- **Jetpack Libraries:** Hilt for DI, Room for Persistence, Coroutines & Flow for Asynchronicity.
- **Testing:** Comprehensive Unit Testing suite with Turbine and Mockk.

---

## 🏛 Architecture Overview

The project follows the principles of **Clean Architecture** combined with a **Modular** approach.

### 📦 Module Structure

- **`:app`**: The entry point of the application. Handles high-level configuration and dependency graph initialization.
- **`:features`**: Contains UI-related modules.
    - **`:features:launches`**: Launch list and detail screens implemented with a mix of XML and Compose.
- **`:core`**: Core infrastructure and shared logic.
    - **`:core:domain`**: Pure Kotlin module containing Business Logic, Models, and Use Case definitions.
    - **`:core:data`**: Implementation of repositories and data sources. Handles both **GraphQL** and **REST** service implementations.
    - **`:core:localstorage`**: Room Database implementation, DAOs, and Migrations.
    - **`:core:testing`**: Shared testing utilities and fakes.

### 🎨 Presentation Layer (MVI & UI)

The presentation layer showcases a dual approach to UI development:
- **MVI Pattern**: Used across all screens to ensure a single source of truth for the UI state.
- **Jetpack Compose**: Used for modern, reactive UI components.
- **XML / View Binding**: Maintained to demonstrate interoperability and expertise in legacy view systems.

### 💾 Data Layer (Repository Pattern & SSOT)

The **Repository Pattern** is used to abstract the data sources. The app follows a **Single Source of Truth (SSOT)** strategy where the Local Storage (Room) acts as the primary data source for the UI, while the remote sources (REST or GraphQL) update the local database.

---

## 🛠 Tech Stack

- **Language**: Kotlin
- **Dependency Injection**: Hilt
- **Asynchronous Flow**: Coroutines & Flow
- **Networking**: Apollo GraphQL & Retrofit (REST)
- **Local Database**: Room (with Migrations)
- **UI Stack**: Jetpack Compose & XML (View Binding)
- **Testing**: JUnit 4, Turbine (for Flow testing), Mockk

---

## 🧪 Testing Strategy

Quality is a core pillar of this project. The testing strategy includes:
- **Unit Tests**: Business logic in Use Cases and State management in ViewModels are strictly tested.
- **Flow Testing**: `Turbine` is used to verify Flow emissions in a concise way.
- **Data Testing**: Ensuring correct mapping and persistence logic for both REST and GraphQL data models.
- **(Coming Soon)**: End-to-End (E2E) tests using Espresso/Kaspresso.

---

## ⚙️ Getting Started

1. Clone the repository.
2. Open the project in Android Studio (Ladybug or newer).
3. Build and run the `:app` module.

```bash
git clone https://github.com/yourusername/SpaceX-Android-Clean-Architecture.git
```

---

## 🛡 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
