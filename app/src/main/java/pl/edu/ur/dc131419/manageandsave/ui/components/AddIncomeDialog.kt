package pl.edu.ur.dc131419.manageandsave.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.style.TextOverflow
import pl.edu.ur.dc131419.manageandsave.data.local.db.entity.TransactionEntity
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import java.util.Date

@Composable
fun AddIncomeDialog(
    open: Boolean,
    onOpenChange: (Boolean) -> Unit,
    currentMonthLabel: String,
    initial: TransactionEntity? = null,
    showHistoryButton: Boolean = false,
    onShowHistory: () -> Unit = {},
    onSave: (amount: Double, description: String, dateMillis: Long) -> Unit
) {
    if (!open) return

    val plPL = remember { java.util.Locale("pl", "PL") }
    val dateInputFmt = remember {
        java.text.SimpleDateFormat("dd.MM.yyyy", plPL).apply { isLenient = false }
    }

    fun parseDateOrToday(text: String): java.util.Date {
        return try {
            dateInputFmt.parse(text) ?: java.util.Date()
        } catch (_: Exception) {
            java.util.Date()
        }
    }
    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var dateText by remember { mutableStateOf("") }

    LaunchedEffect(open, initial?.id) {
        if (open) {
            amount = initial?.amount?.toString()?.replace('.', ',') ?: ""
            description = initial?.description.orEmpty()
            dateText = initial?.dateMillis?.let { millis ->
                dateInputFmt.format(Date(millis))
            } ?: dateInputFmt.format(Date())
        }
    }

    val isEdit = initial != null

    ResponsiveM3Dialog(
        open = open,
        onDismissRequest = { onOpenChange(false) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = if (isEdit) "Edytuj przychód" else "Dodaj przychód",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = currentMonthLabel,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (showHistoryButton) {
                        TextButton(
                            onClick = {
                                onOpenChange(false)
                                onShowHistory()
                            }
                        ) {
                            Text(
                                text = "Historia",
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Kwota (zł)", style = MaterialTheme.typography.labelMedium)
                    OutlinedTextField(
                        value = amount,
                        onValueChange = { amount = it },
                        placeholder = { Text("0,00") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Data", style = MaterialTheme.typography.labelMedium)
                    OutlinedTextField(
                        value = dateText,
                        onValueChange = { dateText = it },
                        placeholder = { Text("DD.MM.RRRR") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Opis (opcjonalnie)", style = MaterialTheme.typography.labelMedium)
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        placeholder = { Text("np. Pensja, Premia") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { onOpenChange(false) },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) { Text("Anuluj") }

                    Button(
                        onClick = {
                            val numAmount = amount.replace(',', '.').toDoubleOrNull() ?: 0.0
                            if (numAmount > 0) {
                                val txDate = parseDateOrToday(dateText)
                                val txDesc = description.trim().ifBlank { "Przychód" }
                                onSave(numAmount, txDesc, txDate.time)
                                onOpenChange(false)
                            }
                        },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) { Text(if (isEdit) "Zapisz" else "Dodaj") }
                }
            }
        }
    }

