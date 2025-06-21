# Core Module

The `core` module is a collection of library modules that provide shared functionalities and resources for the entire Finance Tracker application. It is designed to enforce consistency, reduce code duplication, and promote a clean, modular architecture. The `core` module itself does not contain any code, but rather serves as a container for its submodules.

## Submodules

The `core` module is composed of the following submodules:

### [`core/common`](./common/README.md)

Contains essential utilities and base classes shared across the application, such as `Result`, `UseCase`, and coroutine dispatchers. It forms the foundational layer for business logic and data handling.

### [`core/navigation`](./navigation/README.md)

Manages the main navigation graph of the application. It integrates navigation flows from different feature modules into a single, cohesive user experience.

### [`core/ui`](./ui/README.md)

A central library for all shared UI elements. This includes Jetpack Compose components, the application theme (colors, typography), UI-level data models, and mappers. It is the cornerstone of a consistent user interface.

### [`core/testing`](./testing/README.md)

A designated place for shared testing utilities. Although currently empty, it is set up to hold custom test rules, fakes, and helper functions to support both unit and instrumentation tests across the project.

## Architecture Philosophy

By separating these cross-cutting concerns into distinct modules, we achieve:
-   **High Cohesion**: Each module has a single, well-defined responsibility.
-   **Low Coupling**: Feature modules can depend on these core modules without depending on each other.
-   **Improved Build Times**: Changes in one core module only trigger the recompilation of dependent modules, not the entire application.
-   **Scalability**: New features can be developed more easily by leveraging the shared functionalities provided by the core modules. 