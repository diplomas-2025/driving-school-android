package ru.driving.school.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import ru.driving.school.data.network.NetworkApi
import ru.driving.school.data.network.UserStorage
import ru.driving.school.data.network.models.TicketDetailDto

@Composable
fun TicketDetailsScreen(
    id: Int,
    networkApi: NetworkApi,
    navController: NavController,
    storage: UserStorage
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var selectedAnswerId by remember { mutableStateOf<Int?>(null) }
    var ticketDetailDto by remember { mutableStateOf<TicketDetailDto?>(null) }
    var currentQuestionIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        ticketDetailDto = networkApi.getTicketById(id)
    }

    Scaffold(
        topBar = {
            QuestionsProgressBar(
                ticketDetailDto = ticketDetailDto,
                currentQuestionIndex = currentQuestionIndex,
                onClick = {}
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.TopStart
        ) {
            Column {
                ticketDetailDto?.questions?.getOrNull(currentQuestionIndex)?.let {
                    QuestionDetailsContent(it, selectedAnswerId, null) { answerId ->
                        scope.launch {
                            selectedAnswerId = answerId
                            if (it.answers.firstOrNull { it.id == answerId }?.isCorrect == true) {
                                networkApi.progressQuestion(it.id, "Bearer " + storage.getAccessToken())
                                Toast.makeText(context, "Ответ записан", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    onClick = {
                        if ((ticketDetailDto?.questions?.size ?: 0) - 1 == currentQuestionIndex) {
                            navController.navigateUp()
                        } else {
                            currentQuestionIndex++
                        }
                    }
                ) {
                    Text(text = "Продолжить")
                }
            }
        }
    }
}

@Composable
fun QuestionsProgressBar(
    ticketDetailDto: TicketDetailDto?,
    currentQuestionIndex: Int,
    onClick: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp, horizontal = 16.dp)
    ) {
        Spacer(Modifier.height(30.dp))

        Text(
            text = "Прогресс вопросов",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ticketDetailDto?.questions?.forEachIndexed { index, _ ->
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(50))
                        .background(
                            if (index == currentQuestionIndex) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant
                            }
                        )
                        .border(
                            width = if (index == currentQuestionIndex) 2.dp else 1.dp,
                            color = if (index == currentQuestionIndex) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.outline
                            },
                            shape = RoundedCornerShape(50)
                        )
                        .clickable {
                            onClick(index)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (index + 1).toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (index == currentQuestionIndex) {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }
        }
    }
}
