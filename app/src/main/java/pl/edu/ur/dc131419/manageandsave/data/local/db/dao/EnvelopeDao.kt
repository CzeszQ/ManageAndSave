package pl.edu.ur.dc131419.manageandsave.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import pl.edu.ur.dc131419.manageandsave.data.local.db.entity.EnvelopeEntity
import java.util.UUID

@Dao
interface EnvelopeDao {

    @Query("SELECT * FROM envelopes ORDER BY name ASC")
    fun observeEnvelopes(): Flow<List<EnvelopeEntity>>

    @Query("SELECT * FROM envelopes WHERE id = :id LIMIT 1")
    suspend fun getById(id: UUID): EnvelopeEntity?

    @Query("SELECT * FROM envelopes WHERE isSavings = 1 LIMIT 1")
    suspend fun getSavingsEnvelopeOrNull(): EnvelopeEntity?

    @Query("SELECT COALESCE(SUM(defaultLimit), 0) FROM envelopes WHERE isSavings = 0")
    fun observePlannedAllocation(): Flow<Double>

    @Upsert
    suspend fun upsert(envelope: EnvelopeEntity)

    @Upsert
    suspend fun upsertAll(envelopes: List<EnvelopeEntity>)

    @Query("DELETE FROM envelopes WHERE id = :id AND isSavings = 0")
    suspend fun deleteByIdIfNotSavings(id: UUID)
}