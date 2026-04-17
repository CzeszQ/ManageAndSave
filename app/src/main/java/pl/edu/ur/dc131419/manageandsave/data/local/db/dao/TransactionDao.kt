package pl.edu.ur.dc131419.manageandsave.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import pl.edu.ur.dc131419.manageandsave.data.local.db.entity.TxType
import pl.edu.ur.dc131419.manageandsave.data.local.db.entity.TransactionEntity
import java.util.UUID
import androidx.room.Upsert
import pl.edu.ur.dc131419.manageandsave.data.local.db.entity.EnvelopeEntity

@Dao
interface TransactionDao {
    @Upsert
    suspend fun upsert(tx: TransactionEntity)

    @Upsert
    suspend fun upsertAll(txs: List<TransactionEntity>)
    @Query("""
        SELECT COALESCE(SUM(amount), 0)
        FROM transactions
        WHERE type = :type AND dateMillis >= :start AND dateMillis < :end
    """)
    fun observeSumForMonth(type: TxType, start: Long, end: Long): Flow<Double>

    // Wydatki "normalne" (bez oszczędności)
    @Query("""
        SELECT COALESCE(SUM(t.amount), 0)
        FROM transactions t
        INNER JOIN envelopes e ON e.id = t.envelopeId
        WHERE t.type = 'EXPENSE'
          AND e.isSavings = 0
          AND t.dateMillis >= :start AND t.dateMillis < :end
    """)
    fun observeExpensesForMonth(start: Long, end: Long): Flow<Double>

    // Oszczędności = wydatki do koperty oszczędności
    @Query("""
        SELECT COALESCE(SUM(t.amount), 0)
        FROM transactions t
        INNER JOIN envelopes e ON e.id = t.envelopeId
        WHERE t.type = 'EXPENSE'
          AND e.isSavings = 1
          AND t.dateMillis >= :start AND t.dateMillis < :end
    """)
    fun observeSavingsForMonth(start: Long, end: Long): Flow<Double>

    // Suma wydatków dla konkretnej koperty (pod "spent" na kafelku)
    @Query("""
        SELECT COALESCE(SUM(amount), 0)
        FROM transactions
        WHERE type = 'EXPENSE'
          AND envelopeId = :envelopeId
          AND dateMillis >= :start AND dateMillis < :end
    """)
    fun observeSpentForEnvelopeMonth(envelopeId: UUID, start: Long, end: Long): Flow<Double>
    @Query("""
    SELECT * FROM transactions
    WHERE type = 'EXPENSE'
      AND envelopeId = :envelopeId
      AND dateMillis >= :start AND dateMillis < :end
    ORDER BY dateMillis DESC
""")
    fun observeTransactionsForEnvelope(envelopeId: UUID, start: Long, end: Long): Flow<List<TransactionEntity>>

    @Query("DELETE FROM transactions WHERE id = :transactionId")
    suspend fun deleteTransaction(transactionId: UUID)

    @Query("""
  SELECT * FROM transactions
  WHERE type = :type AND dateMillis >= :start AND dateMillis < :end
  ORDER BY dateMillis DESC
""")
    fun observeTransactionsByType(type: TxType, start: Long, end: Long): Flow<List<TransactionEntity>>

    @Query("""
  SELECT 
        COALESCE(SUM(CASE WHEN type = 'INCOME' THEN amount ELSE 0 END), 0)
      - COALESCE(SUM(CASE WHEN type = 'EXPENSE' THEN amount ELSE 0 END), 0) 
    FROM transactions
""")
    fun observeglobalBalance(): Flow<Double>

    @Query("""
    SELECT COALESCE(SUM(t.amount), 0)
    FROM transactions t
    INNER JOIN envelopes e ON e.id = t.envelopeId
    WHERE t.type = 'EXPENSE'
      AND e.isSavings = 1
""")
    fun observeTotalSavings(): Flow<Double>


}

