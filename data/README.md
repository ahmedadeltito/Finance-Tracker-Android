# Data Module

The `data` module is responsible for managing the application's data layer. It handles all data operations, sourcing data from the local database and providing a clean API for the domain layer to access it. This module implements the repository interfaces defined in the `domain` module, effectively decoupling the business logic from the data sources.

## Key Components

### Local Data Source (`local`)

This package contains all the components related to the local Room database.

-   **Database (`FinanceTrackerDatabase.kt`)**: The core of the local data source. It's the Room database class that defines the entities and the database version.
-   **DAOs (`TransactionDao.kt`, `TransactionCategoryDao.kt`)**: Data Access Objects that define the methods for interacting with the database tables (e.g., `INSERT`, `QUERY`, `UPDATE`, `DELETE`).
-   **Entities (`entity/`)**: These are the data classes annotated with `@Entity` that represent the tables in the Room database (`TransactionEntity.kt`, `TransactionCategoryEntity.kt`).
-   **Type Converters (`converter/RoomTypeConverters.kt`)**: Provides methods to convert custom types to and from types that Room can persist.
-   **Database Callback (`DatabaseCallback.kt`)**: Used to perform an action when the database is created, such as pre-populating it with initial data (e.g., default transaction categories).

### Repository Implementation (`repository`)

-   **`TransactionRepositoryImpl.kt`**: This class provides the concrete implementation of the `TransactionRepository` interface from the `domain` module. It uses the DAOs to interact with the local database and maps the database entities to domain models.

### Dependency Injection (`di`)

This package contains Hilt modules for providing dependencies related to the data layer.

-   **`DatabaseModule.kt`**: Provides the Room database instance and the DAOs.
-   **`RepositoryModule.kt`**: Binds the repository implementations (e.g., `TransactionRepositoryImpl`) to their corresponding interfaces from the domain layer.

### Database Schemas (`schemas`)

This directory stores the exported JSON schemas of the Room database for each version. These schemas are essential for writing and verifying database migrations.

## Architecture

The `data` module follows the principles of Clean Architecture. The `domain` layer defines the contracts (repository interfaces), and the `data` layer provides the concrete implementations. This ensures that the application's business logic is independent of the data source implementation details. Any changes to the database or data-fetching logic are contained within this module and do not affect the rest of the application. 