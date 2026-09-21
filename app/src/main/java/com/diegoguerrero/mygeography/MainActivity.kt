package com.diegoguerrero.mygeography

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.diegoguerrero.mygeography.data.datasource.PaisesData
import com.diegoguerrero.mygeography.data.model.TipoQuiz
import com.diegoguerrero.mygeography.ui.screens.globo.GloboTerraqueoScreen
import com.diegoguerrero.mygeography.ui.screens.listado.ListadoPaisesScreen
import com.diegoguerrero.mygeography.ui.screens.menu.MenuScreen
import com.diegoguerrero.mygeography.ui.screens.quiz.QuizScreen
import com.diegoguerrero.mygeography.ui.screens.quiz.QuizViewModel
import com.diegoguerrero.mygeography.ui.screens.resumen.ResultadosScreen
import com.diegoguerrero.mygeography.ui.screens.splash.SplashScreen
import com.diegoguerrero.mygeography.ui.theme.DarkBackground
import com.diegoguerrero.mygeography.ui.theme.MyGeographyTheme

sealed class Screen(val route: String) {
    object Menu : Screen("menu")
    object Quiz : Screen("quiz")
    object Resultados : Screen("resultados")
    object Listado : Screen("listado")
    object Globo : Screen("globo")
}

class MainActivity : ComponentActivity() {

    private val quizViewModel: QuizViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PaisesData.inicializar(applicationContext)
        setContent {
            MyGeographyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DarkBackground
                ) {
                    var showSplash by remember { mutableStateOf(true) }

                    Crossfade(targetState = showSplash, label = "splashTransition") { isSplash ->
                        if (isSplash) {
                            SplashScreen(onSplashFinished = { showSplash = false })
                        } else {
                            MainAppNavigation(quizViewModel = quizViewModel)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MainAppNavigation(quizViewModel: QuizViewModel) {
    val navController = rememberNavController()
    val uiState by quizViewModel.uiState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Screen.Menu.route
    ) {
        composable(Screen.Menu.route) {
            MenuScreen(
                onIniciarQuizBanderas = { region, incluirDependientes ->
                    quizViewModel.iniciarQuiz(TipoQuiz.BANDERAS, region, incluirDependientes)
                    navController.navigate(Screen.Quiz.route)
                },
                onIniciarQuizCapitales = { region, incluirDependientes ->
                    quizViewModel.iniciarQuiz(TipoQuiz.CAPITALES, region, incluirDependientes)
                    navController.navigate(Screen.Quiz.route)
                },
                onAbrirListado = {
                    navController.navigate(Screen.Listado.route)
                },
                onAbrirGlobo = {
                    navController.navigate(Screen.Globo.route)
                }
            )
        }

        composable(Screen.Quiz.route) {
            QuizScreen(
                viewModel = quizViewModel,
                onVolverAlMenu = {
                    navController.navigate(Screen.Menu.route) {
                        popUpTo(Screen.Menu.route) { inclusive = true }
                    }
                },
                onQuizTerminado = {
                    navController.navigate(Screen.Resultados.route) {
                        popUpTo(Screen.Quiz.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Resultados.route) {
            ResultadosScreen(
                tipoQuiz = uiState.tipoQuiz,
                region = uiState.region,
                respuestas = uiState.respuestas,
                onVolverAlMenu = {
                    navController.navigate(Screen.Menu.route) {
                        popUpTo(Screen.Menu.route) { inclusive = true }
                    }
                },
                onReiniciarQuiz = {
                    quizViewModel.iniciarQuiz(uiState.tipoQuiz, uiState.region)
                    navController.navigate(Screen.Quiz.route) {
                        popUpTo(Screen.Resultados.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Listado.route) {
            ListadoPaisesScreen(
                onVolverAlMenu = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Globo.route) {
            GloboTerraqueoScreen(
                onVolverAlMenu = {
                    navController.popBackStack()
                }
            )
        }
    }
}
