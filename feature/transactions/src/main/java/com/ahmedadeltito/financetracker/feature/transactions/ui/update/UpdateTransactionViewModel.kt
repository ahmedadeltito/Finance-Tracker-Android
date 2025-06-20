package com.ahmedadeltito.financetracker.feature.transactions.ui.update

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.ahmedadeltito.financetracker.common.NoParameters
import com.ahmedadeltito.financetracker.common.Result
import com.ahmedadeltito.financetracker.common.di.CoroutineDispatchers
import com.ahmedadeltito.financetracker.domain.entity.Transaction
import com.ahmedadeltito.financetracker.domain.entity.TransactionCategory
import com.ahmedadeltito.financetracker.domain.usecase.category.GetTransactionCategoriesUseCase
import com.ahmedadeltito.financetracker.domain.usecase.transaction.GetTransactionUseCase
import com.ahmedadeltito.financetracker.domain.usecase.transaction.GetTransactionUseCase.Params
import com.ahmedadeltito.financetracker.domain.usecase.transaction.UpdateTransactionUseCase
import com.ahmedadeltito.financetracker.feature.transactions.navigation.transactionIdArg
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
import javax.inject.Inject

@HiltViewModel
class UpdateTransactionViewModel @Inject constructor(
    private val updateTransactionUseCase: UpdateTransactionUseCase,
    private val getTransactionUseCase: GetTransactionUseCase,
    private val getTransactionCategoriesUseCase: GetTransactionCategoriesUseCase,
    override val dispatchers: CoroutineDispatchers,
    savedStateHandle: SavedStateHandle
) : BaseTransactionFormViewModel<UpdateTransactionState, UpdateTransactionState.Success>(
    dispatchers = dispatchers,
    initialState = UpdateTransactionState.Loading
) {

    private val transactionId: String? = savedStateHandle[transactionIdArg]

    private val _sideEffect = Channel<UpdateTransactionSideEffect>()
    val sideEffect = _sideEffect.receiveAsFlow()

    val state: StateFlow<UpdateTransactionState> = baseState

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
            val getTransaction: Result<Transaction> = getTransactionUseCase(Params(transactionId))
            when (getTransaction) {
                is Result.Loading -> _state.value = UpdateTransactionState.Loading
                is Result.Success<Transaction> -> loadCategories(
                    transactionUiModel = getTransaction.data.toUiModel()
                )
                is Result.Error -> _state.value = UpdateTransactionState.Error(
                    message = getTransaction.exception.message ?: "Failed to load transaction"
                )
            }
        }
    }

    private fun loadCategories(transactionUiModel: TransactionUiModel) {
        viewModelScope.launch(dispatchers.io) {
            val getTransactionCategories: Flow<Result<List<TransactionCategory>>> =
                getTransactionCategoriesUseCase(NoParameters)
            getTransactionCategories.collectLatest { result ->
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

    private fun updateTransaction() {
        val currentState = _state.value as? UpdateTransactionState.Success ?: return
        val formData = currentState.transaction
        val validation = TransactionFormValidator.validateForm(formData)

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

            val updateTransaction: Result<Transaction> =
                updateTransactionUseCase(UpdateTransactionUseCase.Params(transaction))
            when (updateTransaction) {
                is Result.Success -> {
                    _sideEffect.send(UpdateTransactionSideEffect.ShowSnackbar("Transaction updated successfully"))
                    _sideEffect.send(UpdateTransactionSideEffect.NavigateBack)
                }
                is Result.Error -> _state.value = UpdateTransactionState.Error(
                    message = updateTransaction.exception.message ?: "Failed to save transaction"
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

    override fun copySuccessState(
        current: UpdateTransactionState.Success,
        transaction: TransactionUiModel,
        validation: ValidationState
    ): UpdateTransactionState.Success = current.copy(transaction = transaction, validation = validation)
} 