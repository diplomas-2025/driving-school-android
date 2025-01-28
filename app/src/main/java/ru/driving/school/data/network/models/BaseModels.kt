package ru.driving.school.data.network.models

enum class TicketState {
    NOT_PASSED, // Не прошел
    RESOLVED    // Решено
}

enum class QuestionState {
    NOT_PASSED, // Не прошел
    RESOLVED    // Решено
}

enum class ThemeState {
    NOT_PASSED,      // Пробовал
    RESOLVED    // Решено
}

data class TicketDto(
    val id: Int,
    val name: String,
    val status: TicketState
)

data class TicketDetailDto(
    val id: Int,
    val name: String,
    val theme: ThemeDto,
    val questions: List<QuestionDetailsDto>
)

data class ThemeDto(
    val id: Int,
    val name: String,
    val description: String?,
    val status: ThemeState
)

data class QuestionDto(
    val id: Int,
    val text: String,
    val status: QuestionState
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

data class User(
    val username: String
)

data class SignUp(
    val firstName: String,
    val username: String,
    val password: String
)

data class SignIn(
    val username: String,
    val password: String
)

data class UserStatistics(
    val countThemes: Int = 100,
    val countUseThemes: Int = 0,

    val countTickets: Int = 100,
    val countUseTickets: Int = 0,

    val countQuestions: Int = 100,
    val countUseQuestions: Int = 0,
)

data class Token(
    val accessToken: String
)