package pl.edu.ur.dc131419.manageandsave.ui.screens

import android.text.format.DateFormat
import android.view.HapticFeedbackConstants
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import pl.edu.ur.dc131419.manageandsave.data.local.db.entity.EnvelopeEntity
import pl.edu.ur.dc131419.manageandsave.data.local.db.entity.TransactionEntity
import pl.edu.ur.dc131419.manageandsave.data.local.db.entity.TxType
import pl.edu.ur.dc131419.manageandsave.model.Envelope
import pl.edu.ur.dc131419.manageandsave.ui.components.ActionsDialog
import pl.edu.ur.dc131419.manageandsave.ui.components.AddEnvelopeDialog
import pl.edu.ur.dc131419.manageandsave.ui.components.AddExpenseDialog
import pl.edu.ur.dc131419.manageandsave.ui.components.AddIncomeDialog
import pl.edu.ur.dc131419.manageandsave.ui.components.BudgetSummaryHeader
import pl.edu.ur.dc131419.manageandsave.ui.components.EnvelopeCard
import pl.edu.ur.dc131419.manageandsave.ui.components.ViewMode
import pl.edu.ur.dc131419.manageandsave.ui.dashboard.DashboardViewModel
import pl.edu.ur.dc131419.manageandsave.ui.settings.SettingsViewModel
import pl.edu.ur.dc131419.manageandsave.util.monthRangeForDate
import java.util.Calendar
import java.util.Date
import java.util.UUID

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BudgetScreen(viewModel: DashboardViewModel, settingsViewModel: SettingsViewModel) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val balance = uiState.balance

    var currentDate by remember { mutableStateOf(Date()) }
    var viewMode by remember { mutableStateOf(ViewMode.LIST) }
    var selectedEnvelopeId by remember { mutableStateOf<String?>(null) }

    // Dodawanie/edycja koperty
    var isEnvelopeDialogOpen by remember { mutableStateOf(false) }
    var editingEnvelope by remember { mutableStateOf<Envelope?>(null) }

    // Dialog wydatku (dodaj/edytuj tx)
    var isExpenseDialogOpen by remember { mutableStateOf(false) }
    var editingTx by remember { mutableStateOf<TransactionEntity?>(null) }
    var envelopeForExpenseDialog by remember { mutableStateOf<Envelope?>(null) }

    // Menu akcji (koperty)
    var menuEnvelope by remember { mutableStateOf<Envelope?>(null) }

    var isIncomeDialogOpen by remember { mutableStateOf(false) }
    var showIncomeHistory by remember { mutableStateOf(false) }

    val currentMonthLabel = DateFormat.format("LLLL yyyy", currentDate).toString()
    val income = uiState.income
    val totalBudget = uiState.planned
    val totalSpent = uiState.expenses + uiState.savings

    var showSettings by remember { mutableStateOf(false) }
    // ustawienia wibracje
    val settingsUi by settingsViewModel.uiState.collectAsState()
    val vibrateEnabled = settingsUi.vibrateEnabled


    // Jedno źródło haptyki (Views API)
    val view = LocalView.current
    fun hapticConfirm() {
        if (vibrateEnabled) view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
    }
    fun hapticReject() {
        if (vibrateEnabled) view.performHapticFeedback(HapticFeedbackConstants.REJECT)
    }
    fun hapticTap() {
        if (vibrateEnabled) view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
    }

    val monthRange = remember(currentDate) { monthRangeForDate(currentDate) }

    val envelopes = uiState.envelopes.map { entity ->
        val spent by viewModel.observeSpentForEnvelope(
            entity.id,
            monthRange.start,
            monthRange.end
        ).collectAsStateWithLifecycle(initialValue = 0.0)

        Envelope(
            id = entity.id.toString(),
            name = entity.name,
            icon = entity.icon,
            limit = entity.defaultLimit,
            spent = spent,
            color = entity.color
        )
    }

    fun previousMonth() {
        val cal = Calendar.getInstance().apply { time = currentDate }
        cal.add(Calendar.MONTH, -1)
        currentDate = cal.time
        val newRange = monthRangeForDate(currentDate)
        viewModel.setMonthRange(newRange.start, newRange.end)
    }

    fun nextMonth() {
        val cal = Calendar.getInstance().apply { time = currentDate }
        cal.add(Calendar.MONTH, 1)
        currentDate = cal.time
        val newRange = monthRangeForDate(currentDate)
        viewModel.setMonthRange(newRange.start, newRange.end)
    }

    val selectedEnvelope = envelopes.find { it.id == selectedEnvelopeId }
    if (selectedEnvelope != null) {
        EnvelopeDetailsScreen(
            envelope = selectedEnvelope,
            viewModel = viewModel,
            settingsViewModel = settingsViewModel,
            monthRange = monthRange,
            onBack = { selectedEnvelopeId = null }
        )
        return
    }

    if (showSettings) {
        SettingsRoute(vm = settingsViewModel, onBack = { showSettings = false })

        return
    }

    if (showIncomeHistory) {
        IncomeScreen(
            viewModel = viewModel,
            settingsViewModel = settingsViewModel,
            monthRange = monthRange,
            onBack = { showIncomeHistory = false }
        )
        return
    }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    editingEnvelope = null
                    isEnvelopeDialogOpen = true
                    hapticTap()
                },
                icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                text = { Text("Nowa koperta") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            BudgetSummaryHeader(
                currentDate = currentDate,
                income = income,
                totalBudget = totalBudget,
                totalSpent = totalSpent,
                balance = balance,
                onPrevMonth = { previousMonth() },
                onNextMonth = { nextMonth() },
                onAddIncomeClick = { isIncomeDialogOpen = true;  },
                onSettingsClick = { showSettings = true; hapticTap() }
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Koperty budżetowe",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f)
                    )

                    SingleChoiceSegmentedButtonRow {
                        SegmentedButton(
                            selected = viewMode == ViewMode.LIST,
                            onClick = { viewMode = ViewMode.LIST;  },
                            shape = SegmentedButtonDefaults.itemShape(0, 2),
                            label = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Lista") }
                        )

                        SegmentedButton(
                            selected = viewMode == ViewMode.GRID,
                            onClick = { viewMode = ViewMode.GRID; },
                            shape = SegmentedButtonDefaults.itemShape(1, 2),
                            label = { Icon(Icons.Filled.GridView, contentDescription = "Kafelki") }
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                val openExpenseDialog: (Envelope) -> Unit = { envelope ->
                    envelopeForExpenseDialog = envelope
                    editingTx = null
                    isExpenseDialogOpen = true
                    hapticTap()
                }

                if (viewMode == ViewMode.LIST) {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 24.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(items = envelopes, key = { it.id }) { env ->
                            EnvelopeCard(
                                envelope = env,
                                viewMode = ViewMode.LIST,
                                onAddExpenseClick = { openExpenseDialog(env) },
                                modifier = Modifier.combinedClickable(
                                    onClick = { selectedEnvelopeId = env.id; hapticTap() },
                                    onLongClick = { menuEnvelope = env },
                                    hapticFeedbackEnabled = vibrateEnabled
                                )
                            )
                        }
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(envelopes, key = { it.id }) { env ->
                            EnvelopeCard(
                                envelope = env,
                                viewMode = ViewMode.GRID,
                                onAddExpenseClick = { openExpenseDialog(env) },
                                modifier = Modifier.combinedClickable(
                                    onClick = { selectedEnvelopeId = env.id; hapticTap() },
                                    onLongClick = { menuEnvelope = env },
                                    hapticFeedbackEnabled = vibrateEnabled
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    // === MENU AKCJI (koperta) ===
    menuEnvelope?.let { env ->
        ActionsDialog(
            title = "Koperta",
            message = env.name,
            onDismiss = { menuEnvelope = null },
            onEdit = {
                editingEnvelope = env
                isEnvelopeDialogOpen = true
                menuEnvelope = null
                hapticTap()
            },
            onDelete = {
                viewModel.deleteEnvelope(UUID.fromString(env.id))

                menuEnvelope = null
            }
        )
    }

    // === DIALOG (koperta) ===
    AddEnvelopeDialog(
        open = isEnvelopeDialogOpen,
        onOpenChange = { isEnvelopeDialogOpen = it },
        initial = editingEnvelope,
        vibrateEnabled = vibrateEnabled,
        onSave = { envelope ->
            viewModel.upsertEnvelope(
                EnvelopeEntity(
                    id = UUID.fromString(envelope.id),
                    name = envelope.name,
                    icon = envelope.icon,
                    color = envelope.color,
                    defaultLimit = envelope.limit,
                    isSavings = false
                )
            )
            // Jeśli w dialogu też masz CONFIRM, usuń to poniżej, żeby nie było podwójnie.


            editingEnvelope = null
            isEnvelopeDialogOpen = false
        }
    )

    // === DIALOG (wydatek) ===
    envelopeForExpenseDialog?.let { envelope ->
        AddExpenseDialog(
            open = isExpenseDialogOpen,
            onOpenChange = { open ->
                isExpenseDialogOpen = open
                if (!open) {
                    editingTx = null
                    envelopeForExpenseDialog = null
                }
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
                hapticConfirm()

                editingTx = null
                envelopeForExpenseDialog = null
                isExpenseDialogOpen = false
            }
        )
    }

    // === DIALOG (przychód) ===
    AddIncomeDialog(
        open = isIncomeDialogOpen,
        onOpenChange = { isIncomeDialogOpen = it },
        currentMonthLabel = currentMonthLabel,
        initial = null,
        showHistoryButton = true,
        onShowHistory = {
            isIncomeDialogOpen = false
            showIncomeHistory = true
        },
        onSave = { amount, desc, dateMillis ->
            viewModel.addIncome(amount, desc, dateMillis)
            hapticConfirm()
        }
    )
}
