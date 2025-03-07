package fr.isen.fournier.isensmartcompanion.models

import java.io.Serializable

data class Event(
    val id: String,
    val title: String,
    val date: String,
    val description: String,
    val category: String,
    val location: String
) : Serializable