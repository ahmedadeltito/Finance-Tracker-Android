package com.ahmedadeltito.financetracker.feature.transactions.common

import androidx.lifecycle.ViewModel
import com.ahmedadeltito.financetracker.common.di.CoroutineDispatchers
import com.ahmedadeltito.financetracker.ui.mapper.DateMapper
import com.ahmedadeltito.financetracker.ui.model.TransactionFormValidationState
import com.ahmedadeltito.financetracker.ui.model.TransactionTypeUiModel
import com.ahmedadeltito.financetracker.ui.model.TransactionUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Date

/**
 * A generic base ViewModel that owns the common form-field update logic shared between
 * Add- and Update- transaction screens.
 *
 * [State]  – the full sealed state type used by the concrete screen (e.g. AddTransactionState).
 * [Success] – the *Success* subtype of that state, implementing [TransactionFormSuccess].
 */
abstract class BaseTransactionFormViewModel<State, Success : TransactionFormSuccess>(
    protected open val dispatchers: CoroutineDispatchers,
    initialState: State
) : ViewModel() {

    @Suppress("PropertyName")
    protected open val _state: MutableStateFlow<State> = MutableStateFlow(initialState)
    val baseState: StateFlow<State> = _state.asStateFlow()

    protected abstract fun copySuccessState(
        current: Success,
        transaction: TransactionUiModel = current.transaction,
        validation: TransactionFormValidationState = current.validation
    ): Success

    protected fun updateAmount(amount: String) {
        updateSuccessState { current ->
            val validation = current.validation.copy(
                amountError = TransactionFormValidator.validateAmount(amount)
            )
            copySuccessState(
                current = current,
                transaction = current.transaction.copy(amount = amount),
                validation = validation
            )
        }
    }

    protected fun updateDescription(description: String) {
        updateSuccessState { current ->
            val validation = current.validation.copy(
                descriptionError = TransactionFormValidator.validateDescription(description)
            )
            copySuccessState(
                current = current,
                transaction = current.transaction.copy(note = description),
                validation = validation
            )
        }
    }

    protected fun updateDate(date: Date) {
        updateSuccessState { current ->
            val formatted = DateMapper.formatDate(date)
            val validation = current.validation.copy(
                dateError = TransactionFormValidator.validateDate(formatted)
            )
            copySuccessState(
                current = current,
                transaction = current.transaction.copy(formattedDate = formatted),
                validation = validation
            )
        }
    }

    protected fun updateType(type: TransactionTypeUiModel) {
        updateSuccessState { current ->
            copySuccessState(
                current = current,
                transaction = current.transaction.copy(type = type)
            )
        }
    }

    protected fun updateCategory(categoryId: String) {
        updateSuccessState { current ->
            val validation = current.validation.copy(
                categoryError = TransactionFormValidator.validateCategory(categoryId)
            )
            val updatedCategory = current.transaction.category.copy(id = categoryId)
            copySuccessState(
                current = current,
                transaction = current.transaction.copy(category = updatedCategory),
                validation = validation
            )
        }
    }

    @Suppress("UNCHECKED_CAST")
    private inline fun updateSuccessState(transform: (Success) -> Success) {
        val current = _state.value as? Success ?: return
        _state.value = transform(current) as State
    }
} 