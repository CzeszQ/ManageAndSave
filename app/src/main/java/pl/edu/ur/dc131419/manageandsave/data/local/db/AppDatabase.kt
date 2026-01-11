package pl.edu.ur.dc131419.manageandsave.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import pl.edu.ur.dc131419.manageandsave.data.local.db.dao.EnvelopeDao
import pl.edu.ur.dc131419.manageandsave.data.local.db.dao.TransactionDao
import pl.edu.ur.dc131419.manageandsave.data.local.db.entity.EnvelopeEntity
import pl.edu.ur.dc131419.manageandsave.data.local.db.entity.TransactionEntity

@Database(
    entities = [EnvelopeEntity::class, TransactionEntity::class],
    version = 2
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun envelopeDao(): EnvelopeDao
    abstract fun transactionDao(): TransactionDao
}