package pl.edu.ur.dc131419.manageandsave.ui.components

import android.view.HapticFeedbackConstants
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import pl.edu.ur.dc131419.manageandsave.data.local.db.entity.TransactionEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.style.TextOverflow
@Composable
fun AddExpenseDialog(
    open: Boolean,
    onOpenChange: (Boolean) -> Unit,
    envelopeName: String,
    initial: TransactionEntity?,
    vibrateEnabled: Boolean,
    onSave: (Double, String, Date) -> Unit
) {
    if (!open) return

    val plPL = remember { Locale.Builder().setLanguage("pl").setRegion("PL").build() }
    val dateInputFmt = remember { SimpleDateFormat("dd.MM.yyyy", plPL).apply { isLenient = false } }

    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var dateText by remember { mutableStateOf("") }

    fun parseDateOrToday(text: String): Date {
        return try {
            dateInputFmt.parse(text) ?: Date()
        } catch (_: Exception) {
            Date()
        }
    }

    LaunchedEffect(open, initial?.id) {
        if (open) {
            amount = initial?.amount?.toString()?.replace('.', ',') ?: ""
            description = initial?.description.orEmpty()
            dateText = initial?.dateMillis?.let { millis -> dateInputFmt.format(Date(millis)) }
                ?: dateInputFmt.format(Date())
        }
    }

    val isEdit = initial != null
    val view = LocalView.current

    ResponsiveM3Dialog(
        open = open,
        onDismissRequest = { onOpenChange(false) }
    )  {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
                Text(
                    text = if (isEdit) "Edytuj wydatek" else "Dodaj wydatek",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = envelopeName,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Kwota
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Kwota (zł)", style = MaterialTheme.typography.labelMedium)
                    OutlinedTextField(
                        value = amount,
                        onValueChange = { amount = it },
                        placeholder = { Text("0,00") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                // Data (PL)
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Data", style = MaterialTheme.typography.labelMedium)
                    OutlinedTextField(
                        value = dateText,
                        onValueChange = { dateText = it },
                        placeholder = { Text("DD.MM.RRRR") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                // Opis
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Opis (opcjonalnie)", style = MaterialTheme.typography.labelMedium)
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        placeholder = { Text("np. Zakupy w Biedronce") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                // Przyciski
                // Przyciski
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            if (vibrateEnabled) {
                                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                            }
                            onOpenChange(false)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Anuluj",
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Clip
                        )
                    }

                    Button(
                        onClick = {
                            val numAmount = amount.replace(',', '.').toDoubleOrNull() ?: 0.0

                            if (numAmount > 0) {
                                // Mocniejsze niż CONFIRM: LONG_PRESS (jak chcesz wyraźniej)
                                if (vibrateEnabled) {
                                    view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                }

                                val txDate = parseDateOrToday(dateText)
                                val txDesc = description.trim().ifBlank { "Wydatek" }
                                onSave(numAmount, txDesc, txDate)

                                amount = ""
                                description = ""
                                onOpenChange(false)
                            } else {
                                if (vibrateEnabled) {
                                    view.performHapticFeedback(HapticFeedbackConstants.REJECT)
                                }
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (isEdit) "Zapisz" else "Dodaj",
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Clip
                        )
                    }
                }
            }
        }
    }
