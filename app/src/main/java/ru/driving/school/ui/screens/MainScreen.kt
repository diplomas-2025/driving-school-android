package ru.driving.school.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import ru.driving.school.ui.nav.models.QuestionsNav
import ru.driving.school.ui.nav.models.ThemesNav
import ru.driving.school.ui.nav.models.TicketsNav
import ru.driving.school.ui.view.BaseLottieAnimation
import ru.driving.school.ui.view.CircularIndicator
import ru.driving.school.ui.view.LottieAnimationType

private enum class MainScreenState(
    val title: String
) {
    Themes("Темы"),
    Tickets("Билеты"),
    Questions("Вопросы")
}

data class UserStatistics(
    val countThemes: Int = 0,
    val countUseThemes: Int = 100,

    val countTickets: Int = 0,
    val countUseTickets: Int = 100,

    val countQuestions: Int = 0,
    val countUseQuestions: Int = 100,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavController
) {
    var selectedIndex by remember { mutableIntStateOf(0) }
    var userStatistics by remember { mutableStateOf(UserStatistics()) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Ладья-Самара",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedIndex == 0,
                    onClick = { selectedIndex = 0 },
                    icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
                    label = { Text("Обучения") }
                )
                NavigationBarItem(
                    selected = selectedIndex == 1,
                    onClick = { selectedIndex = 1 },
                    icon = { Icon(Icons.Filled.Person, contentDescription = "Profile") },
                    label = { Text("Профиль") }
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.TopStart
        ) {
            Column {
                BaseLottieAnimation(
                    type = LottieAnimationType.CAR,
                    modifier = Modifier.fillMaxWidth().height(350.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    MainScreenState.entries.forEach { state ->
                        CircularIndicator(
                            canvasSize = 100.dp,
                            indicatorValue = when(state) {
                                MainScreenState.Themes -> userStatistics.countThemes
                                MainScreenState.Tickets -> userStatistics.countTickets
                                MainScreenState.Questions -> userStatistics.countQuestions
                            },
                            maxIndicatorValue = when(state) {
                                MainScreenState.Themes -> userStatistics.countUseThemes
                                MainScreenState.Tickets -> userStatistics.countUseTickets
                                MainScreenState.Questions -> userStatistics.countUseQuestions
                            },
                            backgroundIndicatorStrokeWidth = 40f,
                            foregroundIndicatorStrokeWidth = 40f,
                            smallText = state.title,
                            bigTextFontSize = MaterialTheme.typography.titleMedium.fontSize,
                            smallTextFontSize = 14.sp,
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    BaseButton(
                        text = "Темы",
                        modifier = Modifier
                            .weight(1f)
                            .padding(5.dp)
                    ) {
                        navController.navigate(ThemesNav)
                    }

                    BaseButton(
                        text = "Билеты",
                        modifier = Modifier
                            .weight(1f)
                            .padding(5.dp)
                    ) {
                        navController.navigate(TicketsNav)
                    }
                }

                BaseButton(
                    text = "Вопросы",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp)
                ) {
                    navController.navigate(QuestionsNav)
                }
            }
        }
    }
}

@Composable
private fun BaseButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier
    ) {
        Text(text = text)
    }
}