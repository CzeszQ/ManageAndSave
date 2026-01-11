package pl.edu.ur.dc131419.manageandsave.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import pl.edu.ur.dc131419.manageandsave.data.BudgetRepository
import pl.edu.ur.dc131419.manageandsave.data.local.db.AppDatabase
import pl.edu.ur.dc131419.manageandsave.data.local.db.dao.EnvelopeDao
import pl.edu.ur.dc131419.manageandsave.data.local.db.dao.TransactionDao
import pl.edu.ur.dc131419.manageandsave.data.local.db.entity.EnvelopeEntity
import java.util.UUID
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private const val DB_NAME = "manage_and_save.db"

    private val SAVINGS_ID: UUID =
        UUID.fromString("11111111-1111-1111-1111-111111111111")

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

        lateinit var db: AppDatabase

        val callback = object : RoomDatabase.Callback() {

            override fun onCreate(dbSql: SupportSQLiteDatabase) {
                super.onCreate(dbSql)
                scope.launch {
                    db.envelopeDao().upsert(defaultSavingsEnvelope())
                }
            }

            override fun onOpen(dbSql: SupportSQLiteDatabase) {
                super.onOpen(dbSql)
                scope.launch {
                    // “samoleczenie” + brak duplikatów dzięki stałemu ID + upsert
                    db.envelopeDao().upsert(defaultSavingsEnvelope())
                }
            }
        }

        db = Room.databaseBuilder(context, AppDatabase::class.java, DB_NAME)
            .addCallback(callback)
            .build()

        return db
    }

    @Provides
    fun provideEnvelopeDao(database: AppDatabase): EnvelopeDao = database.envelopeDao()

    @Provides
    fun provideTransactionDao(database: AppDatabase): TransactionDao = database.transactionDao()

    @Provides
    @Singleton
    fun provideBudgetRepository(
        envelopeDao: EnvelopeDao,
        transactionDao: TransactionDao
    ): BudgetRepository = BudgetRepository(envelopeDao, transactionDao)

    private fun defaultSavingsEnvelope() = EnvelopeEntity(
        id = SAVINGS_ID,
        name = "Oszczędności",
        icon = "💰",
        color = "bg-purple-500",
        defaultLimit = 0.0,
        isSavings = true
    )
}
