package com.ahmedadeltito.financetracker.feature.transactions.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ahmedadeltito.financetracker.common.Result
import com.ahmedadeltito.financetracker.common.di.CoroutineDispatchers
import com.ahmedadeltito.financetracker.domain.entity.Transaction
import com.ahmedadeltito.financetracker.domain.usecase.transaction.DeleteTransactionUseCase
import com.ahmedadeltito.financetracker.domain.usecase.transaction.GetTransactionStatsUseCase
import com.ahmedadeltito.financetracker.domain.usecase.transaction.GetTransactionUseCase
import com.ahmedadeltito.financetracker.domain.usecase.transaction.GetTransactionsUseCase
import com.ahmedadeltito.financetracker.feature.transactions.mapper.toUiModel
import com.ahmedadeltito.financetracker.feature.transactions.ui.list.TransactionListEvent.HardDeleteTransaction
import com.ahmedadeltito.financetracker.feature.transactions.ui.list.TransactionListEvent.OnAddTransactionClick
import com.ahmedadeltito.financetracker.feature.transactions.ui.list.TransactionListEvent.OnTransactionClick
import com.ahmedadeltito.financetracker.feature.transactions.ui.list.TransactionListEvent.Refresh
import com.ahmedadeltito.financetracker.feature.transactions.ui.list.TransactionListEvent.SoftDeleteTransaction
import com.ahmedadeltito.financetracker.feature.transactions.ui.list.TransactionListEvent.UndoDelete
import com.ahmedadeltito.financetracker.feature.transactions.ui.list.TransactionListSideEffect.NavigateToAddTransaction
import com.ahmedadeltito.financetracker.feature.transactions.ui.list.TransactionListSideEffect.NavigateToTransactionDetails
import com.ahmedadeltito.financetracker.feature.transactions.ui.list.TransactionListSideEffect.ShowUndoSnackbar
import com.ahmedadeltito.financetracker.ui.model.TransactionUiModel
import com.ahmedadeltito.financetracker.ui.model.TransactionUiModel.Companion.EMPTY
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class TransactionListViewModel @Inject constructor(
    private val getTransactionsUseCase: GetTransactionsUseCase,
    private val getTransactionUseCase: GetTransactionUseCase,
    private val getTransactionStatsUseCase: GetTransactionStatsUseCase,
    private val deleteTransactionUseCase: DeleteTransactionUseCase,
    private val dispatchers: CoroutineDispatchers
) : ViewModel() {

    private val _state = MutableStateFlow<TransactionListUiState>(TransactionListUiState.Loading)
    val state: StateFlow<TransactionListUiState> = _state.asStateFlow()

    private val _sideEffect = Channel<TransactionListSideEffect>()
    val sideEffect = _sideEffect.receiveAsFlow()

    private var transactionToBeDeleted: TransactionUiModel = EMPTY

    init {
        loadTransactionsAndStats()
    }

    fun onEvent(event: TransactionListEvent) {
        when (event) {
            is Refresh -> loadTransactionsAndStats()
            is SoftDeleteTransaction -> softDeleteTransaction(event.transactionId)
            is HardDeleteTransaction -> deleteTransaction(transactionToBeDeleted)
            is UndoDelete -> undoDelete()
            is OnTransactionClick -> sendSideEffect(NavigateToTransactionDetails(event.transactionId))
            is OnAddTransactionClick -> sendSideEffect(NavigateToAddTransaction)
        }
    }

    private fun sendSideEffect(sideEffect: TransactionListSideEffect){
        viewModelScope.launch(dispatchers.main) {
            _sideEffect.send(sideEffect)
        }
    }

    private fun loadTransactionsAndStats() {
        viewModelScope.launch(dispatchers.io) {
            val now = Date()
            val startOfMonth = Calendar.getInstance().apply {
                time = now
                set(Calendar.DAY_OF_MONTH, 1)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.time

            val getTransactions: Flow<Result<List<Transaction>>> =
                getTransactionsUseCase(GetTransactionsUseCase.Params.All)
            val getTransactionStats: Flow<Result<GetTransactionStatsUseCase.TransactionStats>> =
                getTransactionStatsUseCase(
                    GetTransactionStatsUseCase.Params(startDate = startOfMonth, endDate = now)
                )
            combine(
                getTransactions,
                getTransactionStats
            ) { transactionsResult, statsResult ->
                when {
                    transactionsResult is Result.Success && statsResult is Result.Success -> {
                        val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.getDefault())
                        TransactionListUiState.Success(
                            transactions = transactionsResult.data.map { it.toUiModel() },
                            totalIncome = currencyFormatter.format(statsResult.data.totalIncome),
                            totalExpense = currencyFormatter.format(statsResult.data.totalExpense),
                            balance = currencyFormatter.format(statsResult.data.balance)
                        )
                    }
                    statsResult is Result.Error -> TransactionListUiState.Error(
                        message = statsResult.exception.message
                            ?: "Failed to load transaction stats"
                    )
                    transactionsResult is Result.Error -> TransactionListUiState.Error(
                        message = transactionsResult.exception.message
                            ?: "Failed to load transactions"
                    )
                    else -> TransactionListUiState.Loading
                }
            }.collectLatest { state ->
                _state.value = state
            }
        }
    }

    private fun deleteTransaction(transaction: TransactionUiModel) {
        viewModelScope.launch(dispatchers.io) {
            val deleteTransaction: Result<Unit> =
                deleteTransactionUseCase(DeleteTransactionUseCase.Params(transaction.id))
            when (deleteTransaction) {
                is Result.Loading -> _state.value = TransactionListUiState.Loading
                is Result.Success -> {
                    loadTransactionsAndStats()
                    sendSideEffect(
                        ShowUndoSnackbar(
                            transactionId = null,
                            message = "Transaction deleted successfully"
                        )
                    )
                }
                is Result.Error -> {
                    loadTransactionsAndStats()
                    sendSideEffect(
                        ShowUndoSnackbar(
                            transactionId = null,
                            message = deleteTransaction.exception.message ?: "Failed to delete transaction"
                        )
                    )
                }
            }
        }
    }

    private fun softDeleteTransaction(transactionId: String) {
        viewModelScope.launch(dispatchers.io) {
            val getTransaction: Result<Transaction> =
                getTransactionUseCase(GetTransactionUseCase.Params(transactionId))
            when (getTransaction) {
                is Result.Loading -> _state.value = TransactionListUiState.Loading
                is Result.Success -> {
                    transactionToBeDeleted = getTransaction.data.toUiModel().copy(isSoftDeleted = true)

                    val currentState = _state.value as? TransactionListUiState.Success ?: return@launch
                    _state.value = currentState.copy(
                        transactions = currentState.transactions.filterNot { it.id == transactionId }
                    )

                    sendSideEffect(
                        ShowUndoSnackbar(
                            transactionId = transactionToBeDeleted.id,
                            message = "Transaction deleted",
                        )
                    )
                }
                is Result.Error -> _state.value = TransactionListUiState.Error(
                    message = getTransaction.exception.message ?: "Failed to load transaction"
                )
            }
        }
    }

    private fun undoDelete() {
        viewModelScope.launch(dispatchers.io) {
            if (transactionToBeDeleted != EMPTY) {
                loadTransactionsAndStats()
                sendSideEffect(ShowUndoSnackbar(transactionId = null, message = "Transaction restored"))
                transactionToBeDeleted = EMPTY
            }
        }
    }
} 