package ru.driving.school.ui.nav.models

import kotlinx.serialization.Serializable

@Serializable
data object QuestionsNav

@Serializable
data class QuestionDetailsNav(
    val id: Int
)