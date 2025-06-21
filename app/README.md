# App Module

The `app` module is the final composition root of the Finance Tracker application. It is the entry point for the user and is responsible for assembling all the different `core` and `feature` modules into a single, runnable Android application.

## Key Responsibilities

### 1. Application and Activity Creation

-   **`FinanceTrackerApplication.kt`**: The main `Application` class. It is annotated with `@HiltAndroidApp`, which is essential for initializing the Dagger Hilt dependency injection framework for the entire application.
-   **`MainActivity.kt`**: The single entry point `Activity` for the application. Its primary role is to set up the main UI content. It uses the `FinanceTrackerTheme` from the `core-ui` module and hosts the `FinanceTrackerNavGraph` from the `core-navigation` module.

### 2. Module Orchestration

The `app` module's `build.gradle.kts` file is where all the other modules are brought together. It declares `implementation` dependencies on:
-   All `feature` modules (e.g., `feature-transactions`, `feature-currency-conversion`).
-   All `core` modules (e.g., `core-ui`, `core-navigation`, `core-common`).

By doing this, it makes their provided functionalities and dependency injection modules available to the application at runtime.

### 3. Top-Level Configuration

This module holds essential top-level configuration files:
-   **`build.gradle.kts`**: Defines the application's `applicationId`, `versionCode`, `versionName`, and other crucial build configurations.
-   **`AndroidManifest.xml`**: The main manifest for the application, declaring the application class, the main activity, permissions, etc.
-   **`proguard-rules.pro`**: Defines the ProGuard rules for release builds to ensure code is correctly shrunk and obfuscated without causing runtime crashes.
-   **`res/`**: Contains global resources, such as the app icon (`mipmap-*`), and base values for `colors.xml`, `strings.xml`, and `themes.xml`.

## Architecture

The `app` module is the "top" layer in the Clean Architecture pyramid. It depends on all other modules but no other modules depend on it. Its job is not to contain business logic or UI screens itself, but rather to **configure and assemble** the components defined in the lower-level modules. This makes it a lightweight and stable foundation for the application. 