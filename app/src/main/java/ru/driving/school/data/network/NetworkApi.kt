package ru.driving.school.data.network

import retrofit2.http.GET
import retrofit2.http.Path
import ru.driving.school.data.network.models.QuestionDetailsDto
import ru.driving.school.data.network.models.QuestionDto
import ru.driving.school.data.network.models.ThemeDto
import ru.driving.school.data.network.models.TicketDto

interface NetworkApi {

    @GET("tickets")
    suspend fun getTickets(): List<TicketDto>

    @GET("themes")
    suspend fun getThemes(): List<ThemeDto>

    @GET("themes/{id}")
    suspend fun getThemeById(@Path("id") id: Int): ThemeDto

    @GET("themes/{id}/tickets")
    suspend fun getTicketsByThemeId(@Path("id") themeId: Int): List<TicketDto>

    @GET("themes/{id}/questions")
    suspend fun getQuestionsByThemeId(@Path("id") themeId: Int): List<QuestionDto>

    @GET("questions")
    suspend fun getQuestions(): List<QuestionDto>

    @GET("questions/{id}")
    suspend fun getQuestionById(@Path("id") questionId: Int): QuestionDetailsDto
}