package pl.edu.ur.dc131419.manageandsave.ui.components

import android.view.HapticFeedbackConstants
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import pl.edu.ur.dc131419.manageandsave.model.Envelope
import java.util.UUID

@Composable
fun AddEnvelopeDialog(
    open: Boolean,
    onOpenChange: (Boolean) -> Unit,
    initial: Envelope?,
    vibrateEnabled: Boolean,
    onSave: (Envelope) -> Unit
) {
    if (!open) return

    val view = LocalView.current

    val iconOptions = listOf("🛒", "🚗", "🏠", "💡", "🎬", "🍔", "👕", "💊", "📚", "✈️", "🎮", "💰")
    val colorOptions = listOf(
        Color(0xFFEF4444),
        Color(0xFFF97316),
        Color(0xFFEAB308),
        Color(0xFF22C55E),
        Color(0xFF3B82F6),
        Color(0xFF6366F1),
        Color(0xFFA855F7),
        Color(0xFFEC4899)
    )

    var name by rememberSaveable(initial?.id) { mutableStateOf(initial?.name.orEmpty()) }
    var limit by rememberSaveable(initial?.id) { mutableStateOf(initial?.limit?.toString().orEmpty()) }
    var selectedIcon by rememberSaveable(initial?.id) { mutableStateOf(initial?.icon ?: iconOptions.first()) }
    var selectedColorIndex by rememberSaveable(initial?.id) {
        mutableIntStateOf(
            initial?.color?.removePrefix("custom_")?.toIntOrNull() ?: 0
        )
    }

    ResponsiveM3Dialog(
        open = open,
        onDismissRequest = { onOpenChange(false) },
        maxHeight = 720.dp
    ) {
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {

            Text(
                text = if (initial == null) "Nowa koperta budżetowa" else "Edytuj kopertę",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(12.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                    Column {
                        Text("Nazwa kategorii", style = MaterialTheme.typography.labelMedium)
                        Spacer(Modifier.height(4.dp))
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            placeholder = { Text("np. Zakupy spożywcze") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                    Column {
                        Text("Limit miesięczny (zł)", style = MaterialTheme.typography.labelMedium)
                        Spacer(Modifier.height(4.dp))
                        OutlinedTextField(
                            value = limit,
                            onValueChange = { limit = it },
                            placeholder = { Text("0.00") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    Column {
                        Text("Ikona", style = MaterialTheme.typography.labelMedium)
                        Spacer(Modifier.height(4.dp))

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(6),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier.heightIn(max = 160.dp)
                        ) {
                            items(iconOptions.size) { index ->
                                val icon = iconOptions[index]
                                val selected = icon == selectedIcon

                                Surface(
                                    onClick = { selectedIcon = icon },
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(
                                        2.dp,
                                        if (selected) MaterialTheme.colorScheme.primary else Color(0xFFE5E7EB)
                                    ),
                                    color = if (selected)
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                                    else Color.White,
                                    modifier = Modifier
                                        .size(48.dp)
                                        .animateContentSize()
                                ) {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = icon,
                                            fontSize = 24.sp,
                                            modifier = Modifier.fillMaxWidth(),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Column {
                        Text("Kolor", style = MaterialTheme.typography.labelMedium)
                        Spacer(Modifier.height(4.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            colorOptions.forEachIndexed { index, color ->
                                val selected = index == selectedColorIndex
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                        .border(
                                            width = if (selected) 3.dp else 0.dp,
                                            color = if (selected) Color.Black else Color.Transparent,
                                            shape = CircleShape
                                        )
                                        .clickable { selectedColorIndex = index }
                                )
                            }
                        }
                    }
                }

            HorizontalDivider()
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                    OutlinedButton(
                        onClick = { onOpenChange(false) },
                        modifier = Modifier.weight(1f),
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
                            // Parsowanie z obsługą przecinka (PL): "10,50"
                            val numLimit = limit.replace(',', '.').toDoubleOrNull() ?: 0.0
                            val ok = name.isNotBlank() && numLimit > 0

                            if (ok) {
                                if (vibrateEnabled) {
                                    view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                                }

                                val id = initial?.id ?: UUID.randomUUID().toString()
                                onSave(
                                    Envelope(
                                        id = id,
                                        name = name.trim(),
                                        limit = numLimit,
                                        icon = selectedIcon,
                                        color = "custom_$selectedColorIndex"
                                    )
                                )
                                onOpenChange(false)
                            }
                            else {
                                if (vibrateEnabled) view.performHapticFeedback(HapticFeedbackConstants.REJECT)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (initial == null) "Utwórz" else "Zapisz",
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Clip
                        )
                    }
                }
            }
        }
    }

