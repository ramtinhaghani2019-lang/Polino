package ir.polino.app.sms

import ir.polino.app.data.MoneyUnit
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SmsParserTest {
    @Test fun parsesPersianTomanExpense() {
        val p = SmsParser.parse("برداشت مبلغ ۲,۵۰۰,۰۰۰ تومان از کارت ****1234 در فروشگاه نمونه")!!
        assertEquals(25_000_000L, p.amountIrr)
        assertEquals(2_500_000L, p.originalAmount)
        assertEquals(MoneyUnit.TOMAN, p.originalUnit)
        assertEquals("EXPENSE", p.type)
        assertEquals("1234", p.cardLast4)
    }

    @Test fun ignoresOtp() {
        assertNull(SmsParser.parse("رمز پویا ۱۲۳۴۵۶ برای خرید ۵۰۰,۰۰۰ ریال"))
    }
}
