package pl.edu.ur.dc131419.manageandsave.ui.screens

import android.text.format.DateFormat
import android.view.HapticFeedbackConstants
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import pl.edu.ur.dc131419.manageandsave.data.local.db.entity.TransactionEntity
import pl.edu.ur.dc131419.manageandsave.data.local.db.entity.TxType
import pl.edu.ur.dc131419.manageandsave.ui.components.ActionsDialog
import pl.edu.ur.dc131419.manageandsave.ui.components.AddIncomeDialog
import pl.edu.ur.dc131419.manageandsave.ui.dashboard.DashboardViewModel
import pl.edu.ur.dc131419.manageandsave.ui.settings.SettingsViewModel
import pl.edu.ur.dc131419.manageandsave.util.Haptics
import pl.edu.ur.dc131419.manageandsave.util.MonthRange
import java.util.Date
import java.util.UUID

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun IncomeScreen(
    viewModel: DashboardViewModel,
    settingsViewModel: SettingsViewModel,
    monthRange: MonthRange,
    onBack: () -> Unit
) {


    val incomeEntities by viewModel.observeIncomeHistory(monthRange.start, monthRange.end)
        .collectAsStateWithLifecycle(initialValue = emptyList())

    val sortedEntities = remember(incomeEntities) {
        incomeEntities.sortedByDescending { it.dateMillis }
    }

    var menuTx by remember { mutableStateOf<TransactionEntity?>(null) }
    var editingTx by remember { mutableStateOf<TransactionEntity?>(null) }
    var isIncomeDialogOpen by remember { mutableStateOf(false) }

    val monthIncome = remember(sortedEntities) { sortedEntities.sumOf { it.amount } }
    val monthLabel = remember(monthRange) {
        DateFormat.format("LLLL yyyy", Date(monthRange.start)).toString()
    }

    val headerBg = MaterialTheme.colorScheme.primary
    val headerContent = MaterialTheme.colorScheme.onPrimary
    val view = LocalView.current
    val settingsUi by settingsViewModel.uiState.collectAsState()
    val vibrateEnabled = settingsUi.vibrateEnabled

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // === HEADER ===
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(headerBg)
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
                                contentDescription = "Wstecz",
                                tint = headerContent
                            )
                        }

                        Spacer(Modifier.width(8.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Przychody",
                                color = headerContent,
                                fontSize = 20.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = monthLabel,
                                color = headerContent.copy(alpha = 0.8f),
                                fontSize = 13.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Button(
                            onClick = {
                                editingTx = null
                                isIncomeDialogOpen = true
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = headerContent.copy(alpha = 0.16f),
                                contentColor = headerContent
                            ),
                            shape = RoundedCornerShape(999.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                        ) {
                            Text("Dodaj", fontSize = 14.sp)
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = headerContent.copy(alpha = 0.14f),
                        contentColor = headerContent,
                        tonalElevation = 0.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(
                                text = "Dodano w miesiącu",
                                color = headerContent.copy(alpha = 0.8f),
                                fontSize = 13.sp
                            )
                            Text(
                                text = "${"%.2f".format(monthIncome)} zł",
                                color = headerContent,
                                fontSize = 28.sp
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // === LISTA ===
            if (sortedEntities.isEmpty()) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    tonalElevation = 1.dp,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Brak przychodów", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "Dodaj pierwszy przychód w tym miesiącu",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
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
                    items(
                        items = sortedEntities,
                        key = { it.id }
                    ) { entity ->
                        val date = remember(entity.dateMillis) { Date(entity.dateMillis) }

                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            tonalElevation = 1.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .combinedClickable(
                                    onClick = {
                                        editingTx = entity
                                        isIncomeDialogOpen = true
                                    },
                                    onLongClick = { menuTx = entity }
                                )
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(Modifier.weight(1f)) {
                                    Text(entity.description.ifBlank { "Przychód" })
                                    Text(
                                        "${DateFormat.format("dd.MM.yyyy", date)} • ${DateFormat.format("HH:mm", date)}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Text(
                                    text = "+${"%.2f".format(entity.amount)} zł",
                                    color = Color(0xFF16A34A)
                                )
                            }
                        }
                    }
                }
            }
        }

        // === DIALOG (dodaj/edytuj) ===
        AddIncomeDialog(
            open = isIncomeDialogOpen,
            onOpenChange = { open ->
                isIncomeDialogOpen = open
                if (!open) editingTx = null
            },
            currentMonthLabel = monthLabel,
            initial = editingTx,
            onSave = { amount, desc, dateMillis ->
                val id = editingTx?.id ?: UUID.randomUUID()
                viewModel.upsertTransaction(
                    TransactionEntity(
                        id = id,
                        type = TxType.INCOME,
                        envelopeId = null,
                        amount = amount,
                        dateMillis = dateMillis,
                        description = desc
                    )
                )
                if (vibrateEnabled) view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)

                editingTx = null
                isIncomeDialogOpen = false
            }
        )

        // === MENU AKCJI (edytuj/usuń) ===
        menuTx?.let { tx ->
            ActionsDialog(
                title = "Przychód",
                message = tx.description.ifBlank { "Przychód" },
                onDismiss = { menuTx = null },
                onEdit = {
                    editingTx = tx
                    isIncomeDialogOpen = true
                    menuTx = null
                },
                onDelete = {
                    viewModel.deleteTransaction(tx.id)

                    menuTx = null
                    if (vibrateEnabled) view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                }
            )
        }
    }
}
