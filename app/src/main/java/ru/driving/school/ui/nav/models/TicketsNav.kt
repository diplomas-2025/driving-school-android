package ru.driving.school.ui.nav.models

import kotlinx.serialization.Serializable

@Serializable
data object TicketsNav

@Serializable
data class TicketDetailsNav(
    val id: Int
)