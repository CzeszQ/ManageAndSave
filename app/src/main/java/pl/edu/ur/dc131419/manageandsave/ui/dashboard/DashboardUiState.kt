package pl.edu.ur.dc131419.manageandsave.ui.dashboard

import pl.edu.ur.dc131419.manageandsave.data.local.db.entity.EnvelopeEntity

data class DashboardUiState(
    // global
    val globalBalance: Double = 0.0,    // saldo kont bieżących
    val totalSavings: Double = 0.0,     // suma wszystkich aktywów/oszczędności

    // miesięczne
    val monthIncome: Double = 0.0,
    val monthExpenses: Double = 0.0,
    val monthSavings: Double = 0.0,
    val monthPlanned: Double = 0.0,
    val envelopes: List<EnvelopeEntity> = emptyList()
) {
    val totalBudget: Double get() = globalBalance + totalSavings
    val monthBalance: Double get() = monthIncome - monthExpenses - monthSavings
    val monthToAllocate: Double get() = monthIncome - monthPlanned
}

