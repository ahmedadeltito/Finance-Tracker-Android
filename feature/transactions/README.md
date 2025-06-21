# Transactions Feature Module

The `feature-transactions` module encapsulates all the user-facing functionality related to creating, viewing, updating, and deleting financial transactions. It is a self-contained feature module that depends on the `domain` and `core` modules to perform its functions.

## Key Features

This module provides the following screens and functionalities:

-   **Transaction List (`ui/list/`)**: Displays a list of all transactions. It allows users to view their financial history and serves as the main entry point for this feature.
-   **Add Transaction (`ui/add/`)**: A screen with a form for creating a new transaction.
-   **Update Transaction (`ui/update/`)**: A screen that allows editing the details of an existing transaction.

## Architecture

The module follows the Model-View-ViewModel (MVVM) architecture pattern, combined with a unidirectional data flow (UDF) approach, which is implemented using `StateFlow` and `SharedFlow` for UI state and events.

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

This package contains components for handling currency exchange rates specifically for the transactions feature.
-   **`ExchangeRateHostProvider.kt`**: A provider that interacts with an external exchange rate service.
-   **`remote/`**: Contains the Retrofit service (`ExchangeRateHostService.kt`) and data transfer objects (`DTOs`) for the exchange rate API.
-   **DI Modules**: Hilt modules for providing the necessary dependencies for the currency exchange functionality. 