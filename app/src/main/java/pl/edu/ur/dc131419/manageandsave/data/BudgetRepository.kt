package pl.edu.ur.dc131419.manageandsave.data

import kotlinx.coroutines.flow.Flow
import pl.edu.ur.dc131419.manageandsave.data.local.db.dao.EnvelopeDao
import pl.edu.ur.dc131419.manageandsave.data.local.db.dao.TransactionDao
import pl.edu.ur.dc131419.manageandsave.data.local.db.entity.EnvelopeEntity
import pl.edu.ur.dc131419.manageandsave.data.local.db.entity.TxType
import pl.edu.ur.dc131419.manageandsave.data.local.db.entity.TransactionEntity
import java.util.UUID

class BudgetRepository(
    private val envelopeDao: EnvelopeDao,
    private val transactionDao: TransactionDao
) {

    fun observeGlobalBalance(): Flow<Double> = transactionDao.observeglobalBalance()
    fun observePlannedAllocation(): Flow<Double> =
        envelopeDao.observePlannedAllocation()

    fun observeIncome(start: Long, end: Long): Flow<Double> =
        transactionDao.observeSumForMonth(TxType.INCOME, start, end)

    fun observeExpenses(start: Long, end: Long): Flow<Double> =
        transactionDao.observeExpensesForMonth(start, end)

    fun observeSavings(start: Long, end: Long): Flow<Double> =
        transactionDao.observeSavingsForMonth(start, end)

    fun observeTotalSavings(): Flow<Double> =
        transactionDao.observeTotalSavings()

    suspend fun addTransaction(tx: TransactionEntity) {
        transactionDao.upsert(tx)
    }
    suspend fun deleteTransaction(transactionId: UUID) {
        transactionDao.deleteTransaction(transactionId)
    }

    fun observeTransactionsByType(type: TxType, start: Long, end: Long)
            : Flow<List<TransactionEntity>> =
        transactionDao.observeTransactionsByType(type, start, end)

    fun observeTransactionsForEnvelope(envelopeId: UUID, start: Long, end: Long): Flow<List<TransactionEntity>> {
        return transactionDao.observeTransactionsForEnvelope(envelopeId, start, end)
    }

    fun observeSpentForEnvelope(envelopeId: UUID, start: Long, end: Long): Flow<Double> {
        return transactionDao.observeSpentForEnvelopeMonth(envelopeId, start, end)
    }
    fun observeEnvelopes() = envelopeDao.observeEnvelopes()
    suspend fun addEnvelope(envelope: EnvelopeEntity) {
        envelopeDao.upsert(envelope)
    }

    suspend fun deleteEnvelope(envelopeId: UUID) {
        envelopeDao.deleteByIdIfNotSavings(envelopeId)
    }

    suspend fun ensureSavingsEnvelope() {
        if (envelopeDao.getSavingsEnvelopeOrNull() == null) {
            envelopeDao.upsert(
                pl.edu.ur.dc131419.manageandsave.data.local.db.entity.EnvelopeEntity(
                    id = UUID.randomUUID(),
                    name = "Oszczędności",
                    icon = "💰",
                    color = "bg-purple-500",
                    defaultLimit = 0.0,
                    isSavings = true
                )
            )
        }
    }
}
