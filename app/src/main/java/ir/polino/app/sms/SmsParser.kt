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
    private val persianDigits = mapOf('۰' to '0','۱' to '1','۲' to '2','۳' to '3','۴' to '4','۵' to '5','۶' to '6','۷' to '7','۸' to '8','۹' to '9','٠' to '0','١' to '1','٢' to '2','٣' to '3','٤' to '4','٥' to '5','٦' to '6','٧' to '7','٨' to '8','٩' to '9')

    private fun normalize(text: String): String = buildString {
        text.forEach { append(persianDigits[it] ?: it) }
    }.replace('٬', ',')

    fun parse(raw: String): ParsedSmsTransaction? {
        val text = normalize(raw)
        if (listOf("رمز", "پویا", "کد ورود", "otp").any { text.contains(it, true) }) return null

        val unit = when {
            text.contains("تومان") -> MoneyUnit.TOMAN
            text.contains("ریال") -> MoneyUnit.IRR
            else -> return null
        }

        val amountRegex = Regex("(?:مبلغ\\s*[:：]?\\s*)?([0-9][0-9,]{2,})\\s*(ریال|تومان)")
        val matches = amountRegex.findAll(text).toList()
        if (matches.isEmpty()) return null
        val amount = matches.first().groupValues[1].replace(",", "").toLongOrNull() ?: return null

        val type = when {
            listOf("برداشت", "خرید", "پرداخت").any { text.contains(it) } -> "EXPENSE"
            listOf("واریز", "واریزي", "واریزی").any { text.contains(it) } -> "INCOME"
            else -> "UNKNOWN"
        }

        val card = Regex("(?:کارت|card)[^0-9]{0,10}(?:\\*+)?([0-9]{4})", RegexOption.IGNORE_CASE)
            .find(text)?.groupValues?.getOrNull(1)

        val merchant = Regex("(?:پذیرنده|فروشگاه)\\s*[:：]?\\s*([^\\n]+)")
            .find(text)?.groupValues?.getOrNull(1)?.trim()

        return ParsedSmsTransaction(
            amountIrr = CurrencyNormalizer.toIrr(amount, unit),
            originalAmount = amount,
            originalUnit = unit,
            type = type,
            cardLast4 = card,
            merchant = merchant,
            confidence = if (type == "UNKNOWN") 65 else 85
        )
    }
}
