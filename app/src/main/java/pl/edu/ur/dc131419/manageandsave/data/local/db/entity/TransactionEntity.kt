package pl.edu.ur.dc131419.manageandsave.data.local.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.descriptors.SerialDescriptor
import java.util.UUID

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = EnvelopeEntity::class,
            parentColumns = ["id"],
            childColumns = ["envelopeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("envelopeId"), Index("dateMillis"), Index("type")]
)
data class TransactionEntity(
    @PrimaryKey val id: UUID,
    val type: TxType,
    val envelopeId: UUID?,
    val amount: Double,
    val dateMillis: Long,
    val description: String,
)

