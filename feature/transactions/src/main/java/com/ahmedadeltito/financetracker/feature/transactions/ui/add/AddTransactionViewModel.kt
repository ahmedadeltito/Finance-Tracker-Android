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
import com.ahmedadeltito.financetracker.ui.model.ValidationState
import com.ahmedadeltito.financetracker.ui.mapper.TransactionMapper.fromFormData
import com.ahmedadeltito.financetracker.ui.mapper.TransactionMapper.parseDateString
import com.ahmedadeltito.financetracker.ui.mapper.TransactionMapper.toUiModel
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
) : BaseTransactionFormViewModel<AddTransactionState, AddTransactionState.Success>(
    dispatchers = dispatchers,
    initialState = AddTransactionState.Loading
) {

    private val _sideEffect = Channel<AddTransactionSideEffect>()
    val sideEffect = _sideEffect.receiveAsFlow()

    val state: StateFlow<AddTransactionState> = baseState

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

    private fun saveTransaction() {
        val currentState = _state.value as? AddTransactionState.Success ?: return

        val formData = currentState.transaction
        val validation = TransactionFormValidator.validateForm(formData)

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

            val addTransaction: Result<Transaction> =
                addTransactionUseCase(AddTransactionUseCase.Params(transaction))
            when (addTransaction) {
                is Result.Success -> {
                    _sideEffect.send(AddTransactionSideEffect.ShowSnackbar("Transaction added successfully"))
                    _sideEffect.send(AddTransactionSideEffect.NavigateBack)
                }
                is Result.Error -> _state.value = AddTransactionState.Error(
                    message = addTransaction.exception.message ?: "Failed to save transaction"
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

    override fun copySuccessState(
        current: AddTransactionState.Success,
        transaction: TransactionUiModel,
        validation: ValidationState
    ): AddTransactionState.Success = current.copy(transaction = transaction, validation = validation)
} 