package pl.edu.ur.dc131419.manageandsave.ui.components

import android.text.format.DateFormat
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Date

@Composable
fun BudgetSummaryHeader(
    currentDate: Date,

    totalBudget: Double,      // GLOBAL: globalBalance + totalSavings
    totalSavings: Double,     // GLOBAL: suma oszczędności (koperta savings)

    monthIncome: Double,      // MIESIĄC
    monthSpent: Double,       // MIESIĄC: expenses bez savings (u Ciebie observeExpensesForMonth)
    monthSavings: Double,     // MIESIĄC: savings (u Ciebie observeSavingsForMonth)
    monthPlanned: Double,     // MIESIĄC: suma defaultLimit (alokacja)

    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onAddIncomeClick: () -> Unit,
    onSettingsClick: () -> Unit = {}
){
    val monthBalance = monthIncome - monthSpent - monthSavings
    val monthToAllocate = monthIncome - monthPlanned
    val monthProgressBase = monthPlanned.coerceAtLeast(0.0)
    val monthProgress = if (monthProgressBase > 0) ((monthSpent + monthSavings) / monthProgressBase).coerceIn(0.0, 1.0) else 0.0

    val currentMonth = DateFormat.format("LLLL yyyy", currentDate).toString()

    val headerBg = MaterialTheme.colorScheme.primary
    val headerContent = MaterialTheme.colorScheme.onPrimary

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(headerBg)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Column {
            //  PIERWSZY WIERSZ: MENU + „Mój Budżet" + PRZYCHÓD


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Bottom  // ← TO NAPRAWIA!
            ) {
                // === LEWA: MENU + „Mój Budżet" ===
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ){
                    var menuExpanded by remember { mutableStateOf(false) }
                    Box {
                        IconButton(onClick = { menuExpanded = true }) {
                            Icon(Icons.Default.MoreVert, null, tint = headerContent)
                        }
                        DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                            DropdownMenuItem(text = { Text("Ustawienia") }, onClick = {
                                menuExpanded = false
                                onSettingsClick()
                            })
                        }
                    }
                    Text(
                        text = "Mój Budżet",
                        color = headerContent,
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                Spacer(Modifier.weight(1f))

                //  PRAWA GRUPA: przycisk Przychód
                Button(
                    onClick = onAddIncomeClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = headerContent.copy(alpha = 0.16f),
                        contentColor = headerContent
                    ),
                    shape = RoundedCornerShape(999.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text("Przychód", fontSize = 14.sp)
                }
            }

            Spacer(Modifier.height(12.dp))

            // GLOBAL: ponad miesiącem
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = headerContent.copy(alpha = 0.14f),
                contentColor = headerContent,
                tonalElevation = 0.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(16.dp)) {
                    SummaryRow("Łączny budżet", totalBudget)
                    SummaryRow("Oszczędności", totalSavings)
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onPrevMonth) {
                    Icon(Icons.Default.KeyboardArrowLeft, null, tint = headerContent)
                }
                Text(
                    text = currentMonth,
                    color = headerContent,
                    style = MaterialTheme.typography.bodyLarge
                )
                IconButton(onClick = onNextMonth) {
                    Icon(Icons.Default.KeyboardArrowRight, null, tint = headerContent)
                }
            }

            Spacer(Modifier.height(12.dp))

            // Karta na headerze: fioletowa, ale jaśniejsza (overlay z onPrimary)
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = headerContent.copy(alpha = 0.14f),
                contentColor = headerContent,
                tonalElevation = 0.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(16.dp)) {
                    SummaryRow("Przychód (miesiąc)", monthIncome)
                    SummaryRow("Zaplanowano", monthPlanned)
                    SummaryRow("Nadwyżka / niedobór planu", monthToAllocate)
                    SummaryRow("Wydano", monthSpent + monthSavings)
                    SummaryRow("Bilans (miesiąc)", monthBalance)

                    Spacer(Modifier.height(8.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(999.dp))
                            .background(headerContent.copy(alpha = 0.25f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(monthProgress.toFloat())
                                .background(headerContent)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryRow(label: String, amount: Double) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = LocalContentColor.current.copy(alpha = 0.80f), fontSize = 14.sp)
        Text("${"%.2f".format(amount)} zł", color = LocalContentColor.current, fontSize = 16.sp)
    }
}
