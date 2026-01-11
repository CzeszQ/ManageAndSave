package pl.edu.ur.dc131419.manageandsave.model

import java.util.Date

data class BudgetTransaction(
    val id: String,
    val amount: Double,
    val date: Date,
    val description: String
)
