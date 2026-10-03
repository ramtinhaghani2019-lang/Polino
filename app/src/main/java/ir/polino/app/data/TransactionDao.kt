package ir.polino.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [TransactionEntity::class],
    version = 1,
    exportSchema = false
)
abstract class PolinoDatabase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao

    companion object {

        @Volatile
        private var INSTANCE: PolinoDatabase? = null

        fun get(context: Context): PolinoDatabase {
            return INSTANCE ?: synchronized(this) {

                Room.databaseBuilder(
                    context.applicationContext,
                    PolinoDatabase::class.java,
                    "polino.db"
                )
                .build()
                .also {
                    INSTANCE = it
                }
            }
        }
    }
}
