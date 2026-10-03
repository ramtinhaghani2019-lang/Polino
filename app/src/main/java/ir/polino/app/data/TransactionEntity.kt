package ir.polino.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val amountIrr: Long,
    val originalAmount: Long,
    val originalUnit: String,
    val type: String,
    val category: String,
    val note: String = "",
    val bank: String? = null,
    val cardLast4: String? = null,
    val merchant: String? = null,
    val source: String = "manual",
    val createdAt: Long = System.currentTimeMillis()
)
