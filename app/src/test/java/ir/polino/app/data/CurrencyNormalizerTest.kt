package ir.polino.app.data

import org.junit.Assert.assertEquals
import org.junit.Test

class CurrencyNormalizerTest {
    @Test fun tomanToIrr() {
        assertEquals(5_000_000L, CurrencyNormalizer.toIrr(500_000L, MoneyUnit.TOMAN))
    }

    @Test fun irrStaysIrr() {
        assertEquals(5_000_000L, CurrencyNormalizer.toIrr(5_000_000L, MoneyUnit.IRR))
    }

    @Test fun irrToTomanForDisplay() {
        assertEquals(500_000L, CurrencyNormalizer.displayFromIrr(5_000_000L, MoneyUnit.TOMAN))
    }
}
