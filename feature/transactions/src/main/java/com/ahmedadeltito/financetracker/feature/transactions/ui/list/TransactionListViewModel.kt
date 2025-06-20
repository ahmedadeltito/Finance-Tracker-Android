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
import com.ahmedadeltito.financetracker.feature.transactions.ui.list.TransactionListEvent.HardDeleteTransaction
import com.ahmedadeltito.financetracker.feature.transactions.ui.list.TransactionListEvent.OnAddTransactionClick
import com.ahmedadeltito.financetracker.feature.transactions.ui.list.TransactionListEvent.OnTransactionClick
import com.ahmedadeltito.financetracker.feature.transactions.ui.list.TransactionListEvent.Refresh
import com.ahmedadeltito.financetracker.feature.transactions.ui.list.TransactionListEvent.SoftDeleteTransaction
import com.ahmedadeltito.financetracker.feature.transactions.ui.list.TransactionListEvent.UndoDelete
import com.ahmedadeltito.financetracker.feature.transactions.ui.list.TransactionListSideEffect.NavigateToAddTransaction
import com.ahmedadeltito.financetracker.feature.transactions.ui.list.TransactionListSideEffect.NavigateToTransactionDetails
import com.ahmedadeltito.financetracker.feature.transactions.ui.list.TransactionListSideEffect.ShowUndoSnackbar
import com.ahmedadeltito.financetracker.ui.mapper.TransactionMapper.toUiModel
import com.ahmedadeltito.financetracker.ui.model.TransactionUiModel
import com.ahmedadeltito.financetracker.ui.model.TransactionUiModel.Companion.EMPTY
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
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

    private val _state = MutableStateFlow<TransactionListState>(TransactionListState.Loading)
    val state: StateFlow<TransactionListState> = _state.asStateFlow()

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
            is OnTransactionClick -> viewModelScope.launch(dispatchers.main) {
                _sideEffect.send(NavigateToTransactionDetails(event.transactionId))
            }
            is OnAddTransactionClick -> viewModelScope.launch(dispatchers.main) {
                _sideEffect.send(NavigateToAddTransaction)
            }
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

            combine(
                getTransactionsUseCase(parameters = GetTransactionsUseCase.Params.All),
                getTransactionStatsUseCase(
                    GetTransactionStatsUseCase.Params(
                        startDate = startOfMonth,
                        endDate = now
                    )
                )
            ) { transactionsResult, statsResult ->
                when {
                    transactionsResult is Result.Success && statsResult is Result.Success -> {
                        val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.getDefault())
                        TransactionListState.Success(
                            transactions = transactionsResult.data.map { it.toUiModel() },
                            totalIncome = currencyFormatter.format(statsResult.data.totalIncome),
                            totalExpense = currencyFormatter.format(statsResult.data.totalExpense),
                            balance = currencyFormatter.format(statsResult.data.balance)
                        )
                    }
                    statsResult is Result.Error -> TransactionListState.Error(
                        message = statsResult.exception.message
                            ?: "Failed to load transaction stats"
                    )
                    transactionsResult is Result.Error -> TransactionListState.Error(
                        message = transactionsResult.exception.message
                            ?: "Failed to load transactions"
                    )
                    else -> TransactionListState.Loading
                }
            }.collectLatest { state ->
                _state.value = state
            }
        }
    }

    private fun deleteTransaction(transaction: TransactionUiModel) {
        viewModelScope.launch(dispatchers.io) {
            when (val result = deleteTransactionUseCase(parameters = DeleteTransactionUseCase.Params(transaction.id))) {
                is Result.Loading -> _state.value = TransactionListState.Loading
                is Result.Success -> {
                    loadTransactionsAndStats()
                    _sideEffect.send(
                        ShowUndoSnackbar(
                            transactionId = null,
                            message = "Transaction deleted successfully",
                        )
                    )
                }
                is Result.Error -> {
                    loadTransactionsAndStats()
                    _sideEffect.send(
                        ShowUndoSnackbar(
                            transactionId = null,
                            message = result.exception.message ?: "Failed to delete transaction"
                        )
                    )
                }
            }
        }
    }

    private fun softDeleteTransaction(transactionId: String) {
        viewModelScope.launch(dispatchers.io) {
            when (val result = getTransactionUseCase(GetTransactionUseCase.Params(transactionId))) {
                is Result.Loading -> _state.value = TransactionListState.Loading
                is Result.Success<Transaction> -> {
                    transactionToBeDeleted = result.data.toUiModel().copy(isSoftDeleted = true)

                    val currentState = _state.value as? TransactionListState.Success ?: return@launch
                    _state.value = currentState.copy(
                        transactions = currentState.transactions.filterNot { it.id == transactionId }
                    )

                    _sideEffect.send(
                        ShowUndoSnackbar(
                            transactionId = transactionToBeDeleted.id,
                            message = "Transaction deleted",
                        )
                    )
                }
                is Result.Error -> _state.value = TransactionListState.Error(
                    message = result.exception.message ?: "Failed to load transaction"
                )
            }
        }
    }

    private fun undoDelete() {
        viewModelScope.launch(dispatchers.io) {
            if (transactionToBeDeleted != EMPTY) {
                loadTransactionsAndStats()
                _sideEffect.send(
                    ShowUndoSnackbar(
                        transactionId = null,
                        message = "Transaction restored"
                    )
                )
                transactionToBeDeleted = EMPTY
            }
        }
    }
} 