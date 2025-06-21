# Core Navigation Module

The `core-navigation` module is responsible for defining the main navigation structure of the Finance Tracker application. It orchestrates how different feature modules are integrated into the app's navigation graph.

## Key Components

### `FinanceTrackerNavGraph`

- **`FinanceTrackerNavGraph.kt`**: This file contains the primary `NavHost` for the application. It aggregates navigation graphs from various feature modules, such as `transactionsGraph` and `currencyConversionScreen`, into a single, cohesive navigation flow.

The `startDestination` is provided externally, allowing for flexibility in determining the initial screen of the application (e.g., for different launch scenarios or A/B testing).

## Architecture

By centralizing the top-level navigation graph, this module helps in decoupling feature modules from each other. Features define their own internal navigation (as nested graphs) and expose them as extension functions on `NavGraphBuilder`. The `core-navigation` module then consumes these extensions to build the final navigation graph.

This approach offers several benefits:
-   **Modularity**: Each feature manages its own navigation logic.
-   **Scalability**: New features can be easily added to the app's navigation without modifying existing feature modules.
-   **Decoupling**: Feature modules don't need to know about each other's routes. 