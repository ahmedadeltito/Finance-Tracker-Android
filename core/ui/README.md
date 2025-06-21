# Core UI Module

The `core-ui` module is a central library for all UI-related elements that are shared across the Finance Tracker application. It ensures a consistent look and feel by providing a common set of UI components, theme definitions, and UI-level models.

## Key Features

### Components

Located in the `com.ahmedadeltito.financetracker.ui.components` package, this section contains a collection of reusable Jetpack Compose components that form the building blocks of the application's user interface.

-   **`Button.kt`**: A customizable button component.
-   **`ComponentPreviews.kt`**: Contains Previews for multiple components.
-   **`DatePickerComponent.kt`**: A component for selecting dates.
-   **`ErrorComponent.kt`**: A component to display an error message.
-   **`LoadingComponent.kt`**: A component to indicate a loading state.
-   **`TransactionAlertDialog.kt`**: A dialog for confirming transaction-related actions.
-   **`TransactionCard.kt`**: A card to display a summary of a transaction.
-   **`TransactionCategoryItem.kt`**: An item representing a transaction category in a list.
-   **`TransactionCategorySelector.kt`**: A component for selecting a transaction category.
-   **`TransactionFormContent.kt`**: The main content of the transaction form.
-   **`TransactionTextFieldComponent.kt`**: A text field for transaction input.
-   **`TransactionTypeSelectorComponent.kt`**: A component to select the type of transaction (e.g., income, expense).

These components are designed to be generic and customizable to fit various use cases within feature modules.

### Theme

The `com.ahmedadeltito.financetracker.ui.theme` package defines the visual identity of the application:
-   **`Color.kt`**: Contains the application's color palette.
-   **`Theme.kt`**: Defines the main application theme, bringing together colors, typography, and shapes.
-   **`Type.kt`**: Defines the application's typography styles.

Feature modules should use this theme to ensure UI consistency.

### UI Models and Mappers

-   **`model` package**: Contains UI-specific data classes (`*UiModel`) that are tailored for presentation on the screen. These models are what the Composables in the feature modules will typically observe and render.
-   **`mapper` package**: Provides mapper classes responsible for converting domain-layer entities or data-layer models into `UiModel`s. This separation of concerns is crucial for a clean architecture, as it prevents the domain layer from being polluted with presentation-specific logic.

### Preview Data

The `com.ahmedadeltito.financetracker.ui.preview` package holds sample data used for Jetpack Compose previews. This allows developers to see and test UI components in Android Studio without needing to run the full application.

## How to Use

Feature modules that need to build a user interface should include `core-ui` as a dependency. They can then use the provided theme, components, and models to construct their screens. 