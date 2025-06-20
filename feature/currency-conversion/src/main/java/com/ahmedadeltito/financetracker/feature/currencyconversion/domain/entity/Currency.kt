package com.ahmedadeltito.financetracker.feature.currencyconversion.domain.entity

/**
 * Represents a fiat currency.
 *
 * @property code ISO-4217 3-letter code, e.g. "USD", "EUR".
 * @property name A user-friendly name (optional, can be empty) such as "US Dollar".
 */
data class Currency(
    val code: String,
    val name: String = code,
) 