package ru.driving.school.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import ru.driving.school.R
import ru.driving.school.data.network.NetworkApi
import ru.driving.school.data.network.UserStorage
import ru.driving.school.data.network.models.User
import ru.driving.school.data.network.models.UserStatistics
import ru.driving.school.ui.nav.models.LoginNav
import ru.driving.school.ui.nav.models.MainNav
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    networkApi: NetworkApi,
    storage: UserStorage,
    navController: NavController
) {
    var selectedIndex by remember { mutableIntStateOf(0) }
    var userStatistics by remember { mutableStateOf(UserStatistics()) }
    var user by remember { mutableStateOf<User?>(null) }

    LaunchedEffect(Unit) {
        storage.getAccessToken()?.let { token ->
            user = networkApi.getInfo("Bearer $token")
            userStatistics = networkApi.getProgress("Bearer $token")
        }
    }

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
                when(selectedIndex) {
                    0 -> {
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
                                        MainScreenState.Themes -> userStatistics.countUseThemes
                                        MainScreenState.Tickets -> userStatistics.countUseTickets
                                        MainScreenState.Questions -> userStatistics.countUseQuestions
                                    },
                                    maxIndicatorValue = when(state) {
                                        MainScreenState.Themes -> userStatistics.countThemes
                                        MainScreenState.Tickets -> userStatistics.countTickets
                                        MainScreenState.Questions -> userStatistics.countQuestions
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
                    1 -> {
                        Spacer(Modifier.height(20.dp))

                        Image(
                            painter = painterResource(R.drawable.profile),
                            contentDescription = null,
                            modifier = Modifier.fillMaxWidth().height(200.dp)
                        )

                        Spacer(Modifier.height(5.dp))

                        Text(
                            text = user?.username ?: "",
                            fontSize = 22.sp,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )

                        Spacer(Modifier.height(50.dp))

                        BaseButton(
                            text = "Выйти",
                            color = Color.Red,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(5.dp)
                        ) {
                            storage.saveAccessToken(null)
                            navController.navigate(LoginNav) {
                                popUpTo(MainNav) {
                                    inclusive = true
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BaseButton(
    text: String,
    color: Color = Color.Unspecified,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = color
        )
    ) {
        Text(text = text)
    }
}