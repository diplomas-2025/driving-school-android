package ru.driving.school.data.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import ru.driving.school.data.network.models.QuestionDetailsDto
import ru.driving.school.data.network.models.QuestionDto
import ru.driving.school.data.network.models.SignIn
import ru.driving.school.data.network.models.SignUp
import ru.driving.school.data.network.models.ThemeDto
import ru.driving.school.data.network.models.TicketDetailDto
import ru.driving.school.data.network.models.TicketDto
import ru.driving.school.data.network.models.Token
import ru.driving.school.data.network.models.User
import ru.driving.school.data.network.models.UserStatistics

interface NetworkApi {

    @GET("tickets")
    suspend fun getTickets(@Header("Authorization") token: String): List<TicketDto>

    @GET("tickets/{id}")
    suspend fun getTicketById(@Path("id") id: Int): TicketDetailDto

    @GET("themes")
    suspend fun getThemes(@Header("Authorization") token: String): List<ThemeDto>

    @GET("themes/{id}")
    suspend fun getThemeById(@Path("id") id: Int, @Header("Authorization") token: String): ThemeDto

    @GET("themes/{id}/tickets")
    suspend fun getTicketsByThemeId(
        @Path("id") themeId: Int,
        @Header("Authorization") token: String
    ): List<TicketDto>

    @GET("themes/{id}/questions")
    suspend fun getQuestionsByThemeId(
        @Path("id") themeId: Int,
        @Header("Authorization") token: String
    ): List<QuestionDto>

    @GET("questions")
    suspend fun getQuestions(@Header("Authorization") token: String): List<QuestionDto>

    @GET("questions/{id}")
    suspend fun getQuestionById(@Path("id") questionId: Int): QuestionDetailsDto

    @POST("users/security/sign-up")
    suspend fun signUp(@Body body: SignUp): Response<Token>

    @POST("users/security/sign-in")
    suspend fun signIn(@Body body: SignIn): Response<Token>

    @GET("users/info")
    suspend fun getInfo(@Header("Authorization") token: String): User

    @GET("users/progress")
    suspend fun getProgress(@Header("Authorization") token: String): UserStatistics

    @POST("users/progress/question/{id}")
    suspend fun progressQuestion(@Path("id") id: Int, @Header("Authorization") token: String)
}