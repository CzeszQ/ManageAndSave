package pl.edu.ur.dc131419.manageandsave.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "envelopes")
data class EnvelopeEntity(
    @PrimaryKey val id: UUID,
    val name: String,
    val icon: String,
    val color: String,
    val defaultLimit: Double,
    val isSavings: Boolean = false,
)
