package com.ahmedadeltito.financetracker.feature.transactions.ui.update

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ahmedadeltito.financetracker.common.NoParameters
import com.ahmedadeltito.financetracker.common.Result
import com.ahmedadeltito.financetracker.common.di.CoroutineDispatchers
import com.ahmedadeltito.financetracker.domain.entity.Transaction
import com.ahmedadeltito.financetracker.domain.usecase.category.GetTransactionCategoriesUseCase
import com.ahmedadeltito.financetracker.domain.usecase.transaction.GetTransactionUseCase
import com.ahmedadeltito.financetracker.domain.usecase.transaction.GetTransactionUseCase.Params
import com.ahmedadeltito.financetracker.domain.usecase.transaction.UpdateTransactionUseCase
import com.ahmedadeltito.financetracker.feature.transactions.navigation.transactionIdArg
import com.ahmedadeltito.financetracker.feature.transactions.ui.model.ValidationState
import com.ahmedadeltito.financetracker.ui.mapper.TransactionMapper.formatDate
import com.ahmedadeltito.financetracker.ui.mapper.TransactionMapper.fromFormData
import com.ahmedadeltito.financetracker.ui.mapper.TransactionMapper.parseDateString
import com.ahmedadeltito.financetracker.ui.mapper.TransactionMapper.toUiModel
import com.ahmedadeltito.financetracker.ui.model.TransactionTypeUiModel
import com.ahmedadeltito.financetracker.ui.model.TransactionUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class UpdateTransactionViewModel @Inject constructor(
    private val updateTransactionUseCase: UpdateTransactionUseCase,
    private val getTransactionUseCase: GetTransactionUseCase,
    private val getTransactionCategoriesUseCase: GetTransactionCategoriesUseCase,
    private val dispatchers: CoroutineDispatchers,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val transactionId: String? = savedStateHandle[transactionIdArg]

    private val _state = MutableStateFlow<UpdateTransactionState>(UpdateTransactionState.Loading)
    val state: StateFlow<UpdateTransactionState> = _state.asStateFlow()

    private val _sideEffect = Channel<UpdateTransactionSideEffect>()
    val sideEffect = _sideEffect.receiveAsFlow()

    init {
        if (transactionId != null) {
            loadTransaction(transactionId)
        }
    }

    fun onEvent(event: UpdateTransactionEvent) {
        when (event) {
            is UpdateTransactionEvent.OnAmountChange -> updateAmount(event.amount)
            is UpdateTransactionEvent.OnDescriptionChange -> updateDescription(event.description)
            is UpdateTransactionEvent.OnDateChange -> updateDate(event.date)
            is UpdateTransactionEvent.OnTypeChange -> updateType(event.type)
            is UpdateTransactionEvent.OnCategorySelect -> updateCategory(event.categoryId)
            is UpdateTransactionEvent.OnUpdateClick -> updateTransaction()
            is UpdateTransactionEvent.OnBackClick -> navigateBack()
        }
    }

    private fun loadTransaction(transactionId: String) {
        viewModelScope.launch(dispatchers.io) {
            when (val result = getTransactionUseCase(Params(transactionId))) {
                is Result.Loading -> _state.value = UpdateTransactionState.Loading
                is Result.Success<Transaction> -> loadCategories(
                    transactionUiModel = result.data.toUiModel()
                )

                is Result.Error -> _state.value = UpdateTransactionState.Error(
                    message = result.exception.message ?: "Failed to load transaction"
                )
            }
        }
    }

    private fun loadCategories(transactionUiModel: TransactionUiModel) {
        viewModelScope.launch(dispatchers.io) {
            getTransactionCategoriesUseCase(parameters = NoParameters).collect { result ->
                when (result) {
                    is Result.Loading -> _state.value = UpdateTransactionState.Loading
                    is Result.Success -> _state.value = UpdateTransactionState.Success(
                        transaction = transactionUiModel,
                        categories = result.data.map { it.toUiModel() },
                    )

                    is Result.Error -> _state.value = UpdateTransactionState.Error(
                        message = result.exception.message ?: "Failed to load categories"
                    )
                }
            }
        }
    }

    private fun updateAmount(amount: String) {
        val currentState = _state.value as? UpdateTransactionState.Success ?: return
        val validation = validateAmount(amount)
        _state.value = currentState.copy(
            transaction = currentState.transaction.copy(amount = amount),
            validation = currentState.validation.copy(amountError = validation)
        )
    }

    private fun updateDescription(description: String) {
        val currentState = _state.value as? UpdateTransactionState.Success ?: return
        val validation = validateDescription(description)
        _state.value = currentState.copy(
            transaction = currentState.transaction.copy(note = description),
            validation = currentState.validation.copy(descriptionError = validation)
        )
    }

    private fun updateDate(date: Date) {
        val currentState = _state.value as? UpdateTransactionState.Success ?: return
        val formattedDate = formatDate(date)
        val validation = validateDate(formattedDate)
        _state.value = currentState.copy(
            transaction = currentState.transaction.copy(formattedDate = formattedDate),
            validation = currentState.validation.copy(dateError = validation)
        )
    }

    private fun updateType(type: TransactionTypeUiModel) {
        val currentState = _state.value as? UpdateTransactionState.Success ?: return
        _state.value = currentState.copy(
            transaction = currentState.transaction.copy(type = type)
        )
    }

    private fun updateCategory(categoryId: String) {
        val currentState = _state.value as? UpdateTransactionState.Success ?: return
        val updatedCategory = currentState.transaction.category.copy(id = categoryId)
        val validation = validateCategory(categoryId)
        _state.value = currentState.copy(
            transaction = currentState.transaction.copy(category = updatedCategory),
            validation = currentState.validation.copy(categoryError = validation)
        )
    }

    private fun validateAmount(amount: String): String? {
        return when {
            amount.isBlank() -> "Amount is required"
            amount.toBigDecimalOrNull() == null -> "Invalid amount"
            amount.toBigDecimal() <= BigDecimal.ZERO -> "Amount must be greater than zero"
            else -> null
        }
    }

    private fun validateDescription(description: String): String? {
        return when {
            description.isBlank() -> "Description is required"
            description.length < 3 -> "Description must be at least 3 characters"
            else -> null
        }
    }

    private fun validateCategory(categoryId: String?): String? {
        return when {
            categoryId == null -> "Category is required"
            else -> null
        }
    }

    private fun validateDate(formattedDate: String?): String? = when {
        formattedDate == null || formattedDate.isBlank() -> "Date is required"
        else -> null
    }

    private fun validateForm(formData: TransactionUiModel): ValidationState {
        return ValidationState(
            amountError = validateAmount(formData.amount),
            descriptionError = validateDescription(formData.note ?: ""),
            categoryError = validateCategory(formData.category.id),
            dateError = validateDate(formData.formattedDate)
        )
    }

    private fun updateTransaction() {
        val currentState = _state.value as? UpdateTransactionState.Success ?: return
        val formData = currentState.transaction
        val validation = validateForm(formData)

        if (validation.hasErrors) {
            _state.value = currentState.copy(validation = validation)
            return
        }

        viewModelScope.launch(dispatchers.io) {
            _state.value = UpdateTransactionState.Loading

            val amount = formData.amount.toBigDecimalOrNull() ?: return@launch
            val transaction = fromFormData(
                id = formData.id,
                amount = amount,
                description = formData.note ?: "",
                date = parseDateString(formData.formattedDate),
                type = formData.type,
                categoryId = formData.category.id
            )

            when (val result = updateTransactionUseCase(UpdateTransactionUseCase.Params(transaction))) {
                is Result.Success -> {
                    _sideEffect.send(UpdateTransactionSideEffect.ShowSuccess("Transaction updated successfully"))
                    _sideEffect.send(UpdateTransactionSideEffect.NavigateBack)
                }
                is Result.Error -> _state.value = UpdateTransactionState.Error(
                    message = result.exception.message ?: "Failed to save transaction"
                )
                is Result.Loading -> _state.value = UpdateTransactionState.Loading
            }
        }
    }

    private fun navigateBack() {
        viewModelScope.launch(dispatchers.main) {
            _sideEffect.send(UpdateTransactionSideEffect.NavigateBack)
        }
    }
} 