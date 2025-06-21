# Domain Module

The `domain` module is the core of the Finance Tracker application's business logic. It follows the principles of Clean Architecture, which means it is a pure Kotlin module with no dependencies on the Android framework or any specific data source implementation. This module defines the "what" of the application's business rules, leaving the "how" to the outer layers (like `data` and `app`).

## Key Components

### Entities (`entity/`)

-   **`Transaction.kt`**: This file defines the core `Transaction` business object. It is a plain Kotlin data class that represents a single financial transaction, independent of how it is stored in a database or displayed on the UI.

### Repositories (`repository/`)

-   **`TransactionRepository.kt`**: This is an interface that defines the contract for data operations related to transactions. It abstracts the data source, meaning the `domain` module doesn't know or care whether the data comes from a local database, a remote API, or a combination of both. The concrete implementation of this repository is provided in the `data` module.

### Use Cases (`usecase/`)

Use cases (or interactors) represent single, discrete business operations. They are the entry points to the `domain` layer and are called by the ViewModels in the feature modules. Each use case typically has a single public method, `invoke`, and orchestrates the flow of data from repositories to perform its specific task.

The use cases are organized by feature:

#### Category Use Cases (`usecase/category/`)

-   **`AddTransactionCategoryUseCase.kt`**: Handles the logic for adding a new transaction category.
-   **`GetTransactionCategoriesUseCase.kt`**: Retrieves a list of all available transaction categories.
-   **`UpdateTransactionCategoryUseCase.kt`**: Updates an existing transaction category.

#### Transaction Use Cases (`usecase/transaction/`)

-   **`AddTransactionUseCase.kt`**: Encapsulates the logic for adding a new transaction.
-   **`DeleteTransactionUseCase.kt`**: Handles the deletion of a transaction.
-   **`GetTransactionStatsUseCase.kt`**: Calculates statistics based on the existing transactions (e.g., total income vs. expense).
-   **`GetTransactionsUseCase.kt`**: Retrieves a list of all transactions.
-   **`GetTransactionUseCase.kt`**: Fetches a single transaction by its ID.
-   **`UpdateTransactionUseCase.kt`**: Manages the logic for updating an existing transaction.

## Architecture

By being a pure Kotlin module, the `domain` layer is highly testable, reusable, and maintainable. It ensures a clear separation of concerns, where business logic is not entangled with platform-specific details. This makes the application more robust and easier to scale. 