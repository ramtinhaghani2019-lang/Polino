package ir.polino.app.sms

import ir.polino.app.data.CurrencyNormalizer
import ir.polino.app.data.MoneyUnit

data class ParsedSmsTransaction(
    val amountIrr: Long,
    val originalAmount: Long,
    val originalUnit: MoneyUnit,
    val type: String,
    val cardLast4: String?,
    val merchant: String?,
    val confidence: Int
)

object SmsParser {

    private fun normalize(text: String): String {
        val digits = mapOf(
            '۰' to '0','۱' to '1','۲' to '2',
            '۳' to '3','۴' to '4','۵' to '5',
            '۶' to '6','۷' to '7','۸' to '8',
            '۹' to '9'
        )

        return text.map { digits[it] ?: it }
            .joinToString("")
            .replace(",", "")
    }

    fun parse(raw: String): ParsedSmsTransaction? {

        val text = normalize(raw)

        val unit = when {
            text.contains("ریال") -> MoneyUnit.RIAL
            text.contains("تومان") -> MoneyUnit.TOMAN
            else -> MoneyUnit.TOMAN
        }

        // تشخیص نوع تراکنش
        val type = when {

            text.contains("+") ||
            text.contains("واریز") ||
            text.contains("افزایش موجودی") ->
                "INCOME"

            text.contains("-") ||
            text.contains("برداشت") ||
            text.contains("خرج شد") ||
            text.contains("خرید") ||
            text.contains("از حساب شما") ->
                "EXPENSE"

            else ->
                "UNKNOWN"
        }

        // استخراج مبلغ
        val amountRegex = when {

            text.contains("مبلغ") ->
                Regex("""مبلغ[:\s]+(\d+)""")

            text.contains("از حساب شما") ->
                Regex("""(\d+)\s*ریال""")

            else ->
                Regex("""[+-]?\s*(\d+)""")
        }

        val amountMatch = amountRegex.find(text)
            ?: return null

        val amount = amountMatch.groupValues[1]
            .toLongOrNull()
            ?: return null


        // کارت
        val card = Regex("""(\d{4})""")
            .find(text)
            ?.groupValues
            ?.firstOrNull()


        // نام فروشگاه/توضیح
        val merchant = when {
            text.contains("محک") -> "خیریه محک"
            text.contains("امیرآباد") -> "امیرآباد"
            else -> null
        }


        return ParsedSmsTransaction(
            amountIrr = CurrencyNormalizer.toIrr(amount, unit),
            originalAmount = amount,
            originalUnit = unit,
            type = type,
            cardLast4 = card,
            merchant = merchant,
            confidence = if (type == "UNKNOWN") 60 else 95
        )
    }
}
