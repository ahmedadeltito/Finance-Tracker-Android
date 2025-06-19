package com.ahmedadeltito.financetracker.feature.transactions.ui.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ahmedadeltito.financetracker.common.NoParameters
import com.ahmedadeltito.financetracker.common.Result
import com.ahmedadeltito.financetracker.common.di.CoroutineDispatchers
import com.ahmedadeltito.financetracker.domain.usecase.category.GetTransactionCategoriesUseCase
import com.ahmedadeltito.financetracker.domain.usecase.transaction.AddTransactionUseCase
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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.util.Date
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    private val addTransactionUseCase: AddTransactionUseCase,
    private val getTransactionCategoriesUseCase: GetTransactionCategoriesUseCase,
    private val dispatchers: CoroutineDispatchers,
) : ViewModel() {

    private val _state = MutableStateFlow<AddTransactionState>(AddTransactionState.Loading)
    val state: StateFlow<AddTransactionState> = _state.asStateFlow()

    private val _sideEffect = Channel<AddTransactionSideEffect>()
    val sideEffect = _sideEffect.receiveAsFlow()

    init {
        loadCategories(transactionUiModel = TransactionUiModel.EMPTY)
    }

    fun onEvent(event: AddTransactionEvent) {
        when (event) {
            is AddTransactionEvent.OnAmountChange -> updateAmount(event.amount)
            is AddTransactionEvent.OnDescriptionChange -> updateDescription(event.description)
            is AddTransactionEvent.OnDateChange -> updateDate(event.date)
            is AddTransactionEvent.OnTypeChange -> updateType(event.type)
            is AddTransactionEvent.OnCategorySelect -> updateCategory(event.categoryId)
            is AddTransactionEvent.OnSaveClick -> saveTransaction()
            is AddTransactionEvent.OnBackClick -> navigateBack()
        }
    }

    private fun loadCategories(transactionUiModel: TransactionUiModel) {
        viewModelScope.launch(dispatchers.io) {
            getTransactionCategoriesUseCase(parameters = NoParameters).collectLatest { result ->
                when (result) {
                    is Result.Success -> _state.value = AddTransactionState.Success(
                        transaction = transactionUiModel,
                        categories = result.data.map { it.toUiModel() },
                    )
                    is Result.Error -> _state.value = AddTransactionState.Error(
                        message = result.exception.message ?: "Failed to load categories"
                    )
                    is Result.Loading -> _state.value = AddTransactionState.Loading
                }
            }
        }
    }

    private fun updateAmount(amount: String) {
        val currentState = _state.value as? AddTransactionState.Success ?: return
        val validation = validateAmount(amount)
        _state.value = currentState.copy(
            transaction = currentState.transaction.copy(amount = amount),
            validation = currentState.validation.copy(amountError = validation)
        )
    }

    private fun updateDescription(description: String) {
        val currentState = _state.value as? AddTransactionState.Success ?: return
        val validation = validateDescription(description)
        _state.value = currentState.copy(
            transaction = currentState.transaction.copy(note = description),
            validation = currentState.validation.copy(descriptionError = validation)
        )
    }

    private fun updateDate(date: Date) {
        val currentState = _state.value as? AddTransactionState.Success ?: return
        val formattedDate = formatDate(date)
        val validation = validateDate(formattedDate)
        _state.value = currentState.copy(
            transaction = currentState.transaction.copy(formattedDate = formattedDate),
            validation = currentState.validation.copy(dateError = validation)
        )
    }

    private fun updateType(type: TransactionTypeUiModel) {
        val currentState = _state.value as? AddTransactionState.Success ?: return
        _state.value = currentState.copy(
            transaction = currentState.transaction.copy(type = type)
        )
    }

    private fun updateCategory(categoryId: String) {
        val currentState = _state.value as? AddTransactionState.Success ?: return
        val updatedCategory = currentState.transaction.category.copy(id = categoryId)
        val validation = validateCategory(categoryId)
        _state.value = currentState.copy(
            transaction = currentState.transaction.copy(category = updatedCategory),
            validation = currentState.validation.copy(categoryError = validation)
        )
    }

    private fun validateAmount(amount: String): String? = when {
        amount.isBlank() -> "Amount is required"
        amount.toBigDecimalOrNull() == null -> "Invalid amount"
        amount.toBigDecimal() <= BigDecimal.ZERO -> "Amount must be greater than zero"
        else -> null
    }

    private fun validateDescription(description: String): String? = when {
        description.isBlank() -> "Description is required"
        description.length < 3 -> "Description must be at least 3 characters"
        else -> null
    }

    private fun validateCategory(categoryId: String?): String? = when {
        categoryId == null || categoryId.isBlank() -> "Category is required"
        else -> null
    }

    private fun validateDate(formattedDate: String?): String? = when {
        formattedDate == null || formattedDate.isBlank() -> "Date is required"
        else -> null
    }

    private fun validateForm(formData: TransactionUiModel): ValidationState = ValidationState(
        amountError = validateAmount(formData.amount),
        descriptionError = validateDescription(formData.note ?: ""),
        categoryError = validateCategory(formData.category.id),
        dateError = validateDate(formData.formattedDate)
    )

    private fun saveTransaction() {
        val currentState = _state.value as? AddTransactionState.Success ?: return

        val formData = currentState.transaction
        val validation = validateForm(formData)

        if (validation.hasErrors) {
            _state.value = currentState.copy(validation = validation)
            return
        }

        viewModelScope.launch(dispatchers.io) {
            _state.value = AddTransactionState.Loading

            val amount = formData.amount.toBigDecimalOrNull() ?: return@launch
            val transaction = fromFormData(
                id = UUID.randomUUID().toString(),
                amount = amount,
                description = formData.note ?: "",
                date = parseDateString(formData.formattedDate),
                type = formData.type,
                categoryId = formData.category.id
            )

            when (val result = addTransactionUseCase(AddTransactionUseCase.Params(transaction))) {
                is Result.Success -> {
                    _sideEffect.send(AddTransactionSideEffect.ShowSuccess("Transaction added successfully"))
                    _sideEffect.send(AddTransactionSideEffect.NavigateBack)
                }
                is Result.Error -> _state.value = AddTransactionState.Error(
                    message = result.exception.message ?: "Failed to save transaction"
                )
                is Result.Loading -> _state.value = AddTransactionState.Loading
            }
        }
    }

    private fun navigateBack() {
        viewModelScope.launch(dispatchers.main) {
            _sideEffect.send(AddTransactionSideEffect.NavigateBack)
        }
    }
} 