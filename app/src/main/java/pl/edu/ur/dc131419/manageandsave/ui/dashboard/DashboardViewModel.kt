package pl.edu.ur.dc131419.manageandsave.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import pl.edu.ur.dc131419.manageandsave.data.BudgetRepository
import pl.edu.ur.dc131419.manageandsave.data.local.db.entity.EnvelopeEntity
import pl.edu.ur.dc131419.manageandsave.data.local.db.entity.TransactionEntity
import pl.edu.ur.dc131419.manageandsave.data.local.db.entity.TxType
import pl.edu.ur.dc131419.manageandsave.util.MonthRange
import pl.edu.ur.dc131419.manageandsave.util.currentMonthRange
import java.util.UUID

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repo: BudgetRepository
) : ViewModel() {

    private val monthRange = MutableStateFlow(currentMonthRange())

    fun setMonthRange(start: Long, end: Long) {
        monthRange.value = MonthRange(start, end)
    }

    fun observeIncomeHistory(start: Long, end: Long): Flow<List<TransactionEntity>> =
        repo.observeTransactionsByType(TxType.INCOME, start, end)

    fun addIncome(amount: Double, description: String, dateMillis: Long) {
        viewModelScope.launch {
            val tx = TransactionEntity(
                id = UUID.randomUUID(),
                type = TxType.INCOME,
                amount = amount,
                description = description,
                dateMillis = dateMillis,
                envelopeId = null // jeśli pole jest nullable
            )
            repo.addTransaction(tx)
        }
    }

    fun upsertIncome(id: UUID?, amount: Double, description: String, dateMillis: Long) {
        viewModelScope.launch {
            repo.addTransaction(
                TransactionEntity(
                    id = id ?: UUID.randomUUID(),
                    type = TxType.INCOME,
                    envelopeId = null,
                    amount = amount,
                    dateMillis = dateMillis,
                    description = description
                )
            )
        }
    }

    fun observeSpentForEnvelope(envelopeId: UUID, start: Long, end: Long): Flow<Double> {
        return repo.observeSpentForEnvelope(envelopeId, start, end)
    }

    fun observeTransactionsForEnvelope(envelopeId: UUID, start: Long, end: Long): Flow<List<TransactionEntity>> {
        return repo.observeTransactionsForEnvelope(envelopeId, start, end)
    }

    fun upsertTransaction(tx: TransactionEntity) {
        viewModelScope.launch { repo.addTransaction(tx) }
    }
    fun upsertEnvelope(entity: EnvelopeEntity) = viewModelScope.launch {
        repo.addEnvelope(entity)
    }

    fun deleteEnvelope(id: UUID) = viewModelScope.launch {
        repo.deleteEnvelope(id)
    }

    fun deleteTransaction(transactionId: UUID) {
        viewModelScope.launch {
            repo.deleteTransaction(transactionId)
        }
    }
    private data class TotalsA(
        val globalBalance: Double,
        val income: Double,
        val expenses: Double,
        val monthSavings: Double,
        val planned: Double
    )

    private data class Totals(
        val globalBalance: Double,
        val totalSavings: Double,
        val monthIncome: Double,
        val monthExpenses: Double,
        val monthSavings: Double,
        val monthPlanned: Double
    )

    val uiState: StateFlow<DashboardUiState> =
        monthRange.flatMapLatest { range ->
            val start = range.start
            val end = range.end

            val totalsAFlow: Flow<TotalsA> = combine(
                repo.observeGlobalBalance(),
                repo.observeIncome(start, end),
                repo.observeExpenses(start, end),
                repo.observeSavings(start, end),
                repo.observePlannedAllocation()
            ) { globalBalance, income, expenses, monthSavings, planned ->
                TotalsA(globalBalance, income, expenses, monthSavings, planned)
            }

            val totalsFlow: Flow<Totals> = combine(
                totalsAFlow,
                repo.observeTotalSavings()
            ) { a, totalSavings ->
                Totals(
                    globalBalance = a.globalBalance,
                    totalSavings = totalSavings,
                    monthIncome = a.income,
                    monthExpenses = a.expenses,
                    monthSavings = a.monthSavings,
                    monthPlanned = a.planned
                )
            }

            combine(totalsFlow, repo.observeEnvelopes()) { totals, envelopes ->
                DashboardUiState(
                    globalBalance = totals.globalBalance,
                    totalSavings = totals.totalSavings,
                    monthIncome = totals.monthIncome,
                    monthExpenses = totals.monthExpenses,
                    monthSavings = totals.monthSavings,
                    monthPlanned = totals.monthPlanned,
                    envelopes = envelopes
                )
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DashboardUiState())



}

