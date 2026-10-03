package ir.polino.app.sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import ir.polino.app.PolinoApp
import ir.polino.app.data.TransactionEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BankSmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return
        val pending = goAsync()
        val body = Telephony.Sms.Intents.getMessagesFromIntent(intent).joinToString("") { it.messageBody.orEmpty() }
        val parsed = SmsParser.parse(body) ?: run { pending.finish(); return }
        val app = context.applicationContext as PolinoApp
        CoroutineScope(Dispatchers.IO).launch {
            try {
                app.database.transactionDao().insert(
                    TransactionEntity(
                        amountIrr = parsed.amountIrr,
                        originalAmount = parsed.originalAmount,
                        originalUnit = parsed.originalUnit.name,
                        type = parsed.type,
                        category = "نیازمند بررسی",
                        cardLast4 = parsed.cardLast4,
                        merchant = parsed.merchant,
                        source = "sms"
                    )
                )
            } finally {
                pending.finish()
            }
        }
    }
}
