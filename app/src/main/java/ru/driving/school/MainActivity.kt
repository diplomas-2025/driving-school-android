package ru.driving.school

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import ru.driving.school.data.network.NetworkApi
import ru.driving.school.ui.nav.models.LoginNav
import ru.driving.school.ui.nav.models.MainNav
import ru.driving.school.ui.nav.models.QuestionDetailsNav
import ru.driving.school.ui.nav.models.QuestionsNav
import ru.driving.school.ui.nav.models.SignUpNav
import ru.driving.school.ui.nav.models.ThemeDetailsNav
import ru.driving.school.ui.nav.models.ThemesNav
import ru.driving.school.ui.nav.models.TicketDetailsNav
import ru.driving.school.ui.nav.models.TicketsNav
import ru.driving.school.ui.screens.LoginScreen
import ru.driving.school.ui.screens.MainScreen
import ru.driving.school.ui.screens.QuestionDetailsScreen
import ru.driving.school.ui.screens.QuestionsScreen
import ru.driving.school.ui.screens.SignUpScreen
import ru.driving.school.ui.screens.ThemeDetailsScreen
import ru.driving.school.ui.screens.ThemesScreen
import ru.driving.school.ui.screens.TicketDetailsScreen
import ru.driving.school.ui.screens.TicketsScreen
import ru.driving.school.ui.theme.DrivingschoolandroidTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val networkApi = remember {
                Retrofit.Builder()
                    .baseUrl("https://spotdiff.ru/driving-school-api/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create<NetworkApi>()
            }

            DrivingschoolandroidTheme {
                NavHost(
                    navController = navController,
                    startDestination = MainNav
                ) {
                    composable<SignUpNav> {
                        SignUpScreen(
                            navController = navController
                        )
                    }

                    composable<LoginNav> {
                        LoginScreen(
                            navController = navController
                        )
                    }

                    composable<TicketsNav> {
                        TicketsScreen(
                            networkApi = networkApi,
                            navController = navController
                        )
                    }

                    composable<QuestionsNav> {
                        QuestionsScreen(
                            networkApi = networkApi,
                            navController = navController
                        )
                    }

                    composable<ThemesNav> {
                        ThemesScreen(
                            networkApi = networkApi,
                            navController = navController
                        )
                    }

                    composable<MainNav> {
                        MainScreen(
                            networkApi = networkApi,
                            navController = navController
                        )
                    }

                    composable<ThemeDetailsNav> {
                        ThemeDetailsScreen(
                            networkApi = networkApi,
                            navController = navController,
                            id = it.toRoute<ThemeDetailsNav>().id
                        )
                    }

                    composable<QuestionDetailsNav> {
                        QuestionDetailsScreen(
                            networkApi = networkApi,
                            navController = navController,
                            id = it.toRoute<QuestionDetailsNav>().id
                        )
                    }

                    composable<TicketDetailsNav> {
                        TicketDetailsScreen(
                            networkApi = networkApi,
                            navController = navController,
                            id = it.toRoute<TicketDetailsNav>().id
                        )
                    }
                }
            }
        }
    }
}
