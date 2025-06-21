package com.ahmedadeltito.financetracker.feature.transactions.ui.add

import androidx.lifecycle.viewModelScope
import com.ahmedadeltito.financetracker.common.NoParameters
import com.ahmedadeltito.financetracker.common.Result
import com.ahmedadeltito.financetracker.common.di.CoroutineDispatchers
import com.ahmedadeltito.financetracker.domain.entity.Transaction
import com.ahmedadeltito.financetracker.domain.entity.TransactionCategory
import com.ahmedadeltito.financetracker.domain.usecase.category.GetTransactionCategoriesUseCase
import com.ahmedadeltito.financetracker.domain.usecase.transaction.AddTransactionUseCase
import com.ahmedadeltito.financetracker.feature.transactions.common.BaseTransactionFormViewModel
import com.ahmedadeltito.financetracker.feature.transactions.common.TransactionFormValidator
import com.ahmedadeltito.financetracker.feature.transactions.mapper.TransactionFormMapper
import com.ahmedadeltito.financetracker.feature.transactions.mapper.toUiModel
import com.ahmedadeltito.financetracker.ui.mapper.DateMapper
import com.ahmedadeltito.financetracker.ui.model.TransactionFormValidationState
import com.ahmedadeltito.financetracker.ui.model.TransactionUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    private val addTransactionUseCase: AddTransactionUseCase,
    private val getTransactionCategoriesUseCase: GetTransactionCategoriesUseCase,
    override val dispatchers: CoroutineDispatchers,
) : BaseTransactionFormViewModel<AddTransactionUiState, AddTransactionUiState.Success>(
    dispatchers = dispatchers,
    initialState = AddTransactionUiState.Loading
) {

    private val _sideEffect = Channel<AddTransactionSideEffect>()
    val sideEffect = _sideEffect.receiveAsFlow()

    val state: StateFlow<AddTransactionUiState> = baseState

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
            val getTransactionCategories: Flow<Result<List<TransactionCategory>>> =
                getTransactionCategoriesUseCase(NoParameters)
            getTransactionCategories.collectLatest { result ->
                when (result) {
                    is Result.Success -> _state.value = AddTransactionUiState.Success(
                        transaction = transactionUiModel,
                        categories = result.data.map { it.toUiModel() },
                    )
                    is Result.Error -> _state.value = AddTransactionUiState.Error(
                        message = result.exception.message ?: "Failed to load categories"
                    )
                    is Result.Loading -> _state.value = AddTransactionUiState.Loading
                }
            }
        }
    }

    private fun saveTransaction() {
        val currentState = _state.value as? AddTransactionUiState.Success ?: return

        val formData = currentState.transaction
        val validation = TransactionFormValidator.validateForm(formData)

        if (validation.hasErrors) {
            _state.value = currentState.copy(validation = validation)
            return
        }

        viewModelScope.launch(dispatchers.io) {
            _state.value = AddTransactionUiState.Loading

            val amount = formData.amount.toBigDecimalOrNull() ?: return@launch
            val transaction = TransactionFormMapper.fromFormData(
                id = UUID.randomUUID().toString(),
                amount = amount,
                description = formData.note ?: "",
                date = DateMapper.parseDateString(formData.formattedDate),
                type = formData.type,
                categoryId = formData.category.id
            )

            val addTransaction: Result<Transaction> =
                addTransactionUseCase(AddTransactionUseCase.Params(transaction))
            when (addTransaction) {
                is Result.Success -> {
                    _sideEffect.send(AddTransactionSideEffect.ShowSnackbar("Transaction added successfully"))
                    _sideEffect.send(AddTransactionSideEffect.NavigateBack)
                }
                is Result.Error -> _state.value = AddTransactionUiState.Error(
                    message = addTransaction.exception.message ?: "Failed to save transaction"
                )
                is Result.Loading -> _state.value = AddTransactionUiState.Loading
            }
        }
    }

    private fun navigateBack() {
        viewModelScope.launch(dispatchers.main) {
            _sideEffect.send(AddTransactionSideEffect.NavigateBack)
        }
    }

    override fun copySuccessState(
        current: AddTransactionUiState.Success,
        transaction: TransactionUiModel,
        validation: TransactionFormValidationState
    ): AddTransactionUiState.Success = current.copy(transaction = transaction, validation = validation)
} 