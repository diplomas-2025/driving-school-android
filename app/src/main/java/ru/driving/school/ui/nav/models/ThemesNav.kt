package ru.driving.school.ui.nav.models

import kotlinx.serialization.Serializable

@Serializable
data object ThemesNav

@Serializable
data class ThemeDetailsNav(
    val id: Int
)