package ru.driving.school.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessAlarm
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import ru.driving.school.data.network.NetworkApi
import ru.driving.school.data.network.models.ThemeDto
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.driving.school.data.network.UserStorage
import ru.driving.school.data.network.models.QuestionDto
import ru.driving.school.data.network.models.QuestionState
import ru.driving.school.data.network.models.ThemeState
import ru.driving.school.data.network.models.TicketDto
import ru.driving.school.data.network.models.TicketState
import ru.driving.school.ui.nav.models.QuestionDetailsNav
import ru.driving.school.ui.nav.models.TicketDetailsNav

@Composable
fun ThemeDetailsScreen(
    id: Int,
    networkApi: NetworkApi,
    navController: NavController,
    storage: UserStorage
) {
    var theme by remember { mutableStateOf<ThemeDto?>(null) }
    val tickets = remember { mutableStateListOf<TicketDto>() }
    val questions = remember { mutableStateListOf<QuestionDto>() }

    val tabs = listOf("Билеты", "Вопросы")
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        isLoading = true
        try {
            theme = networkApi.getThemeById(id, "Bearer " + storage.getAccessToken())

            tickets.clear()
            questions.clear()
            tickets.addAll(networkApi.getTicketsByThemeId(id, "Bearer " + storage.getAccessToken()))
            questions.addAll(networkApi.getQuestionsByThemeId(id, "Bearer " + storage.getAccessToken()))

            errorMessage = null
        } catch (e: Exception) {
            errorMessage = "Failed to load tickets: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {},
        content = { padding ->
            LazyColumn(contentPadding = padding) {
                item {
                    // Определение цвета для состояния
                    val stateColors = when (theme?.status) {
                        ThemeState.NOT_PASSED, null -> MaterialTheme.colorScheme.secondary
                        ThemeState.RESOLVED -> MaterialTheme.colorScheme.primary
                    }

                    // Используем Card для более современного дизайна
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        elevation = CardDefaults.cardElevation(8.dp),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                        ) {
                            // Название темы
                            Text(
                                text = theme?.name ?: "",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Описание темы
                            Text(
                                text = theme?.description ?: "",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Иконка состояния
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = when (theme?.status) {
                                        ThemeState.NOT_PASSED, null -> Icons.Filled.AccessAlarm
                                        ThemeState.RESOLVED -> Icons.Filled.CheckCircle
                                    },
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .padding(end = 16.dp),
                                    tint = stateColors
                                )

                                // Статус темы
                                Text(
                                    text = when (theme?.status) {
                                        ThemeState.NOT_PASSED, null -> "В процессе"
                                        ThemeState.RESOLVED -> "Решено"
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = stateColors
                                )
                            }
                        }
                    }
                }


                item {
                    TabRow(
                        selectedTabIndex = selectedTabIndex,
                        indicator = { tabPositions ->
                            TabIndicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                                color = MaterialTheme.colorScheme.primary,
                                height = 3.dp
                            )
                        },
                        divider = {}
                    ) {
                        tabs.forEachIndexed { index, tab ->
                            Tab(
                                selected = selectedTabIndex == index,
                                onClick = { selectedTabIndex = index },
                                text = {
                                    Text(
                                        text = tab,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
                                        ),
                                        color = if (selectedTabIndex == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                    )
                                },
                                modifier = Modifier.padding(horizontal = 16.dp) // Паддинг для ширины вкладки
                            )
                        }
                    }
                }

                when (selectedTabIndex) {
                    0 -> items(tickets) { ticket ->
                        Box(
                            modifier = Modifier.padding(horizontal = 15.dp, vertical = 5.dp)
                        ) {
                            TicketCard(ticket = ticket) {
                                navController.navigate(TicketDetailsNav(id = ticket.id))
                            }
                        }
                    }
                    1 -> items(questions) { question ->
                        Box(
                            modifier = Modifier.padding(horizontal = 15.dp, vertical = 5.dp)
                        ) {
                            QuestionCard(question = question) {
                                navController.navigate(QuestionDetailsNav(question.id))
                            }
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun TabIndicator(
    modifier: Modifier,
    color: Color,
    height: Dp
) {
    Box(
        modifier = modifier
            .height(height)
            .fillMaxWidth()
            .background(color)
    )
}