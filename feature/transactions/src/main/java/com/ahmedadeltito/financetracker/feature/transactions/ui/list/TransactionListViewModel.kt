package com.ahmedadeltito.financetracker.feature.transactions.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ahmedadeltito.financetracker.common.Result
import com.ahmedadeltito.financetracker.common.di.CoroutineDispatchers
import com.ahmedadeltito.financetracker.domain.usecase.transaction.DeleteTransactionUseCase
import com.ahmedadeltito.financetracker.domain.usecase.transaction.GetTransactionStatsUseCase
import com.ahmedadeltito.financetracker.domain.usecase.transaction.GetTransactionsUseCase
import com.ahmedadeltito.financetracker.domain.usecase.transaction.GetTransactionsUseCase.Params
import com.ahmedadeltito.financetracker.ui.mapper.TransactionMapper.toUiModel
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
    private val getTransactionStatsUseCase: GetTransactionStatsUseCase,
    private val deleteTransactionUseCase: DeleteTransactionUseCase,
    private val dispatchers: CoroutineDispatchers
) : ViewModel() {

    private val _state = MutableStateFlow<TransactionListState>(TransactionListState.Loading)
    val state: StateFlow<TransactionListState> = _state.asStateFlow()

    private val _sideEffect = Channel<TransactionListSideEffect>()
    val sideEffect = _sideEffect.receiveAsFlow()

    init {
        loadTransactionsAndStats()
    }

    fun onEvent(event: TransactionListEvent) {
        when (event) {
            TransactionListEvent.Refresh -> loadTransactionsAndStats()
            is TransactionListEvent.OnTransactionClick -> {
                viewModelScope.launch(dispatchers.main) {
                    _sideEffect.send(TransactionListSideEffect.NavigateToTransactionDetails(event.transactionId))
                }
            }

            TransactionListEvent.OnAddTransactionClick -> {
                viewModelScope.launch(dispatchers.main) {
                    _sideEffect.send(TransactionListSideEffect.NavigateToAddTransaction)
                }
            }

            is TransactionListEvent.DeleteTransaction -> deleteTransaction(event.transactionId)
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
                getTransactionsUseCase(parameters = Params.All),
                getTransactionStatsUseCase(
                    GetTransactionStatsUseCase.Params(
                        startDate = startOfMonth,
                        endDate = now
                    )
                )
            ) { transactionsResult, statsResult ->
                when {
                    transactionsResult is Result.Error -> {
                        TransactionListState.Error(
                            message = transactionsResult.exception.message
                                ?: "Failed to load transactions"
                        )
                    }

                    statsResult is Result.Error -> {
                        TransactionListState.Error(
                            message = statsResult.exception.message
                                ?: "Failed to load transaction stats"
                        )
                    }

                    transactionsResult is Result.Success && statsResult is Result.Success -> {
                        val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.getDefault())
                        TransactionListState.Success(
                            transactions = transactionsResult.data.map { it.toUiModel() },
                            totalIncome = currencyFormatter.format(statsResult.data.totalIncome),
                            totalExpense = currencyFormatter.format(statsResult.data.totalExpense),
                            balance = currencyFormatter.format(statsResult.data.balance)
                        )
                    }

                    else -> TransactionListState.Loading
                }
            }.collectLatest { state ->
                _state.value = state
            }
        }
    }

    private fun deleteTransaction(transactionId: String) {
        viewModelScope.launch(dispatchers.io) {
            val result =
                deleteTransactionUseCase(parameters = DeleteTransactionUseCase.Params(transactionId))
            when (result) {
                is Result.Success -> {
                    loadTransactionsAndStats()
                    _sideEffect.send(
                        TransactionListSideEffect.ShowSnackbar("Transaction deleted successfully")
                    )
                }

                is Result.Error -> {
                    _sideEffect.send(
                        TransactionListSideEffect.ShowSnackbar(
                            result.exception.message ?: "Failed to delete transaction"
                        )
                    )
                    loadTransactionsAndStats() // Reload to ensure UI is in sync
                }

                is Result.Loading -> _state.value = TransactionListState.Loading
            }
        }
    }
} 