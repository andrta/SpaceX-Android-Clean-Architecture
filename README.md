# SpaceX Android Clean Architecture

An Android application demonstrating modern development practices, built with a focus on scalability, maintainability, and testability. This project showcases the implementation of **Clean Architecture**, **MVI (Model-View-Intent)**, and **Offline-First** strategies using the SpaceX GraphQL API.

## 🚀 Key Features

- **GraphQL Integration:** Consumes SpaceX API using Apollo Kotlin.
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
    - **`:features:launches`**: Launch list and detail screens.
- **`:core`**: Core infrastructure and shared logic.
    - **`:core:domain`**: Pure Kotlin module containing Business Logic, Models, and Use Case definitions.
    - **`:core:data`**: Implementation of repositories and data sources (Remote & Local).
    - **`:core:localstorage`**: Room Database implementation, DAOs, and Migrations.
    - **`:core:testing`**: Shared testing utilities and fakes.

### 🎨 Presentation Layer (MVI)

Each feature uses the **MVI (Model-View-Intent)** pattern:
- **State**: A single source of truth for the UI state.
- **Intent**: Represents user actions or system events.
- **Effect**: Side effects like navigation, showing toasts, or one-time events.

### 💾 Data Layer (Repository Pattern & SSOT)

The **Repository Pattern** is used to abstract the data sources. The app follows a **Single Source of Truth (SSOT)** strategy where the Local Storage (Room) acts as the primary data source for the UI, while the remote source (GraphQL) updates the local database.

---

## 🛠 Tech Stack

- **Language**: Kotlin
- **Dependency Injection**: Hilt
- **Asynchronous Flow**: Coroutines & Flow
- **Local Database**: Room (with Migrations)
- **Networking**: Apollo GraphQL
- **UI**: View Binding / XML (Transitioning/Support for Compose)
- **Testing**: JUnit 4, Turbine (for Flow testing), Mockk

---

## 🧪 Testing Strategy

Quality is a core pillar of this project. The testing strategy includes:
- **Unit Tests**: Business logic in Use Cases and State management in ViewModels are strictly tested.
- **Flow Testing**: `Turbine` is used to verify Flow emissions in a concise way.
- **Room Migrations**: Database migrations are tested to ensure data integrity.
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
