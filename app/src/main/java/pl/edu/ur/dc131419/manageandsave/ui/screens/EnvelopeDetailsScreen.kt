package pl.edu.ur.dc131419.manageandsave.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import pl.edu.ur.dc131419.manageandsave.data.local.db.entity.TransactionEntity
import pl.edu.ur.dc131419.manageandsave.data.local.db.entity.TxType
import pl.edu.ur.dc131419.manageandsave.model.Envelope
import pl.edu.ur.dc131419.manageandsave.ui.components.ActionsDialog
import pl.edu.ur.dc131419.manageandsave.ui.components.AddExpenseDialog
import pl.edu.ur.dc131419.manageandsave.ui.dashboard.DashboardViewModel
import pl.edu.ur.dc131419.manageandsave.ui.settings.SettingsViewModel
import pl.edu.ur.dc131419.manageandsave.ui.theme.tailwindBgToColor
import pl.edu.ur.dc131419.manageandsave.util.MonthRange
import java.text.SimpleDateFormat
import java.util.Date
import java.util.UUID
import kotlin.math.abs
import kotlin.math.min

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EnvelopeDetailsScreen(
    envelope: Envelope,
    viewModel: DashboardViewModel,
    settingsViewModel: SettingsViewModel,
    monthRange: MonthRange,
    onBack: () -> Unit
) {
    val settingsUi by settingsViewModel.uiState.collectAsState()
    val vibrateEnabled = settingsUi.vibrateEnabled

    // Obserwacja transakcji z bazy
    val transactionsEntities by viewModel.observeTransactionsForEnvelope(
        UUID.fromString(envelope.id),
        monthRange.start,
        monthRange.end
    ).collectAsStateWithLifecycle(initialValue = emptyList())

    // Menu + dialog transakcji
    var menuTx by remember { mutableStateOf<TransactionEntity?>(null) }
    var editingTx by remember { mutableStateOf<TransactionEntity?>(null) }
    var isExpenseDialogOpen by remember { mutableStateOf(false) }

    // Sortuj transakcje od najnowszych
    val sortedEntities = remember(transactionsEntities) {
        transactionsEntities.sortedByDescending { it.dateMillis }
    }

    // Obliczenia procentowe
    val percentage = if (envelope.limit > 0) (envelope.spent / envelope.limit) * 100.0 else 0.0
    val remaining = envelope.limit - envelope.spent
    val isOverBudget = remaining < 0

    val plPL = remember { java.util.Locale.Builder().setLanguage("pl").setRegion("PL").build() }
    val dateFmt = remember { SimpleDateFormat("dd.MM.yyyy", plPL) }
    val timeFmt = remember { SimpleDateFormat("HH:mm", plPL) }

    val cs = MaterialTheme.colorScheme
    val view = LocalView.current

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(cs.background)
        ) {
            // Header (kolor z koperty – custom)
            val headerColor = tailwindBgToColor(envelope.color)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(headerColor)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(999.dp))
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Wróć",
                                tint = Color.White
                            )
                        }

                        Spacer(Modifier.width(8.dp))

                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(envelope.icon, fontSize = 28.sp, color = Color.White)
                        }

                        Spacer(Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = envelope.name,
                                color = Color.White,
                                fontSize = 20.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${"%.2f".format(envelope.spent)} / ${"%.2f".format(envelope.limit)} zł",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 14.sp
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    // Stats card (na headerze)
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = Color.White.copy(alpha = 0.15f),
                        tonalElevation = 0.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Wykorzystano", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                                Text("${percentage.toInt()}%", color = Color.White, fontSize = 20.sp)
                            }

                            Spacer(Modifier.height(10.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(12.dp)
                                    .clip(RoundedCornerShape(999.dp))
                                    .background(Color.White.copy(alpha = 0.2f))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .fillMaxWidth((min(percentage, 100.0) / 100.0).toFloat())
                                        .background(Color.White, RoundedCornerShape(999.dp))
                                )
                            }

                            Spacer(Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Limit", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                                    Text("${"%.2f".format(envelope.limit)} zł", color = Color.White, fontSize = 16.sp)
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        if (isOverBudget) "Przekroczono o" else "Pozostało",
                                        color = Color.White.copy(alpha = 0.8f),
                                        fontSize = 12.sp
                                    )
                                    Text(
                                        "${"%.2f".format(abs(remaining))} zł",
                                        color = if (isOverBudget) Color(0xFFFFE4E6) else Color.White,
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Transactions header + button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Historia transakcji", style = MaterialTheme.typography.titleMedium, color = cs.onBackground)

                Button(
                    onClick = {
                        editingTx = null
                        isExpenseDialogOpen = true
                    },
                    shape = RoundedCornerShape(999.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = cs.primary,
                        contentColor = cs.onPrimary
                    )
                ) {
                    Text("Dodaj wydatek", fontSize = 14.sp)
                }
            }

            // Lista transakcji
            if (sortedEntities.isEmpty()) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    tonalElevation = 1.dp,
                    shadowElevation = 2.dp,
                    color = cs.surface,
                    contentColor = cs.onSurface,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Brak transakcji", color = cs.onSurface, fontSize = 16.sp)
                        Spacer(Modifier.height(4.dp))
                        Text("Dodaj pierwszy wydatek do tej koperty", color = cs.onSurfaceVariant, fontSize = 13.sp)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(sortedEntities, key = { it.id }) { entity ->
                        val date = remember(entity.dateMillis) { Date(entity.dateMillis) }

                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            tonalElevation = 1.dp,
                            shadowElevation = 2.dp,
                            color = cs.surface,
                            contentColor = cs.onSurface,
                            modifier = Modifier
                                .fillMaxWidth()
                                .combinedClickable(
                                    onClick = {},
                                    onLongClick = { menuTx = entity }
                                )
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        entity.description,
                                        fontSize = 16.sp,
                                        color = cs.onSurface,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(Modifier.height(6.dp))
                                    Text(
                                        "${dateFmt.format(date)} • ${timeFmt.format(date)}",
                                        fontSize = 13.sp,
                                        color = cs.onSurfaceVariant
                                    )
                                }

                                Spacer(Modifier.width(12.dp))

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "-${"%.2f".format(entity.amount)} zł",
                                        fontSize = 16.sp,
                                        color = cs.error
                                    )

                                    IconButton(onClick = { menuTx = entity }) {
                                        Icon(
                                            Icons.Filled.MoreVert,
                                            contentDescription = "Więcej",
                                            tint = cs.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialog dodawania/edycji wydatku
    AddExpenseDialog(
        open = isExpenseDialogOpen,
        onOpenChange = { open ->
            isExpenseDialogOpen = open
            if (!open) editingTx = null
        },
        envelopeName = envelope.name,
        initial = editingTx,
        vibrateEnabled = vibrateEnabled,
        onSave = { amount, description, date ->
            val id = editingTx?.id ?: UUID.randomUUID()
            viewModel.upsertTransaction(
                TransactionEntity(
                    id = id,
                    type = TxType.EXPENSE,
                    amount = amount,
                    description = description,
                    dateMillis = date.time,
                    envelopeId = UUID.fromString(envelope.id)
                )
            )


            editingTx = null
            isExpenseDialogOpen = false
        }
    )

    // Menu akcji dla transakcji
    menuTx?.let { tx ->
        ActionsDialog(
            title = "Transakcja",
            message = tx.description,
            onDismiss = { menuTx = null },
            onEdit = {
                editingTx = tx
                isExpenseDialogOpen = true
                menuTx = null
            },
            onDelete = {
                viewModel.deleteTransaction(tx.id)
                menuTx = null
            }
        )
    }
}
