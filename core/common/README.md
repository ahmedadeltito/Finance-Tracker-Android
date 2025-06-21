# Core Common Module

The `core-common` module contains essential utilities and base classes that are shared across different modules in the Finance Tracker application. This helps in promoting code reusability and maintaining a consistent architecture.

## Key Components

### Coroutine Dispatchers

- **`CoroutineDispatchers.kt`**: Defines an interface for providing coroutine dispatchers. This allows for easy swapping of dispatchers during testing.
- **`DispatchersModule.kt`**: A Hilt module that provides the concrete implementation of `CoroutineDispatchers`, making it easy to inject dispatchers wherever they are needed in the application.

### Result

- **`Result.kt`**: A generic sealed class used to represent the result of an operation that can either be a `Success` or an `Error`. This is a standardized way to handle success and failure cases throughout the app, particularly in the data and domain layers.

### UseCase

- **`UseCase.kt`**: An abstract base class for all use cases in the application. It defines a standard way of executing business logic on a background thread and returning a `Result`. This aligns with the principles of Clean Architecture.

## Usage

This module is intended to be a dependency for other modules that require these fundamental utilities. For instance, any feature module that implements use cases will depend on `core-common` to access the `UseCase` base class and the `Result` wrapper. 