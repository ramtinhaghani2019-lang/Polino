package ir.polino.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

data class CategoryExpense(
    val category: String,
    val total: Long
)

@Dao
interface TransactionDao {

    @Query("SELECT * FROM transactions ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: TransactionEntity)


    @Query("""
        SELECT COALESCE(SUM(amountIrr),0)
        FROM transactions
        WHERE type = 'EXPENSE'
    """)
    fun getTotalExpense(): Flow<Long>


    @Query("""
        SELECT COUNT(*)
        FROM transactions
    """)
    fun getTransactionCount(): Flow<Int>


    @Query("""
        SELECT COALESCE(SUM(amountIrr),0)
        FROM transactions
        WHERE type = 'EXPENSE'
        AND date(createdAt / 1000,'unixepoch') = date('now')
    """)
    fun getTodayExpense(): Flow<Long>


    @Query("""
        SELECT category, SUM(amountIrr) as total
        FROM transactions
        WHERE type = 'EXPENSE'
        GROUP BY category
        ORDER BY total DESC
    """)
    fun getExpenseByCategory(): Flow<List<CategoryExpense>>
}
