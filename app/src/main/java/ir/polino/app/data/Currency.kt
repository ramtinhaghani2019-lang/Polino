package ir.polino.app.data

enum class MoneyUnit { IRR, TOMAN }

object CurrencyNormalizer {
    fun toIrr(amount: Long, unit: MoneyUnit): Long = when (unit) {
        MoneyUnit.IRR -> amount
        MoneyUnit.TOMAN -> Math.multiplyExact(amount, 10L)
    }

    fun displayFromIrr(amountIrr: Long, unit: MoneyUnit): Long = when (unit) {
        MoneyUnit.IRR -> amountIrr
        MoneyUnit.TOMAN -> amountIrr / 10L
    }
}
