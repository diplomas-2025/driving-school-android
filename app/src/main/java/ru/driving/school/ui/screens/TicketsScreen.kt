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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ru.driving.school.data.network.NetworkApi
import ru.driving.school.data.network.models.TicketDto
import ru.driving.school.data.network.models.TicketState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketsScreen(
    networkApi: NetworkApi,
    navController: NavController
) {
    var tickets by remember { mutableStateOf<List<TicketDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Fetch tickets
    LaunchedEffect(Unit) {
        isLoading = true
        try {
            tickets = networkApi.getTickets()
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
                    Text("Билеты (${tickets.size})", style = MaterialTheme.typography.titleLarge)
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
                tickets.isEmpty() -> EmptyStateView()
                else -> TicketList(tickets = tickets, navController = navController)
            }
        }
    }
}

@Composable
fun TicketList(tickets: List<TicketDto>, navController: NavController) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(tickets) { ticket ->
            TicketCard(ticket = ticket, state = TicketState.entries.random()) {
                navController.navigate("ticketDetails/${ticket.id}")
            }
        }
    }
}

@Composable
fun TicketCard(ticket: TicketDto, state: TicketState, onClick: () -> Unit) {
    // Определение цветов для разных состояний
    val stateColors = when (state) {
        TicketState.NOT_PASSED -> MaterialTheme.colorScheme.error
        TicketState.TRIED -> MaterialTheme.colorScheme.secondary
        TicketState.RESOLVED -> MaterialTheme.colorScheme.primary
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp) // Немного увеличиваем высоту для комфортного отображения
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
                    TicketState.NOT_PASSED -> Icons.Filled.Error
                    TicketState.TRIED -> Icons.Filled.AccessAlarm
                    TicketState.RESOLVED -> Icons.Filled.CheckCircle
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
                    text = ticket.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                // Дополнительная информация о статусе
                Text(
                    text = when (state) {
                        TicketState.NOT_PASSED -> "Не прошел"
                        TicketState.TRIED -> "Пробовал"
                        TicketState.RESOLVED -> "Решено"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = stateColors
                )
            }

            // Добавление статуса с цветом и иконкой
            if (state == TicketState.RESOLVED) {
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

@Composable
fun ErrorView(errorMessage: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = errorMessage,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { /* Retry logic */ }) {
            Text("Retry")
        }
    }
}

@Composable
fun EmptyStateView() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "No tickets available",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
    }
}
