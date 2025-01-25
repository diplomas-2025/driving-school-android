package ru.driving.school.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ru.driving.school.data.network.NetworkApi
import ru.driving.school.data.network.models.AnswerDto
import ru.driving.school.data.network.models.QuestionDetailsDto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionDetailsScreen(
    id: Int,
    networkApi: NetworkApi,
    navController: NavController,
) {
    var question by remember { mutableStateOf<QuestionDetailsDto?>(null) }
    var selectedAnswerId by remember { mutableStateOf<Int?>(null) }
    var answerStatus by remember { mutableStateOf<AnswerStatus?>(null) }

    LaunchedEffect(Unit) {
        question = networkApi.getQuestionById(id)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {  },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentAlignment = Alignment.Center
        ) {
            question?.let {
                QuestionDetailsContent(it, selectedAnswerId, answerStatus) { answerId ->
                    if (answerStatus == null) {
                        selectedAnswerId = answerId
                        answerStatus = if (it.answers.firstOrNull { it.id == answerId }?.isCorrect == true) {
                            AnswerStatus.Correct
                        } else {
                            AnswerStatus.Incorrect(it.answers.first { it.isCorrect }.text)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuestionDetailsContent(
    question: QuestionDetailsDto,
    selectedAnswerId: Int?,
    answerStatus: AnswerStatus?,
    onAnswerSelected: (Int) -> Unit
) {
    // Используем Card для аккуратного оформления вопроса
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(8.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Заголовок вопроса
            Text(
                text = question.text,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Перебираем и отображаем варианты ответов
            question.answers.forEach { answer ->
                AnswerRow(
                    answer = answer,
                    isSelected = selectedAnswerId == answer.id,
                    onClick = { onAnswerSelected(answer.id) }
                )
                Spacer(modifier = Modifier.height(8.dp)) // Отступ между ответами
            }

            // Показываем статус ответа (правильный/неправильный)
            answerStatus?.let {
                Spacer(modifier = Modifier.height(16.dp))
                if (it is AnswerStatus.Incorrect) {
                    Text(
                        text = "Неправильно! Правильный ответ: ${it.correctAnswer}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                } else {
                    Text(
                        text = "Правильно!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun AnswerRow(
    answer: AnswerDto,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        // Чекбокс для отображения выбора ответа
        Checkbox(
            checked = isSelected,
            onCheckedChange = null, // Не изменяемый, только для отображения
            colors = CheckboxDefaults.colors(
                checkedColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.padding(end = 16.dp)
        )

        // Текст варианта ответа
        Text(
            text = answer.text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// Состояние ответа (правильный/неправильный)
sealed class AnswerStatus {
    object Correct : AnswerStatus()
    data class Incorrect(val correctAnswer: String) : AnswerStatus()
}