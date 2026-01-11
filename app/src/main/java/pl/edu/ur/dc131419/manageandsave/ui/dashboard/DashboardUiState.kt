package pl.edu.ur.dc131419.manageandsave.ui.dashboard

import pl.edu.ur.dc131419.manageandsave.data.local.db.entity.EnvelopeEntity

data class DashboardUiState(
    val income: Double = 0.0,
    val expenses: Double = 0.0,
    val savings: Double = 0.0,
    val planned: Double = 0.0,
    val envelopes: List<EnvelopeEntity> = emptyList()
) {
    val balance: Double get() = income - expenses - savings
    val toAllocate: Double get() = income - planned
}
