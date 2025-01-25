package ru.driving.school.data.network.models

enum class TicketState {
    NOT_PASSED, // Не прошел
    TRIED,      // Пробовал
    RESOLVED    // Решено
}

enum class QuestionState {
    NOT_PASSED, // Не прошел
    TRIED,      // Пробовал
    RESOLVED    // Решено
}

enum class ThemeState {
    TRIED,      // Пробовал
    RESOLVED    // Решено
}

data class TicketDto(
    val id: Int,
    val name: String
)

data class ThemeDto(
    val id: Int,
    val name: String,
    val description: String?
) {
    val state: ThemeState = ThemeState.RESOLVED
}

data class QuestionDto(
    val id: Int,
    val text: String
)

data class AnswerDto(
    val id: Int,
    val text: String,
    val isCorrect: Boolean
)

data class QuestionDetailsDto(
    val id: Int,
    val text: String,
    val answers: List<AnswerDto>
)