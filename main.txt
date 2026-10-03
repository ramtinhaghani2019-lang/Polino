package ir.polino.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewModelScope
import ir.polino.app.data.*
import ir.polino.app.ui.PolinoTheme
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { PolinoTheme { PolinoRoot() } }
    }
}

class MainViewModel(app: PolinoApp) : androidx.lifecycle.AndroidViewModel(app) {
    private val repo = FinanceRepository(app.database.transactionDao())
    val transactions = repo.transactions.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun add(amount: Long, unit: MoneyUnit, category: String, note: String) {
        viewModelScope.launch { repo.addManual(amount, unit, "EXPENSE", category, note) }
    }
}

@Composable
private fun PolinoRoot() {
    val app = androidx.compose.ui.platform.LocalContext.current.applicationContext as PolinoApp
    val vm: MainViewModel = viewModel(factory = object : androidx.lifecycle.ViewModelProvider.Factory {
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST") return MainViewModel(app) as T
        }
    })
    val tx by vm.transactions.collectAsStateWithLifecycle()
    var showAdd by remember { mutableStateOf(false) }
    val monthExpense = tx.filter { it.type == "EXPENSE" }.sumOf { it.amountIrr }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(onClick = { showAdd = true }) { Text("ثبت هزینه") }
        }
    ) { padding ->
        CompositionLocalProvider(androidx.compose.ui.platform.LocalLayoutDirection provides androidx.compose.ui.unit.LayoutDirection.Rtl) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 22.dp)
            ) {
                item {
                    Text("پولینو", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                    Text("پولت را بشناس، آینده‌ات را بساز", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                item {
                    Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)) {
                        Column(Modifier.fillMaxWidth().padding(22.dp)) {
                            Text("هزینه ثبت‌شده", color = MaterialTheme.colorScheme.onPrimary.copy(alpha=.8f))
                            Spacer(Modifier.height(8.dp))
                            Text("${formatMoney(monthExpense)} ریال", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(8.dp))
                            Text("${tx.size} تراکنش", color = MaterialTheme.colorScheme.onPrimary.copy(alpha=.8f))
                        }
                    }
                }
                item { Text("آخرین تراکنش‌ها", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold) }
                if (tx.isEmpty()) item {
                    Card { Box(Modifier.fillMaxWidth().padding(28.dp), contentAlignment = Alignment.Center) { Text("هنوز تراکنشی ثبت نشده") } }
                }
                items(tx.take(20), key = { it.id }) { item ->
                    ListItem(
                        headlineContent = { Text(item.category) },
                        supportingContent = { Text(item.note.ifBlank { if (item.source == "sms") "ثبت‌شده از پیامک" else "ثبت دستی" }) },
                        trailingContent = { Text("${formatMoney(item.amountIrr)} ریال", fontWeight = FontWeight.Bold) },
                        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surface)
                    )
                }
            }
        }
    }

    if (showAdd) AddTransactionDialog(onDismiss = { showAdd = false }, onSave = { a,u,c,n -> vm.add(a,u,c,n); showAdd=false })
}

@Composable
private fun AddTransactionDialog(
    onDismiss: () -> Unit,
    onSave: (Long, MoneyUnit, String, String) -> Unit
) {
    var amount by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf(MoneyUnit.IRR) }
    var category by remember { mutableStateOf("خرید") }
    var note by remember { mutableStateOf("") }
    val categories = listOf("غذا", "خرید", "پوشاک", "خودرو", "خانه", "درمان", "تفریح", "آموزش", "انتقال وجه", "سایر")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("ثبت تراکنش") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(value = amount, onValueChange = { amount = it.filter(Char::isDigit) }, label = { Text("مبلغ") }, singleLine = true)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    FilterChip(selected = unit == MoneyUnit.IRR, onClick = { unit = MoneyUnit.IRR }, label = { Text("ریال") })
                    Spacer(Modifier.width(8.dp))
                    FilterChip(selected = unit == MoneyUnit.TOMAN, onClick = { unit = MoneyUnit.TOMAN }, label = { Text("تومان") })
                }
                Text("دسته‌بندی")
                androidx.compose.foundation.lazy.LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(categories.size) { i -> FilterChip(selected = category == categories[i], onClick = { category = categories[i] }, label = { Text(categories[i]) }) }
                }
                OutlinedTextField(value = note, onValueChange = { note = it }, label = { Text("توضیح") }, singleLine = true)
                if (amount.isNotBlank() && unit == MoneyUnit.TOMAN) {
                    Text("معادل ${formatMoney((amount.toLongOrNull() ?: 0L) * 10)} ریال ذخیره می‌شود", style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            Button(onClick = { amount.toLongOrNull()?.takeIf { it > 0 }?.let { onSave(it, unit, category, note) } }) { Text("ذخیره") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("لغو") } }
    )
}

private fun formatMoney(value: Long): String = NumberFormat.getNumberInstance(Locale.US).format(value)
