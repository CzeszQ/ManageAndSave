package pl.edu.ur.dc131419.manageandsave.data.local.db

import androidx.room.TypeConverter
import java.util.UUID

object Converters {
    @TypeConverter
    fun fromUuid(uuid: UUID?): String? = uuid?.toString()

    @TypeConverter
    fun toUuid(value: String?): UUID? = value?.let(UUID::fromString)
}