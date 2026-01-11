package pl.edu.ur.dc131419.manageandsave.model

data class Envelope(
    val id: String,
    val name: String,
    val icon: String,
    val limit: Double,
    val spent: Double = 0.0,
    val color: String
)
