package ir.polino.app.data

class FinanceRepository(private val dao: TransactionDao) {
    val transactions = dao.observeAll()

    suspend fun addManual(
        amount: Long,
        unit: MoneyUnit,
        type: String,
        category: String,
        note: String
    ) {
        val irr = CurrencyNormalizer.toIrr(amount, unit)
        dao.insert(
            TransactionEntity(
                amountIrr = irr,
                originalAmount = amount,
                originalUnit = unit.name,
                type = type,
                category = category,
                note = note
            )
        )
    }
}
