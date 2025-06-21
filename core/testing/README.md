# Core Testing Module

The `core-testing` module is designated to hold shared testing utilities, such as custom JUnit rules, test doubles (fakes, stubs), and helper functions that can be reused across instrumented (`androidTest`) and unit (`test`) tests in other modules.

## Purpose

The main goal of this module is to:
-   **Avoid code duplication** in test suites.
-   **Provide a consistent testing framework** and utilities for all modules.
-   **Isolate testing dependencies** into a single module.

## Current Status

This module is currently empty but is set up to be populated with testing utilities as the project grows and common testing patterns emerge. 