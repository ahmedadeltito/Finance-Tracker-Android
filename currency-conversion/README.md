# Currency Conversion Feature Module

The `currency-conversion` module is a self-contained feature module that provides users with the ability to convert between different currencies based on the latest exchange rates.

## Architecture: A Vertical Slice

This module is architected as a "vertical slice" of the Clean Architecture pattern. Unlike other parts of the application where `data`, `domain`, and `ui` are in separate top-level modules, here they are organized as packages within the feature module itself. This approach encapsulates the entire feature—from UI to data fetching—in a single, cohesive module.

### UI Layer (`ui/`)

This package is responsible for the presentation logic. It follows the MVVM pattern with a UDF approach.
-   **`CurrencyConverterScreen.kt`**: The Jetpack Compose screen where the user interacts with the currency converter.
-   **`CurrencyConverterViewModel.kt`**: Manages the UI state, handles user input, and calls the `domain` layer's use cases.
-   **`CurrencyConverterContract.kt`**: Defines the `State`, `Event`, and `Effect` for the screen, ensuring a clear and predictable data flow.
-   **`CurrencyConversionFormValidator.kt`**: Provides validation logic for the user's input.

### Navigation (`navigation/`)

-   **`CurrencyConversionNavigation.kt`**: Defines the feature's navigation entry point. It exposes a `NavGraphBuilder` extension function that the main `core-navigation` module can use to integrate this feature into the app's overall navigation graph.

### Domain Layer (`domain/`)

This package contains the core business logic, free from any Android or implementation-specific dependencies.
-   **Use Cases (`usecase/`)**:
    -   `ConvertCurrencyUseCase.kt`: Contains the logic for performing the currency conversion.
    -   `ExchangeRatesProvidersUseCase.kt`: Fetches the available currencies/rates.
-   **Repository Interface (`repository/`)**:
    -   `ExchangeRateRepository.kt`: Defines the contract for fetching exchange rate data.
-   **Port (`port/`)**:
    -   `ExchangeRateProviderPort.kt`: An abstraction for the external exchange rate provider, allowing for different providers to be swapped in and out.

### Data Layer (`data/`)

This package is responsible for implementing the contracts defined in the `domain` layer.
-   **Repository Implementation (`repository/`)**:
    -   `ExchangeRateRepositoryImpl.kt`: The concrete implementation of `ExchangeRateRepository`, which uses the `ExchangeRateProviderPort` to fetch data.
-   **Remote (`remote/`)**:
    -   `FrankfurterExchangeRateProvider.kt`: An implementation of the `ExchangeRateProviderPort` that uses the Frankfurter API.
    -   `ExchangeRateApiService.kt`: The Retrofit service interface for the Frankfurter API.
    -   `FrankfurterLatestResponse.kt`: The DTO for parsing the API's JSON response.

### Dependency Injection (`di/`)

Contains the Hilt modules (`CurrencyConversionNetworkModule`, `CurrencyConversionProviderModule`, `CurrencyConversionRepositoryModule`) that provide all the necessary dependencies for this feature, from the Retrofit instance to the repository implementation.

## Hexagonal Architecture: Ports and Adapters for Currency Exchange

A key architectural feature of this module is its use of the **Hexagonal (or Ports and Adapters) Architecture** for fetching exchange rate data. This pattern is designed to isolate the core business logic of the module from the external services it depends on.

### The Port: `ExchangeRateProviderPort`

The `domain/port/ExchangeRateProviderPort.kt` interface acts as a **"Port"**. It defines a contract that the application's core logic (the "hexagon") requires. The port specifies *what* data is needed (e.g., a method `getRate(...)`) but has no knowledge of *how* that data is obtained.

```kotlin
interface ExchangeRateProviderPort {
    val id: String
    val displayName: String
    suspend fun getRate(fromCurrencyCode: String, toCurrencyCode: String): Result<BigDecimal>
}
```

### The Adapter: `FrankfurterExchangeRateProvider`

The `data/remote/FrankfurterExchangeRateProvider.kt` class is an **"Adapter"**. It provides a concrete implementation of the `ExchangeRateProviderPort`. Its job is to adapt the external world (in this case, the `Frankfurter.dev` REST API) to fit the requirements of the port.

### Connecting via Dependency Injection

The magic happens with Hilt. The `di/CurrencyConversionProviderModule.kt` module uses Dagger Hilt's multibinding feature (`@IntoSet`) to provide a `Set` of `ExchangeRateProviderPort` implementations.

```kotlin
@Module
@InstallIn(SingletonComponent::class)
interface CurrencyConversionProviderModule {
    @Binds
    @IntoSet
    fun frankfurterProviderIntoSet(provider: FrankfurterExchangeRateProvider): ExchangeRateProviderPort
}
```
This tells Hilt: "When someone asks for a `Set<ExchangeRateProviderPort>`, create an instance of `FrankfurterExchangeRateProvider` and add it to the set."

### How to Integrate a New Exchange Rate Provider

This architecture makes it incredibly easy to add new exchange rate sources or even replace existing ones without touching the core logic of the currency conversion feature.

For example, the `feature-transactions` module has its own exchange rate provider, `ExchangeRateHostProvider`. To make it available to the currency conversion screen, the steps are simple:

1.  **Ensure the new provider implements the port**: The `ExchangeRateHostProvider` would need to implement `ExchangeRateProviderPort`.

2.  **Bind the new provider using Hilt**: In the `feature-transactions` module, a Hilt module would bind the new provider into the set.

    ```kotlin
    // In the feature-transactions module
    @Module
    @InstallIn(SingletonComponent::class)
    interface ExchangeRateHostProviderModule {
        @Binds
        @IntoSet // Binds this provider into the same set
        fun exchangeRateHostProvider(provider: ExchangeRateHostProvider): ExchangeRateProviderPort
    }
    ```

That's it! The `ExchangeRatesProvidersUseCase` in this module injects `Set<ExchangeRateProviderPort>`, so it will automatically receive *all* registered providers. The UI can then let the user choose which provider they want to use. This makes the system extremely flexible and scalable. 