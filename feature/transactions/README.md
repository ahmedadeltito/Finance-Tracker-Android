# Transactions Feature Module

The `feature-transactions` module encapsulates all the user-facing functionality related to creating, viewing, updating, and deleting financial transactions. It is a self-contained feature module that depends on the `domain` and `core` modules to perform its functions.

## Key Features

This module provides the following screens and functionalities:

-   **Transaction List (`ui/list/`)**: Displays a list of all transactions. It allows users to view their financial history and serves as the main entry point for this feature.
-   **Add Transaction (`ui/add/`)**: A screen with a form for creating a new transaction.
-   **Update Transaction (`ui/update/`)**: A screen that allows editing the details of an existing transaction.

## Architecture

The module follows the Model-View-ViewModel (MVVM) architecture pattern, combined with a unidirectional data flow (UDF) approach, which is implemented using `StateFlow` and `SharedFlow` for UI state and events.

### In-Depth Architecture: Unidirectional Data Flow (UDF)

The communication between the UI (Screen) and the business logic holder (ViewModel) is strictly managed using a UDF pattern. This makes the state of the screen predictable and easy to debug. This is enforced by a UI `Contract`.

-   **State**: A data class (typically a `sealed interface`) that represents everything that can be displayed on the screen. For example, `TransactionListUiState` can be `Loading`, `Success`, or `Error`. The ViewModel exposes this as a `StateFlow`, and the Composable screen observes it.

-   **Event**: Represents user actions or events originating from the UI, such as button clicks or text input. For example, `TransactionListEvent` includes actions like `OnAddTransactionClick` or `SoftDeleteTransaction`. These events are sent from the UI to the ViewModel.

-   **Side Effect**: Represents one-off events that the ViewModel sends to the UI, such as navigating to a new screen or showing a Snackbar. These are typically handled via a `SharedFlow` to ensure they are consumed only once. `TransactionListSideEffect.NavigateToTransactionDetails` is a prime example.

This strict contract ensures that the UI's only responsibility is to display the state and send user events, while the ViewModel handles all the logic and state management.

### Code Reusability: `BaseTransactionFormViewModel`

To avoid duplicating code, the `AddTransactionViewModel` and `UpdateTransactionViewModel` both inherit from `BaseTransactionFormViewModel`. This base class contains all the common logic for handling form field updates and validation.

-   It manages the core state of the transaction being edited (`TransactionUiModel`).
-   It provides shared methods like `updateAmount()`, `updateDescription()`, `updateDate()`, etc.
-   Concrete implementations (`AddTransactionViewModel`, `UpdateTransactionViewModel`) are then only responsible for their specific logic, such as fetching the initial data (for update) or handling the final save action.

### UI Layer (`ui/`)

The UI is built entirely with Jetpack Compose. Each screen (`list`, `add`, `update`) has its own:
-   **Screen (`*Screen.kt`)**: The main Composable function that builds the UI.
-   **ViewModel (`*ViewModel.kt`)**: The ViewModel responsible for holding the UI state, handling user events, and interacting with the `domain` layer through use cases.
-   **UI Contract (`*Contract.kt`)**: A file that defines the `State`, `Event`, and `Effect` for a given screen, making the communication between the UI and the ViewModel explicit and predictable.

### Common Logic (`common/`)

This package contains shared logic within the `transactions` feature.
-   **`BaseTransactionFormViewModel.kt`**: A base ViewModel that contains common logic for both the add and update transaction forms, reducing code duplication.
-   **`TransactionFormValidator.kt`**: A utility for validating the input in the transaction forms.

### Mappers (`mapper/`)

This package contains mappers responsible for converting `domain` models (like `Transaction`) into `UiModel`s that are tailored for the UI. This separation ensures that the `domain` layer remains independent of any presentation logic.

### Navigation (`navigation/`)

-   **`TransactionsNavigation.kt`**: Defines the navigation graph for the transactions feature. It exposes an extension function on `NavGraphBuilder` that can be integrated into the main application's navigation graph in the `core-navigation` module. This keeps the feature's navigation self-contained.

### Currency Exchange (`currency/`)

This package contains components for handling currency exchange rates specifically for the transactions feature. It includes its own `ExchangeRateHostProvider` which consumes the exchangerate.host API.

While this provider is currently only used within this module, it is a perfect candidate to be adapted to the hexagonal architecture used in the `currency-conversion` module. To make it available application-wide, one would simply:
1.  Make `ExchangeRateHostProvider` implement the `ExchangeRateProviderPort` from the `currency-conversion` module.
2.  Create a Hilt module in `feature-transactions` to bind it into the global `Set<ExchangeRateProviderPort>` using the `@IntoSet` annotation. 