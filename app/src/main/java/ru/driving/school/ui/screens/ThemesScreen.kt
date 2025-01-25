package ru.driving.school.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessAlarm
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ru.driving.school.data.network.NetworkApi
import ru.driving.school.data.network.models.QuestionDto
import ru.driving.school.data.network.models.ThemeDto
import ru.driving.school.data.network.models.ThemeState
import ru.driving.school.data.network.models.TicketState
import ru.driving.school.ui.nav.models.ThemeDetailsNav

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemesScreen(
    networkApi: NetworkApi,
    navController: NavController
) {
    val themes = remember { mutableStateListOf<ThemeDto>() }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Fetch tickets
    LaunchedEffect(Unit) {
        isLoading = true
        try {
            themes.clear()
            themes.addAll(networkApi.getThemes())
            errorMessage = null
        } catch (e: Exception) {
            errorMessage = "Failed to load tickets: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Темы (${themes.size})", style = MaterialTheme.typography.titleLarge)
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.TopCenter
        ) {
            when {
                isLoading -> CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                errorMessage != null -> ErrorView(errorMessage = errorMessage ?: "Unknown error")
                themes.isEmpty() -> EmptyStateView()
                else -> ThemesList(themes = themes, navController = navController)
            }
        }
    }
}

@Composable
fun ThemesList(themes: List<ThemeDto>, navController: NavController) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(themes) { theme ->
            ThemeCard(theme = theme, state = ThemeState.entries.random()) {
                navController.navigate(ThemeDetailsNav(id = theme.id))
            }
        }
    }
}

@Composable
fun ThemeCard(theme: ThemeDto, state: ThemeState, onClick: () -> Unit) {
    // Определение цветов для разных состояний
    val stateColors = when (state) {
        ThemeState.TRIED -> MaterialTheme.colorScheme.secondary
        ThemeState.RESOLVED -> MaterialTheme.colorScheme.primary
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp) // Немного увеличиваем высоту для комфортного отображения
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(8.dp),
        shape = MaterialTheme.shapes.large // Округленные углы для более современного вида
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Иконка состояния
            Icon(
                imageVector = when (state) {
                    ThemeState.TRIED -> Icons.Filled.AccessAlarm
                    ThemeState.RESOLVED -> Icons.Filled.CheckCircle
                },
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .padding(end = 16.dp),
                tint = stateColors
            )

            // Текстовое описание билета
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = theme.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))

                // Описание темы
                Text(
                    text = theme.description ?: "Нет описания", // Если описание пустое, выводится "Нет описания"
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))

                // Дополнительная информация о статусе
                Text(
                    text = when (state) {
                        ThemeState.TRIED -> "В процессе"
                        ThemeState.RESOLVED -> "Решено"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = stateColors
                )
            }

            // Добавление статуса с цветом и иконкой
            if (state == ThemeState.RESOLVED) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = "Resolved",
                    modifier = Modifier.size(24.dp),
                    tint = stateColors
                )
            }
        }
    }
}